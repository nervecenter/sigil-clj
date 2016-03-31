(ns sigil.views.site-admin
  (:require [sigil.auth :refer [user-or-nil is-user-site-admin?]]
            [sigil.views.layout :as layout]
            [sigil.views.not-found :refer [not-found-handler]]
            [sigil.db.orgs :refer [get-all-orgs get-org-by-user]]
            [sigil.db.core :as db]
            [sigil.db.roles :as roles]
            [hiccup.core :refer [html]]))

(declare site-admin-handler site-admin-body)


(defn error-partial
  [error]
  [:div.panel.panel-info.issue-panel-partial
   [:div.panel-body
    [:div.media
     [:div.media-body
      [:h4.media-heading (:error_message error)]
      [:p.pull-right
       (str "Created at " (clj-time.coerce/to-local-date (:created_at error)) "\n by user_id") 
       (:user_assoc error) " with org_id " (:org_assoc error) " with issue_id " (:issue_assoc error)]]]]
   [:div.panel-body (:additional_info error)]])

(defn site-admin-handler [req]
  (let [user (user-or-nil req)]
    (if (is-user-site-admin? user)
      (let [user-org (get-org-by-user user)
            all-orgs (get-all-orgs)
            roles (roles/get-all-roles)
            errors (db/errors)]
        (layout/render req
                       user
                       user-org
                       "Sigil - Site Administration"
                       (site-admin-body all-orgs roles errors)))
     (not-found-handler req))))

(defn site-admin-body [all-orgs roles errors]
  [:div.container.settings-container
   [:div.row
    [:div.col-lg-6
     ;; Orgs
     [:h2 "Orgs"]
     (for [o all-orgs]
       [:div.panel.panel-default
        [:div.panel-body
         [:div.media
          [:div.pull-left
           [:img.media-object {:src (:icon_100 o)}]]
          [:media-body
           [:div.media-heading (:org_name o)]
           (for [k (keys o)]
             (html
              [:span {:style "font-size:12px;"}
               [:b k] ": " (str (k o))]
              [:br]))]]
         (if (:org_approved o)
           [:a.btn.btn-block.btn-success "Activate"]
           [:a.btn.btn-block.btn-danger "Deactivate"])]])]
    [:div.col-lg-6
     ;; Roles, error log
     [:h2 "Roles"]
     [:div.panel.panel-default
      [:div.panel-body
       (for [r roles]
         [:h4 (:role_name r)])]]
     [:h2 "Error Log"]
     [:div.panel.panel-default
      [:div.panel-body
       (if (some? (first errors))
         (for [e errors]
           (error-partial e))
         [:h3 "No new errors."])]]]]])
