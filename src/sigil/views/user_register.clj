(ns sigil.views.user-register
  (:require [hiccup.core :refer [html]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.actions.db :as db]
            [sigil.helpers :refer [get-return]]
            [hiccup.page :refer [html5]])
  (:use hiccup.form))

(declare user-register-get user-register-post user-register-body user-register-page)

(defn user-register-get [req]
  (let [return (get-return req)
        validations ((:query-params req) "invalid")
        passwords-not-match? (if (= validations "m") true false)
        short-username? (if (= validations "u") true false)
        short-password? (if (= validations "p") true false)]
    (user-register-page req
                        return
                        passwords-not-match?
                        short-username?
                        short-password?)))

(defn user-register-post [req]
  (let [register-data (:form-params req)
        username (register-data "username")
        email (register-data "email")
        password (register-data "password")
        confirm-password (register-data "confirm-password")
        return (register-data "return")]
    (cond
      (not= password confirm-password)
      {:status 302
       :headers {"Location" (str "register?invalid=m&return=" return)}}
      (< (count username) 5)
      {:status 302
       :headers {"Location" (str "register?invalid=u&return=" return)}}
      (< (count password) 6)
      {:status 302
       :headers {"Location" (str "register?invalid=p&return=" return)}}
      :else
      (do
        ;; Add the user
        (db/register-user
         {:username username
          :email email
          :pass_hash (buddy.hashers/encrypt password)})
        ;; Give them their token with a redirect to the return
        (let [user (sigil.db.users/get-user-by-email email)]
          {:status 302
           :headers {"Location" return}
           :body ""
           :cookies {:user {:value (sigil.auth/make-user-token user)
                            :max-age 2628000
                            ;;:secure true
                            ;;:http-only true
                            ;;:domain ".sigil.tech"
                            }}})))))

(defn user-register-page
  [req
   return
   passwords-not-match?
   short-username?
   short-password?]
  (html5
   (layout/head "Sigil - Register")
   [:body.page
    [:div.wrap
     (layout/navbar (:uri req))
     [:div.container.main-container
      [:div.row
       (user-register-body req
                           return
                           passwords-not-match?
                           short-username?
                           short-password?)]]]]))

(defn user-register-body [req return passwords-not-match? short-username? short-password?]
  [:div.container.maxw-400
   [:h2 "Join Sigil today"]
   [:div.row
    [:div.col-lg-12
     [:div.panel.panel-default
      [:div.panel-body

       (if passwords-not-match?
         [:h3 "Password confirmation does not match."] nil)
       (if short-username?
         [:h3 "Username must be at least 5 characters."] nil)
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
