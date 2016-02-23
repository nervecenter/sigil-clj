(ns sigil.views.partials.navbar
  (:require [sigil.auth :refer [authenticated? identity]])
  (:use hiccup.form))

(defn navbar [req]
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
     (if (authenticated? req)
       ;; Logged in part
       ;; Not logged in part
       )]]])
