(ns sigil.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]

            [hiccup.core :refer [html]]

            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]

            [sigil.views.landing :refer [landing-handler]]
            [sigil.views.login :refer [login-get login-post]]
            [sigil.views.usertest :refer [usertest-handler]]

            [sigil.actions.logout :refer [logout-handler]]

            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]))

(defroutes sigil-routes
  (GET "/" req (landing-handler))
  (GET "/login" req (login-get req))
  (POST "/login" req (login-post req))
  (GET "/logout" req (logout-handler req))
  (GET "/usertest" req (usertest-handler req))
  (GET "/printrequest" req (html [:p {} req]))
  (route/resources "/")
  (route/not-found "404"))

(def app
  (-> (handler/site sigil-routes)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-cookies)
      (wrap-params)))

(defonce server (jetty/run-jetty #'app {:port 3000 :join? false}))
