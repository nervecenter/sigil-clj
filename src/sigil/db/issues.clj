(ns sigil.db.issues
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]
            [clj-time.local :as time]
            [sigil.db.users :as users]
            [sigil.db.orgs]
            [clj-time.jdbc]))


;;-----------------------------------------------------------------
; Querys

(defn get-issue-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM issues WHERE issue_id = ?;"  id])))

(defn get-issue-insert-id
  [issue]
  (first (sql/query db/spec ["SELECT issue_id FROM issues WHERE title = ? AND text = ?" (:title issue) (:text issue)])))

(defn get-hottest-issues-by-org
  [org]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE org_id = ? ORDER BY last_voted ASC" (:org_id org)])))

(defn get-hottest-issues-by-org-id
  [org_id]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE org_id = ? ORDER BY last_voted ASC" org_id])))

(defn get-issues-by-org
  [org]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE org_id = ?;" (:org_id org)])))

(defn get-issues-by-user
  [user]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE user_id = ?" (:user_id user)])))

(defn get-user-home-page-issues-and-posters
  [user]
  (let [user-issues (get-issues-by-user user)
        hot-issues  (map #(get-hottest-issues-by-org-id %) (set (map :org_id user-issues)))]
    
    (map #(hash-map :issue %
                    :poster (users/get-user-by-id (:user_id %)))
         (set (flatten (conj user-issues hot-issues))))))

(defn get-responded-issues-by-org
  [org]
  (into [] (sql/query db/spec ["SELECT * FROM issues WHERE org_id = ? AND responded = TRUE" (:org_id org)])))

(defn get-landing-issues-with-posters
  []
  (let [landing-issues (sql/query db/spec ["SELECT * FROM issues ORDER BY last_voted ASC LIMIT 30"])
        num-orgs (count landing-issues)]
    (partition-all (Math/ceil (/ num-orgs 3)) (map #(hash-map :issue %
                                                              :poster (users/get-user-by-id (:user_id %)))
                                                   landing-issues))))

;;------------------------------------------------------------------
; Updates/Inserts

(defn issue-view-inc
  [db-conn [issue_id]]
  (sql/execute! db-conn ["UPDATE issues SET views = array_append(views, LOCALTIMESTAMP), times_viewed = 1 + times_viewed WHERE issue_id = ?" issue_id]))

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

(defn delete-issue
  ([issue] (delete-issue issue false))
  ([issue perm]
   (if perm
     (sql/delete! db/spec :issues ["issue_id = ?" (:issue_id issue)])
     (sql/update! db/spec :issues {:user_id 0
                                   :edited_at (time/local-now)} ["issue_id = ?" (:issue_id issue)]))))

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
   [:tags :bigint "ARRAY" "DEFAULT ARRAY[]::bigint[]"]
   [:responded :boolean "NOT NULL" "DEFAULT false"]))
