(ns sigil.core
  (:gen-class)
  (:import [org.eclipse.jetty.server.handler StatisticsHandler])
  (:require [ring.adapter.jetty :as jetty]

            [hiccup.core :refer [html]]

            [compojure.core :refer [defroutes GET POST ANY]]
            [compojure.route :as route]
            [compojure.handler :as handler]

            [sigil.views.home :refer [home-handler]]
            [sigil.views.legal :refer [legal-handler]]
            [sigil.views.landing :refer [landing-handler]]
            [sigil.views.login :refer [login-get login-post]]
            [sigil.views.usertest :refer [usertest-handler]]
            [sigil.views.org-page :refer [org-page-handler]]
            [sigil.views.issue-page :refer [issue-page-handler]]
            [sigil.views.org-settings :refer [org-settings-handler]]
            [sigil.views.user-register :refer [user-register-get user-register-post]]
            [sigil.views.org-register :refer [org-register-get org-register-post]]
            [sigil.views.not-found :refer [not-found-handler]]

            [sigil.auth :refer [authenticated?]]

            [sigil.actions.logout :refer [logout-handler]]
            [sigil.actions.db :as db-actions]
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
  (GET "/legal" req (legal-handler req))
  (GET "/login" req (login-get req))
  (POST "/login" req (login-post req))
  (GET "/logout" req (logout-handler req))
  (GET "/orgsettings" req (if (authenticated? req)
                            (org-settings-handler req)
                            "404"))
  (GET "/register" req (user-register-get req))
  (POST "/newissue" req (db-actions/add-issue-post req))
  (GET "/usernotes" req (db-actions/get-user-notifications req))
  (GET "/countusernotes" req (db-actions/get-number-user-notifications req))
  (POST "/register" req (user-register-post req))
  (GET "/orgregister" req (org-register-get req))
  (POST "/orgregister" req (org-register-post req))
  (GET "/printrequest" req (html [:p {} req]))
  (GET "/printrequest/:x" req (html [:p {} req]))
  (GET "/404" req (not-found-handler req))
  (GET "/search/:term" req ())
  (GET "/:org_url" req (org-page-handler req))
  ;(GET "/:org_url/:issue_id" req (issue-page-handler req))
  (ANY "*" req (not-found-handler req))
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
