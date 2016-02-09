(ns sigil.core
  (:gen-class)
  (:require [ring.adapter.jetty :as jetty]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [sigil.views.landing.logic :as landing])
  (:use ring.middleware.resource
        ring.middleware.content-type
        ring.middleware.not-modified))

(defroutes sigil-routes
  (GET "/" [] landing/page)
  (route/resources "/")
  (route/not-found "404"))

(def app
  (-> (handler/site sigil-routes)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)))

(defn start-server [] (jetty/run-jetty app {:port 3000}))
