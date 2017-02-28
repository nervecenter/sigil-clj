(ns sigil.views.legal
  (:require [sigil.auth :refer [user-or-nil]]
            [sigil.db.orgs :refer [get-org-by-user]]
            [sigil.views.layout :as layout]
            [hiccup.core :refer [html]]))

(declare legal-handler legal-body)

(defn legal-handler [req]
  (let [user (user-or-nil req)
        user-org (get-org-by-user user)]
    (layout/render req
                   user
                   user-org
                   "Sigil - Legal"
                   legal-body)))

(def legal-body
  (html
   [:div.container.maxw-1000
    [:div.row
     [:div.col-lg-12
      [:h3 {:style "margin-top:0px;"} "Legal policies"]
      [:div.panel.panel-default
       [:div.panel-body
        [:div.form-group
         [:h4 [:a {:href "/terms"} "Terms of Use"]]
         [:hr]
         [:h4 [:a {:href "/acceptableuse"} "Acceptable Use Policy"]]
         [:hr]
         [:h4 [:a {:href "/privacy"} "Privacy Policy"]]]]]
      [:h3 {:style "margin-top:0px;"} "Sigil would like to give attribution to:"]
      [:div.panel.panel-default
       [:div.panel-body
        [:p
         [:a {:href "http://ionicons.com/"} "ionicons"]
         " - Used under the "
         [:a {:href "https://en.wikipedia.org/wiki/MIT_License"} "MIT License"] "."]
        [:p
         "Photos obtained from "
         [:a {:href "https://stocksnap.io/"} "StockSnap.io"]
         " - Used under the "
         [:a {:href "https://creativecommons.org/publicdomain/zero/1.0/"} "Creative Commons CC0 License"]
         "."]]]]]]))
