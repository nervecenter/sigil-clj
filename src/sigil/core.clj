(ns sigil.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]

            [buddy.auth.backends.token :refer [jwe-backend]]
            [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.core.keys :as keys]

            [sigil.views.landing.logic :as landing]
            [sigil.views.login.logic :as login])
  (:use ring.middleware.resource
        ring.middleware.content-type
        ring.middleware.not-modified))

(def pubkey (keys/public-key "private/sigil_rsa.pub"))
(def privkey (keys/private-key "private/sigil_rsa"))

(def auth-backend (jwe-backend {:secret privkey
                           :options {:alg :rsa}}))

(defn login-handler [request]
  (let [data (:form-params request)
        user ()]))

(defroutes sigil-routes
  (GET "/" req (landing/landing-handler req))
  (GET "/login" req (login/login-get req))
  (POST "/login" req (login/login-post req))
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
