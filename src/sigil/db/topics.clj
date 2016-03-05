(ns sigil.db.topics
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn create-topic
  [db-conn {:keys [:topic_url :topic_name :banner] :as new-topic}]
  (sql/insert! db-conn :topics
               new-topic))

(defn topic_model
  []
  (sql/create-table-ddl
   :topics
   [:topic_id :bigserial "PRIMARY KEY"]
   [:topic_url :text "NOT NULL"]
   [:topic_name :text "NOT NULL"]
   [:banner :text "NOT NULL"]))

