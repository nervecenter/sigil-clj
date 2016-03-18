(ns sigil.actions.comment
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.auth :as auth]
            [sigil.db.votes :as votes]
            [sigil.db.comments :as comments]))

(def not-nil? (complement nil?))

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


