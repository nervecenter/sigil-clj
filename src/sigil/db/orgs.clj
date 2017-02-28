(ns sigil.db.orgs
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :refer [spec]]))


;;-----------------------------------------------------
; Querie

(defn get-org-by-id
  [id]
  (first (sql/query @spec ["SELECT * FROM orgs WHERE org_id = ?;" id])))

(defn get-all-orgs
  []
  (into [] (sql/query @spec ["SELECT * FROM orgs"])))

(defn get-five-orgs-by-term
  [term]
  (sql/query @spec ["SELECT *, levenshtein(org_name, ?) FROM orgs ORDER BY levenshtein(org_name, ?) ASC LIMIT 5;" term term]))

(defn get-org-by-name
  [name]
  (first (sql/query @spec ["SELECT * FROM orgs WHERE org_name = ?" name])))

(defn get-org-by-url
  [url]
  (first (sql/query @spec ["SELECT * FROM orgs WHERE org_url = ?;" url])))

(defn get-org-by-user [user]
  (if (= (:org_id user) 0)
    nil
    (first (sql/query @spec ["SELECT * FROM orgs WHERE org_id = ?" (:org_id user)]))))

(defn get-org-by-issue
  [issue]
  (first (sql/query @spec ["SELECT * FROM orgs WHERE org_id = ?;" (:org_id issue)])))

(defn get-twelve-random-orgs []
  (into [] (sql/query @spec ["SELECT * FROM orgs ORDER BY random() LIMIT 12;"])))

;;-----------------------------------------------------
; Updates/Inserts

(defn org-visit-inc
  "Called every time an org page is visited. "
  [db-conn [org_id]]
  (sql/execute! db-conn ["UPDATE orgs SET times_viewed = 1 + times_viewed, views = ARRAY_APPEND(views, LOCALTIMESTAMP) WHERE org_id = ?" org_id]))

(defn update-org
  [db-conn [org updated-rows]]
  (sql/update! db-conn :orgs updated-rows ["org_id = ?" (:org_id org)]))

(defn create-org
  "Creates a new org from passed in map."
  [db-conn [new-org]]
  (sql/insert! db-conn
               :orgs
               new-org))

(defn delete-org
  ([org] (delete-org org false))
  ([org perm]
   (if perm
     (sql/delete! @spec :orgs ["org_id = ?" (:org_id org)])
     (sql/update! @spec :orgs {:org_is_active false} ["org_id = ?" (:org_id org)]))))

(defn orgs_model
  "Defines the org model in the db"
  [& specs]
  (sql/create-table-ddl
   :orgs
   [:org_id :bigserial "PRIMARY KEY"]
   [:org_url :text "NOT NULL" "UNIQUE"]
   [:org_name :text "NOT NULL" "UNIQUE"]
   [:website :text "NOT NULL" "DEFAULT '#'"]
   [:org_topic_ids :bigint "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::bigint[]"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_30 :text]
   [:icon_100 :text]
   [:banner :text]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:views :timestamp "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::timestamp[]"]
   [:subscription_level :int "NOT NULL" "DEFAULT 1"]
   [:address :text]
   [:city :text]
   [:state :text]
   [:zip_codes :int "ARRAY" "NOT NULL" "DEFAULT ARRAY[]::int[]"]
   [:phone :text]
   [:org_is_active :boolean "NOT NULL" "DEFAULT TRUE"]
   [:org_approved :boolean "NOT NULL" "DEFAULT FALSE"]))
