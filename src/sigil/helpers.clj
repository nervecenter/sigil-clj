(ns sigil.helpers
  (:require [sigil.db.orgs :as orgs]
            [sigil.db.topics :as topics]
            [sigil.db.tags :as tags]
            [sigil.db.issues :as issues]
            [sigil.db.users :as users]
            [clojure.string :as str]))

(defn get-return [req]
  (if (some? ((:query-params req) "return"))
    ((:query-params req) "return")
    "/"))

(defn search-orgs-tags-topics
  [term]
  (let [matched-orgs (filter #(str/starts-with? (:org_name %) term) (orgs/get-all-orgs))
        matched-topics (filter #(str/starts-with? (:topic_name %) term) (topics/get-all-topics))
        matched-tags (filter #(str/starts-with? (:tag_name %) term) (tags/get-all-tags))]
    {:orgs matched-orgs
     :topics matched-topics
     :tags matched-tags}))

(defn user-favorites-or-nil
  [user]
  (if (nil? (:user_id user))
    nil
    (map #(orgs/get-org-by-id %) (:favorites (users/get-user-favorites (:user_id user))))))


(defn get-issue-with-user-and-org-by-issue-id
  [issue_id]
  (let [issue (issues/get-issue-by-id (read-string issue_id))
        user (users/get-user-by-id (:user_id issue))
        org (orgs/get-org-by-id (:org_id issue))
        ]
      [issue user org]))
