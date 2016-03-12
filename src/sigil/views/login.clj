(ns sigil.views.login
  (:require [sigil.helpers :refer [get-return]]
            [buddy.hashers :refer [check]]
            [sigil.views.layout :as layout]
            [hiccup.page :refer [html5]])
  (:use sigil.auth
        sigil.db.users
        hiccup.form))

(declare redirect-invalid login-get login-post login-page login-body)

(defn redirect-invalid [return]
  {:status 302
   :headers {"Location" (str "login?invalid=t&return=" return)}})

(defn login-get
  "Handles GET requests to /login. Returns the login page. If the request is authenticated (the user is already logged in), redirects back to home."
  [req]
  (if (authenticated? req)
    {:status 302
     :headers {"Location" "/"}}
    (let [return (get-return req)
          invalid? (if (= "t" ((:query-params req) "invalid")) true false)]
      (login-page (:uri req) return invalid?))))

(defn login-post
  "Handles POST requests to /login. Attempts to handle login and exchange a token with the client, redirecting to the return address."
  [req]
  (let [login-data (:form-params req)
        email (login-data "email")
        password (login-data "password")
        return (login-data "return")
        user (get-user-by-email email)]
    (if (some? user)
      (if (check password (:pass_hash user))
        {:status 302
         :headers {"Location" return}
         :body ""
         :cookies {:user {:value (make-user-token user)
                          :max-age 2628000
                          ;;:secure true
                          ;;:http-only true
                          ;;:domain ".sigil.tech"
                          }}}
        (redirect-invalid return))
      (redirect-invalid return))))

(defn login-page
  "Render the login page. Takes in a string representing the domain URI for the view to be returned to after login success, and an optional collection of validation messages to be rendered at the top of the form."
  [uri return invalid?]
  (html5
   (layout/head "Sigil - Login")

   [:body.page
    [:div.wrap
     (layout/navbar uri)
     [:div.container.main-container
      [:div.row
       (login-body return
                   invalid?)]]]]))

(defn login-body [return invalid?]
  [:div.container.maxw-400
   [:h2 "Log in to Sigil"]
   [:div.row
    [:div.col-lg-12
     [:div.panel-panel-default
      [:div.panel-body
       (if invalid?
         [:h3 {:style "color:red;"} "Invalid email or password."]
         nil)
       (form-to
        [:post "/login"]
        (hidden-field {:id "return"} "return" return)

        [:div.form-group
         (label "email" "Email")
         (text-field {:id "email"
                      :placeholder "Email"
                      :class "form-control"} "email")]

        [:div.form-group
         (label "password" "Password")
         (password-field {:id "password"
                          :placeholder "Password"
                          :class "form-control"} "password")]

        [:div.login-page-remember {:style "margin-bottom:10px;"}
         (check-box {:id "remember"} "checkbox" false "true")
         (label "remember" "Remember me?")]

        [:div.btn-group.btn-group-justified
         [:div.btn-group
          (submit-button {:class "btn btn-primary"} "Log In")]
         [:div.btn-group
          [:a.btn.btn-info {:href "register"} "Sign Up"]]])]]]]])
