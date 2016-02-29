(ns sigil.views.org-page
  (:require [sigil.db.orgs :refer [get-org-by-url]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.db.orgs :refer [get-org-by-url]]
            [sigil.db.tags :refer [get-tags-by-org]]
            [sigil.db.issues :refer [get-hottest-issues-by-org]]
            [sigil.partials.sidebar :refer [sidebar-partial]])
  (:use hiccup.form))

(declare org-page-handler org-page-body)

(defn org-page-handler [req]
  (let [user (user-or-nil req)
        org (get-org-by-url (:org_url (:route-params req)))
        tags (get-tags-by-org (:org_id org))]
    (if (some? org)
      (let [issues (get-hottest-issues-by-org (:org_id org))]
        (layout/render (str "Sigil - " (:org_name org))
                       (org-page-body req user org tags issues)))
      ("404"))))

(defn org-page-body [req user org tags issues]
  [:div#main-col.col-md-9.col-lg-9
   [:img.img-rounded.img-responsive.org-banner-small
    {:src (:banner org)}]
   [:div.btn-group.btn-group-sm.btn-group-justified
    {:style "margin-bottom:20px;"}
    [:a.btn.btn-default.active "Main feed"]
    [:a.btn.btn-info {:href (str "/" (:org_url org) "/responses")}]]
   [:div.panel
    [:div.panel-body
     (form-to
      {:id "issue-search-post-form"}
      [:post "/postissue"]
      [:div.form-group
       (label {:id "suggest-label"} "title" "I suggest you...")
       (text-area {:id "issues-by-org-search"
                   :class "form-control org-feedback-input-box"
                   :data-orgid (:org_id org)}
                  "title")]
      [:div#new-feedback-group.form-group
       [:div#new-feedback-button.btn.btn-primary.pull-right
        {:style "padding:4px 9px;"} "Submit this as new feedback"]
       (label {:class "pull-right" :style "margin:5px 10px;"} "new-feedback" "Has nobody posted what you're suggesting?")]
      [:div#tag-select-group.form-group
       (label "tag-select" "Tag your feedback by product, department, or category:")
       [:select#tag-select.form-control {:name "tag-select"}
        (for [t tags]
          [:option {:value (:tag_id t)} (:tag_name t)])]])]]
   [:div.issues
    (for [i issues]
      (issue-partial req i user true))]]
  (sidebar-partial org user))
