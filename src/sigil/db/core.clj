(ns sigil.db.core
  (:require [clojure.java.jdbc :as sql]))

(def postgres-debug-db {:subprotocol "postgresql"
                        :classname "org.postgresql.Driver"
                        :subname "//localhost:5432/sigildb"})

(def db postgres-debug-db)

(def spec "postgresql://localhost:5432/sigildb") ;; I wanted to alias this ns in the other model files but I didn't want it to be db/db everywhere


;;Postgres arrays to clojure vecs
(extend-protocol sql/IResultSetReadColumn
  org.postgresql.jdbc42.Jdbc42Array
  (result-set-read-column [pgobj metadata i]
    (vec (.getArray pgobj))))


;;Clojure veccs to postgres arrays
(defn vec->arr [array-vector]
  (.createArrayOf (sql/get-connection spec) "long" (into-array Long array-vector)))

(extend-protocol sql/ISQLValue
    clojure.lang.IPersistentVector
    (sql-value [v]
    (vec->arr v)))


;; Error Logging
(defn errors
  ([] (into [] (sql/query spec ["SELECT * FROM errors"])))
  ([id] (first (sql/query spec ["SELECT * FROM errors WHERE error_id = ?" id]))))

(defn create-error
  ([msg & error_assocs]
   (try
     (sql/insert! spec
                  :errors
                  [:error_message :user_assoc :org_assoc :issue_assoc]
                  [msg (get error_assocs 0) (get error_assocs 1) (get error_assocs 2)])
     (catch Exception e))))

(defn error_model
  "Defines the error model in the db"
  []
  (sql/create-table-ddl
   :errors
   [:error_id :bigserial "PRIMARY KEY"]
   [:error_message :varchar "NOT NULL"]
   [:user_assoc :int]
   [:org_assoc :int]
   [:issue_assoc :int]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:viewed :boolean "NOT NULL" "DEFAULT false"]))
