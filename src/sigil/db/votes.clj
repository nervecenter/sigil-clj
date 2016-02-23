(ns sigil.db.votes
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

(defn user-voted-on-issue?
  [user_id issue_id]
  (empty? (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND issue_id = ?" user_id issue_id] ))))



(defn user-voted-on-comment?
  [user_id comment_id]
  (empty? (into [] (sql/query db/spec ["SELECT * FROM votes WHERE user_id = ? AND comment_id = ?" user_id comment_id] ))))


(defn create-vote
  ([db-conn user_id issue_id]
   (sql/insert! db-conn :votes
                [:user_id :issue_id]
                [user_id issue_id]))
  ([db-conn user_id issue_id comment_id]
   (sql/insert! db-conn :votes
                [:user_id :issue_id :comment_id]
                [user_id issue_id comment_id]
    )))

(defn votes_model_table
  "Defines the vote model table in the db"
  []
  (sql/create-table-ddl
   :votes
   [:vote_id :bigserial "PRIMARY KEY"]
   [:user_id :bigint "NOT NULL"]
   [:issue_id :varchar "NOT NULL"]
   [:comment_id :bigint]
   [:official_response_id :bigint]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
