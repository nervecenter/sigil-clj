(ns sigil.db.issues
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]
            [clj-time.local :as time]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.db.orgs]
            [clj-time.jdbc]
            [sigil.db.users :refer [get-user-by-id]]
            [sigil.db.orgs :refer [get-org-by-id]]))


;;-----------------------------------------------------------------
; Querys

(defn get-all-issues []
  (sql/query @db/spec ["SELECT * FROM issues;"]))

(defn get-issue-by-id
  [id]
  (first (sql/query @db/spec ["SELECT * FROM issues WHERE issue_id = ? AND archived = false;"  id])))

(defn get-issue-with-poster-by-id
  [id]
  (let [issue (first (sql/query @db/spec ["SELECT * FROM issues WHERE issue_id = ? AND archived = false;"  id]))
        user (get-user-by-id (:user_id issue))]
    (assoc issue :poster user)))

(defn get-issue-by-user-and-title
  [user title]
  (first (sql/query @db/spec ["SELECT * FROM issues WHERE title = ? AND user_id = ? AND archived = false" title (:user_id user)])))

(defn get-hottest-issues-by-org
  [org]
  (into [] (sql/query @db/spec ["SELECT * FROM issues WHERE org_id = ? AND archived = false ORDER BY last_voted ASC" (:org_id org)])))

(defn get-hottest-issues-by-org-id
  [org_id]
  (into [] (sql/query @db/spec ["SELECT * FROM issues WHERE org_id = ? AND archived = false ORDER BY last_voted ASC" org_id])))

(defn get-issues-by-org
  [org]
  (into [] (sql/query @db/spec ["SELECT * FROM issues WHERE org_id = ? AND archived = false;" (:org_id org)])))

(defn get-issues-by-user
  [user]
  (into [] (sql/query @db/spec ["SELECT * FROM issues WHERE user_id = ? AND archived = false" (:user_id user)])))

(defn get-handful-issues-by-org
  [org]
  (into [] (sql/query @db/spec ["SELECT * FROM issues WHERE org_id = ? AND archived = false ORDER BY random() LIMIT ?;" (:org_id org) (+ 3 (rand-int 3))])))

(defn get-twelve-org-issue-boxes []
  (let [orgs (orgs/get-twelve-random-orgs)]
    (map #(hash-map :org %
                    :issues (get-handful-issues-by-org %)) orgs)))

(defn get-responded-issues-by-org
  [org]
  (into [] (sql/query @db/spec ["SELECT * FROM issues WHERE org_id = ? AND responded = TRUE AND archived = false" (:org_id org)])))

;;------------------------------------------------------------------
; Updates/Inserts

(defn issue-view-inc
  [db-conn [issue_id]]
  (sql/execute! db-conn ["UPDATE issues SET views = array_append(views, LOCALTIMESTAMP), times_viewed = 1 + times_viewed WHERE issue_id = ?" issue_id]))

(defn issue-set-responded
  [db-conn [issue]]
  (sql/execute! db-conn ["UPDATE issues SET responded = TRUE WHERE issue_id = ?" (:issue_id issue)]))

(defn issue-voted
  "Increments issues total_votes and sets last_voted to current time."
  [db-conn [issue]]
  (sql/execute! db-conn ["UPDATE issues SET last_voted = LOCALTIMESTAMP, total_votes = 1 + total_votes WHERE issue_id = ?" (:issue_id issue)]))

(defn issue-unvoted
  "Decrements issues total_votes"
  [db-conn [issue]]
  (sql/execute! db-conn ["UPDATE issues SET total_votes = total_votes - 1 WHERE issue_id = ?" (:issue_id issue)]))

(defn update-issue
  [db-conn [issue_id updated-rows]]
  (sql/update! db-conn :issues updated-rows ["issue_id = ?" issue_id]))

(defn create-issue
  [db-conn [new-issue]]
  (sql/insert! db-conn
               :issues
               new-issue))

(defn archive-issue
  [db-conn [issue]]
  (sql/update! db-conn :issues {:archived true
                                :edited_at (time/local-now)} ["issue_id = ?" (:issue_id issue)]))

(defn delete-issue
  [db-conn [issue]]
  (sql/delete! db-conn :issues ["issue_id = ?" (:issue_id issue)]))

(defn issues_model
  "Defines the tag model in the db"
  []
  (sql/create-table-ddl
   :issues
   [:issue_id :bigserial "PRIMARY KEY"]
   [:org_id :bigint "NOT NULL"] ;; foriegn key to org
   [:user_id :bigint "NOT NULL"]
   [:title :text "NOT NULL"]
   [:text :text "NOT NULL" "DEFAULT ''"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:edited_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:total_votes :int "NOT NULL" "DEFAULT 1"]
   [:last_voted :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:views :timestamp "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::timestamp[]"]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   ;;[:tags :bigint "ARRAY" "DEFAULT ARRAY[]::bigint[]"]
   [:tag_id :int "NOT NULL" "DEFAULT 0"]
   [:responded :boolean "NOT NULL" "DEFAULT false"]
   [:archived :boolean "NOT NULL" "DEFAULT false"]
    ))
