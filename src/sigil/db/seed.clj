(ns sigil.db.seed
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]
            [sigil.db.migrations :as migrate])
  (:use [sigil.db.issues]
        [sigil.db.orgs]
        [sigil.db.comments]
        [sigil.db.tags]
        [sigil.db.comments]
        [sigil.db.users]
        [sigil.db.officialresponses]))


(def org_seed [["sigil" "Sigil" "beta.sigil.tech"]
               ["test" "Test Org" "test.com"]])

(def tag_seed [["beta" "Sigil Beta" 1]]) ;;hard setting org id as one in hopes of db being fresh

(def issue_seed [[1 1 "This is a test issue." "This issue is from sigil.db.seed. Thanks Obama."]
                  [1 1 "This is another test issue." "Why not zoidberg. Thanks Obama."]])

(def user_seed [["joe@gmail.com" "JoeTestUser" "pass-hash"]])

(defn seed-db
  []
  (sql/with-db-connection [db-conn db/spec]
    (into [] (map #(create-org db-conn (get %1 0) (get %1 1) (get %1 2)) org_seed))
    (into [] (map #(create-tag db-conn (get %1 0) (get %1 1) (get %1 2)) tag_seed))
    (into [] (map #(create-user db-conn (get %1 0) (get %1 1) (get %1 2)) user_seed))
    (into [] (map #(create-issue db-conn (get %1 0) (get %1 1) (get %1 2) (get %1 3)) issue_seed))))



(defn drop-create-seed
  []
  (do
    (migrate/drop-db-tables)
    (migrate/create-db-tables)
    (seed-db)))
