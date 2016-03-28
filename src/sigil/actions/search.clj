(ns sigil.actions.search
  (:require [sigil.helpers :refer [search-orgs-tags-topics]]
            [clojure.string :as str]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :refer [get-org-by-id get-five-orgs-by-term]]
            [cheshire.core :as json]))

(defn auto-complete-search
  [req]
  (let [matched (get-five-orgs-by-term (:term (:query-params req)))]
    (println matched)
    (let [sorted (sort-by :leven (map #(hash-map :label (:org_name %)
                                                 :value (:org_url %)
                                                 :leven (:levenshtein %))
                                      matched))]
      (println sorted)
      (json/generate-string sorted))))

;; (for [org matched
;;       i (range 0 5)]
;;   {:label (:org_name org)
;;    :value (:org_url org)
;;    :index i}))))

;; Old matching scheme
;; (for [[k v] matched]
;;   (cond
;;     (= k :orgs) (for [org matched]
;;                   {:label (:org_name org)
;;                    :value (:org_url org)})
;;     (= k :tags) (for [tag v]
;;                   {:label (:tag_name tag)
;;                    :value (:tag_url tag)})
;;     (= k :topics) (for [topic v]
;;                     {:label (:topic_name topic)
;;                      :value (:topic_url topic)})))

;;-------------------------------------------------
; Org_page search issues

;;TODO:: Need to jsonify the return issues
(defn search-org-issues
  [req]
  (let [search-params (:route-params req)
        org (get-org-by-id (search-params :org-id))
        term (search-params :term)]
    (if (not= term "")
      (filter #(str/starts-with? (:title %) term) (issues/get-issues-by-org org))
      ())))
