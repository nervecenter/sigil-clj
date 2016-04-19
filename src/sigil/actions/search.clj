(ns sigil.actions.search
  (:require [sigil.helpers :refer [search-orgs-tags-topics]]
            [clojure.string :as str]
            [sigil.auth :refer [user-or-nil]]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :refer [get-org-by-id get-five-orgs-by-term]]
            [sigil.views.partials.issue :refer [issue-partial]]
            [cheshire.core :as json]
            [hiccup.core :refer [html]]))

(defn auto-complete-search
  [req]
  (let [matched (get-five-orgs-by-term (:term (:query-params req)))]
    ;;(println matched)
    (let [sorted (sort-by :leven (map #(hash-map :label (:org_name %)
                                                 :value (:org_url %)
                                                 :leven (:levenshtein %))
                                      matched))]
      ;;(println sorted)
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

(defn search-org-issues-handler
  [req]
  (let [user (user-or-nil req)
        org (get-org-by-id (read-string (:id (:params req))))
        term (:term (:params req))]
    (if (= term "")
      (let [all-issues (issues/get-issues-by-org org)]
          (reduce str (map #(html (issue-partial (str "/" (:org_url org))
                                                 %
                                                 user
                                                 true))
                           all-issues)))
      (let [matched-issues (filter #(str/starts-with? (:title %) term)
                                   (issues/get-issues-by-org org))]
        (if (= 0 (count matched-issues))
          "<h3>No issues found that match your search.</h3>"
          (reduce str (map #(html (issue-partial (str "/" (:org_url org))
                                           %
                                           user
                                           true))
                           matched-issues)))))))
