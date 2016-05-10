(ns sigil.views.home
  (:require [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.db.issues :as issues]
            [hiccup.core :refer [html]]))

(declare home-body home-handler)

(defn home-handler [req]
  ;; Home page expects user
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)
        user-issues (issues/get-issues-by-user user)
        issue-boxes (issues/get-twelve-org-issue-boxes)]
    (layout/render req
                   user
                   user-org
                   "Sigil"
                   (home-body (:uri req) user user-issues issue-boxes))))

(defn home-body [uri user user-issues issue-boxes]
  (html
   [:div.col-md-9.col-lg-9
    [:div.panel.panel-default
     [:div.panel-body
      [:h3 "Welcome, " (:username user) "!"]
      [:br]
      [:p.empty-home-text "Here's the latest feedback on Sigil."]
      [:br]
      [:p.empty-home-text "Want to find public offices? Use the " [:b "search bar"] " up top, or just " [:b [:a {:href "/companies"} "browse all gov't offices on Sigil"]] "."]]]
    [:div.panel.panel-info
     [:div.panel-body
      [:h4 {:style "margin-top:10px;"} "Issues you've posted:"]]]
    (if (empty? user-issues)
      [:p {:style "margin-bottom:50px;"}
       "You haven't posted any feedback yet. Search for an office and help change things!"]
      (for [i user-issues]
        (issue-partial uri i user true)))
    (for [box issue-boxes]
      (html
       [:div.panel.panel-default
        [:div.panel-heading
         [:a {:href (str "/" (:org_url (:org box)))}
          [:img {:src (:icon_30 (:org box))
                 :style "margin-right:5px;"}]
          (:org_name (:org box))]]]
       (for [issue (:issues box)]
         (issue-partial "/" issue user true))))]
   (sidebar-partial nil user)))
