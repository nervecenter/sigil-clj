(ns sigil.views.partials.issue
  (:require [sigil.db.votes :refer [user-voted-on-issue?]]
            [sigil.db.officialresponses :refer [get-latest-official-response-by-issue]]
            [sigil.helpers :refer [get-return]]))

(declare issue-partial issue-panel issue-without-panel)

(defn issue-partial [uri issue user in-panel?]
  ;; We need: The issue, whether the user is authed, and whether they voted
  (let [authenticated? (some? user)
        user-voted? (if authenticated?
                      (user-voted-on-issue? (:user_id user) (:issue_id issue))
                      false)]
    (if in-panel?
      ;; We need: a response
      (issue-panel uri
                   issue
                   authenticated?
                   user-voted?
                   (get-latest-official-response-by-issue (:issue_id issue)))
      (issue-without-panel uri issue authenticated? user-voted?))))

(defn issue-panel [uri issue authenticated? user-voted? response]
  (if (some? response)
    [:div.panel.panel-info.issue-panel-partial
     (issue-without-panel uri issue authenticated? user-voted?)
     [:div.panel-footer
      [:b "Response: "]
      (if (> (count (:text response)) 100)
        [:span (str (subs (:text response) 0 100) "...")]
        [:span {:text response}])]]
    [:div.panel.panel-default.issue-panel-partial
     (issue-without-panel uri issue authenticated? user-voted?)]))

(defn issue-without-panel [uri issue authenticated? user-voted?]
  [:div.panel-body
   [:div.media
    [:div.media-object.pull-left.votebutton-box
     (if authenticated?
       (if user-voted?
         [:img.unvoteup {:data-issueid (:issue_id issue)
                         :src "images/voted.png"}]
         [:img.voteup {:data-issueid (:issue_id issue)
                       :src "images/notvoted.png"}])
       [:a {:href (str "login?return=" uri)}
        [:img.votelogin {:src "images/notvoted.png"}]])]]])
