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

(def ipsum "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer tristique sagittis purus a mollis. Vestibulum non consectetur arcu. Aliquam ultricies, ex at cursus dictum, ex quam aliquam metus, eget mollis leo quam vel est. Nulla ac pharetra est. Aliquam sit amet gravida turpis. Donec vulputate pellentesque lectus sit amet cursus. Aenean pulvinar ex nec placerat varius. Aliquam erat volutpat. Nulla a tempor neque. Vestibulum a mattis nibh.")

(def org-seed-dev [{;:org_id 1
                :org_url "sigil"
                :org_name "Sigil"
                :website "sigil.tech"
                :address "10707 Ayrshire Dr"
                :city "Tampa"
                :state "FL"
                :zip_codes [33626]
                :phone "813-334-3699"
                :icon_30 "/db_imgs/org/sigil_30.png"
                :icon_100 "/db_imgs/org/sigil_100.png"
                :banner "/db_imgs/org/sigil_banner.png"}
               {;:org_id 2
                :org_url "usf"
                :org_name "University of South Florida"
                :website "www.usf.edu"
                :address "4202 E Fowler Ave"
                :city "Tampa"
                :state "FL"
                :zip_codes [33620]
                :phone "813-974-2011"
                :icon_30 "/db_imgs/org/usf_30.png"
                :icon_100 "/db_imgs/org/usf_100.png"
                :banner "/db_imgs/org/usf_banner.jpg"}
               {;:org_id 3
                :org_url "hillsborough"
                :org_name "Hillsborough County"
                :website "www.hillsboroughcounty.org"
                :address "601 E. Kennedy Blvd."
                :city "Tampa"
                :state "FL"
                :zip_codes [33602]
                :phone "813-272-5900"
                :icon_30 "/db_imgs/org/hillsborough_30.png"
                :icon_100 "/db_imgs/org/hillsborough_100.png"
                :banner "/db_imgs/org/hillsborough_banner.png"}
               {;:org_id 4
                :org_url "tampa"
                :org_name "City of Tampa"
                :website "www.tampagov.net"
                :address "306 E. Jackson St."
                :city "Tampa"
                :state "FL"
                :zip_codes [33602]
                :phone "813-274-8211"
                :icon_30 "/db_imgs/org/tampa_30.png"
                :icon_100 "/db_imgs/org/tampa_100.png"
                :banner "/db_imgs/org/tampa_banner.jpg"}
               {;:org_id 5
                :org_url "gusbilirakis"
                :org_name "Congressman Gus Bilirakis, Florida's 12th Congressional District"
                :website "bilirakis.house.gov"
                :address "600 Klosterman Rd. Room BB-038"
                :city "Tarpon Springs"
                :state "FL"
                :zip_codes [34689]
                :phone "727-940-5860"
                :icon_30 "/db_imgs/org/gusbilirakis_30.png"
                :icon_100 "/db_imgs/org/gusbilirakis_100.png"
                :banner "/db_imgs/org/gusbilirakis_banner.png"}
               ])

(def role-seed [{:role_name "org-admin"}
                {:role_name "site-admin"}])

(def tag-seed-dev [{;:tag_id 0
                :tag_name "DEFAULT TAG"
                :org_id 1
                :icon_30 (rand-nth db/default_icon_30)}
               ;; USF
               {;:tag_id 1
                :tag_name "Groundskeeping"
                :org_id 2
                :icon_30 (rand-nth db/default_icon_30)}
               {;:tag_id 2
                :tag_name "College of Engineering"
                :org_id 2
                :icon_30 (rand-nth db/default_icon_30)}
               {;:tag_id 5
                :tag_name "Library"
                :org_id 2
                :icon_30 (rand-nth db/default_icon_30)}
               ;; Hillsborough
               {;:tag_id 3
                :tag_name "Transportation & Roads"
                :org_id 3
                :icon_30 (rand-nth db/default_icon_30)}
               {;:tag_id 4
                :tag_name "Education & Schools"
                :org_id 3
                :icon_30 (rand-nth db/default_icon_30)}
               {;:tag_id 10
                :tag_name "Water & Maritime"
                :org_id 3
                :icon_30 (rand-nth db/default_icon_30)}
               ;; Tampa
               {;:tag_id 6
                :tag_name "Transportation & Roads"
                :org_id 4
                :icon_30 (rand-nth db/default_icon_30)}
               {;:tag_id 7
                :tag_name "Utilities"
                :org_id 4
                :icon_30 (rand-nth db/default_icon_30)}
               ;; Gus Bilirakis
               {;:tag_id 8
                :tag_name "House Bills & Votes"
                :org_id 5
                :icon_30 (rand-nth db/default_icon_30)}
               {;:tag_id 9
                :tag_name "Federal Programs"
                :org_id 5
                :icon_30 (rand-nth db/default_icon_30)}
               ])

(def issue-seed-dev [;; Sigil
                 {:org_id 1
                  :user_id 1
                  :title "I need a button that gives me bacon."
                  :text "The previous buttons all lied to me."}
                 {:org_id 1
                  :user_id 2
                  :title "I need a button that prints money."
                  :text "Doesn't grow on trees! Lorem ipsum etc."}
                 ;; USF
                 {:org_id 2
                  :user_id 1
                  :tag_id 1
                  :total_votes 397
                  :title "Build an additional sidewalk crossing the grass east of Engineering from the bus stop."
                  :text ipsum}
                 {:org_id 2
                  :user_id 2
                  :tag_id 2
                  :total_votes 578
                  :title "Engage in more engineering competitions such as robotics and computer vision."
                  :text ipsum}
                 {:org_id 2
                  :user_id 2
                  :tag_id 5
                  :total_votes 275
                  :title "Install programming tools such as Visual Studio on the Digital Media Commons computers."
                  :text ipsum}
                 ;; Hillsborough
                 {:org_id 3
                  :user_id 1
                  :tag_id 3
                  :total_votes 1664
                  :title "Hillsborough should prioritize welcoming a self-driving taxi fleet to act as public transit."
                  :text ipsum}
                 {:org_id 3
                  :user_id 2
                  :tag_id 4
                  :total_votes 2802
                  :title "Schools should be incentivized according to what children learn, rather than arbitrary grade requirements."
                  :text ipsum}
                 {:org_id 3
                  :user_id 3
                  :tag_id 10
                  :total_votes 1490
                  :title "Will motors be allowed in the new fishing area on the Hillsborough River?"
                  :text ipsum}
                 ;; Tampa
                 {:org_id 4
                  :user_id 1
                  :tag_id 6
                  :total_votes 3756
                  :title "Place signs downtown and on route to it which warn of high traffic due to events."
                  :text ipsum}
                 {:org_id 4
                  :user_id 2
                  :tag_id 7
                  :total_votes 4200
                  :title "Trash pickup needs to happen farther in from the curb, bins fall in the street too often."
                  :text ipsum}
                 ;; Gus Bilirakis
                 {:org_id 5
                  :user_id 1
                  :tag_id 8
                  :total_votes 3797
                  :title "Vote no on government warrantless spy program extensions, and please find a way to repeal existing programs."
                  :text ipsum}
                 {:org_id 5
                  :user_id 2
                  :tag_id 9
                  :total_votes 3031
                  :title "Refuse funding from federal programs which strong-arm state and district policy."
                  :text ipsum}
                 ])

(def comment-seed-dev
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
    :text "I think they should pay for it if they really want it."}
   ;; USF sidewalk issue
   {:issue_id 3
    :user_id 1
    :text "Yes! Take it from the bus stop to the parking lot at the south of engineering."}
   ;; USF engineering issue
   {:issue_id 4
    :user_id 2
    :text "Maybe USF should help find funding for winners, and endorse them."}
   ;; USF programming tools issue
   {:issue_id 5
    :user_id 1
    :text "Please install Emacs, Java, and Clojure. They're the most useful to me."}
   {:issue_id 5
    :user_id 2
    :text "I'd like a copy of the Godot Engine installed if it's possible."}
   ])

;;There needs be a matching vote seed for every issue seed
(def vote-seed-dev [{:user_id 1
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
                 :org_id 2}
                {:user_id 2
                 :issue_id 6
                 :org_id 3}
                {:user_id 1
                 :issue_id 7
                 :org_id 3}
                {:user_id 2
                 :issue_id 8
                 :org_id 3}
                {:user_id 1
                 :issue_id 9
                 :org_id 4}
                {:user_id 2
                 :issue_id 10
                 :org_id 4}
                {:user_id 1
                 :issue_id 11
                 :org_id 5}
                {:user_id 2
                 :issue_id 12
                 :org_id 5}
                 ])

(def user-seed-dev [{:email "cjcollazo@sigil.tech"
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
                {:email "pr@usf.edu"
                 :username "USF-PR"
                 :pass_hash (buddy.hashers/encrypt "gobulls")
                 :icon_100 "/db_imgs/org/usf_100.png"
                 :roles [1]
                 :org_id 2}
                {:email "visitor@usf.edu"
                 :username "Visitor"
                 :pass_hash (buddy.hashers/encrypt "visitor")
                 :icon_100 (str (rand-nth db/default_icon_100))}
                {:email "pr@hillsboroughcounty.org"
                 :username "Hillsborough-PR"
                 :pass_hash (buddy.hashers/encrypt "hillsborough")
                 :icon_100 "/db_imgs/org/hillsborough_100.png"
                 :roles [1]
                 :org_id 3}
                ])

;(def topic-seed [{:topic_url "testtopic"
;                  :topic_name "TestTopic"
;                  :banner (str (rand-nth db/default_banner))}])

;; rofl
(assert (= (count issue-seed-dev) (count vote-seed-dev)) "EACH ISSUE NEEDS A VOTE YOU DUMMY")

;; DEV Functions

(defn seed-orgs-dev
  []
  (apply #(db/db-trans [create-org %]) org-seed-dev))

(defn seed-db-dev
  []
  (do
    (doall (map #(db/db-trans [create-org %]) org-seed-dev))
    (doall (map #(db/db-trans [create-role %]) role-seed))
    (doall (map #(db/db-trans [create-user %]) user-seed-dev))
    (doall (map #(db/db-trans [create-tag %]) tag-seed-dev))
    (doall (map #(db/db-trans [create-issue %]) issue-seed-dev))
    (doall (map #(db/db-trans [create-comment %]) comment-seed-dev))
    ;(doall (map #(db/db-trans [create-topic %]) topic_seed))
    (doall (map #(db/db-trans [create-vote %]) vote-seed-dev))))

(defn rebase-db-dev
  "Drops live database, creates new database, creates tables and then seeds with live data. "
  []
  (try
    (do
      (migrate/drop-create-db)
      (.println System/out "Db droped and Created.")
      (migrate/create-db-tables)
      (.println System/out "Tables created.")
      (seed-db-dev)
      (.println System/out "Db Seeded"))
    (catch Exception e (.getNextException e))))

;; LIVE Functions

(def org-seed-live [{;:org_id 1
                :org_url "sigil"
                :org_name "Sigil"
                :website "sigil.tech"
                :address "10707 Ayrshire Dr"
                :city "Tampa"
                :state "FL"
                :zip_codes [33626]
                :phone "813-334-3699"
                :icon_30 "/db_imgs/org/sigil_30.png"
                :icon_100 "/db_imgs/org/sigil_100.png"
                :banner "/db_imgs/org/sigil_banner.png"}
               ])

(def tag-seed-live [{:tag_name "Bugs"
                     :org_id 1
                     :icon_30 (rand-nth db/default_icon_30)}])

(def user-seed-live [{:email "cjcollazo@sigil.tech"
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
                      :org_id 1}])

(defn seed-db-live
  "Seeds db with live site settings."
  []
  (do
    (doall (map #(db/db-trans [create-org %]) org-seed-live))
    (doall (map #(db/db-trans [create-role %]) role-seed))
    (doall (map #(db/db-trans [create-user %]) user-seed-live))
    (doall (map #(db/db-trans [create-tag %]) tag-seed-live))))

(defn fill-existing-db-live
  "Creates db tables in db and seeds with live data"
  []
  (do
    (migrate/create-db-tables)
    (seed-db-live)))

(defn rebase-db-live
  "Drops live database, creates new database, creates tables and then seeds with live data. "
  []
  (do
    (migrate/drop-create-db)
    (migrate/create-db-tables)
    (seed-db-live)))

;(defn create-and-seed
;  []
;  (do
;    (migrate/create-db)
;    (seed-db)))
