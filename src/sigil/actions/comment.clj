(ns sigil.actions.comment
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.auth :as auth]
            [sigil.db.votes :as votes]
            [sigil.db.comments :as comments]))

(def not-nil? (complement nil?))

;;TODO -- rework the db-trans part
(defn post-comment
  [req]
  (let [new-comment-data (:form-params req)
        user (auth/user-or-nil req)
        issue (issues/get-issue-by-id (read-string (new-comment-data "issue-id")))
        new-comment {:user_id (:user_id user)
                     :issue_id (:issue_id issue)
                     :text (new-comment-data "add-comment-box")}]
    (if (= :success (do
                      (db/db-trans [comments/create-comment new-comment])
                      (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                                       :issue_id (:issue_id issue)
                                                       :org_id (:org_id issue)
                                                       :comment_id (comments/get-last-user-comment-id user)}])))
      {:status 302
       :headers {"Location" (str ((:headers req) "referer"))}})))

(defn delete-comment-post
  [req]
  (let [new-comment-data (:form-params req)
        user (auth/user-or-nil req)
        comment-to-delete (comments/get-comment-by-id (read-string (new-comment-data "comment-id")))
        vote-to-delete (votes/get-user-comment-vote user comment-to-delete)]
    (if (= :success (do
                      (comments/delete-comment comment-to-delete true)
                      (db/db-trans [votes/delete-vote vote-to-delete])
                      ))
      {:status 302
       :headers {"Location" ((:headers req) "referer")}}
      ;;else redirect and let them know whats wrong....
      )))

(defn vote-comment
  [req]
  (let [issue (issues/get-issue-by-id (:issue_id (:route-params req)))
        comment (comments/get-comment-by-id (:comment_id (:route-params req)))
        user (auth/user-or-nil req)]
    (if (and (not-nil? user) (not (votes/user-voted-on-comment? user comment)))
      (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                 :issue_id (:issue_id issue)
                                 :comment_id (:comment_id comment)}]))))

(defn unvote-comment
  [req]
  (let [issue (issues/get-issue-by-id (:issue_id (:route-params req)))
        comment (comments/get-comment-by-id (:comment_id (:route-params req)))
        user (auth/user-or-nil req)
        vote (votes/get-user-comment-vote user comment)]
    (if (not-nil? vote)
      (votes/delete-vote vote))))


