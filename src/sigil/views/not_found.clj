(ns sigil.views.not-found
  (:require [hiccup.core :refer [html]]
            [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil]]
            [sigil.db.core :as db]
            [sigil.db.orgs :refer [get-org-by-user]]))

(declare not-found-handler not-found-body)

(defn not-found-handler [req]
  ;; Maybe we want to log the req that led to the 404?
  (let [user (user-or-nil req)
        user-org (get-org-by-user user)]
    (do (db/create-error {:error_message "Path not found 404."
                          :additional_info (str req)
                          :user_assoc (:user_id user)})
      (layout/render req
                     user
                     user-org
                     "Sigil - 404 Not Found"
                     not-found-body))))

(def not-found-body
  (html
   [:div.col-md-4.col-md-offset-4
    [:h2 "404 - Not found :("]
    [:h3
     "Maybe you'd like to head back "
     [:a {:href "/"} "home"]
     "?"]]))
