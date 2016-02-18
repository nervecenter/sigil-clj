(ns sigil.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]

            [hiccup.core :refer [html]]

            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]

            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.core.keys :as keys]

            [sigil.views.landing.logic :as landing]
            [sigil.views.login.logic :as login]

            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]))

(def pubkey (keys/public-key "resources/private/pubkey.pem"))
(def privkey (keys/private-key "resources/private/privkey.pem"))

(def auth-backend (jwe-backend {:secret privkey
                                :options {:alg :rsa}}))

(defroutes sigil-routes
  (GET "/" req (landing/landing-handler))
  (GET "/login" req (login/login-get req))
  (POST "/login" req (login/login-post req))
  (GET "/printcookie" req (html [:p {} req]))
  (route/resources "/")
  (route/not-found "404"))

(def app
  (-> (handler/site sigil-routes)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-cookies)
      (wrap-params)
      (wrap-authentication auth-backend)))

(defonce server (jetty/run-jetty #'app {:port 3000 :join? false}))
