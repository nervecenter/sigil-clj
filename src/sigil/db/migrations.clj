(ns sigil.db.migrations
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :refer [spec error_model]]
            [sigil.db.issues :refer [issues_model]]
            [sigil.db.orgs :refer [orgs_model]]
            [sigil.db.users :refer [users_model]]
            [sigil.db.comments :refer [comment_model]]
            [sigil.db.tags :refer [tags_model]]
            [sigil.db.votes :refer [votes_model]]
            ;[sigil.db.topics :refer [topics_model]]
            [sigil.db.officialresponses :refer [official_response_model]]
            [sigil.db.notifications :refer [notification_model]]
            [sigil.db.roles :refer [roles_model]]
            [sigil.db.reports :refer [reports_model]]
            [sigil.db.petitions :refer [petitions_model]]))

(declare drop-create-db create-db create-db-tables)

;(def spec {:subprotocol "postgresql"
;           :subname "//sigil-alpha-db.cjlqgk36ylxa.us-west-2.rds.amazonaws.com:5432/postgres"
;           :user "sigildbadmin"
;           :password "Sigiltech1027!"})

(defn drop-db []
  "Drops the DB on the spec's connection."
  (sql/with-db-connection [conn ;@spec
                           (assoc @spec
                                       :subname
                                       (clojure.string/replace (:subname @spec) #"/sigildb" "/postgres"))
                           ]
    (with-open [s (.createStatement (:connection conn))]
      (.addBatch s (str "drop database sigildb;"))
      (seq (.executeBatch s)))))

(defn create-db []
  "Creates the default 'sigildb' database with tables."
  (sql/with-db-connection [conn @spec]
    (with-open [s (.createStatement (:connection conn))]
      (.addBatch s (str "create database sigildb;"))
      (seq (.executeBatch s))))
  (create-db-tables))

(defn drop-create-db []
  "Drops and then creates the DB on the spec's connection."
  (sql/with-db-connection [conn (assoc @spec
                                       :subname
                                       (clojure.string/replace (:subname @spec) #"/sigildb" "/postgres"))]
    (with-open [s (.createStatement (:connection conn))]
      (.addBatch s (str "drop database sigildb;"))
      (.addBatch s (str "create database sigildb;"))
      (seq (.executeBatch s)))))

(defn create-db-tables
  []
  (sql/db-do-commands @spec
                      (orgs_model)
                      (users_model)
                      (tags_model)
                      (issues_model)
                      (comment_model)
                      (votes_model)
                      (official_response_model)
                      ;(topic_model)
                      (notification_model)
                      (roles_model)
                      (reports_model)
                      (petitions_model)
                      (error_model)))
