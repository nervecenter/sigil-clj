(ns sigil.views.landing.logic
  (:require [clojure.string :refer [join]]
            [sigil.views.landing.render :refer [page]]
            [clojure.java.jdbc :refer [query]]
            [sigil.db.core :refer [db]]))

(def get-issues-query
  (join " "
        ["SELECT"
         "DISTINCT ON (issue_id)"
         "issues.title, users.display_name"
         "FROM issues"
         "LEFT JOIN users"
         "ON (issues.user_id = users.user_id);"]))

(defn get-issues []
  (set (query db ["SELECT DISTINCT ON (issue_id) issues.issue_title, users.username FROM issues LEFT JOIN users ON (issues.user_id = users.user_id);"])))

(defn landing-handler []
  ;; 1. Submit queries, wait for response data
  ;; 2. Check data
  ;; 3. Organize data if necessary
  ;; 4. Pass data to page render, return rendered page
  (page (get-issues)))
