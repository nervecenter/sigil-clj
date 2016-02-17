(ns sigil.views.landing.logic
  (:require [sigil.views.landing.render :as r])
   (:use sigil.db.core))

(defn get-issues []
  (set (j/query db ["SELECT DISTINCT ON (issue_id) issues.title, users.display_name FROM issues LEFT JOIN users ON (issues.user_id = users.user_id);"])))

(defn landing-handler []
  ;; 1. Submit queries, wait for response data
  ;; 2. Check data
  ;; 3. Organize data if necessary
  ;; 4. Pass data to page render, return rendered page
  (page (get-issues)))
