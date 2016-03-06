(ns sigil.db.tags
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

(defn get-tag-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM tags WHERE tag_id = ?;" id])))


(defn get-tags-by-org-id
  [org_id]
  (into [] (sql/query db/spec ["SELECT * FROM tags WHERE org_id = ?;" org_id])))


(defn create-tag
  [db-conn [new-tag]]
  (sql/insert! db-conn
               :tags
               new-tag))

(defn tags_model
  "Defines the tag model in the db"
  []
  (sql/create-table-ddl
   :tags
   [:tag_id :bigserial "PRIMARY KEY"]
   [:tag_url :text "NOT NULL" "UNIQUE"]
   [:tag_name :text "NOT NULL" "UNIQUE"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_20 :text]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "NOT NULL"]))



