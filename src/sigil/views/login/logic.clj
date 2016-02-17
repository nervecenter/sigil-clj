(ns sigil.views.login.logic
  (:require [sigil.views.login.render :as render]))

(defn login-post
  "Handles POST requests to /login. Attempts to handle login and exchange a token with the client, redirecting to ."
  [req])

(defn login-get
  "Handles GET requests to /login. Returns the login page. If a post, attempts to handle login and token passing with the client."
  [req]
  (let [return ((:query-params req) "return")]
    (render/page return))))
