(ns sigil.db.reports
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))

;;-------------------------------------------------------------
; Queries

(defn get-reports-by-issue [issue]
  (first (sql/query db/spec ["SELECT * FROM reports WHERE issue_id = ?;" (:issue_id issue)])))

(defn get-report-by-user-and-issue [user issue]
  (first (sql/query db/spec ["SELECT * FROM reports WHERE user_id = ? AND issue_id = ?;" (:user_id user) (:issue_id issue)])))

(defn user-reported-issue? [user issue]
  (if (not-empty (sql/query db/spec ["SELECT * FROM reports WHERE user_id = ? AND issue_id = ?;" (:user_id user) (:issue_id issue)]))
    true
    false))

;;--------------------------------------------------------------
; Updates/Inserts/Deletes

(defn create-report
  [db-conn [new-report]]
  (sql/insert! db-conn
               :reports
               new-report))

(defn delete-report
  [db-conn [report]]
  (sql/delete! db-conn :reports ["report_id = ?" (:report_id report)]))

(defn reports_model
  "Defines the reports model in the db"
  []
  (sql/create-table-ddl
   :reports
   [:report_id :bigserial "PRIMARY KEY"]
   [:user_id :int "NOT NULL"]
   [:issue_id :int "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   ))
