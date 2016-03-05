(ns sigil.db.orgs
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]))


(def default_org_icon_20 ["images/default/default20.png"])
(def default_org_icon_100 ["images/default/default100_1.png" "images/default/default100_2.png" "images/default/default100_3.png" "images/default/default100_4.png" "images/default/default100_5.png"])
(def default_org_banner ["images/default/defaultbanner.png"])

(defn get-org-by-id
  [id]
  (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_id = ?;" id])))


(defn get-org-by-url
  [url]
  (first (sql/query db/spec ["SELECT * FROM orgs WHERE org_url = ?;" url])))

(defn create-org
  "Creates a new org from passed in map."
  ;; ([db-conn [org_url org_name website]]
  ;;  (create-org db-conn org_url org_name website (rand-nth default_org_icon_20) (rand-nth default_org_icon_100) (rand-nth default_org_banner)))
  ([db-conn {:keys [:org_url :org_name :website :icon_20 :icon_100 :banner] :as new-org}]
   (sql/insert! db-conn
                :orgs
                new-org
                ;; [:org_url :org_name :website :icon_20 :icon_100 :banner]
                ;; [(:org_url new-org) (:org_name new-org) (:website new-org) (:icon_20 new-org) (:icon_100 new-org) (:banner new-org)]
                )))


(defn orgs_model
  "Defines the org model in the db"
  []
  (sql/create-table-ddl
   :orgs
   [:org_id :bigserial "PRIMARY KEY"]
   [:org_url :text "NOT NULL" "UNIQUE"]
   [:org_name :text "NOT NULL" "UNIQUE"]
   [:website :text ]
   [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]
   [:icon_20 :text]
   [:icon_100 :text]
   [:banner :text]
   [:last_viewed :timestamp]
   [:times_viewed :int "NOT NULL" "DEFAULT 0"]
   [:subscription_level :int "NOT NULL" "DEFAULT 1"]
   ))



