(ns sigil.db.notifications
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


;;-----------------------------------------------------------------
; Querys


(defn get-users-notifications
  [user]
  ())

;;-----------------------------------------------------------------
; Updates/Inserts/Deletes

(defn create-notification
  [db-conn [new-note]]
  ())

(defn archive-notification
  []
  ())

(defn delete-notification
  []
  ())

(defn notification_model
  "Defines the org model in the db"
  []
  (sql/create-table-ddl
   :notifications
   [:note_id :bigserial "PRIMARY KEY"]
   [:from_user_id :bigint "NOT NULL"]
   [:to_user_id :bigint "NOT NULL"]
   [:note_message :text "DEFAULT ''"]
   [:issue_id :bigint]
   [:comment_id :bigint]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:viewed_at :timestamp]
   [:archived :boolean "NOT NULL" "DEFAULT FALSE"]))

