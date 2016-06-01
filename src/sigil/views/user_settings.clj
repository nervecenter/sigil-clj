(ns sigil.views.user-settings
  (:require [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.db.issues :as issues]
            [sigil.views.partials.issue :refer [issue-partial]])
  (:use [hiccup.form]))

(declare user-settings-handler user-settings-page)

(defn user-settings-handler [req]
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)
        user-issues (issues/get-issues-by-user user)
        icon-invalid? (if (= "l" ((:query-params req) "invalid"))
                        true
                        false)
        pass-invalid? ((:query-params req) "invalid")
        successful? ((:query-params req) "success")]
    (if (some? user)
      (layout/render req
                     user
                     user-org
                     "Sigil - Settings"
                     (user-settings-page user user-issues icon-invalid? pass-invalid? successful?))
      {:status 302
       :headers {"Location" "/"}})))

(defn user-settings-page [user user-issues icon-invalid? pass-invalid? successful?]
  [:div.container.settings-container
   [:h2.settings-page-header "Account settings for " (:username user)]
   [:h3 {:style "color:green;"}
    (cond
      (= successful? "p") "Password Updated"
      (= successful? "i") "Icon Updated")]
   [:div.row
    [:div.col-lg-6
     [:h3 "Issues you've posted:"]
     (for [i user-issues]
       (issue-partial "/settings" i user))]
    [:div.col-lg-6
     [:div.panel.panel-default
      [:div.panel-body
       (if icon-invalid?
         [:p.text-success "User icon must be .jpg or .png at most 100 x 100 pixels."])
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
         (submit-button {:class "btn btn-default disabled form-control"}
                        "Upload new icon")]]]]
     [:div.panel.panel-default
      [:div.panel-body
       [:h3 {:style "color:red;"} (cond
                                    (= "m" pass-invalid?)  "New Password Fields did not match."
                                    (= "b" pass-invalid?)  "Old Password Incorrect"
                                    (= "c" pass-invalid?)  "Passwords need to be atleast 6 characters")]
       [:form {:method "post" :action "/userpasschange"}
        [:div.form-group
         (label "password" "Old Password")
         (password-field {:id "old-password"
                          :placeholder "Old Password"
                          :class "form-control"} "old-password")]
        [:div.form-group
         (label "password" "New Password")
         (password-field {:id "new-password"
                          :placeholder "Password"
                          :class "form-control"} "new-password")]

        [:div.form-group
         (label "confirm-new-password" "Confirm New password")
         (password-field {:id "confirm-new-password"
                          :placeholder "Confirm New Password"
                          :class "form-control"} "confirm-new-password")]
        [:div.btn-group.btn-group-justified
         [:div.btn-group
          (submit-button {:class "btn btn-primary disabled"} "Change Password")]]]]]]]])
