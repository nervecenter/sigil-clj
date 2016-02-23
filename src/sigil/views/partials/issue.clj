(ns sigil.views.partials.issue
  (:require [sigil.auth :refer [authenticated? identity]]
            [sigil.db.votes :refer [user-voted-on-issue?]]))

(declare issue-partial issue-panel issue-without-panel)

(defn issue-partial [req issue in-panel?]
  (if in-panel?
    (issue-panel req issue)
    (issue-without-panel req issue)))

(defn issue-panel [req issue]
  (if {:responded issue}
    [:div.panel.panel-info.issue-panel-partial
     (issue-without-panel req issue)
     [:div.panel-footer
      [:b "Response: "]
      (let [first-response (first {:responses issue})]
        (if (> (count first-response) 100)
          [:span (str (subs first-response 0 100) "...")]
          [:span first-response]))]]
    [:div.panel.panel-default.issue-panel-partial
     (issue-without-panel req issue)]))

(defn issue-without-panel [req issue]
  [:div.panel-body
   [:div.media
    [:div.media-object.pull-left.votebutton-box
     (if (authenticated? req)
       (if (user-voted-on-issue? (identity req) issue)
         [:img.unvoteup {;;Get the most current source on this
                         }]))]]])
