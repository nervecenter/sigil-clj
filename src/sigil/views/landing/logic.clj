(ns sigil.views.landing.logic
  (:use hiccup.core
        hiccup.page))

(def page
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]

    [:link {:rel "shortcut icon" :href "images/favicon.png"}]

    (include-js "js/jquery-1.11.3.js"
                "js/jquery-ui-1.9.2.custom.min.js")

    (include-css "css/jquery-ui-1.9.2.custom.css"
                 "css/bootstrap-flatly.css"
                 "css/site.css")



    [:title "Sigil"]]
   [:body.page
    [:div.page-header.page-header-landing
     [:img {:src "sample.png"}]]]))
