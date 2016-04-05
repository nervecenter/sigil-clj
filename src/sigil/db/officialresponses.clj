(ns sigil.db.officialresponses
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]
            [clj-time.local :as time]
            [sigil.db.users :as users]
            [clj-time.jdbc]))

;;-------------------------------------------------------------
; Querys

(defn get-official-response-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM official_responses WHERE comment_id = ?" id])))

(defn get-official-responses-by-issue
  [issue]
  (into [] (sql/query db/spec ["SELECT * FROM official_responses WHERE issue_id = ?" (:issue_id issue)])))

(defn get-latest-official-response-by-issue
  [issue]
  (first (into [] (sql/query db/spec ["SELECT * FROM official_responses WHERE issue_id = ? ORDER BY edited_at DESC LIMIT 1" (:issue_id issue)]))))

(defn get-responses-with-responders-by-issue
  [issue]
  (let [responses (get-official-responses-by-issue issue)]
    (map #(assoc % :responder (users/get-user-by-id (:user_id %)))
         responses)))

(defn get-official-responses-by-org
  [org]
  (into [] (sql/query db/spec ["SELECT * FROM official_responses WHERE org_id = ?" (:org_id org)])))

;;--------------------------------------------------------------
; Inserts/Updates/Deletes

(defn official-response-upvote
  [db-conn [official_response_id]]
  (sql/execute! db-conn ["UPDATE official_responses SET helpful_votes = helpful_votes + 1, last_helpful = LOCALTIMESTAMP WHERE official_response_id = ?" official_response_id]))

(defn official-response-un-upvote
  [db-conn [official_response_id]]
  (sql/execute! db-conn ["UPDATE official_responses SET helpful_votes = helpful_votes - 1 WHERE official_response_id = ?" official_response_id]))

(defn official-response-downvote
  [db-conn [official_response_id]]
  (sql/execute! db-conn ["UPDATE official_responses SET unhelpful_votes = unhelpful_votes + 1, last_unhelpful = LOCALTIMESTAMP WHERE official_response_id = ?" official_response_id]))

(defn official-response-un-downvote
  [db-conn [official_response_id]]
  (sql/execute! db-conn ["UPDATE official_responses SET unhelpful_votes = unhelpful_votes - 1 WHERE official_response_id = ?" official_response_id]))

(defn create-official-response
  [db-conn [new-official]]
  (sql/insert! db-conn
               :official_responses
               new-official))

(defn delete-official-response
  ([off_res] (delete-official-response off_res false))
  ([off_res perm]
   (if perm
     (sql/delete! db/spec :official_responses ["official_response_id = ?" (:official_response_id off_res)])
     (sql/update! db/spec :official_responses {:user_id 0
                                               :edited_at (time/local-now)}))))

(defn official_response_model
  "Defines the comments model table in the db"
  []
  (sql/create-table-ddl
   :official_responses
   [:official_response_id :bigserial "PRIMARY KEY"]
   [:issue_id :bigint "NOT NULL"]
   [:org_id :bigint "NOT NULL"]
   [:user_id :bigint "NOT NULL"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:edited_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:text :text "NOT NULL"]
   [:helpful_votes :int "NOT NULL" "DEFAULT 0"]
   [:unhelpful_votes :int "NOT NULL" "DEFAULT 0"]
   [:last_helpful :timestamp]
   [:last_unhelpful :timestamp]))
