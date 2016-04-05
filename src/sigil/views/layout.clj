(ns sigil.views.layout
  (require [sigil.views.partials.footer :as footer]
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
           :href "images/favicon.png"}]

   (include-css "/css/jquery-ui-1.9.2.custom.css"
                "/css/bootstrap-flatly.css"
                "/css/site.css")

   [:title title]])

(defn navbar
  ([uri] (navbar uri nil nil))
  ([uri user user-org]
   [:div.navbar.navbar-fixed-top.navbar-default
    [:div.container-fluid
     [:div#navbar-header.navbar-header
      [:button.navbar-toggle.collapsed {:type "button"
                                        :data-toggle "collapse"
                                        :data-target "#collapser"
                                        :aria-expanded "false"}
       [:span.sr-only "Toggle navigation"]
       [:span.icon-bar]
       [:span.icon-bar]
       [:span.icon-bar]]
      [:a.navbar-brand {:href "/" :style "padding: 10px 15px;height:40px;"}
       [:img {:alt "Sigil" :src "/images/symbol-small.png"}]]
      [:div.navbar-brand "Beta"]]
     [:div#collapser.navbar-collapse.collapse
      (form-to
       {:class "navbar-form navbar-left"}
       [:post "/search"]
       [:div.form-group {:style "width:100%;"}
        (text-field {:id "site-search-box"
                     :data-provide "typeahead"
                     :class "form-control typeahead"
                     :placeholder "Search for a company, person, or product"}
                    "search-term")])
      ;; Logged in part
      (if (some? user)
        (html
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href (str "/logout?return=" uri)} "Log Out"]]]
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href "/settings"} (:username user)]]]
         [:ul.nav.navbar-nav.navbar-right.hidden-xs
          [:li {:style "position:relative;"}
           [:img#header-user-icon.img-rounded.img-responsive
            {:src (:icon_100 user)
             :style "height:40px;margin-top:10px;"}]
           [:img#num-notes-back {:src "/images/num-notes-back.png"}]
           [:h5#num-notes
            (get-number-notifications-by-user user)]]]
         (if (some? user-org)
           [:ul.nav.navbar-nav.navbar-right
            [:li
             [:a {:href "/"(:org_url user-org)} (str (:org_name user-org) " Page")]]
            [:li
             [:a {:href "/orgsettings"} (str (:org_name user-org) " Settings")]]]
           nil)
         (if (user-has-role? user :site-admin)
           [:ul.nav.navbar-nav.navbar-right
            [:li
             [:a {:href "/sadmin"}]]]
           nil))
        ;; Not logged in part
        (html
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href (str "/register?return=" uri)} "Sign Up"]]]
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href (str "/login?return=" uri)}]]]))]]]))

(defn render
  "Renders the default layout with navbar and footer. Expects the request, the current user or nil, the user's org or nil, the title of the page, and the body of the page usually rendered somewhere in the page view."
  [req user user-org title body]

  (html5
   (head title)
   [:body.page
    [:div.wrap
     (navbar (:uri req) user user-org)
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
                "/js/issue-form.js"
                "/js/bootstrap-datepicker.js"
                "https://www.google.com/jsapi" "/js/graph.js")]))
