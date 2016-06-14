(ns sigil.views.partials.sidebar
  (:require [sigil.db.tags :refer [get-tags-by-org]]
            [hiccup.core :refer [html]]))

(declare sidebar-partial sidebar org-box link-box ad ad-box sidebar-footer)

(defn sidebar-partial [org]
  (if (some? org)
    (sidebar org
             (get-tags-by-org org))
    (sidebar nil
             nil)))

(defn sidebar [org org-tags]
  [:div.col-md-3.col-lg-3.col-sm-12.col-xs-12
   (if (some? org)
     (org-box org org-tags))
   link-box
   (ad-box)
   sidebar-footer])

(defn org-box [org org-tags]
  [:div.panel.panel-default
   [:div.panel-body
    [:h3 {:style "margin: 10px auto 18px;font-size:22px;"}
     [:a {:href (:org_url org)}
      [:img {:src (:icon_30 org)
             :style "width:25px;height:25px;margin-bottom:5px;"}]
      " " (:org_name org)]]
    [:a {:href (str "http://" (:website org))
         :target "_blank"}
     [:img.sub-org-icon {:src "/images/website.png"}]
     (:website org)]
    [:hr.tiny-hr]
    (let [address (str (:address org) ", " (:city org) ", " (:state org) " " (:zip_code org))]
      [:a {:href (str "http://maps.google.com?q=" (str (:org_name org) ", " address))
           :target "_blank"}
       [:img.sub-org-icon {:src "/images/map.png"}]
       address])
    [:hr.tiny-hr]
    [:span
     [:img.sub-org-icon {:src "/images/telephone.png"}]
     (:phone org)]
    ;[:hr.tiny-hr]
    ;[:h4 "Tags"]
    ;[:div.form-group
    ; [:select#tag-select.form-control
    ;  [:option.tag-option-all {:value 0} "All"]
    ;  (for [tag org-tags]
    ;    [:option.tag-option {:value (:tag_id tag)} (:tag_name tag)])]]
    ]])

(def link-box
  (html
   [:div.panel.panel-default
    [:div.panel-body
     [:a {:href "/features"} [:b "Features"]]
     [:hr.sidebar-divider]
     [:a {:href "/orgs"} "Browse organizations"]
     ]]))

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
  ;(condp = (rand-int 2)
    ;0 (ad "mailto:contact@sigil.tech" "/images/advertise.png" false)
    ;1 (ad "/sigil" "/images/feedback-ad.png" true))
  (ad "/sigil" "/images/feedback-ad.png" true))

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
