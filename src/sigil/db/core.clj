(ns sigil.db.core
  (:require [clojure.java.jdbc :as sql])
  (import java.sql.SQLException))

;; (def postgres-debug-db {:subprotocol "postgresql"
;;                         :classname "org.postgresql.Driver"
;;                         :subname "//localhost:5432/sigildb"})

;; (def db postgres-debug-db)

(def spec "postgresql://localhost:5432/sigildb") ;; I wanted to alias this ns in the other model files but I didn't want it to be db/db everywhere


(def default_icon_20 ["/db_imgs/default/default_20.png"])
(def default_icon_100 ["/db_imgs/default/default100_1.png" "/db_imgs/default/default100_2.png" "/db_imgs/default/default100_3.png" "/db_imgs/default/default100_4.png" "/db_imgs/default/default100_5.png"])
(def default_banner ["/db_imgs/default/defaultbanner.png"])

(declare errors create-error)

;;Postgres arrays to clojure vecs
(extend-protocol sql/IResultSetReadColumn
  org.postgresql.jdbc.PgArray
  (result-set-read-column [pgobj metadata i]
    (vec (.getArray pgobj))))


;;Clojure veccs to postgres arrays
(defn vec->arr [array-vector]
  (.createArrayOf (sql/get-connection spec) "long" (into-array Long array-vector)))

;; (extend-protocol sql/ISQLValue
;;     clojure.lang.IPersistentVector
;;     (sql-value [v]
;;     (vec->arr v)))

(extend-protocol clojure.java.jdbc/ISQLParameter
  clojure.lang.IPersistentVector
  (set-parameter [v ^java.sql.PreparedStatement stmt ^long i]
    (let [conn (.getConnection stmt)
          meta (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta i)]
      (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
        (.setObject stmt i (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt i v)))))


(defn db-trans
  "Accepts db insert and update functions in the form [f1 a1 a2...] [f2 a1 a2....]. Where f is the db insert or update function followed by the necessary function arguements which is all contained in a vector.
  ex. (db-trans [sigil.db.orgs/create-org new-org-map]
               [sigil.db.orgs/org-visit-inc])"
  [& fs]
  (try
    (sql/with-db-transaction [db-conn spec]
      (if (= (count fs) (count (flatten (map (fn [[& f]]
                                               ((first f) db-conn (rest f))) fs))))
        :success
        :fail))
    (catch Exception e
      (do
        (create-error (str e))
        (if (isa? (type e) SQLException)
          (create-error (str (.getNextException e))))
        :fail))))

  ;; Error Logging

(defn errors
  ([] (into [] (sql/query spec ["SELECT * FROM errors"])))
  ([id] (first (sql/query spec ["SELECT * FROM errors WHERE error_id = ?" id]))))

(defn create-error
  ([msg & error_assocs]
   (sql/insert! spec
                :errors
                [:error_message :user_assoc :org_assoc :issue_assoc]
                [msg (get error_assocs 0) (get error_assocs 1) (get error_assocs 2)])))

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
