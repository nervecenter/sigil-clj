(ns sigil.views.login.logic
  (:require [sigil.views.login.render :as render]
            [buddy.hashers :as hashers]
            [clojure.java.jdbc :refer [query]]
            [sigil.db.core :refer [db]]
            [clojure.string :refer [join]]))

(defn get-user-by-email-query [email]
  (join " "
        ["SELECT user_id, email, pass_hash"
         "FROM issues"
         (str "WHERE email = '" email "';")]))

(defn get-user-by-email [email]
  (first
   (query db [(get-user-by-email-query email)])))

(defn login-post
  "Handles POST requests to /login. Attempts to handle login and exchange a token with the client, redirecting to the return address."
  [req]
  (let [login-data (:form-params req)
        email (:email login-data)
        password (:password login-data)
        return (:return login-data)
        user (get-user-by-email-query email)]
    ;; Get the user by email
    ;; Compare the hashed pass to the stored hash
    ;; If they match, create a token, pass it in 302 targeted at return
    ;; If not, back to login page with validation message
    {:status 302
     :headers {"Location" return}
     :body ""}
    ))

(defn login-get
  "Handles GET requests to /login. Returns the login page. If a post, attempts to handle login and token passing with the client."
  [req]
  (let [return ((:query-params req) "return")]
    (render/page return)))
