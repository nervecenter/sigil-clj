(ns sigil.views.site-admin
  (:require [sigil.auth :refer [user-or-nil is-user-site-admin?]]
            [sigil.views.layout :as layout]
            [sigil.views.not-found :refer [not-found-handler]]
            [sigil.db.orgs :refer [get-all-orgs get-org-by-user]]
            [hiccup.core :refer [html]]))

(declare site-admin-handler site-admin-body)

(defn site-admin-handler [req]
  (let [user (user-or-nil req)]
    (if (is-user-site-admin? user)
      (let [user-org (get-org-by-user user)
            all-orgs (get-all-orgs)
            ]
        (layout/render req
                       user
                       user-org
                       "Sigil - Site Administration"
                       (site-admin-body all-orgs)))
     (not-found-handler req))))

(defn site-admin-body [all-orgs]
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
       [:p "WHAT THE HELL DO WE DO HERE"]
       [:p "Maybe we do a roles table again"]]]
     [:h2 "Error Log"]
     [:div.panel.panel-default
      [:div.panel-body
       ;;(for [e errors])
       ]]]]])
