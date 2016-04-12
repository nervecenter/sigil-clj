(ns sigil.views.org-page
  (:require [sigil.db.orgs :refer [get-org-by-url]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.db.orgs :refer [get-org-by-url get-org-by-user org-visit-inc]]
            [sigil.db.tags :refer [get-tags-by-org]]
            [sigil.db.issues :refer [get-hottest-issues-by-org]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            [sigil.views.not-found :refer [not-found-handler]])
  (:use hiccup.form
        hiccup.page
        hiccup.core))

(declare org-page-handler org-page-body)

(defn org-page-handler [req]
  (let [user (user-or-nil req)
        user-org (get-org-by-user user)
        org (get-org-by-url (:org_url (:route-params req)))
        tags (get-tags-by-org org)]
    (if (some? org)
      (let [issues (get-hottest-issues-by-org org)]
        (do
          (sigil.db.core/db-trans [org-visit-inc (:org_id org)])
          (layout/render req
                         user
                         user-org
                         (str "Sigil - " (:org_name org))
                         (org-page-body req user org tags issues))))
      (not-found-handler req))))

(defn org-page-body [req user org tags issues]
  (html
   [:div#main-col.col-md-9.col-lg-9
     [:img.img-rounded.img-responsive.org-banner-small
      {:src (:banner org)}]
     [:div.btn-group.btn-group-sm.btn-group-justified
      {:style "margin-bottom:20px;"}
      [:a.btn.btn-default.active "Main feed"]
      [:a.btn.btn-info {:href (str "/" (:org_url org) "/responses")} "Responses"]]
     [:div.panel
      [:div.panel-body
       [:form#issue-search-post-form
        {:method "post" :action "/postissue"}
        [:div.form-group
         (label {:id "suggest-label"} "title" "I suggest you...")
         (text-area {:id "issues-by-org-search"
                     :class "form-control org-feedback-input-box"
                     :data-orgid (:org_id org)}
                    "title")]
        [:div#new-feedback-group.form-group
         [:a#new-feedback-button.btn.btn-primary.pull-right
          {:style "padding:4px 9px"}
          "Submit this as new feedback"]
         (label {:class "pull-right" :style "margin:5px 10px;"} "new-feedback" "Has nobody posted what you're suggesting?")]
        (hidden-field "org-id" (:org_id org))]]]
     [:div#issues
      (for [i issues]
        (issue-partial (:uri req) i org user true))]]
   (sidebar-partial org user)))

