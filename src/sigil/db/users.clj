(ns sigil.db.users
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;-------------------------------------------------------------
; Querys

(defn get-user-by-id [id]
  (first (sql/query db/spec ["SELECT * FROM users WHERE user_id = ?;" id])))

(defn get-user-by-email [email]
  (first (sql/query db/spec ["SELECT * FROM users WHERE email = ?;" email])))

(defn get-user-subscriptions [id]
  (first (sql/query db/spec ["SELECT tag_subscriptions FROM users WHERE user_id = ?" id])))


;;--------------------------------------------------------------
; Updates/Inserts

(defn add-user-tags
  [db-conn [user_id & tag_ids]]
  ())

(defn user-login-inc
  [db-conn [user_id]]
  (sql/execute! db-conn ["UPDATE users SET last_login = LOCALTIMESTAMP, times_visited = times_visited + 1 WHERE user_id = ?" user_id]))

(defn update-user
  [db-conn [user_id updated-rows]]
  (sql/update! db-conn :users updated-rows ["user_id = ?" user_id]))

(defn create-user
  [db-conn [new-user]]
  (sql/insert! db-conn
               :users
               new-user))

(defn delete-user
  ([user] (delete-user user false))
  ([user perm]
   (if perm
     (sql/delete! db/spec :users ["user_id = ?" (:user_id user)])
     (sql/update! db/spec :users {:user_is_active false} ["user_id = ?" (:user_id user)]))))


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
   [:user_is_active :boolean "NOT NULL" "DEFAULT true"]))
