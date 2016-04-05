(ns sigil.views.org-register
  (:require [hiccup.core :refer [html]]
            [sigil.auth :refer [user-or-nil]]
            [sigil.views.layout :as layout]
            [sigil.actions.register :as register]
            [sigil.helpers :refer [get-return]]
            [hiccup.page :refer [html5]])
  (:use hiccup.form))

(declare org-register-get org-register-post org-register-body org-register-page)

(defn org-register-get [req]
  (let [return (get-return req)
        validations ((:query-params req) "invalid")
        passwords-not-match? (if (= validations "m") true false)
        short-username? (if (= validations "u") true false)
        short-password? (if (= validations "p") true false)
        user-exists? (if (= validations "e") true false)
        org-exists? (if (= validations "o") true false)]
    (org-register-page req return passwords-not-match? short-username? short-password? user-exists? org-exists?)))

(defn org-register-page
  [req
   return
   passwords-not-match?
   short-username?
   short-password?
   user-exists?
   org-exists?]
  (html5
   (layout/head "Sigil - Org Register")
   [:body.page
    [:div.wrap
     (layout/navbar (:uri req))
     [:div.container.main-container
      [:div.row
       (org-register-body  req
                           return
                           passwords-not-match?
                           short-username?
                           short-password?
                           user-exists?
                           org-exists?
        )]]]]))

(defn org-register-post [req]
  (let [register-data (:form-params req)
        org-name (register-data "org-name")
        ;org-url (register-data "org-url")
        ;website (register-data "website")
        username (register-data "username")
        email (register-data "email")
        password (register-data "password")
        confirm-password (register-data "confirm-password")
        org (zipmap [:org_name :org_url :website :address :city :state :zip_code :phone-num :hours]
                    (map #(register-data %) ["org-name" "org-url" "website" "address" "city" "state" "zip-code" "phone-number" "hours"]))
        return (register-data "return")]
    (cond
      (not= password confirm-password)
      {:status 302
       :headers {"Location" (str "orgregister?invalid=m&return=" return)}}
      (< (count username) 5)
      {:status 302
       :headers {"Location" (str "oegregister?invalid=u&return=" return)}}
      (< (count password) 6)
      {:status 302
       :headers {"Location" (str "orgregister?invalid=p&return=" return)}}
      (or (not (nil? (sigil.db.users/get-user-by-email email)))
          (not (nil? (sigil.db.users/get-user-by-username username))))
      {:status 302
       :headers {"Location" (str "orgregister?invalid=e&return=" return)}}
      (not (nil? (sigil.db.orgs/get-org-by-name org-name)))
      {:status 302
       :headers {"Location" (str "orgregister?invalid=o&return=" return)}}
      :else
      (if (= :success (register/register-org-and-admin
                       org
                       {:username username
                        :email email
                        :pass_hash (buddy.hashers/encrypt password)}))
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
                            }}})
        {:status 400
         :headers {"Location" ((:headers req) "referer")}}))))

(defn org-register-body [req return passwords-not-match? short-username? short-password? user-exists? org-exists?]
  [:div.container.maxw-1000
   [:div.row
    [:div.col-lg-12
     [:h2 "Create a Sigil page for your company"]
     [:div.panel.panel-default
      [:div.panel-body
       (if passwords-not-match?
         [:h3 {:style "color:red;"} "Password confirmation does not match."] nil)
       (if short-username?
         [:h3 {:style "color:red;"} "Username must be at least 5 characters."] nil)
       (if short-password?
         [:h3 {:style "color:red;"} "Password must be at least 6 characters."] nil)
       (if user-exists?
         [:h3 {:style "color:red;"} "A user with the provided email or username already exists. " [:a {:href "/login"} "Login"]])
       (if org-exists?
         [:h3 {:style "color:red;"} "An Organization with that name already exists. Please login or ..." ])
       (form-to
        [:post "/orgregister"]
        
        (hidden-field {:id "return"} "return" return)

        [:div.form-group
         (label "org-name" "The name of your company or Resturant.")
         (text-field {:id "org-name"
                      :placeholder "Company/Resturant name"
                      :class "form-control"} "org-name")]

        [:div.form-group
         (label "org-url" "The URL handle for your company/resturant on Sigil, i.e. http://sigil.tech/<your company>. Must be lowercase with no spaces.")
         (text-field {:id "org-url"
                      :placeholder "Company URL handle"
                      :class "form-control"} "org-url")]

        [:div.form-group
         (label "website" "The web address of your company/resturant website.")
         (text-field {:id "website"
                      :placeholder "Company/Resturant website"
                      :class "form-control"} "website")]

        [:div.form-group
         (label "address" "Address of your Resturant")
         (text-field {:id "address"
                      :placeholder "Resturant Address"
                      :class "form-control"} "address")]

        [:div.form-group
         (label "City" "City of your Resturant")
         (text-field {:id "city"
                      :placeholder "City"
                      :class "form-control"} "city")]

        [:div.form-group
         (label "State" "State your company resides in.")
         (text-field {:id "state"
                      :placeholder "State"
                      :class "form-control"} "state")]
        
        [:div.form-group
         (label "Zip" "Zip code your company resides in.")
         (text-field {:id "zip-code"
                      :placeholder "Zip-Code"
                      :class "form-control"} "zip-code")]

        [:div.form-group
         (label "Phone Number" "Resturant's Phone number")
         (text-field {:id "phone-number"
                      :placeholder "Phone #"
                      :class "form-control"} "phone-number")]

        [:div.form-group
         (label "Hours" "Hours Of Operation")
         (text-field {:id "hours"
                      :placeholder "Hours"
                      :class "form-control"} "hours")]
        
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
