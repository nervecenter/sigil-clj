(ns sigil.db.migrations
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db])
  (:use     [sigil.db.issues]
            [sigil.db.orgs]
            [sigil.db.users]
            [sigil.db.comments]
            [sigil.db.tags]
            [sigil.db.officialresponses]))


(defn create-db-tables
  []
  (sql/db-do-commands db/spec
                      (orgs_model)
                      (users_model)
                      (tags_model)
                      (issues_model)
                      (comment_model)
                      (official_response_model)
                      (db/error_model)))

