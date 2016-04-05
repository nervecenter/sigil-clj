(ns sigil.views.partials.sidebar
  (:require [sigil.db.tags :refer [get-tags-by-org]]
            [sigil.db.users :refer [get-user-favorites]]
            [hiccup.core :refer [html]]))

(declare sidebar-partial sidebar org-box link-box ad ad-box sidebar-footer)

(defn sidebar-partial [org user]
  (cond
    (and (some? org) (some? user))
    (let [org-tags (get-tags-by-org org)
;;        user-favorites (get-favorites-by-user-id (:user_id user))
          ]
      (sidebar org
               org-tags
               ;;user-favorites
               nil))
    (and (some? org) (nil? user))
    (let [org-tags (get-tags-by-org org)]
      (sidebar org org-tags nil))
    (and (nil? org) (some? user))
;;    (let [user-favorites (get-favorites-by-user-id (:user_id user))]
    (sidebar nil
             nil
             ;;user-favorites
             nil)
;;      )
    :else
    (sidebar nil nil nil)))

(defn sidebar [org org-tags user-favorites]
  [:div.col-md-3.col-lg-3.col-sm-12.col-xs-12
   (if (some? org)
     (org-box org org-tags))
   link-box
;;   (if (not-empty user-favorites)
;;     (favorites-box user-favorites))
   (ad-box)
   sidebar-footer])

(defn org-box [org org-tags]
  [:div.panel.panel-default
   [:div.panel-body
    [:h3 {:style "margin: 10px auto 18px;"}
     [:a {:href (:org_url org)}
      [:img {:src (:icon_30 org)
             :style "width:25px;height:25px;"}]
      (:org_name org)]]
    [:a {:href (str "http://" (:website org))}
     [:img.sub-org-icon {:src "/images/website.png"}]
     (:website org)]
    [:hr.tiny-hr]
    [:a {:href (str "http://maps.google.com?q=" "Seattle, Washington")
         :target "_blank"}
     [:img.sub-org-icon {:src "/images/map.png"}]
     "Address"]
    [:hr.tiny-hr]
    [:span
     [:img.sub-org-icon {:src "/images/telephone.png"}]
     "555-555-5555"]
    ;; [:br]
    ;; [:hr.sidebar-divider]
    ;; [:span "Data button might go here."]
    ;; [:hr.sidebar-divider]
    ;; [:h4 {:style "margin-bottom:10px"} "Tags:"]
    ;; (for [t org-tags]
    ;;   (html
    ;;    [:img.sub-org-icon {:src (str "/" (:icon_20 t))}]
    ;;    (:tag_name t)
    ;;    [:br]))
    ]])

(def link-box
  (html
   [:div.panel.panel-default
    [:div.panel-body
     [:a {:href "/companies"} "Browse restaurants on Sigil"]
     [:hr.sidebar-divider]
     [:a {:href "/features"} [:b "Sigil for your restaurant"]]]]))

;;(defn favorites-box [user-favorites]
;;  [:div.panel.panel-default
;;   [:div.panel-heading "Your Favorites"]
;;   [:div.panel-body
;;    (for [f user-favorites]
;;     [:a.sub {:href (:org_url (:org f))}
;;       [:img.sub-org-icon {:src (:icon_30 (:org f))}]
;;      (:org_name (:org f))])]])

(defn ad [url img new-tab?]
  [:a (if new-tab? {:href url :target "_blank"} {:href url})
   [:img.img-responsive.img-rounded {:src img
                                     :style "margin-bottom:21px;"}]])

(defn ad-box []
  (condp = (rand-int 2)
    0 (ad "mailto:contact@sigil.tech" "/images/advertise.png" false)
    1 (ad "/sigil" "/images/feedback-ad.png" true)))

(def sidebar-footer
  (html
   [:div.panel.panel-default
    [:div.panel-body {:style "font-size:12px;"}
     "© 2016 Sigil Technologies Inc."
     [:br]
     [:a {:href "/404"} "About"]
     " | "
     [:a {:href "/legal"} "Legal"]
     " | "
     [:a {:href "mailto:contact@sigil.tech"} "Advertise"]
     " | "
     [:a {:href "#"} "Twitter"]
     " | "
     [:a {:href "https://www.facebook.com/Sigil-Technologies-Inc-1617420208509874/"
          :target "_blank"} "Facebook"]
     " | "
     [:a {:href "mailto:contact@sigil.tech"} "Contact"]]]))
