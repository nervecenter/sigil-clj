(ns sigil.views.issue-page)

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
  (let [user (user-or-nil req)
        issue (get-issue-with-user-and-org-by-id (:issue_id (:route-params req)))
        authenticated? (some? user)
        user-voted? (if authenticated?
                      (user-voted-on-issue? (:user_id user))
                      false)
        responses (get-responses-by-issue (:issue_id issue))
        comments (get-comments-by-issue (:issue_id issue))]
    (layout/render (str "Sigil - " (:title issue))
                   (issue-page-body user
                                    issue
                                    authenticated?
                                    user-voted?
                                    responses
                                    comments))))

(defn issue-page-body [user issue authenticated? user-voted? responses comments]
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
