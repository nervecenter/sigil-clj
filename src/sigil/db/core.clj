(ns sigil.db.core
  (:require [clojure.java.jdbc :as sql]
            [clj-time.core :as t])
  (import java.sql.SQLException))

;;--------------------------- DB Maps and functions --------------------------------

(def spec-dev {:subprotocol "postgresql"
                :subname "//localhost:5432/sigildb"
                ;:user "sigildbadmin"
               ; :password "Sigiltech1027!"
               } )

(def spec-live {:subprotocol "postgresql"
           :subname "//sigil-alpha-db.cjlqgk36ylxa.us-west-2.rds.amazonaws.com:5432/sigildb"
           :user "sigildbadmin"
           :password "Sigiltech1027!"})

(def spec (atom nil))

(defn db-dev []
  (when (or (nil? @spec)
            (= @spec spec-live))
    (reset! spec spec-dev)))

(defn db-live []
  (when (or (nil? @spec)
            (= @spec spec-dev))
    (reset! spec spec-live)))

;;--------------------------- Site Settings ----------------------------------------
(def default_icon_30 ["/db_imgs/default/default_30.png"])
(def default_icon_100 ["/db_imgs/default/default100_1.png"
                       "/db_imgs/default/default100_2.png"
                       "/db_imgs/default/default100_3.png"
                       "/db_imgs/default/default100_4.png"
                       "/db_imgs/default/default100_5.png"])
(def default_banner ["/db_imgs/default/defaultbanner.png"])

(def min-time-zip-change (t/months 1)) ;;72 hrs

;;;------------------------------------------------------------------
(declare errors create-error)

;;Postgres arrays to clojure vecs
(extend-protocol sql/IResultSetReadColumn
  org.postgresql.jdbc.PgArray
  (result-set-read-column [pgobj metadata i]
    (vec (.getArray pgobj))))

;;Allows us to convert clojure vectors of type T to postgres arrays of a compatible type.
(extend-protocol clojure.java.jdbc/ISQLParameter
  clojure.lang.IPersistentVector
  (set-parameter [v ^java.sql.PreparedStatement stmt ^long i]
    (let [conn (.getConnection stmt)
          meta (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta i)]
      (if-let [elem-type (when (= (first type-name)
                                  \_)
                           (apply str (rest type-name)))]
        (.setObject stmt i (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt i v)))))

(defn db-trans
  "Accepts db insert and update functions in the form [f1 a1 a2...] [f2 a1 a2....]. Where f is the db insert or update function followed by the necessary function arguements which is all contained in a vector.
  ex. (db-trans [sigil.db.orgs/create-org new-org-map]
               [sigil.db.orgs/org-visit-inc])"
  [& fs]
  (try
    (sql/with-db-transaction [db-conn @spec]
      (if (= (count fs)
             (count
              (flatten
               (map (fn [[& f]]
                      ((first f) db-conn (rest f))) fs))))
        :success
        :fail))
    (catch Exception e
      (do
        (if (isa? (type e) SQLException)
          (create-error {:error_message (str e)
                         :additional_info (str (.getNextException e))
                         })
          (create-error {:error_message (str e)}))
        :fail))))

  ;; Error Logging


(defn update-error
  [db-conn [error_id updated-rows]]
  (sql/update! db-conn :errors updated-rows ["error_id = ?" error_id]))

(defn errors
  ([] (into [] (sql/query spec ["SELECT * FROM errors"])))
  ([id] (first (sql/query spec ["SELECT * FROM errors WHERE error_id = ?" id]))))

(defn create-error
  ([{:as new-error}]
   (sql/insert! @spec
                :errors
                new-error)))

(defn error_model
  "Defines the error model in the db"
  []
  (sql/create-table-ddl
   :errors
   [:error_id :bigserial "PRIMARY KEY"]
   [:error_message :text "NOT NULL"]
   [:additional_info :text ]
   [:user_assoc :int]
   [:org_assoc :int]
   [:issue_assoc :int]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:viewed :boolean "NOT NULL" "DEFAULT false"]
   [:viewed_at :timestamp]
   [:viewers_msg :text]
   [:viewed_by :bigint]))
