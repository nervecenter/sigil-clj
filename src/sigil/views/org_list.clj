(ns sigil.views.org-list
  (:require [sigil.auth :refer [user-or-nil]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            [sigil.db.orgs :refer [get-all-orgs get-org-by-user]]
            [sigil.views.layout :as layout])
  (:use [hiccup.core]))

(declare org-list-handler org-list-body)

(defn org-list-handler [req]
  (let [user (user-or-nil req)
        user-org (get-org-by-user user)
        orgs (get-all-orgs)]
    (layout/render req
                   user
                   user-org
                   "Sigil - All Companies"
                   (org-list-body user orgs))))

(defn org-list-body [user orgs]
  (html
   [:div#main-col.col-md-9.col-lg-9
    [:h2 "List of all organizations and agencies"]
    [:div.panel.panel-default
     [:div.panel-body
      [:div.media
       [:div.media-left
        [:img.media-object {:src (:icon_100 (first orgs))}]]
       [:div.media-body
        [:h3 [:a {:href (:org_url (first orgs))} (:org_name (first orgs))]]]]
      (for [o (rest orgs)]
        (html
          [:hr]
          [:div.media
           [:div.media-left
            [:img.media-object {:src (:icon_100 o)}]]
           [:div.media-body
            [:h3 [:a {:href (:org_url o)} (:org_name o)]]]]))]]]
   (sidebar-partial nil user)))
