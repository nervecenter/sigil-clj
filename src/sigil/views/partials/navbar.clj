(ns sigil.views.partials.navbar
  (:require [sigil.auth :refer [authenticated? identity]]
            [sigil.helpers :refer [user-has-role?]])
  (:use hiccup.form))

(declare navbar-partial navbar)

(defn navbar-partial [req user]
  (navbar req user))

(defn navbar [req user user-org]
  [:div.navbar.navbar-fixed-top.navbar-default
   [:div.container-fluid
    [:div.#navbar-header.navbar-header
     [:button.navbar-toggle.collapsed {:type "button"
                                       :data-toggle "collapse"
                                       :data-target "#collapser"
                                       :aria-expanded "false"}
      [:span.sr-only "Toggle navigation"]
      [:span.icon-bar]
      [:span.icon-bar]
      [:span.icon-bar]]
     [:a.navbar-brand {:href "/" :style "padding: 10px 15px;height:40px;"}
      [:img {:alt "Sigil" :src "images/symbol-small.png"}]]
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
     (if (some? user)
       ;; Logged in part
       '([:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href (str "/logout?return=" (:uri req))} "Log Out"]]]
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href "/settings"} (:username user)]]]
         [:ul.nav.navbar-nav.navbar-right.hidden-xs
          [:li {:style "position:relative;"}
           [:img#header-user-icon.img-rounded.img-responsive
            {:src (:icon_100 user)
             :style "height:40px;margin-top:10px;"}]
           [:img#num-notes-back {:src "images/num-notes-back.png"}]
           [:h5#num-notes]]])
       ;; Not logged in part
       [:p "Not authenticated"])]]])
