(ns sigil.db.topics
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn topic_model
  []
  (sql/create-table-ddl
   :topics
   [:topic_id :bigserial "PRIMARY KEY"]
   [:topic_url :varchar "NOT NULL"]
   [:topic_name :varchar "NOT NULL"]
   [:banner :varchar "NOT NULL"]))

