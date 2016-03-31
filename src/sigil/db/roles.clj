(ns sigil.db.roles
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;--------------------------------------------------------------------
; Querys


(defn get-site-admin-role
  []
  (first (sql/query db/spec ["SELECT * FROM roles WHERE role_name = 'site-admin'"])))

(defn get-org-admin-role
   []
  (first (sql/query db/spec ["SELECT * FROM roles WHERE role_name = 'org-admin'"])))


(defn get-all-roles
  []
  (into [] (sql/query db/spec ["SELECT * FROM roles"])))


;;--------------------------------------------------------------------
; Updates/Inserts/Deletes

(defn delete-role
  [db-conn [role]]
  (sql/delete! db-conn :roles ["role_id = ?" (:role_id role)]))

(defn create-role
  [db-conn [new-role]]
  (sql/insert! db-conn
               :roles
               new-role))

(defn roles_model
  "Defines the role model table in the db"
  []
  (sql/create-table-ddl
   :roles
   [:role_id :bigserial "PRIMARY KEY"]
   [:role_name :text "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
