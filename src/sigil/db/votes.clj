(ns sigil.db.votes
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;----------------------------------------------------------------
; Querys

(def not-empty? (complement empty?))

(defn user-voted-on-issue?
  [user issue]
   (not-empty? (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND issue_id = ? AND comment_id IS NULL" (:user_id user) (:issue_id issue)] )))

(defn user-voted-on-comment?
  [user comment]
  (empty? (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND comment_id = ?" (:user_id user) (:comment_id comment)] ))))

(defn get-user-votes
  [user]
  (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ?" (:user_id user)])))

(defn get-user-issue-vote
  [user issue]
  (first (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND issue_id = ? AND comment_id IS NULL" (:user_id user) (:issue_id issue)])))

(defn get-user-comment-vote
  [user comment]
  (first (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND comment_id = ?" (:user_id user) (:comment_id comment)])))

(defn get-users-who-voted-issue
  [issue]
  (set (into [] (sql/query db/spec ["SELECT user_id FROM votes WHERE issue_id = ?" (:issue_id issue)]))))

;;----------------------------------------------------------------
; Updates/Inserts/Deletes

(defn delete-vote
  [db-conn [vote]]
  (sql/delete! db-conn :votes ["vote_id = ?" (:vote_id vote)]))

(defn create-vote
  [db-conn [new-vote]]
  (sql/insert! db-conn
               :votes
               new-vote))

(defn votes_model
  "Defines the vote model table in the db"
  []
  (sql/create-table-ddl
   :votes
   [:vote_id :bigserial "PRIMARY KEY"]
   [:user_id :bigint "NOT NULL"]
   [:issue_id :bigint "NOT NULL"]
   [:comment_id :bigint "DEFAULT NULL"]
   [:official_response_id :bigint]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
