(ns sigil.actions.search
  (:require [sigil.helpers :refer [search-orgs-tags-topics]]
            [clojure.string :as str]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]))



(defn auto-complete-search
  [req]
  (let [matched (search-orgs-tags-topics (:term (:route-params req)))]
    (for [[k v] matched]
      (cond
        (= k :orgs) (for [org v]
                      {:label (:org_name org)
                       :value (:org_url org)})
        (= k :tags) (for [tag v]
                      {:label (:tag_name tag)
                       :value (:tag_url tag)})
        (= k :topics) (for [topic v]
                        {:label (:topic_name topic)
                         :value (:topic_url topic)})))))


;;-------------------------------------------------
; Org_page search issues

;;TODO:: Need to jsonify the return issues
(defn search-org-issues
  [req]
  (let [search-params (:route-params req)
        org (orgs/get-org-by-id (search-params :org-id))
        term (search-params :term)]
    (if (not= term "")
      (filter #(str/starts-with? (:title %) term) (issues/get-issues-by-org org))
      ())))
