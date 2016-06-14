(ns sigil.views.home
  (:require [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.db.issues :as issues]
            [hiccup.core :refer [html]]))

(declare home-body home-handler)

(defn home-handler [req]
  ;; Home page expects user
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)
        issue-boxes (issues/get-twelve-org-issue-boxes)]
    (layout/render req
                   user
                   user-org
                   "Sigil"
                   (home-body (:uri req) user issue-boxes))))

(defn home-body [uri user issue-boxes]
  (html
   [:div.col-md-9.col-lg-9
    [:div.panel.panel-default
     [:div.panel-body {:style "text-align:center;"}
      [:h3 {:style "margin-top:10px;"} "Welcome, " (:username user) "! "
       [:img.img-rounded {:src (:icon_100 user)
                          :style "height:40px;"}]]
      [:p.empty-home-text "You can find some of the latest feedback across Sigil below."]
      [:p.empty-home-text "You can use the " [:b "search bar"] " up top, or just " [:b [:a {:href "/companies"} "check out all organizations on Sigil"]] "."]]]
    (for [box issue-boxes]
      (html
         [:h4 [:a {:href (str "/" (:org_url (:org box)))}
          [:img {:src (:icon_30 (:org box))
                 :style "margin-right:5px;"}]
          (:org_name (:org box))]]
       (for [issue (:issues box)]
         (issue-partial "/" issue user))
        [:br]))]
   (sidebar-partial nil)))
