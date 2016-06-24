(ns sigil.views.issue-page
  (:require [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil user-org-or-nil user-is-admin-of-org? user-has-role?]]
            [sigil.db.issues :refer [get-issue-with-poster-by-id]]
            [sigil.db.officialresponses :refer [get-official-responses-by-issue get-responses-with-responders-by-issue]]
            [sigil.db.comments :refer [get-comments-by-issue get-comments-with-commenters-by-issue]]
            [sigil.db.votes :refer [user-voted-on-issue?]]
            [sigil.db.orgs :refer [get-org-by-url]]
            [sigil.db.tags :refer [get-tag-by-issue]]
            [sigil.db.reports :refer [user-reported-issue? get-number-reports-by-issue]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            )
  (:use [hiccup.form]
        [hiccup.core]))

(declare issue-page-handler issue-page-body)

(defn constituency?
  [org poster]
  (if (nil? (:zip_code poster))
    false
    (contains? (:zip_codes org) (:zip_code poster))))

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
        issue (get-issue-with-poster-by-id (read-string (:issue_id (:route-params req))))
        tag (get-tag-by-issue issue)
        org (get-org-by-url (:org_url (:route-params req)))
        in-constituency? (constituency? org (:poster issue))]
    (do (sigil.db.core/db-trans [sigil.db.issues/issue-view-inc (:issue_id issue)])
      (layout/render
       req
       user
       (user-org-or-nil user)
       (str "Sigil - " (:title issue))
       (issue-page-body user
                        issue
                        tag
                        org
                        (some? user)
                        in-constituency?
                        (if (some? user)
                          (user-voted-on-issue? user issue)
                          false)
                        (get-responses-with-responders-by-issue issue)
                        (get-comments-with-commenters-by-issue issue)
                        )))))

(defn issue-page-body
  [user issue tag org authenticated? in-constituency? user-voted? responses comments]
  (html
   [:div.col-md-9.col-lg-9
    [:img.img-rounded.img-responsive.org-banner-small
     {:src (:banner org)}]
    [:div.btn-group.btn-group-sm.btn-group-justified
     {:style "margin-bottom: 20px"}
     [:a.btn.btn-warning {:href (str "/" (:org_url org))} "Main feed"]
     [:a.btn.btn-info
      {:href (str "/" (:org_url org) "/responses")}
      "Responses"]]
    [:div.panel.panel-default.issue-panel-partial
     [:div.panel-body
      {:style "padding-bottom: 0px;"}
      [:div.media
       [:div.media-object.pull-left.votebutton-box
        (if authenticated?
          (if user-voted?
            [:img.vote-button.unvoteup {:src "/images/voted.png"
                                        :data-issueid (:issue_id issue)}]
            [:img.vote-button.voteup {:src "/images/notvoted.png"
                                      :data-issueid (:issue_id issue)}])
       [:a {:href (str "/login?return=" (:org_url org) "/" (:issue_id issue))}
          [:img.votelogin {:src "/images/notvoted.png"}]])
        [:br]
        [:span.voteamount
         {:id (str "count-" (:issue_id issue))} (:total_votes issue)]]
       [:div.media-body
        [:h4.media-heading (:title issue)]
        [:p.pull-left
         [:img.issue-panel-icon {:src (:icon_30 org)}]
         [:a {:href (str "/" (:org_url org))} (str (:org_name org) " ")]
         [:span.label.label-pill.label-default
          [:img.tag-icon {:src (:icon_30 tag)}]
          (:tag_name tag)]]
        [:p.pull-right
         (str "Posted at " (clj-time.coerce/to-local-date (:created_at issue)) " by ")
         [:img {:src (:icon_30 user)}]
         (:username user)
         (when (some? user)
           (html
             " "
             [(if (user-reported-issue? user issue)
                :span.glyphicon.glyphicon-flag.report-flag.reported
                :span.glyphicon.glyphicon-flag.report-flag.unreported)
              {:data-issueid (:issue_id issue)
               :aria-hidden "true"}]))]]]]
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
           [:small [:i " Posted " (clj-time.coerce/to-local-date (:created_at r))]]]
          [:p (:text r)]]]]])
    (if (user-is-admin-of-org? user org)
      [:div.panel.panel-default
       [:div.panel-heading "Make an official response"]
       [:div.panel-body
        (form-to
         [:post "/postofficial"]
         [:div.form-group
          (text-area
           {:class "form-control panel-input-box"
            :id "input-re"
            :placeholder "Address this suggestion for your customers"}
           "response")
          (hidden-field "issue-id" (:issue_id issue))]
         (submit-button {:class "btn btn-primary"
                         :id "submit-response"}
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
             [:small [:i " Posted on " (clj-time.coerce/to-local-date (:created_at c))]]]
            [:p (:text c)]
            [:p.pull-right
             (if (user-has-role? user :site-admin)
               [:form {:method "post" :action "/deletecomment"}
                (hidden-field "comment-id" (:comment_id c))
                (submit-button {:class "btn btn-xs btn-primary"
                                :id "delete-comment"}
                               "Delete Comment")])]]]))
      (if (some? user)
        (form-to
         [:post "/submitcomment"]
         ;{:class "issue-add-comment"}
         [:div.form-group
          (label "add-comment-label" "Add a comment")
          (text-area {:class "form-control panel-input-box"
                      :id "add-comment-box"
                      :placeholder "What would you like to say?"}
                     "add-comment-box")
          (hidden-field "issue-id" (:issue_id issue))]
         (submit-button {:class "btn btn-primary" :id "submit-comment"} "Submit comment"))
        nil)]]]
   (sidebar-partial org)))
