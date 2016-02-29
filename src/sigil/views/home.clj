(ns sigil.views.home
  (:require [sigil.partials.issue :refer [issue-partial]]))

(defn home-handler [req]
  ;; Home page expects user
  ;;
  (layout/render "Sigil"
                 (home-body user)))

(defn home-body []
  [:div.col-md-9.col-lg-9
   (if (> (count issues) 0)
     (for [i issues]
       (issue-partial (:uri req) i user true))
     [:div.panel.panel-default
      [:div.panel-body
       [:h3 "Welcome, " (:username user) "! This is your" [:b "feed"] "."]
       [:br]
       [:p.empty-home-text "The latest feedback from your subscriptions will appear here."]
       [:br]
       [:p.empty-home-text "Want to find companies to subscribe to? Use the " [:b "search bar"] " up top, or just " [:b [:a {:href "/companies"} "browse all companies on Sigil"]] "."]]])])
