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

(def org_seed [{:org_url "sigil"
                :org_name "Sigil"
                :website "beta.sigil.tech"
                :icon_20 (str (rand-nth default_org_icon_20))
                :icon_100 (str (rand-nth default_org_icon_100))
                :banner (str (rand-nth default_org_banner))}
               {:org_url "test"
                :org_name "testname"
                :website "www.test.com"
                :icon_20 (str (rand-nth default_org_icon_20))
                :icon_100 (str (rand-nth default_org_icon_100))
                :banner (str (rand-nth default_org_banner))}])

(def tag_seed [{:tag_url "beta"
                :tag_name "Sigil Beta"
                :org_id 1
                :icon_20 (rand-nth default_org_icon_20)}]) ;;hard setting org id as one in hopes of db being fresh

(def issue_seed [{:org_id 1
                  :user_id 1
                  :title "Test issue 1"
                  :text "Thanks Obama"}
                 {:org_id 1
                  :user_id  1
                  :title "Test issue 2"
                  :text "Thanks Obama again."}])

(def user_seed [{:email "fuckjoe@gmail.com"
                 :username "Joetest"
                 :pass_hash "fuck"
                 :icon_100 (str (rand-nth default_org_icon_100))}])




(defn seed-orgs
  []
  (apply #(db/db-trans [create-org %]) org_seed))

(defn seed-db
  []
  (do
    (doall (map #(db/db-trans [create-org %]) org_seed))
    (doall (map #(db/db-trans [create-user %]) user_seed))
    (doall (map #(db/db-trans [create-tag %]) tag_seed))
    (doall (map #(db/db-trans [create-issue %]) issue_seed))))

(defn drop-create-seed
  []
  (do
    (migrate/drop-db-tables)
    (migrate/create-db-tables)
    (seed-db)))
