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
  [org_url org_name website]
  (try
    (sql/insert! db/spec
                 :orgs
                 [:org_url :org_name :website]
                 [org_url org_name website])))


(defn orgs_model
  "Defines the org model in the db"
  []
  (sql/create-table-ddl
   :orgs
   [:org_id :bigserial "PRIMARY KEY"]
   [:org_url :varchar "NOT NULL" "UNIQUE"]
   [:org_name :varchar "NOT NULL" "UNIQUE"]
   [:website :varchar ]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_20 :varchar]
   [:icon_100 :varchar]
   [:banner :varchar]
   [:last_viewed :timestamp]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:subscription_level :int "NOT NULL" "DEFAULT 1"]
   ))



