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
            [sigil.db.roles]))


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
                      (db/error_model)))


(defn drop-db-tables
  []
  (sql/db-do-commands db/spec
                      (sql/drop-table-ddl :orgs)
                      (sql/drop-table-ddl :users)
                      (sql/drop-table-ddl :tags)
                      (sql/drop-table-ddl :issues)
                      (sql/drop-table-ddl :comments)
                      (sql/drop-table-ddl :official_responses)
                      (sql/drop-table-ddl :errors)
                      (sql/drop-table-ddl :topics)
                      (sql/drop-table-ddl :notifications)
                      (sql/drop-table-ddl :roles)
                      (sql/drop-table-ddl :votes)))
