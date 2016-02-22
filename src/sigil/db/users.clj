(ns sigil.db.users
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(defn get-user-by-id [id]
  (first (sql/query db/spec ["SELECT * FROM users WHERE user_id = ?;" id])))

(defn get-user-by-email [email]
  (first (sql/query db/spec ["SELECT * FROM users WHERE email = ?;" email])))

(defn create-user
  [email username pass_hash]
  (sql/insert! db/spec
               :users
               [:email :username :pass_hash]
               [email username pass_hash]))

(defn users_model
  "Defines the user model in the db"
  []
  (sql/create-table-ddl
   :users
   [:user_id :bigserial "PRIMARY KEY"]
   [:email :varchar "NOT NULL" "UNIQUE"]
   [:username :varchar "NOT NULL" "UNIQUE"]
   [:pass_hash :varchar "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:image_icon :varchar]
   [:roles :varchar "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::varchar[]"]
   [:last_login :timestamp]
   [:times_visited :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "NOT NULL" "DEFAULT 0"] ;; forigen key to orgs for admins
   [:subscriptions :bigint "ARRAY" "DEFAULT ARRAY[]::bigint[]"]
   ))


