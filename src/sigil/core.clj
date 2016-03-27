(ns sigil.core
  ;;(:import [org.eclipse.jetty.server.handler StatisticsHandler])
  (:require [org.httpkit.server :as http]

            [hiccup.core :refer [html]]

            [compojure.core :refer [defroutes context GET POST ANY]]
            [compojure.route :as route]
            [compojure.handler :as handler]

            [sigil.views.home :refer [home-handler]]
            [sigil.views.legal :refer [legal-handler]]
            [sigil.views.landing :refer [landing-handler]]
            [sigil.views.login :refer [login-get login-post]]
            [sigil.views.usertest :refer [usertest-handler]]
            [sigil.views.org-page :refer [org-page-handler]]
            [sigil.views.org-list :refer [org-list-handler]]
            [sigil.views.issue-page :refer [issue-page-handler]]
            [sigil.views.org-settings :refer [org-settings-handler]]
            [sigil.views.user-settings :refer [user-settings-handler]]
            [sigil.views.user-register :refer [user-register-get user-register-post]]
            [sigil.views.org-register :refer [org-register-get org-register-post]]
            [sigil.views.not-found :refer [not-found-handler]]

            [sigil.auth :refer [authenticated?]]

            [sigil.actions.logout :refer [logout-handler]]
            [sigil.actions.issue :as issue-actions]
            [sigil.actions.search :as search-actions]
            [sigil.actions.comment :as comment-actions]
            [sigil.actions.notifications :as note-actions]
            [sigil.actions.image :as image-actions]
            [sigil.actions.tag :as tag-actions]
            [sigil.db.migrations :as mig]

            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.middleware.cookies :refer [wrap-cookies]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]])
  (:gen-class :main true))

(defroutes sigil-routes
  (GET "/" req (if (authenticated? req)
                 (home-handler req)
                 (landing-handler)))
  (GET "/usertest" req (usertest-handler req))
  (GET "/legal" req (legal-handler req))
  (GET "/login" req (login-get req))
  (POST "/login" req (login-post req))
  (GET "/logout" req (logout-handler req))
  (GET "/companies" req (org-list-handler req))
  (GET "/orgsettings" req (if (authenticated? req)
                            (org-settings-handler req)
                            (not-found-handler req)))
  (POST "/orgbanner" req (if (authenticated? req)
                            (image-actions/update-org-banner req)
                            {:status 403}))
  (POST "/orgicon100" req (if (authenticated? req)
                            (image-actions/update-org-icon-100 req)
                            {:status 403}))
  (POST "/orgicon30" req (if (authenticated? req)
                            (image-actions/update-org-icon-30 req)
                            {:status 403}))
  (POST "/orgaddtag" req (if (authenticated? req)
                            (tag-actions/add-org-tag req)
                            {:status 403}))
  (GET "/settings" req (user-settings-handler req))
  (POST "/usericon100" req (if (authenticated? req)
                             (image-actions/update-user-icon req)
                             {:status 403}))

  (GET "/register" req (user-register-get req))
  (POST "/postissue" req (issue-actions/add-issue-post req))
  (GET "/usernotes" req (note-actions/get-user-notifications req))
  (GET "/countusernotes" req (note-actions/get-number-user-notifications req))
  (POST "/register" req (user-register-post req))
  (GET "/orgregister" req (org-register-get req))
  (POST "/orgregister" req (org-register-post req))
  (POST "/submitcomment" req (comment-actions/post-comment req))
  (GET "/printrequest" req (html [:p {} req]))
  ;(GET "/printrequest/:x" req (html [:p {} req]))
  (GET "/404" req (not-found-handler req))
  (context "/search" req
    (GET "/:term" req (search-actions/auto-complete-search req))
    (GET "/:org-id/:term" req (search-actions/search-org-issues req)))
  (context "/:org_url{[a-z0-9]{4,}}" req
    (GET "/" req (org-page-handler req))
    (GET "/:issue_id{[0-9]+}" req (issue-page-handler req))
    ;(POST "/unvoteup/:issue_id{[0-9]+}" req (issue-actions/unvote-issue req))
    ;(POST "/voteup/:issue_id{[0-9]+}" req (issue-actions/vote-issue req))
    )
  (POST "/voteup" req (issue-actions/vote-issue req))
  (POST "/unvoteup" req (issue-actions/unvote-issue req))
  ;(GET "/vote/:issue_id/:comment_id" req (comment-actions/vote-comment req))
  ;(GET "/unvote/:issue_id/:comment_id" req (comment-actions/unvote-comment req))
  (ANY "*" req (not-found-handler req))
  (route/resources "/")
  (route/not-found "404"))

(def app
  (-> (handler/site sigil-routes)
      (wrap-resource "public")
      (wrap-content-type)
      (wrap-not-modified)
      (wrap-cookies)
      (wrap-params)
      (wrap-multipart-params)
      ))

(def server-options {:port 8080})

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (when (nil? @server)
    (reset! server (http/run-server #'app server-options))))

(defn restart-server []
  (when-not (nil? @server)
    (do (stop-server) (start-server))))

(defn -main
  [& args]
  (start-server))
