(ns sigil.actions.issue
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.auth :as auth]
            [sigil.db.votes :as votes]))

(def not-nil? (complement nil?))

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


(defn vote-issue
  [req]
  (let [issue (issues/get-issue-by-id (:issue_id (:route-params req)))
        user (auth/user-or-nil req)]
    (if (and (not-nil? user) (not (votes/user-voted-on-issue? user issue)))
      (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                 :issue_id (:issue_id issue)}]))))

(defn unvote-issue
  [req]
  (let [issue (issues/get-issue-by-id (:route-params req))
        user (auth/user-or-nil req)
        vote (votes/get-user-issue-vote user issue)]
    (if (not-nil? vote)
      (votes/delete-vote vote))))
