(ns sigil.actions.issue
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]))

(defn add-issue-post
  [req]
  (let [new-issue-data (:form-params req)
        return (new-issue-data "return")
        new-issue (zipmap [:org_id :user_id :title :text]
                          (map #(new-issue-data %) ["org-id" "user-id" "title" "text"]))]
    (if (= :success (db/db-trans [issues/create-issue new-issue]))
      {:status 302
       :headers {"Location" (str "return=" return "/" (issues/get-issue-insert-id new-issue))}}
      ;;else redirect and let them know whats wrong....
      )))
