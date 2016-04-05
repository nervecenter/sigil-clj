(ns sigil.views.partials.issue
  (:require [sigil.db.votes :refer [user-voted-on-issue?]]
            [sigil.db.officialresponses :refer [get-latest-official-response-by-issue]]
            [sigil.db.orgs :refer [get-org-by-issue]]
            [sigil.db.users :refer [get-user-by-issue]]
            [sigil.helpers :refer [get-return]]
            [hiccup.core :refer [html]]))

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
        issue-user (get-user-by-issue issue)]
    (if in-panel?
      ;; We need: a response
      (issue-panel uri
                   issue
                   issue-org
                   issue-user
                   authenticated?
                   user-voted?
                   (get-latest-official-response-by-issue issue))
      (issue-without-panel uri
                           issue
                           issue-org
                           issue-user
                           authenticated?
                           user-voted?)))))

(defn issue-panel [uri issue issue-org issue-user authenticated? user-voted? response]
  [(if (and (:responded issue) (some? response))
     :div.panel.panel-info.issue-panel-partial
     :div.panel.panel-default.issue-panel-partial)
   (issue-without-panel uri
                        issue
                        issue-org
                        issue-user
                        authenticated?
                        user-voted?)
   (if (and (:responded issue) (some? response))
     [:div.panel-footer
      [:b "Response: "]
      (if (> (count (:text response)) 100)
        [:span (str (subs (:text response) 0 100) "...")]
        [:span {:text response}])])])

(defn issue-without-panel [uri issue issue-org issue-user authenticated? user-voted?]
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
      [:a {:href (str "/" (:org_url issue-org))} (:org_name issue-org)]]
     [:p.pull-right
      (str "Posted at " (clj-time.coerce/to-local-date (:created_at issue)) " by ")
      [:img {:src (:icon_30 issue-user)}]
      (:username issue-user)]]]])
