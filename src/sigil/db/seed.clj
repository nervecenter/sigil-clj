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
        [sigil.db.votes]
        [sigil.db.roles]))

(def org_seed [{:org_url "sigil"
                :org_name "Sigil"
                :website "sigil.tech"
                :address "10707 Ayrshire Dr"
                :city "Tampa"
                :state "FL"
                :zip_code "33626"
                :phone "813-334-3699"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}
               {:org_url "crowleys"
                :org_name "Crowley's Downtown"
                :website "www.crowleyspub.com"
                :address "269 Central Ave"
                :city "St. Petersburg"
                :state "FL"
                :zip_code "33701"
                :phone "727-821-1111"
                :icon_30 "/db_imgs/org/crowleys_30.png"
                :icon_100 "/db_imgs/org/crowleys_100.png"
                :banner "/db_imgs/org/crowleys_banner.png"}
               {:org_url "theburgbar"
                :org_name "The Burg Bar & Grille"
                :website "www.theburgbar.com"
                :address "1752 Central Ave"
                :city "St. Petersburg"
                :state "FL"
                :zip_code "33712"
                :phone "727-894-2874"
                :icon_30 (str (rand-nth db/default_icon_30))
                :icon_100 (str (rand-nth db/default_icon_100))
                :banner (str (rand-nth db/default_banner))}
               ])

(def role_seed [{:role_name "org-admin"}
                {:role_name "site-admin"}])

(def tag_seed [{:tag_url "beta"
                :tag_name "Beta Feedback"
                :org_id 1
                :icon_30 (rand-nth db/default_icon_30)}]) ;;hard setting org id as one in hopes of db being fresh

(def comment_seed
  [{:issue_id 1
    :user_id 1
    :text "Yea a bacon button would increase my quality of life"}
   {:issue_id 2
    :user_id 1
    :text "Yea Bacon!"}
   {:issue_id 1
    :user_id 2
    :text "I don't think a money printer will help solve their problems."}
   {:issue_id 2
    :user_id 2
    :text "Money printers are expensive. Who'll pay for it?"}
   {:issue_id 2
    :user_id 1
    :text "I think they should pay for it if they really want it."}])

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
                  :title "Offer smaller gravy portion."
                  :text "With my fish and chips, I get a huge cup of gravy, and use not nearly all of it. I'd hate it to go to waste, so serve a little less."}
                 {:org_id 2
                  :user_id 2
                  :title "Add more wait staff Saturday nights."
                  :text "There weren't enough and those waiting were stressed and handling too much. Consider scheduling more!"}
                 {:org_id 3
                  :user_id 1
                  :title "Offer more options for vegan crossfitters."
                  :text "I know it doesn't fit the theme, but I have expectations."}
                 {:org_id 3
                  :user_id 2
                  :title "The tables should be cleaner when newly seated."
                  :text "Whoever is cleaning tables seems to only wipe them down once! Not very hygienic."}
                 ])

;;There needs be a matching vote seed for every issue seed
(def vote_seed [{:user_id 1
                 :issue_id 1
                 :org_id 1}
                {:user_id 2
                 :issue_id 2
                 :org_id 1}
                {:user_id 1
                 :issue_id 3
                 :org_id 2}
                {:user_id 2
                 :issue_id 4
                 :org_id 2}
                {:user_id 1
                 :issue_id 5
                 :org_id 3}
                {:user_id 2
                 :issue_id 6
                 :org_id 3}
                 ])

(def user_seed [{:email "cjcollazo@sigil.tech"
                 :username "Nerve"
                 :pass_hash (buddy.hashers/encrypt "Sigiltech1027!")
                 :icon_100 (str (rand-nth db/default_icon_100))
                 :roles [1 2]
                 :org_id 1}
                {:email "dominiccox@sigil.tech"
                 :username "Dominic"
                 :pass_hash (buddy.hashers/encrypt "323232")
                 :icon_100 (str (rand-nth db/default_icon_100))
                 :roles [1 2]
                 :org_id 1}
                {:email "kicrowley@gmail.com"
                 :username "Matt"
                 :pass_hash (buddy.hashers/encrypt "crowleys")
                 :icon_100 "/db_imgs/org/crowleys_100.png"
                 :roles [1]
                 :org_id 2}
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
    (doall (map #(db/db-trans [create-role %]) role_seed))
    (doall (map #(db/db-trans [create-user %]) user_seed))
    (doall (map #(db/db-trans [create-tag %]) tag_seed))
    (doall (map #(db/db-trans [create-issue %]) issue_seed))
    (doall (map #(db/db-trans [create-comment %]) comment_seed))
    (doall (map #(db/db-trans [create-topic %]) topic_seed))
    (doall (map #(db/db-trans [create-vote %]) vote_seed))))

(defn drop-create-seed
  []
  (do
    (migrate/drop-create-db)
    ;(migrate/drop-db-tables)
    (migrate/create-db-tables)
    (seed-db)))
