(ns sigil.views.issue-page
  (:require [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.helpers :refer [get-issue-with-user-and-org-by-issue-id user-is-admin-of-org?]]
            [sigil.db.officialresponses :refer [get-official-responses-by-issue]]
            [sigil.db.comments :refer [get-comments-by-issue]]
            [sigil.db.votes :refer [user-voted-on-issue?]]
            [sigil.views.layout :as layout])
  (:use [hiccup.form]))

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
  (let [[issue issue-user org] (get-issue-with-user-and-org-by-issue-id (read-string (:issue_id (:route-params req))))
        ;org (get-org-by-url (:org_url (:route-params req)))
        user (user-or-nil req)]
    (do (sigil.db.core/db-trans [sigil.db.issues/issue-view-inc (:issue_id issue)])
      (layout/render
       req
       user
       (user-org-or-nil user)
       (str "Sigil - " (:title issue))
       (issue-page-body user
                        issue
                        org
                        (some? user)
                        (if (some? user)
                          (user-voted-on-issue? (:user_id user) (:issue_id issue))
                          false)
                        nil ;(get-responses-with-responders-by-issue-id (:issue_id issue))
                        nil ;(get-comments-with-commenters-by-issue-id (:issue_id issue))
                        )))))

(defn issue-page-body
  [user
   issue
   org
   authenticated?
   user-voted?
   responses
   comments]

  [:div.col-md-9.col-lg-9
   [:img.img-rounded.img-responsive.org-banner-small
    {:src (:banner issue)}]
   [:div.btn-group.btn-group-sm.btn-group-justified
    {:style "margin-bottom:20px;"}
    [:a.btn.btn-warning {:href (:org_url org)} "Main feed"]
    [:a.btn.btn-info {:href (str (:org_url org) "/responses")}]]
   [:div.panel.panel-default.issue-panel-partial
    [:div.panel-body
     [:div.media
      [:div.media-object.pull-left.votebutton-box
       (if authenticated?
         [:img.votelogin {:src "images/notevoted.png"}]
         (if user-voted?
           [:img.unvoteup {:src "images/voted.png"
                           :data-issueid (:issue_id issue)}]
           [:img.voteup {:src "images/notvoted.png"
                         :data-issueid (:issue_id issue)}]))
       [:br]
       [:span.voteamount
        {:id (str "count-" (:issue_id issue))} (:votes issue)]]
      [:div.media-body
       [:h4.media-heading
        [:a
         {:href (str "/"
                     (:org_url org) "/"
                     (:issue_id issue) "/")}
         (:title issue)]]
       [:p.pull-left
        [:img.issue-panel-icon {:src (:icon_30 org)}]
        [:a {:href (:org_url org)} (:org_name org)]]
       [:p.pull-right "Posted by " (:username (:poster issue))]]]]
    [:div.panel-body (:text issue)]]
   (for [r responses]
     [:div.panel.panel-primary
      [:div.panel-heading "Official Response"]
      [:div.panel-body
       [:div.media
        [:div.pull-left
         [:img.media-object {:src (:icon_100 (:responder r))}]]
        [:div.media-body
         [:h4.media-heading
          (:username (:responder r))
          [:small [:i "Posted some time ago"]]]
         [:p (:text r)]]]]])
   (if (user-is-admin-of-org? user org)
     [:div.panel.panel-default
      [:div.panel-heading "Make an official response"]
      [:div.panel-body
       (form-to
        [:post]
        [:div.form-group
         (text-area
          {:class "form-control panel-input-box"
           :id "input-re"
           :placeholder "Address this suggestion for your customers"}
          "response")]
        (submit-button {:class "btn btn-primary"}
                       "submit-response"
                       "Submit response"))]]
     nil)
   [:div.panel.panel-default
    [:div.panel-heading "Comments"]
    [:div.panel-body
     (if (= 0 (count comments))
       [:i "No comments yet."]
       (for [c comments]
         [:div.media
          [:div.pull-left
           [:img.media-object {:src (:icon_100 (:commenter c))}]]
          [:div.media-body
           [:h4.media-heading
            (:username (:commenter c))
            [:small [:i "Posted on some date"]]]
           [:p (:text c)]]]))
     (if (some? user)
       (form-to
        [:post "/submitcomment"]
        {:class "issue-add-comment"}
        [:div.form-group
         (label "add-comment-label" "Add a comment")
         (text-area {:class "form-control panel-input-box"
                     :id "add-comment-box"
                     :placeholder "What would you like to say?"}
                    "add-comment-box")]
        (submit-button {:class "btn btn-primary"} "Submit comment"))
       nil)]]])
