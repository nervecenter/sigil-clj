(ns sigil.views.layout
  (require [sigil.views.partials.navbar :refer [navbar-partial]]
           [sigil.views.partials.footer :as footer]
           [sigil.auth :refer [user-has-role?]]
           [sigil.db.notifications :refer [get-number-notifications-by-user]])
  (use hiccup.core
       hiccup.page
       hiccup.form))

(defn head [title]
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0"}]

   [:link {:rel "shortcut icon"
           :href "/images/favicon.png"}]

   (include-css "/css/jquery-ui-1.9.2.custom.css"
                "/css/bootstrap-flatly.css"
                "/css/site.css")

   [:title title]])

(defn render
  "Renders the default layout with navbar and footer. Expects the request, the current user or nil, the user's org or nil, the title of the page, and the body of the page usually rendered somewhere in the page view."
  [req user user-org title body]

  (html5
   (head title)
   [:body.page
    [:div.wrap
     (navbar-partial req user user-org)
     [:div.container.main-container
      [:div.row
       body]]]
    [:br.clear]
    footer/footer
    (include-js "/js/jquery-1.11.3.js"
                "/js/jquery-ui-1.9.2.custom.min.js"
                "/js/bootstrap.js"
                "/js/input-listeners.js"
                "/js/voting.js"
                "/js/subscriptions.js"
                "/js/search.js"
                "/js/notifications.js"
                "/js/petition.js"
                "/js/issue-form.js"
                "/js/bootstrap-datepicker.js"
                "https://www.google.com/jsapi"
                "/js/graph.js"
                "/js/ganalytics.js")]))
