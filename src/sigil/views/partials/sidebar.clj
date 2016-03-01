(ns sigil.views.partials.sidebar
  (:require [sigil.db.tags :refer [get-tags-by-org]]
            [sigil.db.users :refer [get-user-subscriptions]]
            [hiccup.core :refer [html]]))

(defn sidebar-partial
  ([user]
   )
  ([user org]
   (let [org-tags (get-tags-by-org (:org_id org))
         user-subs (get-user-subscriptions (:user_id user))]
     
     ))
  ;; Get the org's tags
  ;; Get the user's subscriptions
  )

(defn org-box [org org-tags]
  [:div.panel.panel-default
   [:div.panel-body
    [:h3 {:style "margin: 10px auto -10px;"}
     [:a {:href (:org_url org)}
      [:img {:src (:icon_20 org) :style "width:25px;height:25px;"}]
      (:org_name org)]]
    [:br]
    [:a {:href (:website org)}
     [:img.sub-org-icon {:src "images/website.png"}]]
    [:br]
    [:hr.sidebar-divider]
    [:span "Data button might go here."]
    [:hr.sidebar-divider]
    [:h4 {:style "margin-bottom:10px"} "Tags:"]
    (for [t org-tags]
      (html
       [:img.sub-org-icon {:src (:icon_20 t)}]
       (:tag_name t)
       [:br]))]])

(def link-box
  [:div.panel.panel-default
   [:div.panel-body
    [:a {:href "/companies"} "Browse companies on Sigil"]
    [:hr.sidebar-divider]
    [:a {:href "/features"} [:b "Sigil for your business"]]]])

;;(defn subscription-box [])

(defn ad [url img new-tab?]
  [:a (if new-tab? {:href url :target "_blank"} {:href url})
   [:img.img-responsive.img-rounded {:src img
                                     :style "margin-bottom:21px;"}]])

(defn ad-box []
  (condp = (rand-int 2)
    0 (ad "mailto:contact@sigil.tech" "images/advertise.png" false)
    1 (ad "/sigil" "images/feedback-ad.png" true)))

(def sidebar-footer
  [:div.panel.panel-default
   [:div.panel-body {:style "font-size:12px;"}
    "© 2016 Sigil Technologies Inc."
    [:br]
    [:a {:href "/404"} "About"]
    "|"
    [:a {:href "/legal"} "Legal"]
    "|"
    [:a {:href "mailto:contact@sigil.tech"} "Advertise"]
    "|"
    [:a {:href "#"} "Twitter"]
    "|"
    [:a {:href "https://www.facebook.com/Sigil-Technologies-Inc-1617420208509874/"
         :target "_blank"} "Facebook"]
    "|"
    [:a {:href "mailto:contact@sigil.tech"} "Contact"]]])
