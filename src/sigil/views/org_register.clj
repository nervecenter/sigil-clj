(ns sigil.views.org-register
  (:require [hiccup.core :refer [html]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.actions.db :as db]
            [sigil.helpers :refer [get-return]]
            [hiccup.page :refer [html5]])
  (:use hiccup.form))

(declare org-register-get org-register-post org-register-body org-register-page)

(defn org-register-get [req]
  (let [return (get-return req)
        validations ((:query-params req) "invalid")
        ;;check if valid
]
    (org-register-page req return false false false)))

(defn org-register-page
  [req
   return
   passwords-not-match?
   short-username?
   short-password?]
  (html5
   (layout/head "Sigil - Org Register")
   [:body.page
    [:div.wrap
     (layout/navbar (:uri req))
     [:div.container.main-container
      [:div.row
       (org-register-body  req
                           return
                           ;passwords-not-match?
                           ;short-username?
                           ;short-password?
        )]]]]))

(defn org-register-post [req]
  (let [register-data (:form-params req)
        org-name (register-data "org-name")
        org-url (register-data "org-url")
        website (register-data "website")
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
        (db/register-org-and-admin
         {:org_name org-name
          :org_url org-url
          :website website}
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

(defn org-register-body [req return]
  [:div.container.maxw-1000
   [:div.row
    [:div.col-lg-12
     [:h2 "Create a Sigil page for your company"]
     [:div.panel.panel-default
      [:div.panel-body
       (form-to
        [:post "/orgregister"]
        
        (hidden-field {:id "return"} "return" return)

        [:div.form-group
         (label "org-name" "The name of your company.")
         (text-field {:id "org-name"
                      :placeholder "Company name"
                      :class "form-control"} "org-name")]

        [:div.form-group
         (label "org-url" "The URL handle for your company on Sigil, i.e. http://sigil.tech/<your company>. Must be lowercase with no spaces.")
         (text-field {:id "org-url"
                      :placeholder "Company URL handle"
                      :class "form-control"} "org-url")]

        [:div.form-group
         (label "website" "The URL of your company website.")
         (text-field {:id "website"
                      :placeholder "Company website"
                      :class "form-control"} "website")]

        ;; [:div.form-group
        ;;  (label "website" "The URL of your company website.")
        ;;  (text-field {:id "website"
        ;;               :placeholder "Company website"
        ;;               :class "form-control"} "website")]

        ;; [:div.form-group
        ;;  (label "website" "The URL of your company website.")
        ;;  (text-field {:id "website"
        ;;               :placeholder "Company website"
        ;;               :class "form-control"} "website")]

        [:h4 "Create the Sigil account for your company's first administrator."]
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
          (submit-button {:class "btn btn-primary disabled"}
                         "Create your company page")]])]]]]])
