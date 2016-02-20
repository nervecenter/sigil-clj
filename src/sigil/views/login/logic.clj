(ns sigil.views.login.logic
  (:require [sigil.views.login.render :as render]
            [buddy.hashers :refer [check]])
  (:use sigil.auth
        sigil.db.users))

(defn login-post
  "Handles POST requests to /login. Attempts to handle login and exchange a token with the client, redirecting to the return address."
  [req]
  (let [login-data (:form-params req)
        email (login-data "email")
        password (login-data "password")
        return (login-data "return")
        user (get-user-by-email email)]
    (if (some? user)
      (do
        (println "Found user: " (:display_name user))
        (if (check password (:pass_hash user))
          (do
            (println "Matched password " password " to user " (:display_name user))
            (let [token (make-user-token user)]
              {:status 302
               :headers {"Location" return}
               :body ""
               :cookies {:user {:value token
                                :max-age 2628000
                                ;;:secure true
                                ;;:http-only true
                                ;;:domain ".sigil.tech"
                                }}}))
          (do
            (println "Password " password " did not match for user " (:display_name user))
            {:status 302
             :headers {"Location" "login"}
             :body "Password did not match."})))
      (do
        (println "User with email " email " does not exist.")
        {:status 302
         :headers {"Location" "login"}
         :body "User does not exist."}))))

(defn login-get
  "Handles GET requests to /login. Returns the login page. If a post, attempts to handle login and token passing with the client."
  [req]
  (let [return ((:query-params req) "return")]
    (render/page return)))
