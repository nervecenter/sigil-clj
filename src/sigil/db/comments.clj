(ns sigil.db.comments
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))



(defn get-comment-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM comments WHERE comment_id = ?" id])))

(defn get-comments-by-issue-id
  [id]
  (into [] (sql/query db/spec ["SELECT * FROM comments WHERE issue_id = ?" id])))

(defn get-comments-by-user-id
  [id]
  (into [] (sql/query db/spec ["SELECT * FROM comments WHERE user_id = ?" id])))

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
