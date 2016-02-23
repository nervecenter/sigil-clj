(ns sigil.db.topics
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn create-topic
  [db-conn topic_url topic_name banner]
  (sql/insert! db-conn :topics
               [:topic_url :topic_name :banner]
               [topic_url topic_name banner]))

(defn topic_model
  []
  (sql/create-table-ddl
   :topics
   [:topic_id :bigserial "PRIMARY KEY"]
   [:topic_url :text "NOT NULL"]
   [:topic_name :text "NOT NULL"]
   [:banner :text "NOT NULL"]))

