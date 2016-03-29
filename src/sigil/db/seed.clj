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
        [sigil.db.officialresponses]
        [sigil.db.votes]))

(def org_seed [{:org_url "sigil"
                :org_name "Sigil"
                :website "beta.sigil.tech"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}
               {:org_url "burgstpete"
                :org_name "The Burg"
                :website "www.theburg.com"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}
               {:org_url "worldbeer"
                :org_name "World Of Beer"
                :website "www.worldofbeer.com"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}
               {:org_url "ceviche"
                :org_name "Ceviche"
                :website "www.Ceviche.com"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}
               {:org_url "dunderbocks"
                :org_name "Dunderbocks"
                :website "www.DunderBocks.com"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}])

(def tag_seed [{:tag_url "beta"
                :tag_name "Beta Feedback"
                :org_id 1
                :icon_30 (rand-nth db/default_icon_30)}]) ;;hard setting org id as one in hopes of db being fresh

(def issue_seed [{:org_id 1
                  :user_id 1
                  :title "I need a button that gives me bacon."
                  :text "The previous buttons all lied to me."}
                 {:org_id 1
                  :user_id 2
                  :title "I need a button that prints money."
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 2
                  :user_id 1
                  :title "Your Horseradish is too strong"
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 2
                  :user_id 2
                  :title "How about staffing one more waitress for Saturday's?"
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 3
                  :user_id 1
                  :title "Terrible Service"
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 3
                  :user_id 2
                  :title "Found a hair in my soup"
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 4
                  :user_id 1
                  :title "One of the best sausages I ever had."
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 4
                  :user_id 2
                  :title "Needs more garlic in the beer."
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 5
                  :user_id 1
                  :title "The Giant preztals are the bees knees"
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 {:org_id 5
                  :user_id 2
                  :title "Why not zoidburg?"
                  :text "Doesn't grow on trees! Lorem ipsum etc."}])

;;There needs be a matching vote seed for every issue seed
(def vote_seed [{:user_id 1
                 :issue_id 1}
                {:user_id 2
                 :issue_id 2}
                {:user_id 1
                 :issue_id 3}
                {:user_id 2
                 :issue_id 4}
                {:user_id 1
                 :issue_id 5}
                {:user_id 2
                 :issue_id 6}
                {:user_id 1
                 :issue_id 7}
                {:user_id 2
                 :issue_id 8}
                {:user_id 1
                 :issue_id 9}
                {:user_id 2
                 :issue_id 10}])

(def user_seed [{:email "cjcollazo@sigil.tech"
                 :username "Nerve"
                 :pass_hash (buddy.hashers/encrypt "Sigiltech1027!")
                 :icon_100 (str (rand-nth db/default_icon_100))
                 :roles ["org-admin" "site-admin"]
                 :org_id 1}
                {:email "dominiccox@sigil.tech"
                 :username "Dominic"
                 :pass_hash (buddy.hashers/encrypt "323232")
                 :icon_100 (str (rand-nth db/default_icon_100))
                 :roles ["org-admin" "site-admin"]
                 :org_id 1}
                ])

(def topic_seed [{:topic_url "testtopic"
                  :topic_name "TestTopic"
                  :banner (str (rand-nth db/default_banner))}])

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
    (doall (map #(db/db-trans [create-topic %]) topic_seed))
    (doall (map #(db/db-trans [create-vote %]) vote_seed))))

(defn drop-create-seed
  []
  (do
    (migrate/drop-db-tables)
    (migrate/create-db-tables)
    (seed-db)))
