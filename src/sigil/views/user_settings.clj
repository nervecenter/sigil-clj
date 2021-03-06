(ns sigil.views.user-settings
  (:require [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.db.issues :as issues]
            [sigil.views.partials.issue :refer [issue-partial]]
            [ring.util.response :refer [redirect]])
  (:use [hiccup.form]))

(declare user-settings-handler user-settings-page)

(defn user-settings-handler [req]
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)
        user-issues (issues/get-issues-by-user user)
        validation ((:query-params req) "v")]
    (if (some? user)
      (layout/render req
                     user
                     user-org
                     "Sigil - Settings"
                     (user-settings-page user user-issues validation))
      (redirect "/"))))

(defn user-settings-page [user user-issues validation]
  [:div.container.settings-container
   [:h2.settings-page-header "Account settings for " (:username user)]
   (condp = validation
     "p" [:h3.validation {:style "color:green;"} "Password updated."]
     "i" [:h3.validation {:style "color:green;"} "Icon Updated"]
     "l" [:h3.validation {:style "color:red;"} "User icon must be .jpg or .png at most 100 x 100 pixels."]
     "d" [:h3.validation {:style "color:red;"} "The server had a problem updating your icon. We'll look into it."]
     "m" [:h3.validation {:style "color:red;"} "New password fields did not match."]
     "b" [:h3.validation {:style "color:red;"} "Old password incorrect."]
     "c" [:h3.validation {:style "color:red;"} "Passwords need to be at least 6 characters."]
     "z" [:h3.validation {:style "color:green;"} "Zip code updated."]
     "k" [:h3.validation {:style "color:red;"} "You must wait 1 month before updating your zip code."]
     nil)
   [:div.row
    [:div.col-lg-6
     [:h3 "Issues you've posted:"]
     (if (empty user-issues)
       [:h4 "You haven't posted anything yet. Find an organization and start giving feedback!"]
       (for [i user-issues]
         (issue-partial "/settings" i user)))]
    [:div.col-lg-6
     [:div.panel.panel-default
      [:div.panel-body
       [:img.img-rounded.img-responsive.img-relief
        {:src (:icon_100 user)}]
       [:h4 "User icon: 100 x 100 pixels, .jpg or .png"]
       [:form {:action "/usericon100"
               :method "post"
               :enctype "multipart/form-data"}
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
         (submit-button {:class "btn btn-default disabled form-control"
                         :disabled "disabled"}
                        "Upload new icon")]]]]
     [:div.panel.panel-default
      [:div.panel-body
       [:form {:method "post" :action "/userpasschange"}
        [:div.form-group
         (label "password" "Old Password")
         (password-field {:id "old-password"
                          :placeholder "Old Password"
                          :class "form-control pass-field"} "old-password")]
        [:div.form-group
         (label "password" "New Password")
         (password-field {:id "new-password"
                          :placeholder "Password"
                          :class "form-control pass-field"} "new-password")]

        [:div.form-group
         (label "confirm-new-password" "Confirm New password")
         (password-field {:id "confirm-new-password"
                          :placeholder "Confirm New Password"
                          :class "form-control pass-field"} "confirm-new-password")]
        [:div.form-group
          (submit-button {:id "submit-new-password"
                          :class "btn btn-default disabled form-control"
                          :disabled "disabled"} "Change Password")]]]]
     [:div.panel.panel-default
      [:div.panel-body
       [:h4 "You may change your zip code once every month."]
       [:form {:action "/userzip"
               :method "post"}
        [:div.form-group
         (label "zip" "Zip Code")
         (text-field {:id "zip"
                      :placeholder "Zip Code"
                      :class "form-control"} "zip" (:zip_code user))]
        [:div.form-group
         (submit-button {:id "zip-submit"
                         :class "btn btn-default disabled form-control"
                         :disabled "disabled"} "Change your zip code")]]]]]]])
