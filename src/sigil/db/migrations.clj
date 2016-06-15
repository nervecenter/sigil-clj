(ns sigil.db.migrations
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db])
  (:use     [sigil.db.issues]
            [sigil.db.orgs]
            [sigil.db.users]
            [sigil.db.comments]
            [sigil.db.tags]
            [sigil.db.votes]
            [sigil.db.topics]
            [sigil.db.officialresponses]
            [sigil.db.notifications]
            [sigil.db.roles]
            [sigil.db.reports]
            [sigil.db.petitions]))


(defn create-db-tables
  []
  (sql/db-do-commands db/spec
                      (orgs_model)
                      (users_model)
                      (tags_model)
                      (issues_model)
                      (comment_model)
                      (votes_model)
                      (official_response_model)
                      (topic_model)
                      (notification_model)
                      (roles_model)
                      (reports_model)
                      (petitions_model)
                      (db/error_model)))


(def spec {:subprotocol "postgresql"
           :subname "//sigil-alpha-db.cjlqgk36ylxa.us-west-2.rds.amazonaws.com:5432/postgres"
           :user "sigildbadmin"
           :password "Sigiltech1027!"})

(defn drop-create-db []
  (sql/with-db-connection [conn spec]
    (with-open [s (.createStatement (:connection conn))]
      (.addBatch s (str "drop database sigildb;"))
      (.addBatch s (str "create database sigildb;"))
      (seq (.executeBatch s)))))

(defn create-db []
  "Creates the default 'sigildb' database."
  (sql/with-db-connection [conn spec]
    (with-open [s (.createStatement (:connection conn))]
      (.addBatch s (str "create database sigildb;"))
      (seq (.executeBatch s))))
  (create-db-tables))
