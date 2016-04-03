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

(defn delete-issue-post
  [req]
  (let [new-issue-data (:form-params req)
        user (auth/user-or-nil req)
        issue_org (orgs/get-org-by-id (long (read-string (new-issue-data "org-id"))))
        issue-to-delete (issues/get-issue-by-id (read-string (new-issue-data "issue-id")))
        vote-to-delete (votes/get-user-issue-vote user issue-to-delete)]
    (if (= :success (do
                      (issues/delete-issue issue-to-delete true)
                      (db/db-trans [votes/delete-vote vote-to-delete])))
      {:status 302
       :headers {"Location" ((:headers req) "referer")}}
      ;;else redirect and let them know whats wrong....
      )))


(defn vote-issue
  [req]
  (let [issue (issues/get-issue-by-id (read-string (:issue_id (:params req))))
        user (auth/user-or-nil req)]
    (if (not (votes/user-voted-on-issue? user issue))
      (do
        (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                         :issue_id (:issue_id issue)
                                         :org_id (:org_id issue)}]
                     [issues/issue-voted issue])
        {:status 200})
      {:status 403})))

(defn unvote-issue
  [req]
  (let [issue (issues/get-issue-by-id (read-string (:issue_id (:params req))))
        user (auth/user-or-nil req)
        vote (votes/get-user-issue-vote user issue)]
    (if (not-nil? vote)
      (do
        (db/db-trans
         [votes/delete-vote vote]
         [issues/issue-unvoted issue])
        {:status 200})
      {:status 403})))
