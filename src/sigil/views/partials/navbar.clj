(ns sigil.views.partials.navbar
  (:require [sigil.auth :refer [user-has-role?]]
            [sigil.db.notifications :refer [get-number-notifications-by-user]]
            [hiccup.core :refer [html]])
  (:use hiccup.form))

(declare navbar-partial navbar)

(defn navbar-partial [req user user-org]
  (navbar (:uri req) user user-org))

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
       [:get "/search"]
       [:div.form-group {:style "width:100%;"}
        (text-field {:id "site-search-box"
                     :data-provide "typeahead"
                     :class "form-control typeahead"
                     :placeholder "Search for an organization"}
                    "search")])
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
             [:a {:href (str "/" (:org_url user-org))} (str (:org_name user-org) " Page")]]
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
           [:a {:href (str "/login?return=" uri)} "Log In"]]]))]]]))
