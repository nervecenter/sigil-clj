(ns sigil.views.login.logic
  (:require [sigil.views.login.render :as render]
            [sigil.helpers :refer [get-return]]
            [buddy.hashers :refer [check]])
  (:use sigil.auth
        sigil.db.users))

(defn redirect-invalid [return]
  {:status 302
   :headers {"Location" (str "login?invalid=t&return=" return)}})

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

(defn login-get
  "Handles GET requests to /login. Returns the login page. If a post, attempts to handle login and token passing with the client."
  [req]
  (let [return (get-return req)
        invalid? (if (= "t" ((:query-params req) "invalid")) true false)]
    (render/page return invalid?)))
