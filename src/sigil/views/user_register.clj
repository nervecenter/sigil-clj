(ns sigil.views.user-register
  (:require [hiccup.core :refer [html]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.db.users :refer [get-user-by-email]])
  (:use hiccup.form))

(declare user-register-get user-register-post user-register-body)

(defn user-register-get [req]
  (let [return (get-return req)
        validations ((:query-params req) "invalid")
        passwords-not-match? (if (contains? validations "m") true false)
        short-username? (if (contains? validations "u") true false)
        short-password? (if (contains? validations "p") true false)]
    (user-register-page return passwords-not-match? short-username? short-password?)))

(defn user-register-post [req]
  (let [register-data (:form-params req)
        username (register-data "username")
        email (register-data "email")
        password (register-data "password")
        confirm-password (register-data "confirm-password")
        return (login-data "return")]
    (cond
      (not= password confirm-password)
      {:status 302
       :headers {"Location" (str "register?invalid=m&return=" return)}}
      (< 6 (count username))
      {:status 302
       :headers {"Location" (str "register?invalid=u&return=" return)}}
      (< 6 (count password))
      {:status 302
       :headers {"Location" (str "register?invalid=p&return=" return)}}
      :else
      (do
        ;; Add the user
        (sigil.db.users/register-user
         {:username username
          :email email
          :pass_hash (buddy.hashers/encrypt password)})
        ;; Give them their token with a redirect to the return
        (let [user (get-user-by-email email)]
          {:status 302
           :headers {"Location" return}
           :body ""
           :cookies {:user {:value (make-user-token user)
                            :max-age 2628000
                            ;;:secure true
                            ;;:http-only true
                            ;;:domain ".sigil.tech"
                            }}})))))

(defn user-register-page [return passwords-not-match? short-username? short-password?]
  (layout/render "Sigil - Register"
                 (user-register-body return passwords-not-match? short-username? short-password?)))

(defn user-register-body [return passwords-not-match? short-username? short-password?]
  [:div.container.maxw-400
   [:h2 "Join Sigil today"]
   [:div.row
    [:div.col-lg-12
     [:div.panel.panel-default
      [:div.panel-body

       (if passwords-not-match?
         [:h3 "Password confirmation does not match."] nil)
       (if short-username?
         [:h3 "Username must be at least 6 characters."] nil)
       (if short-password?
         [:h3 "Password must be at least 6 characters."] nil)

       (form-to
        [:post "/register"]
        (hidden-field {:id "return"} "return" return)

        [:div.form-group
         (label "email" "Email")
         (text-field {:id "email"
                      :placeholder "Email"
                      :class "form-control"} "email")]

        [:div.form-group
         (label "username" "Username")
         (text-field {:id "username"
                      :placeholder "Username"
                      :class "form-control"} "username")]

        [:div.form-group
         (label "password" "Password")
         (password-field {:id "password"
                          :placeholder "Password"
                          :class "form-control"} "password")]

        [:div.form-group
         (label "confirm-password" "Confirm password")
         (password-field {:id "confirm-password"
                          :placeholder "Confirm password"
                          :class "form-control"} "confirm-password")]

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
          (submit-button {:class "btn btn-primary disabled"} "Sign Up")]])]]]]])
