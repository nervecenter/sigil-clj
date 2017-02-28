(ns sigil.views.internal-error
  (:require [hiccup.core :refer [html]]
            [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil]]
            [sigil.db.core :as db]
            [sigil.db.orgs :refer [get-org-by-user]]))

(declare internal-error-handler internal-error-body)

(defn internal-error-handler [req message]
  ;; Maybe we want to log the req that led to the 404?
  (let [user (user-or-nil req)
        user-org (get-org-by-user user)]
    (do (db/create-error {:error_message message
                          :additional_info (str req)
                          :user_assoc (:user_id user)})
      (layout/render req
                     user
                     user-org
                     "Sigil - 500 Internal Server Error"
                     (internal-error-body message)))))

(defn internal-error-body [message]
  (html
   [:div.col-md-4.col-md-offset-4
    [:h2 "500 - Internal Server Error"]
    [:h4 "Here's what went wrong:"]
    [:br]
    [:h4 [:b message]]
    [:br]
    [:h4
     "Don't worry, we've logged it and are looking into it. Maybe you'd like to head back "
     [:a {:href "/"} "home"]
     "?"]]))

(defn plain-text-error [message]
  {:status 500
   :headers {"Content-Type" "text/plain"}
   :body message})
