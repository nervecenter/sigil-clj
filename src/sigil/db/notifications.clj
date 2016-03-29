(ns sigil.db.notifications
  (:require [clojure.java.jdbc :as sql]
            [clojure.java.jdbc.deprecated :as oldsql]
            [sigil.db.core :as db]
            [clj-time.local :as time]
            [clj-time.jdbc]))


;;-----------------------------------------------------------------
; Querys

(defn get-notification-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM notifications WHERE note_id = ?" id])))

(defn get-user-notifications
  [user]
  (into [] (sql/query db/spec ["SELECT * FROM notifications WHERE user_id = ? AND archived = FALSE" (:user_id user)])))

(defn get-number-notifications-by-user
  [user]
  (if (some? user)
    (:count (first (sql/query db/spec ["SELECT COUNT(*) FROM notifications WHERE user_id = ? AND archived = FALSE" (:user_id user)])))
    0))

;;-----------------------------------------------------------------
; Updates/Inserts/Deletes

(defn create-notification
  [db-conn new-notes]
  (let [notes (flatten new-notes)]
    (loop [note notes results []]
        (if (empty? note)
          results
          (recur (rest note) (conj results (sql/insert! db-conn :notifications (first note))))))))

(defn archive-notification
  [note]
  (sql/update! db/spec :notifications {:archived true
                                       :viewed_at (time/local-now)} ["note_id = ?" (:note_id note)]))

(defn delete-notification
  ([note] (delete-notification note false))
  ([note perm]
   (if perm
     (sql/delete! db/spec :notifications ["note_id = ?" (:note_id note)])
     (archive-notification note))))

(defn notification_model
  "Defines the org model in the db"
  []
  (sql/create-table-ddl
   :notifications
   [:note_id :bigserial "PRIMARY KEY"]
   [:user_id :bigint "NOT NULL"]
   [:message :text "DEFAULT ''"]
   [:url :text "NOT NULL"]
   [:icon :text "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:viewed_at :timestamp]
   [:archived :boolean "NOT NULL" "DEFAULT FALSE"]))

