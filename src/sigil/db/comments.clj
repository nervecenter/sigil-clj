(ns sigil.db.comments
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]
            [sigil.db.users :as users]
            [clj-time.local :as time]
            [clj-time.jdbc]))

;;---------------------------------------------------------------
; Querys

(defn get-comment-by-id
  [id]
  (first (sql/query @db/spec ["SELECT * FROM comments WHERE comment_id = ?" id])))

(defn get-comments-by-issue
  [issue]
  (into [] (sql/query @db/spec ["SELECT * FROM comments WHERE issue_id = ?" (:issue_id issue)])))

(defn get-comments-by-user
  [user]
  (into [] (sql/query @db/spec ["SELECT * FROM comments WHERE user_id = ?" (:user_id user)])))

(defn get-last-user-comment-id
  [user]
  (:comment_id (first (sql/query @db/spec ["SELECT * FROM comments WHERE user_id = ? ORDER BY created_at DESC" (:user_id user)]))))


(defn get-org-comments
  [org]
  (into [] (sql/query @db/spec ["SELECT comments.comment_id, comments.created_at FROM comments INNER JOIN issues ON (comments.issue_id = issues.issue_id AND issues.org_id = ?)" (:org_id org)])))

(defn get-comments-with-commenters-by-issue
  [issue]
  (let [issue-comments (get-comments-by-issue issue)]
    (map #(assoc % :commenter (users/get-user-by-id (:user_id %)))
         issue-comments)))


(defn get-users-by-issue-comments
  [issue]
  (set (into [] (sql/query @db/spec ["SELECT user_id FROM comments WHERE issue_id = ?" (:issue_id issue)]))))
;;----------------------------------------------------------------
; Updates/Inserts/Deletes

(defn comment-voted
  [db-conn [comment_id]]
  (sql/execute! db-conn ["UPDATE comments SET votes = 1 + votes, last_voted = LOCALTIMESTAMP WHERE comment_id = ?" comment_id]))

(defn comment-unvoted
  [db-conn [comment_id]]
  (sql/execute! db-conn ["UPDATE comments SET votes = votes - 1 WHERE comment_id = ?" comment_id]))

(defn create-comment
  [db-conn [new_comment]]
  (sql/insert! db-conn
               :comments
               new_comment))

(defn delete-comment
  ([comment] (delete-comment comment false))
  ([comment perm]
   (if perm
     (sql/delete! @db/spec :comments ["comment_id = ?" (:comment_id comment)])
     (sql/update! @db/spec :comments {:user_id 0
                                     :edited_at (time/local-now)} ["comment_id = ?" (:comment_id comment)]))))

(defn comment_model
  "Defines the comments model table in the db"
  []
  (sql/create-table-ddl
   :comments
   [:comment_id :bigserial "PRIMARY KEY"]
   [:issue_id :bigint "NOT NULL"]
   [:user_id :bigint "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:edited_at :timestamp]
   [:text :text "NOT NULL"]
   [:votes :bigint "NOT NULL" "DEFAULT 1"]
   [:last_voted :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
