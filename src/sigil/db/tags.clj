(ns sigil.db.tags
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

(defn get-tag-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM tags WHERE tag_id = ?;" id])))


(defn get-tags-by-org
  [org_id]
  (into [] (sql/query db/spec ["SELECT * FROM tags WHERE org_id = ?;" org_id])))


(defn create-tag
  [tag_url tag_name org_id]
  (sql/insert! db/spec
               :tags
               [:tag_url :tag_name :org_id]
               [tag_url tag_name org_id]))

(defn tags_model
  "Defines the tag model in the db"
  []
  (sql/create-table-ddl
   :tags
   [:tag_id :bigserial "PRIMARY KEY"]
   [:tag_url :varchar "NOT NULL" "UNIQUE"]
   [:tag_name :varchar "NOT NULL" "UNIQUE"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_20 :varchar]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "references orgs (org_id)"] ;; forgien key to orgs
))



