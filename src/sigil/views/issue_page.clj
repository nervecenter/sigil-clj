(ns sigil.views.issue-page
  (:require [sigil.auth :refer [user-or-nil]]
            [sigil.db.issues :refer [get-issue-with-user-and-org-by-id]]
            [sigil.db.responses :refer [get-responses-by-issue]]
            [sigil.db.comments :refer [get-comments-by-issue]]))

(declare issue-page-handler issue-page-body)

(defn issue-page-handler [req]
  ;; Issue page needs following data:
  ;; User for navbar, vote check
  ;; The issue, because it's the main content
    ;; The issue's user for icon, name data
    ;; Issue's org for banner, links, etc.
  ;; List of responses to the issue
    ;; Each response should contain responding user for icon, name, etc.
  ;; List of comments on the issue
    ;; Each comment should have commenting user for icon, name
  (layout/render
   (str "Sigil - " (:title issue))
   (issue-page-body (user-or-nil req)
                    (get-issue-with-user-and-org-by-id
                     (:issue_id (:route-params req)))
                    (some? user)
                    (if (some? user)
                      (user-voted-on-issue? (:user_id user))
                      false)
                    (get-responses-by-issue (:issue_id issue))
                    (get-comments-by-issue (:issue_id issue)))))

(defn issue-page-body
  [user
   issue
   authenticated?
   user-voted?
   responses
   comments]

  [:div.col-md-9.col-lg-9
   [:img.img-rounded.img-responsive.org-banner-small
    {:src (:banner issue)}]
   [:div.btn-group.btn-group-sm.btn-group-justified
    {:style "margin-bottom:20px;"}
    [:a.btn.btn-warning {:href (:org_url (:org issue))} "Main feed"]
    [:a.btn.btn-info {:href (str (:org_url (:org issue)) "/responses")}]]
   [:div.panel.panel-default.issue-panel-partial
    [:div.panel-body
     [:div.media
      [:div.media-object.pull-left.votebutton-box]]]]])
