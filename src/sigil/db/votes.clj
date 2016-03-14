(ns sigil.db.votes
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;----------------------------------------------------------------
; Querys

(defn user-voted-on-issue?
  [user_id issue_id]
  (empty? (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND issue_id = ?" user_id issue_id] ))))

(defn user-voted-on-comment?
  [user_id comment_id]
  (empty? (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND comment_id = ?" user_id comment_id] ))))

(defn get-user-votes
  [user_id]
  (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ?" user_id])))


;;----------------------------------------------------------------
; Updates/Inserts/Deletes

(defn delete-vote
  [vote]
  (sql/delete! db/spec :votes ["vote_id = ?" (:vote_id vote)]))

(defn create-vote
  ([db-conn [{:as new-vote}]]
   (sql/insert! db-conn :votes
                new-vote)))

(defn votes_model
  "Defines the vote model table in the db"
  []
  (sql/create-table-ddl
   :votes
   [:vote_id :bigserial "PRIMARY KEY"]
   [:user_id :bigint "NOT NULL"]
   [:issue_id :bigint "NOT NULL"]
   [:comment_id :bigint]
   [:official_response_id :bigint]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
