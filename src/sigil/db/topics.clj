(ns sigil.db.topics
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn update-topic
  [db-conn [topic_id updated-rows]]
  (sql/update! db-conn :topics updated-rows ["topic_id = ?" topic_id]))


(defn create-topic
  [db-conn [new-topic]]
  (sql/insert! db-conn
               :topics
               new-topic))

(defn topic_model
  []
  (sql/create-table-ddl
   :topics
   [:topic_id :bigserial "PRIMARY KEY"]
   [:topic_url :text "NOT NULL"]
   [:topic_name :text "NOT NULL"]
   [:banner :text "NOT NULL"]))

