(ns sigil.db.officialresponses
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

(defn get-official-response-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM official_responses WHERE comment_id = ?" id])))

(defn get-official-responses-by-issue
  [id]
  (into [] (sql/query db/spec ["SELECT * FROM official_responses WHERE issue_id = ?" id])))

(defn get-latest-official-response-by-issue
  [id]
  (first (into [] (sql/query db/spec ["SELECT * FROM official_responses WHERE issue_id = ? ORDER BY edited_at DESC LIMIT 1" id]))))

(defn get-official-responses-by-org
  [id]
  (into [] (sql/query db/spec ["SELECT * FROM official_responses WHERE user_id = ?" id])))


(defn create-official-response
  [db-conn {:keys [:issue_id :org_id :user_id :text] :as new-official}]
  (sql/insert! db-conn
               :official_responses
               new-official))

(defn official_response_model
  "Defines the comments model table in the db"
  []
  (sql/create-table-ddl
   :official_responses
   [:official_response_id :bigserial "PRIMARY KEY"]
   [:issue_id :bigint "NOT NULL"]
   [:org_id :bigint "NOT NULL"]
   [:user_id :bigint "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:edited_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:text :text "NOT NULL"]
   [:helpful_votes :int "NOT NULL" "DEFAULT 0"]
   [:unhelpful_votes :int "NOT NULL" "DEFAULT 0"]
   [:last_helpful :timestamp]
   [:last_unhelpful :timestamp]))
