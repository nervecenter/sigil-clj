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

(defn get-tag-by-issue
  [issue]
  (first (sql/query db/spec ["SELECT * FROM tags WHERE tag_id = ? LIMIT 1" (:tag_id issue)])))

;;---------------------------------------------------------------------
; Updates/Inserts/Deletes


(defn create-tag
  [db-conn [new-tag]]
  (sql/insert! db-conn
               :tags
               new-tag))

;;(defn delete-tag
;;  ([tag] (delete-tag tag false))
;;  ([tag perm]
;;   (if perm
;;     (sql/delete! db/spec :tags ["tag_id = ?" (:tag_id tag)])
;;     (sql/update! db/spec :tags {:tag_is_active false} ["tag_id = ?" (:tag_id tag)]))))

(defn delete-tag [tag]
  (sql/delete! db/spec :tags ["tag_id = ?" (:tag_id tag)]))

(defn update-tag
  [db-conn [tag updated-rows]]
  (sql/update! db-conn :tags updated-rows ["tag_id = ?" (:tag_id tag)]))

;; TODO: Make this a sql/update! so it can be done in transactions atomically.
(defn move-issues-from-tag-to-tag
  [from-tag to-tag]
  (sql/query db/spec ["UPDATE issues SET tag_id = ? WHERE tag_id = ?" (:tag_id to-tag) (:tag_id from-tag)]))

(defn tags_model
  "Defines the tag model in the db"
  []
  (sql/create-table-ddl
   :tags
   [:tag_id :bigserial "PRIMARY KEY"]
   ;;[:tag_url :text "NOT NULL" "UNIQUE"]
   [:tag_name :text "NOT NULL" "UNIQUE"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_30 :text "NOT NULL"]
   ;;[:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "NOT NULL"]
   ;;[:tag_is_active :boolean "NOT NULL" "DEFAULT TRUE"]
   ))
