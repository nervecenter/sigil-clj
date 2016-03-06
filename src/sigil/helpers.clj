(ns sigil.helpers
  (:require [sigil.db.orgs :as orgs]))

(defn get-return [req]
  (if (some? ((:query-params req) "return"))
    ((:query-params req) "return")
    "/"))

(defn user-has-role? [user role]
  (cond
    (= role :org-admin) (if (contains? (:roles user) "org-admin")
                          true
                          false)
    (= role :site-admin) (if (contains? (:roles user) "site-admin")
                          true
                          false)))





(defn search-all
  [term]
  (let [orgs (orgs/search-orgs-by-term term)]
    orgs))


(def memo-search-all (memoize search-all))
