(ns sigil.db.users
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;-------------------------------------------------------------
; Querys

(defn get-user-by-id [id]
  (first (sql/query db/spec ["SELECT * FROM users WHERE user_id = ?;" id])))

(defn get-user-by-email [email]
  (first (sql/query db/spec ["SELECT * FROM users WHERE email = ?;" email])))

(defn get-user-by-issue [issue]
  (first (sql/query db/spec ["SELECT * FROM users WHERE user_id = ?;" (:user_id issue)])))

(defn get-user-favorites [user]
  (first (sql/query db/spec ["SELECT favorites FROM users WHERE user_id = ?" (:user_id user)])))

(defn get-user-by-username
  [username]
  (first (sql/query db/spec ["SELECT * FROM users WHERE username = ?" username])))

(defn get-user-roles
  [user]
  (first (sql/query db/spec ["SELECT roles FROM users WHERE user_id = ?" (:user_id user)])))

;;--------------------------------------------------------------
; Updates/Inserts/Deletes

(defn add-user-favorite
  [db-conn [user_id org_id]]
  (sql/execute! db-conn ["UPDATE users SET favorites = array_append(favorites, ?) WHERE user_id = ?" org_id user_id]))

(defn user-login-inc
  [db-conn [user_id]]
  (sql/execute! db-conn ["UPDATE users SET last_login = LOCALTIMESTAMP, times_visited = times_visited + 1 WHERE user_id = ?" user_id]))

(defn update-user
  [db-conn [user-and-updated-rows]]
  (sql/update! db-conn :users (second user-and-updated-rows) ["user_id = ?" (:user_id (first user-and-updated-rows))]))

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
   [:roles :int "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::int[]"]
   [:last_login :timestamp]
   [:times_visited :int "NOT NULL" "DEFAULT 0"]
   [:org_id :bigint "NOT NULL" "DEFAULT 0"] ;; forigen key to orgs for admins
   [:favorites :bigint "ARRAY" "DEFAULT ARRAY[]::bigint[]"]
   [:user_is_active :boolean "NOT NULL" "DEFAULT TRUE"]))
