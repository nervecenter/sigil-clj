(ns sigil.views.partials.issue
  (:require [sigil.db.votes :refer [user-voted-on-issue?]]
            [sigil.db.officialresponses :refer [get-latest-official-response-by-issue]]
            [sigil.db.orgs :refer [get-org-by-issue]]
            [sigil.db.users :refer [get-user-by-issue]]
            [sigil.db.tags :refer [get-tag-by-issue]]
            [sigil.db.reports :refer [user-reported-issue? get-number-reports-by-issue]]
            [sigil.db.petitions :refer [issue-petitioned?]]
            [sigil.auth :as auth]
            [sigil.helpers :refer [get-return]]
            [hiccup.core :refer [html]])
  (:use [hiccup.form]))

(declare issue-partial issue-panel)

(defn issue-partial
  ([uri issue user]
   (issue-partial uri issue (get-org-by-issue issue) user))
  ([uri issue issue-org user]
    (issue-panel uri
                 user
                 issue
                 issue-org
                 (get-tag-by-issue issue)
                 (get-user-by-issue issue)
                 (some? user)
                 (if (some? user)
                   (user-voted-on-issue? user issue)
                   false)
                 (issue-petitioned? issue)
                 (:responded issue)
                 (get-latest-official-response-by-issue issue))))

(defn issue-panel [uri
                   user
                   issue
                   issue-org
                   issue-tag
                   issue-user
                   authenticated?
                   user-voted?
                   petitioned?
                   responded?
                   latest-response]
  [(if responded?
     :div.panel.panel-info.issue-panel-partial
     :div.panel.panel-default.issue-panel-partial)
   [:div.panel-body
   [:div.media
    [:div.media-object.pull-left.votebutton-box
     (if authenticated?
       (if user-voted?
         [:img.vote-button.unvoteup {:data-issueid (:issue_id issue)
                                     :src "/images/voted.png"}]
         [:img.vote-button.voteup {:data-issueid (:issue_id issue)
                       :src "/images/notvoted.png"}])
       [:a {:href (str "/login?return=" uri)}
        [:img.votelogin {:src "/images/notvoted.png"}]])
     [:br]
     [:span.voteamount
      {:id (str "count-" (:issue_id issue))}
      (:total_votes issue)]]
    [:div.media-body
     [:h4.media-heading
      [:a {:href (str "/" (:org_url issue-org)
                      "/" (:issue_id issue))} (:title issue)]]
     [:p.pull-left
      [:img.issue-panel-icon {:src (:icon_30 issue-org)}]
      [:a {:href (str "/" (:org_url issue-org))} (str (:org_name issue-org) " ")]
      [:span.label.label-pill.label-default
       [:img.tag-icon {:src (:icon_30 issue-tag)}]
       (:tag_name issue-tag)]]
     [:p.pull-right
      (str "Posted at " (clj-time.coerce/to-local-date (:created_at issue)) " by ")
      [:img {:src (:icon_30 issue-user)}]
      (:username issue-user)
      (when (some? user)
        (html
          " "
          [(if (user-reported-issue? user issue)
           :span.glyphicon.glyphicon-flag.report-flag.reported
           :span.glyphicon.glyphicon-flag.report-flag.unreported)
           {:data-issueid (:issue_id issue)
            :aria-hidden "true"}]))
      ;; (if (auth/user-has-role? user :site-admin)
      ;;   [:form {:method "post" :action "/archiveissue"}
      ;;    (hidden-field "org-id" (:org_id issue-org))
      ;;    (hidden-field "issue-id" (:issue_id issue))
      ;;    (submit-button {:class "btn btn-xs btn-primary"
      ;;                    :id "delete-issue"}
      ;;                   "Delete Issue")])
      ]]]]
   (if responded?
     [:div.panel-footer
      [:b "Response: "]
      (if (> (count (:text latest-response)) 100)
        [:span (str (subs (:text latest-response) 0 100) "...")]
        [:span (:text latest-response)])])
   (cond
     (auth/user-has-role? user :site-admin)
     [:div.panel-footer {:style "height:55px;"}
      [:form {:method "post" :action "/archiveissue" :style "float:right;"}
       (hidden-field "org-id" (:org_id issue-org))
       (hidden-field "issue-id" (:issue_id issue))
       (submit-button {:class "btn btn-sm btn-primary"
                       :id "archive-issue"}
                      "Archive Issue")]
      " "
      [:span.label.label-default
       (str (get-number-reports-by-issue issue) " Reports")]]

     (= (:org_id user) (:org_id issue-org))
     [:div.panel-footer
      [:span.label.label-default
       (str (get-number-reports-by-issue issue) " Reports")]
      " "
      (if petitioned?
        [:a.btn.btn-sm.btn-primary.disabled
         "Petition submitted."]
        [:a.btn.btn-sm.btn-primary.start-petition
          {:data-issueid (:issue_id issue)
           :data-orgid (:org_id issue-org)}
          "Petition removal of this issue"])])])
