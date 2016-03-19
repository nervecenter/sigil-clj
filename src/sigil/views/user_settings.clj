(ns sigil.views.user-settings
  (:require [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.views.layout :as layout])
  (:use [hiccup.form]))

(declare user-settings-handler user-settings-page)

(defn user-settings-handler [req]
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)
        icon-invalid? (if (= "l" ((:query-params req) "invalid"))
                        true
                        false)]
    (if (some? user)
      (layout/render req
                     user
                     user-org
                     "Sigil - Settings"
                     (user-settings-page user icon-invalid?))
      {:status 302
       :headers {"Location" "/"}})))

(defn user-settings-page [user icon-invalid?]
  [:div.container.settings-container
   [:h2.settings-page-header "Account settings for " (:username user)]
   [:div.row
    [:div.col-lg-6
     [:div.panel.panel-default
      [:div.panel-body
       [:div.form-group
        [:h4 "Change your password"]]]]]
    [:div.col-lg-6
     [:div.panel.panel-default
      [:div.panel-body
       (if icon-invalid?
         [:p.text-success "User icon must be .jpg or .png at most 100 x 100 pixels."])
       [:img.img-rounded.img-responsive.img-relief {:src (:icon_100 user)}]
       [:h4 "User icon: 100 x 100 pixels, .jpg or .png"]
       [:form {:action "/usericon100" :method "post" :enctype "multipart/form-data"} 
        ;[:post "/usericon100"]
        [:div.form-group
         [:div.input-group
          [:div.input-group-btn
           [:span.btn.btn-default.btn-file
            "Browse"
            (file-upload {:id "usericon100"} "usericon100")]]
          (text-field {:class "form-control"  :readonly ""} "txt-field-icon")
          ]]
        [:div.form-group
         (submit-button {:class "btn btn-default disabled form-control"}
                        "Upload new icon")]]]]]]])
