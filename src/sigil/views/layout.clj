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

   (include-css "https://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css"
                "https://maxcdn.bootstrapcdn.com/bootswatch/3.3.6/flatly/bootstrap.min.css"
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
    (include-js "https://code.jquery.com/jquery-1.11.3.min.js"
                "https://code.jquery.com/ui/1.9.2/jquery-ui.min.js"
                "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
                "/js/input-listeners.js"
                "/js/voting.js"
                "/js/subscriptions.js"
                "/js/search.js"
                "/js/notifications.js"
                "/js/petition.js"
                "/js/issue-form.js"
                "/js/bootstrap-datepicker.js"
                "/js/graph.js"
                "https://www.google.com/jsapi")]))
