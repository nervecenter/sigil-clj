(ns sigil.views.home
  (:require [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]))

(declare home-body home-handler)

(defn home-handler [req]
  ;; Home page expects user
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)]
    (layout/render req
                   user
                   user-org
                   "Sigil"
                   (home-body (:uri req) user nil))))

(defn home-body [uri user issues]
  [:div.col-md-9.col-lg-9
   (if (> (count issues) 0)
     (for [i issues]
       (issue-partial uri i user true))
     [:div.panel.panel-default
      [:div.panel-body
       [:h3 "Welcome, " (:username user) "! This is your" [:b "feed"] "."]
       [:br]
       [:p.empty-home-text "The latest feedback from your subscriptions will appear here."]
       [:br]
       [:p.empty-home-text "Want to find companies to subscribe to? Use the " [:b "search bar"] " up top, or just " [:b [:a {:href "/companies"} "browse all companies on Sigil"]] "."]]])])
