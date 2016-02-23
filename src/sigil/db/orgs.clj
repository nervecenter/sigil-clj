(ns sigil.db.orgs
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn get-org-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_id = ?;" id])))


(defn get-org-by-url
  [url]
  (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_url = ?;" url])))


(defn create-org
  "Creates a new org from passed in map."
  [db-conn org_url org_name website]
  (sql/insert! db-conn
               :orgs
               [:org_url :org_name :website]
               [org_url org_name website]))


(defn orgs_model
  "Defines the org model in the db"
  []
  (sql/create-table-ddl
   :orgs
   [:org_id :bigserial "PRIMARY KEY"]
   [:org_url :text "NOT NULL" "UNIQUE"]
   [:org_name :text "NOT NULL" "UNIQUE"]
   [:website :text ]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_20 :text]
   [:icon_100 :text]
   [:banner :text]
   [:last_viewed :timestamp]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:subscription_level :int "NOT NULL" "DEFAULT 1"]
   ))



