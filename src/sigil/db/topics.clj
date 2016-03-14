(ns sigil.db.topics
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;-------------------------------------------------------------------
; Querys

(defn get-all-topics
  []
  (into [] (sql/query db/spec ["SELECT * FROM topics"])))


;;-------------------------------------------------------------------
; Updates/Inserts/Deletes

(defn update-topic
  [db-conn [topic_id updated-rows]]
  (sql/update! db-conn :topics updated-rows ["topic_id = ?" topic_id]))


(defn create-topic
  [db-conn [new-topic]]
  (sql/insert! db-conn
               :topics
               new-topic))

(defn delete-topic
  ([topic] (delete-topic topic false))
  ([topic perm]
   (if perm
     (sql/delete! db/spec :topics ["topic_id = ?" (:topic_id topic)])
     (sql/update! db/spec :topics {:topic_is_active false} ["topic_id = ?" (:topic_id topic)]))))

(defn topic_model
  []
  (sql/create-table-ddl
   :topics
   [:topic_id :bigserial "PRIMARY KEY"]
   [:topic_url :text "NOT NULL"]
   [:topic_name :text "NOT NULL"]
   [:banner :text "NOT NULL"]
   [:topic_is_active :boolean "NOT NULL" "DEFAULT true"]))

