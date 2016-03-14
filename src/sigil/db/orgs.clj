(ns sigil.db.orgs
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(def default_org_icon_20 ["images/default/default20.png"])
(def default_org_icon_100 ["images/default/default100_1.png" "images/default/default100_2.png" "images/default/default100_3.png" "images/default/default100_4.png" "images/default/default100_5.png"])
(def default_org_banner ["images/default/defaultbanner.png"])


;;-----------------------------------------------------
; Querys

(defn get-org-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_id = ?;" id])))

(defn get-all-orgs
  []
  (into [] (sql/query db/spec ["SELECT * FROM orgs"])))

(defn get-org-by-url
  [url]
  (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_url = ?;" url])))

(defn get-org-by-user [user]
  (if (= (:org_id user) 0)
    nil 
    (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_id = ?" (:org_id user)]))))

;;-----------------------------------------------------
; Updates/Inserts

(defn org-visit-inc
  "Called every time an org page is visited. "
  [db-conn [org_id]]
  (sql/execute! db-conn ["UPDATE orgs SET times_viewed = 1 + times_viewed, last_viewed = CURRENT_TIMESTAMP WHERE org_id = ?" org_id]))

(defn update-org
  [db-conn [org_id updated-rows]]
  (sql/update! db-conn :orgs updated-rows ["org_id = ?" org_id]))

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
     (sql/delete! db/spec :orgs ["org_id = ?" (:org_id org)])
     (sql/update! db/spec :orgs {:org_is_active false} ["org_id = ?" (:org_id org)]))))

(defn orgs_model
  "Defines the org model in the db"
  []
  (sql/create-table-ddl
   :orgs
   [:org_id :bigserial "PRIMARY KEY"]
   [:org_url :text "NOT NULL" "UNIQUE"]
   [:org_name :text "NOT NULL" "UNIQUE"]
   [:website :text "NOT NULL" "DEFAULT '#'"]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_20 :text]
   [:icon_100 :text]
   [:banner :text]
   [:last_viewed :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:subscription_level :int "NOT NULL" "DEFAULT 1"]
   [:address :text]
   [:city :text]
   [:state :text]
   [:zip-code :text]
   [:org_is_active :boolean "NOT NULL" "DEFAULT true"]
   ))



