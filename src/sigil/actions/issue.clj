(ns sigil.actions.issue
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]
            [sigil.auth :as auth]
            [sigil.db.votes :as votes]))

(def not-nil? (complement nil?))

(defn add-issue-post
  [req]
  (let [new-issue-data (:form-params req)
        user (auth/user-or-nil req)
        issue_org (orgs/get-org-by-id (long (read-string (new-issue-data "org-id"))))
        new-issue (assoc (zipmap [:title :text]
                                 (map #(new-issue-data %) ["title" "text"]))
                         :user_id (:user_id user)
                         :org_id (:org_id issue_org)
                         :tags [(long (read-string (new-issue-data "tag-select")))])]
    (if (= :success (do
                      (db/db-trans [issues/create-issue new-issue])
                      (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                                       :issue_id (:issue_id (issues/get-issue-insert-id new-issue))}])))
      {:status 302
       :headers {"Location" (str (:org_url issue_org) "/" (:issue_id (issues/get-issue-insert-id new-issue)))}}
      ;;else redirect and let them know whats wrong....
      )))


(defn vote-issue
  [req]
  (let [issue (issues/get-issue-by-id (:issue_id (:route-params req)))
        user (auth/user-or-nil req)]
    (do (println issue)
      (if (and (not-nil? user) (not (votes/user-voted-on-issue? user issue)))
        (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                         :issue_id (:issue_id issue)}])))))

(defn unvote-issue
  [req]
  (let [issue (issues/get-issue-by-id (:issue_id (:route-params req)))
        user (auth/user-or-nil req)
        vote (votes/get-user-issue-vote user issue)]
    (do (println vote)
      (if (not-nil? vote)
        (votes/delete-vote vote)))))
