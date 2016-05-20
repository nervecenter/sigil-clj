(ns sigil.db.petitions
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;-------------------------------------------------------------
; Queries

(defn get-petitions []
  (sql/query db/spec ["SELECT * FROM petitions ORDER BY created_at DESC;"]))

(defn issue-has-been-petitioned? [issue]
  (if (not-empty (sql/query db/spec ["SELECT * FROM petitions WHERE petition_id = ?;"(:petition_id petition)]))
    true
    false))

;;--------------------------------------------------------------
; Updates/Inserts/Deletes

(defn create-petition
  [db-conn [new-petition]]
  (sql/insert! db-conn
               :petitions
               new-petition))

(defn delete-petition
  [db-conn [petition]]
  (sql/delete! db-conn :petitions ["petition_id = ?" (:petition_id petition)]))

(defn petitions_model
  "Defines the petitions model in the db"
  []
  (sql/create-table-ddl
   :petitions
   [:petition_id :bigserial "PRIMARY KEY"]
   [:user_id :int "NOT NULL"]
   [:issue_id :int "NOT NULL"]
   [:org_id :int "NOT NULL"]
   [:body :text "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   ))
