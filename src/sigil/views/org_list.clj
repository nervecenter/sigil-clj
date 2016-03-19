(ns sigil.views.org-list
  (:require [sigil.auth :refer [user-or-nil]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            [sigil.db.orgs :as orgs])
  (:use [hiccup.core]))

(declare org-list-handler org-list-body)

(defn org-list-handler [req]
  (let [user (user-or-nil req)
        orgs (orgs/get-all-orgs)]
    (org-list-body user orgs)))

(defn org-list-body [user orgs]
  (html
   [:div#main-col.col-md-9.col-lg-9
    [:h2 "List of all companies"]
    [:div.panel.panel-default
     [:div.panel-body
      (for [o orgs]
        [:div.media
         [:div.media-left
          [:img.media-object {:src (:icon_100 o)}]]
         [:div.media-body
          [:h3 [:a {:href (:org_url o)} (:org_name o)]]]
         [:hr]])]]]
   (sidebar-partial nil user)))
