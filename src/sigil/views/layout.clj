(ns sigil.views.layout
  (require [sigil.views.partial.navbar :as navbar]
           [sigil.views.partial.footer :as footer])
  (use hiccup.core
       hiccup.page
       hiccup.form))

(defn head [title]
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0"}]

   [:link {:rel "shortcut icon"
           :href "images/favicon.png"}]

   [:title title]])

(def scripts
  (include-js "js/jquery-1.11.3.js"
              "js/jquery-ui-1.9.2.custom.min.js"
              "js/bootstrap.js"
              "js/input-listeners.js"
              "js/voting.js"
              "js/subscriptions.js"
              "js/search.js"
              "js/notifications.js")

  (include-css "css/jquery-ui-1.9.2.custom.css"
               "css/bootstrap-flatly.css"
               "css/site.css"))

(defn render
  "Render the default layout with the given page title and body. The body will be placed within the main container, navbar above and footer below. The sidebar is rendered by the calling page."
  [title body]
  (html5

   (head title)

   [:body.page
    [:div.wrap
     (navbar/partial)
     [:div.container.main-container
      [:div.row
       (body)]]]

    [:br.clear]

    (footer/partial)

    (scripts)]))