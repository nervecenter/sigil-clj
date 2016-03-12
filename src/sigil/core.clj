(ns sigil.core
  (:gen-class)
  (:import [org.eclipse.jetty.server.handler StatisticsHandler])
  (:require [ring.adapter.jetty :as jetty]

            [hiccup.core :refer [html]]

            [compojure.core :refer [defroutes GET POST]]
            [compojure.route :as route]
            [compojure.handler :as handler]

            [sigil.views.home :refer [home-handler]]
            [sigil.views.landing :refer [landing-handler]]
            [sigil.views.login :refer [login-get login-post]]
            [sigil.views.usertest :refer [usertest-handler]]
            [sigil.views.org-page :refer [org-page-handler]]
            [sigil.views.org-settings :refer [org-settings-handler]]
            [sigil.views.user-register :refer [user-register-get user-register-post]]

            [sigil.auth :refer [authenticated?]]

            [sigil.actions.logout :refer [logout-handler]]
            [sigil.db.migrations :as mig]

            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]))


(defn server-conf
  [s]
  (let [stats-handler (StatisticsHandler.)
        default-handler (.getHandler s)]
    (.setHandler stats-handler default-handler)
    (.setHandler s stats-handler)
    (.setStopTimeout s 60000)
    (.setStopAtShutdown s true)))

(defroutes sigil-routes
  (GET "/" req (if (authenticated? req)
                 (home-handler req)
                 (landing-handler)))
  (GET "/usertest" req (usertest-handler req))
  (GET "/login" req (login-get req))
  (POST "/login" req (login-post req))
  (GET "/logout" req (logout-handler req))
  (GET "/orgsettings" req (if (authenticated? req)
                            (org-settings-handler req)
                            "404"))
  (GET "/register" req (user-register-get req))
  (POST "/register" req (user-register-post req))
  (GET "/printrequest" req (html [:p {} req]))
  (GET "/printrequest/:x" req (html [:p {} req]))
  (GET "/search/:term" req ())
  ;;(GET "/:org_url" req (org-page-handler req))
  (route/resources "/")
  (route/not-found "404"))

(def app
  (-> (handler/site sigil-routes)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-cookies)
      (wrap-params)))

(defonce server (jetty/run-jetty #'app {:port 3000 :join? false :configurator server-conf}))


(defn -main
  []
  ())
