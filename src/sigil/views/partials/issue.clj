(ns sigil.views.partials.issue
  (:require [sigil.db.votes :refer [user-voted-on-issue?]]
            [sigil.db.officialresponses :refer [get-latest-official-response-by-issue]]
            [sigil.db.orgs :refer [get-org-by-issue]]
            [sigil.db.users :refer [get-user-by-issue]]
            [sigil.db.tags :refer [get-tag-by-issue]]
            [sigil.db.reports :refer [user-reported-issue? get-number-reports-by-issue]]
            [sigil.auth :as auth]
            [sigil.helpers :refer [get-return]]
            [hiccup.core :refer [html]])
  (:use [hiccup.form]))

(declare issue-partial issue-panel issue-without-panel)

(defn issue-partial
  ([uri issue user in-panel?]
   (issue-partial uri issue (get-org-by-issue issue) user in-panel?))
  ([uri issue issue-org user in-panel?]
   ;; We need: The issue, whether the user is authed, and whether they voted
   (let [authenticated? (some? user)
         user-voted? (if authenticated?
                       (user-voted-on-issue? user issue)
                       false)
         issue-user (get-user-by-issue issue)
         issue-tag (get-tag-by-issue issue)]
     (if in-panel?
       ;; We need: a response
       (issue-panel uri
                    user
                    issue
                    issue-org
                    issue-tag
                    issue-user
                    authenticated?
                    user-voted?
                    (:responded issue)
                    (get-latest-official-response-by-issue issue))
       (issue-without-panel uri
                            user
                            issue
                            issue-org
                            issue-tag
                            issue-user
                            authenticated?
                            user-voted?)))))

(defn issue-panel [uri user issue issue-org issue-tag issue-user authenticated? user-voted? responded? latest-response]
  [(if responded?
     :div.panel.panel-info.issue-panel-partial
     :div.panel.panel-default.issue-panel-partial)
   (issue-without-panel uri
                        user
                        issue
                        issue-org
                        issue-tag
                        issue-user
                        authenticated?
                        user-voted?)
   (if responded?
     [:div.panel-footer
      [:b "Response: "]
      (if (> (count (:text latest-response)) 100)
        [:span (str (subs (:text latest-response) 0 100) "...")]
        [:span (:text latest-response)])])
   (if (= (:org_id user) (:org_id issue-org))
     [:div.panel-footer
      (if (auth/user-has-role? user :site-admin)
        [:form {:method "post" :action "/deleteissue"}
         (hidden-field "org-id" (:org_id issue-org))
         (hidden-field "issue-id" (:issue_id issue))
         (submit-button {:class "btn btn-xs btn-primary"
                         :id "delete-issue"}
                        "Delete Issue")])
      [:span.label.label-default
       (str (get-number-reports-by-issue issue) " Reports")]])

      ])

(defn issue-without-panel [uri user issue issue-org issue-tag issue-user authenticated? user-voted?]
  [:div.panel-body
   [:div.media
    [:div.media-object.pull-left.votebutton-box
     (if authenticated?
       (if user-voted?
         [:img.unvoteup {:data-issueid (:issue_id issue)
                         :src "/images/voted.png"}]
         [:img.voteup {:data-issueid (:issue_id issue)
                       :src "/images/notvoted.png"}])
       [:a {:href (str "login?return=" uri)}
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
       ;;[:img.issue-panel-icon {:src (:icon_30 issue-tag)}]
       (:tag_name issue-tag)]]
     [:p.pull-right
      (str "Posted at " (clj-time.coerce/to-local-date (:created_at issue)) " by ")
      [:img {:src (:icon_30 issue-user)}]
      (:username issue-user)
      " "
      (if (and (some? user)
               (user-reported-issue? user issue))
        [:span.glyphicon.glyphicon-flag.reported
         {:data-issueid (:issue_id issue)
          :aria-hidden "true"}]
        [:span.glyphicon.glyphicon-flag.unreported
         {:data-issueid (:issue_id issue)
          :aria-hidden "true"}]
        )
      ;; (if (auth/user-has-role? user :site-admin)
      ;;   [:form {:method "post" :action "/deleteissue"}
      ;;    (hidden-field "org-id" (:org_id issue-org))
      ;;    (hidden-field "issue-id" (:issue_id issue))
      ;;    (submit-button {:class "btn btn-xs btn-primary"
      ;;                    :id "delete-issue"}
      ;;                   "Delete Issue")])
      ]]]])
