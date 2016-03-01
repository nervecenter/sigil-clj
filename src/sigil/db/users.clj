(ns sigil.db.users
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn get-user-by-id [id]
  (first (sql/query db/spec ["SELECT * FROM users WHERE user_id = ?;" id])))

(defn get-user-by-email [email]
  (first (sql/query db/spec ["SELECT * FROM users WHERE email = ?;" email])))

(defn get-user-subscriptions [id]
  (first (sql/query db/spec ["SELECT tag_subsctiptions FROM users WHERE user_id = ?" id])))

(defn create-user
  [db-conn email username pass_hash]
  (sql/insert! db-conn
               :users
               [:email :username :pass_hash]
               [email username pass_hash]))



(defn users_model
  "Defines the user model in the db"
  []
  (sql/create-table-ddl
   :users
   [:user_id :bigserial "PRIMARY KEY"]
   [:email :text "NOT NULL" "UNIQUE"]
   [:username :text "NOT NULL" "UNIQUE"]
   [:pass_hash :text "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_100 :text]
   [:roles :text "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::text[]"]
   [:last_login :timestamp]
   [:times_visited :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "NOT NULL" "DEFAULT 0"] ;; forigen key to orgs for admins
   [:tag_subscriptions :bigint "ARRAY" "DEFAULT ARRAY[]::bigint[]"]
   ))


