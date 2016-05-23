(ns sigil.actions.petitions
  (:require [sigil.auth :as auth]
            [sigil.db.petitions :refer [create-petition]]
            [sigil.db.core :as db]))

(defn post-petition
  [req]
  (let [user (auth/user-or-nil req)
        user-org (auth/user-org-or-nil user)
        new-petition-data (:form-params req)
        new-petition {:user_id (:user_id user)
                      :issue_id (read-string (new-petition-data "issueid"))
                      :org_id (read-string (new-petition-data "orgid"))
                      :body (new-petition-data "body")}]
    (if (not= (:org_id user-org) 
              (:org_id user) 
              (read-string (new-petition-data "orgid")))
      {:status 500
       :body "Unauthorized petition posting."}
      (if (= :success
           (db/db-trans [create-petition new-petition]))
        {:status 200}
        {:status 500
         :body "Error uploading petition."}))))
