(ns sigil.views.features
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-js include-css]]
            [hiccup.form :refer [form-to text-field]]
            [sigil.views.layout :as layout]
            [sigil.views.partials.navbar :refer [navbar-partial]]
            [sigil.views.partials.footer :refer [footer]]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]
            ))

(declare features-handler features-page head body)

(defn features-handler [req]
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)]
    (features-page user user-org)))

(defn features-page [user user-org]
  (html5
    head
    [:body.page
     [:div.wrap
      (navbar-partial "/features" user user-org)
      body]
     [:br.clear]
     footer

     (include-js "js/jquery-1.11.3.js"
                 "js/jquery-ui-1.9.2.custom.min.js"
                 "js/bootstrap.js"
                 "js/notifications.js"
                 "js/search.js")]))

(def head
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   (include-css "css/jquery-ui-1.9.2.custom.css"
                "css/bootstrap-flatly.css"
                "css/site.css")
   [:link {:rel "shortcut icon" :href "images/favicon.png"}]

   [:style ".twitter-typeahead {width: 100%;}
            .header-link {color:white;}"]
   [:title "Sigil - Features"]])

(def body
  (html
    [:div.features-splash.features-splash-1
     [:div.container {:style "max-width:800px;"}
      [:div.row {:style "margin-bottom:30px;"}
       [:div.col-lg-12.col-centered {:style "max-width:350px;"}
        [:img.img-responsive {:src "images/logo-600-cent.png"}]
        [:h3 {:style "margin-top:0 auto 0;font-size: 19px;"} "A feedback forum for a new age."]]]]]
    [:div.features-splash.features-splash-2
     [:div.container
      [:div.row {:style "margin-bottom:30px;"}
       [:div.col-md-4.col-md-offset-8.col-centered {:style "max-width:350px;"}
        [:h3 "One place to bring the conversation home."]
        [:p "Many governments, together at last. It's never been easier to bring together your constituents and discuss the future."]
        ]]]]
    [:div.features-splash.features-splash-3
     [:div.container
      [:div.row {:style "margin-bottom:30px;"}
       [:div.col-md-4.col-md-offset-8.col-centered {:style "max-width:350px;"}
        [:h3 "Unified opinions, easy parsing."]
        [:p "The internet isn't paper, so why sift through stacks of suggestions? Our vote system makes opinions clear to parse and analyze, with data tools to delve into greater detail."]
        ]]]]
    [:div.features-splash.features-splash-4
     [:div.container
      [:div.row {:style "margin-bottom:30px;"}
       [:div.col-md-4.col-md-offset-8.col-centered {:style "max-width:350px;"}
        [:h3 "So easy, anyone can do it."]
        [:p "A new solution shouldn't require new employees to manage. Anyone can check up on the discussion in the first 15 minutes of their day."]
        ]]]]
    [:div.features-splash.features-splash-5
     [:div.container
      [:div.row {:style "margin-bottom:30px;"}
       [:div.col-md-4.col-md-offset-8.col-centered {:style "max-width:350px;"}
        [:h3 "Zero integration, zero hassle."]
        [:p "A Sigil page takes minutes to set up. And that's it. No hooks or APIs or feature creep."]
        ]]]]
    [:div.container
      [:div.row {:style "margin-bottom:30px;"}
       [:div.col-md-4.col-md-offset-8.col-centered {:style "max-width:350px;"}
        [:h3 "Zero integration, zero hassle."]
        [:p "A Sigil page takes minutes to set up. And that's it. No hooks or APIs or feature creep."]
        ]]]))