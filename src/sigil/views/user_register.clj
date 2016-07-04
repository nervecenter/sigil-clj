(ns sigil.views.user-register
  (:require [hiccup.core :refer [html]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.views.partials.navbar :refer [navbar-partial]]
            [sigil.actions.register :as register]
            [ring.util.response :refer [redirect]]
            [sigil.helpers :refer [get-return]]
            [hiccup.page :refer [html5 include-js]])
  (:use hiccup.form))

(declare user-register-get user-register-post user-register-body user-register-page)

(defn user-register-get [req]
  (let [return (get-return req)
        validation ((:query-params req) "v")]
    (user-register-page req
                        return
                        validation)))

(defn user-register-post [req]
  (let [register-data (:form-params req)
        username (register-data "username")
        email (register-data "email")
        password (register-data "password")
        confirm-password (register-data "confirm-password")
        return (register-data "return")]
    (cond
      (not= password confirm-password)
      (redirect (str "/register?v=m&return=" return))

      (< (count username) 5)
      (redirect (str "/register?v=u&return=" return))

      (< (count password) 6)
      (redirect (str "/register?v=p&return=" return))

      (or (not (nil? (sigil.db.users/get-user-by-email email)))
          (not (nil? (sigil.db.users/get-user-by-username username))))
      (redirect (str "/register?v=e&return=" return))
      :else
      (do
        ;; Add the user
        (register/register-user
         {:username username
          :email email
          :pass_hash (buddy.hashers/encrypt password)})
        ;; Give them their token with a redirect to the return
        (redirect "/login?v=r")))))

(defn user-register-page
  [req
   return
   validation]
  (html5
   (layout/head "Sigil - Register")
   [:body.page
    [:div.wrap
     (navbar-partial req nil nil)
     [:div.container.main-container
      [:div.row
       (user-register-body req
                           return
                           validation)]]]]
   (include-js "https://code.jquery.com/jquery-1.11.3.min.js"
               "https://code.jquery.com/ui/1.9.2/jquery-ui.min.js"
               "https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"
               "js/input-listeners.js")))

(defn user-register-body [req return validation]
  [:div.container.maxw-400
   [:h2 "Join Sigil today"]
   [:div.row
    [:div.col-lg-12
     [:div.panel.panel-default
      [:div.panel-body
       (condp = validation
         "m" [:h4 {:style "color:red;"} "Password confirmation does not match."]
         "u" [:h4 {:style "color:red;"} "Username must be at least 5 characters."]
         "p" [:h4 {:style "color:red;"} "Password must be at least 6 characters."]
         "e" [:h4 {:style "color:red;"} "A user with the provided email or username already exists. " [:a {:href "/login"} "Login"]]
         nil)

       (form-to
        [:post "/register"]
        (hidden-field {:id "return"} "return" return)

        [:div.form-group
         (label "email" "Email")
         (text-field {:id "email"
                      :placeholder "Email"
                      :class "form-control register-field"} "email")]

        [:div.form-group
         (label "username" "Username")
         (text-field {:id "username"
                      :placeholder "Username"
                      :class "form-control register-field"} "username")]

        [:div.form-group
         (label "password" "Password")
         (password-field {:id "password"
                          :placeholder "Password"
                          :class "form-control register-field"} "password")]

        [:div.form-group
         (label "confirm-password" "Confirm password")
         (password-field {:id "confirm-password"
                          :placeholder "Confirm password"
                          :class "form-control register-field"} "confirm-password")]

        [:div.checkbox
         (label "policy-accept"
                (html
                 (check-box {:id "policy-accept"}
                            "policy-accept"
                            false
                            "true")
                 "I agree to Sigil's "
                 [:a {:href "/terms" :target "_blank"}
                  "Terms of Use"]
                 ", "
                 [:a {:href "/acceptableuse" :target "_blank"}
                  "Acceptable Use"]
                 ", and "
                 [:a {:href "/privacy" :target "_blank"}
                  "Privacy"]
                 " policies."))]

        [:div.btn-group.btn-group-justified
         [:div.btn-group
          (submit-button {:id "sign-up-button"
                          :class "btn btn-primary disabled"
                          :disabled "disabled"} "Sign Up")]])]]]]])
