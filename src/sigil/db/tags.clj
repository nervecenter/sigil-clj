(ns sigil.db.tags
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;-------------------------------------------------------------------
; Querys

(defn get-all-tags
  []
  (into [] (sql/query db/spec ["SELECT * FROM tags"])))

(defn get-tag-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM tags WHERE tag_id = ?" id])))

(defn get-tags-by-org
  [org]
  (into [] (sql/query db/spec ["SELECT * FROM tags WHERE org_id = ?" (:org_id org)])))

;;---------------------------------------------------------------------
; Updates/Inserts/Deletes


(defn create-tag
  [db-conn [new-tag]]
  (sql/insert! db-conn
               :tags
               new-tag))

(defn delete-tag
  ([tag] (delete-tag tag false))
  ([tag perm]
   (if perm
     (sql/delete! db/spec :tags ["tag_id = ?" (:tag_id tag)])
     (sql/update! db/spec :tags {:tag_is_active false} ["tag_id = ?" (:tag_id tag)]))))

(defn tags_model
  "Defines the tag model in the db"
  []
  (sql/create-table-ddl
   :tags
   [:tag_id :bigserial "PRIMARY KEY"]
   [:tag_url :text "NOT NULL" "UNIQUE"]
   [:tag_name :text "NOT NULL" "UNIQUE"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_30 :text]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "NOT NULL"]
   [:tag_is_active :boolean "NOT NULL" "DEFAULT TRUE"]))



