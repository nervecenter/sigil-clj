(ns sigil.views.landing
  (:require [sigil.views.partials.footer :as footer]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.form :refer [form-to]]
            [sigil.db.issues :refer [get-landing-issues-with-posters]]
            [sigil.views.partials.issue :as part-issue]))

(declare landing-handler issue-section landing-page head landing-navbar splash)

(def not-nil? (complement nil?))

(defn landing-handler []
  (let [issue-cols (get-landing-issues-with-posters)]
    (landing-page (first issue-cols) (second issue-cols) (nth issue-cols 2 []))))

(defn landing-page [col-1-issues col-2-issues col-3-issues]
  (html5
   head
   [:body.page
    landing-navbar
    splash
    (issue-section col-1-issues col-2-issues col-3-issues)
    footer/footer

    (include-js "js/jquery-1.11.3.js"
                "js/jquery-ui-1.9.2.custom.min.js"
                "js/bootstrap.js"
                "js/typeahead.js"
                "js/voting.js"
                "js/subscriptions.js"
                "js/search.js")]))

(defn issue-section [icol1 icol2 icol3]
  "Takes 3 lists of issues for display on the landing page."
  [:div.container.landing-container
   [:div.row
    [:div#left-col.col-lg-4
     [:div.panel.panel-default
      [:div.panel-body
       (for [i (flatten icol1)]
         (part-issue/issue-partial "/" (:issue i) (:poster i) true))]]]
    [:div#middle-col.col-lg-4
     [:div.panel.panel-default
      [:div.panel-body
       (for [i (flatten icol2)]
         (part-issue/issue-partial "/" (:issue i) (:poster i) true))]]]
    [:div#right-col.col-lg-4
     [:div.panel.panel-default
      [:div.panel-body
       (for [i (flatten icol3)]
         (part-issue/issue-partial "/" (:issue i) (:poster i) true))]]]]])

(def head
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
   (include-css "css/jquery-ui-1.9.2.custom.css"
                "css/bootstrap-flatly.css"
                "css/site.css")
   [:link {:rel "shortcut icon" :href "images/favicon.png"}]
   [:title "Sigil"]])

(def landing-navbar
  [:div.navbar.navbar-transparent.navbar-static-top.landing-navbar
   {:style "margin-bottom:-60px;"}
   [:div.container-fluid
    [:div#bs-example-navbar-collapse-1
     [:ul.nav.navbar-nav.navbar-right
      [:li.pull-right
       [:a.header-link {:href "register"} "Sign Up"]]
      [:li.pull-right
       [:a.header-link {:href "login?return=/"} "Log In"]]]]]])

(def splash
  [:div.splash.splash-landing
   [:div.container {:style "max-width:800px;"}
    [:div.row {:style "margin-bottom:30px;"}
     [:div.col-lg-12.col-centered {:style "max-width:350px;"}
      [:img.img-responsive {:src "images/logo-600-beta.png"}]
      [:h3 {:style "margin-top:0 auto 0;"} "A focal point for feedback."]]]
    [:div.row
     [:div.col-lg-12.col-centered.email-div
      (form-to {:role "search" :style "width:100%;"} [:put "/search"]
               [:div.form-group {:style "width:100%;"}
                [:input#site-search-box.form-control
                 {:type "text" :autocomplete "off"
                  :placeholder "Search for a company, person, or product"
                  :style "width:100%;"}]])]]
    [:div.row.small-links
     [:a {:href "features"} "See how Sigil can revamp feedback for your company"]
     " | "
     [:a {:href "register"} "Start giving your own feedback"]
     " | "
     [:a {:href "companies"} "See all the companies on Sigil"]]]])
