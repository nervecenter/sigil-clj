(ns sigil.db.officialresponses
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))




(defn get-official-response-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM comments WHERE comment_id = ?" id])))

(defn get-official-responses-by-issue
  [id]
  (into [] (sql/query db/spec ["SELECT * FROM comments WHERE issue_id = ?" id])))

(defn get-official-responses-by-org
  [id]
  (into [] (sql/query db/spec ["SELECT * FROM comments WHERE user_id = ?" id])))


(defn official_response_model
  "Defines the comments model table in the db"
  []
  (sql/create-table-ddl
   :official_responses
   [:official_response_id :bigserial "PRIMARY KEY"]
   [:issue_id :bigint "NOT NULL" "references issues (issue_id)"]
   [:org_id :bigint "NOT NULL" "references orgs (org_id)"]
   [:user_id :bigint "references users (user_id)"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:edited_at :timestamp]
   [:text :varchar "NOT NULL"]
   [:helpful_votes :int "NOT NULL" "DEFAULT 0"]
   [:unhelpful_votes :int "NOT NULL" "DEFAULT 0"]
   [:last_helpful :timestamp]
   [:last_unhelpful :timestamp]))
