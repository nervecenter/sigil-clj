(ns sigil.views.partials.navbar
  (:require [sigil.auth :refer [authenticated?
                                user-identity
                                is-user-site-admin?
                                user-has-role?]]
            [hiccup.core :refer [html]])
  (:use hiccup.form))

(declare navbar-partial navbar)

(defn navbar-partial [req user user-org]
  ;; For navbar, we need:
  ;; the request, for return URI
  ;; the user, for user controls
  ;; the user's org, for link to org settings if they're org admin
  (navbar req user user-org))

(def navbar-header
  (html
   [:div.navbar-header
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
    [:div.navbar-brand "Beta"]]))

(defn navbar
  ([req]
   (navbar req nil nil))
  ([req user user-org]
   [:div.navbar.navbar-fixed-top.navbar-default
    [:div.container-fluid
     navbar-header
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
           [:a {:href (str "/logout?return=" (:uri req))} "Log Out"]]]
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href "/settings"} (:username user)]]]
         [:ul.nav.navbar-nav.navbar-right.hidden-xs
          [:li {:style "position:relative;"}
           [:img#header-user-icon.img-rounded.img-responsive
            {:src (:icon_100 user)
             :style {:height "40px"
                     :margin-top "10px"}}]
           [:img#num-notes-back {:src "/images/num-notes-back.png"}]
           [:h5#num-notes]]]
         (if (some? user-org)
           [:ul.nav.navbar-nav.navbar-right
            [:li
             [:a {:href (:org_url user-org)} (str (:org_name user-org) " Page")]]
            [:li
             [:a {:href "/orgsettings"} (str (:org_name user-org) " Settings")]]]
           nil)
         (if (is-user-site-admin? user)
           [:ul.nav.navbar-nav.navbar-right
            [:li
             [:a {:href "/sadmin"}]]]
           nil))
        ;; Not logged in part
        (html
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href (str "/register?return=" (:uri req))} "Sign Up"]]]
         [:ul.nav.navbar-nav.navbar-right
          [:li
           [:a {:href (str "/login?return=" (:uri req))}]]]))]]]))
