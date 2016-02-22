(ns sigil.db.core
  (:require [clojure.java.jdbc :as sql]
            
            ;; I want to get rid of these circular dependices but for now.....
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]
            [sigil.db.users :as users]
            [sigil.db.comments :as comments]
            [sigil.db.tags :as tags]
            [sigil.db.officialresponses :as officialres]))

(def postgres-debug-db {:subprotocol "postgresql"
                        :classname "org.postgresql.Driver"
                        :subname "//localhost:5432/sigildb"})

(def db postgres-debug-db)

(def spec "postgresql://localhost:5432/sigildb") ;; I wanted to alias this ns in the other model files but I didn't want it to be db/db everywhere

(defn create-db-tables
  []
  (sql/db-do-commands spec
                      (orgs/orgs_model)
                      (users/users_model)
                      (tags/tags_model)
                      (issues/issues_model)
                      (comments/comment_model)
                      (officialres/official_response_model)
                      (error_model)))

;; Error Logging
(defn errors
  ([] (into [] (sql/query spec ["SELECT * FROM errors"])))
  ([id] (first (sql/query spec ["SELECT * FROM errors WHERE error_id = ?" id]))))

(defn error
  ([msg & error_assocs]
   (try
     (sql/insert! spec
                  :errors
                  [:error_message :user_assoc :org_assoc :issue_assoc]
                  [msg (get error_assocs 0) (get error_assocs 1) (get error_assocs 2)])
     (catch Exception e
       (.getNextError e)))))

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
