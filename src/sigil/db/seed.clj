(ns sigil.db.seed
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db]
            [sigil.db.migrations :as migrate]
            buddy.hashers)
  (:use [sigil.db.issues]
        [sigil.db.orgs]
        [sigil.db.comments]
        [sigil.db.tags]
        [sigil.db.comments]
        [sigil.db.topics]
        [sigil.db.users]
        [sigil.db.officialresponses]))

(def org_seed [{:org_url "sigil"
                :org_name "Sigil"
                :website "beta.sigil.tech"
                :icon_30 (str (rand-nth default_org_icon_20))
                :icon_100 (str (rand-nth default_org_icon_100))
                :banner (str (rand-nth default_org_banner))}
               {:org_url "test"
                :org_name "testname"
                :website "www.test.com"
                :icon_30 (str (rand-nth default_org_icon_20))
                :icon_100 (str (rand-nth default_org_icon_100))
                :banner (str (rand-nth default_org_banner))}])

(def tag_seed [{:tag_url "beta"
                :tag_name "Beta Feedback"
                :org_id 1
                :icon_30 (rand-nth default_org_icon_20)}]) ;;hard setting org id as one in hopes of db being fresh

(def issue_seed [{:org_id 1
                  :user_id 1
                  :title "I need a button that gives me bacon."
                  :text "The previous buttons all lied to me."}
                 {:org_id 1
                  :user_id  1
                  :title "I need a button that prints money."
                  :text "Doesn't grow on trees! Lorem ipsum etc."}])

(def user_seed [{:email "cjcollazo@sigil.tech"
                 :username "Nerve"
                 :pass_hash (buddy.hashers/encrypt "Sigiltech1027!")
                 :icon_100 (str (rand-nth default_org_icon_100))
                 :roles ["org-admin" "site-admin"]
                 :org_id 1}
                {:email "dominiccox@sigil.tech"
                 :username "Dominic"
                 :pass_hash (buddy.hashers/encrypt "323232")
                 :icon_100 (str (rand-nth default_org_icon_100))
                 :roles ["org-admin" "site-admin"]
                 :org_id 1}
                ])

(def topic_seed [{:topic_url "testtopic"
                  :topic_name "TestTopic"
                  :banner (str (rand-nth default_org_banner))}])


(defn seed-orgs
  []
  (apply #(db/db-trans [create-org %]) org_seed))

(defn seed-db
  []
  (do
    (doall (map #(db/db-trans [create-org %]) org_seed))
    (doall (map #(db/db-trans [create-user %]) user_seed))
    (doall (map #(db/db-trans [create-tag %]) tag_seed))
    (doall (map #(db/db-trans [create-issue %]) issue_seed))
    (doall (map #(db/db-trans [create-topic %]) topic_seed))))

(defn drop-create-seed
  []
  (do
    (migrate/drop-db-tables)
    (migrate/create-db-tables)
    (seed-db)))
