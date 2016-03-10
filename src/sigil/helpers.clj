(ns sigil.helpers
  (:require [sigil.db.orgs :as orgs]
            [sigil.db.topics :as topics]
            [sigil.db.tags :as tags]
            [clojure.string :as str]))

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

(defn match-orgs-tags-topics
  [term]
  (let [matched-orgs (filter #(str/starts-with? (:org_name %) term) (orgs/get-all-orgs))
        matched-topics (filter #(str/starts-with? (:topic_name %) term) (topics/get-all-topics))
        matched-tags (filter #(str/starts-with? (:tag_name %) term) (tags/get-all-tags))]
    {:orgs matched-orgs
     :topics matched-topics
     :tags matched-tags}))
(defn user-is-org-admin? [user]
  (and (user-has-role? user :org-admin) (not= 0 (:org_id user))))

(defn user-is-admin-of-org? [user org]
  (and (user-has-role? user :org-admin) (= (:org_id org) (:org_id user))))
