(ns sigil.db.issues
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

(defn get-issue-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM issues WHERE issue_id = ?;" id])))

(defn get-hottest-issues-by-org
  [org_id]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE org_id = ?;" org_id])))

(defn get-issues-by-org
  [org_id]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE org_id = ?;" org_id])))

(defn get-landing-issues []
  (set (sql/query db/spec ["SELECT DISTINCT ON (issue_id) issues.title, users.username FROM issues LEFT JOIN users ON (issues.user_id = users.user_id);"])))

(defn create-issue
  [db-conn {:keys [:org_id :user_id :title :text] :as new-issue}]
  (sql/insert! db-conn
               :issues
               new-issue))

(defn issues_model
  "Defines the tag model in the db"
  []
  (sql/create-table-ddl
   :issues
   [:issue_id :bigserial "PRIMARY KEY"]
   [:org_id :bigint "NOT NULL"] ;; foriegn key to org
   [:user_id :bigint "NOT NULL"]
   [:title :text "NOT NULL"]
   [:text :text "NOT NULL" "DEFAULT ''"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:total_votes :int "NOT NULL" "DEFAULT 1"]
   [:last_voted :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:views :timestamp "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::timestamp[]"]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:tags :bigint "ARRAY" "DEFAULT ARRAY[]::bigint[]"]
   [:responded :boolean "NOT NULL" "DEFAULT false"]))
