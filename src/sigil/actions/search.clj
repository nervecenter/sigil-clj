(ns sigil.actions.search
  (:require [sigil.helpers :refer [search-orgs-tags-topics]]
            [clojure.string :as str]
            [sigil.auth :refer [user-or-nil]]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :refer [get-org-by-id ;get-five-orgs-by-term
                                   get-all-orgs]]
            [sigil.db.tags :refer [get-tag-by-id get-tags-by-org]]
            [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.actions.fuzzy :as search]
            [cheshire.core :as json]
            [hiccup.core :refer [html]]))

(defn auto-complete-search
  [req]
  (let [all-orgs (get-all-orgs)
        term (:term (:params req))
        matched  (take 10
                   (reverse
                     (sort-by :score
                              (map #(hash-map :label (:org_name (:original %))
                                              :value (:org_url (:original %))
                                              :score (second (:result %)))
                                   (filter #(first (:result %))
                                                    ;(> (second (:result %)) 0)
                                           (pmap #(hash-map :result
                                                            (search/fuzzy-wuzzy term (:org_name %))
                                                            :original %)
                                                 all-orgs))))))]
    (json/generate-string matched)))


;;-------------------------------------------------
; Org_page search issues

(defn search-org-issues-handler
  [req]
  (let [user (user-or-nil req)
        org (get-org-by-id (read-string (:orgid (:params req))))
        tagid (read-string (:tagid (:params req)))
        term (:term (:params req))]
    (println (:tag_name (get-tag-by-id tagid)))
    (if (= term "")
      (let [all-issues (issues/get-issues-by-org org)]
          (reduce str (map #(html (issue-partial (str "/" (:org_url org))
                                                 %
                                                 user))
                           all-issues)))
      (let [org-issues (issues/get-issues-by-org org)
            matched-issues (filter #(first (:result %))
                                   (pmap #(hash-map :result (search/fuzzy-wuzzy term (:title %))
                                                    :original %)
                                         org-issues))
]
        (if (= 0 (count matched-issues))
          "<h3>No issues found that match your search.</h3>"
            (reduce str (map #(html (issue-partial (str "/" (:org_url org))
                                                   (:original %)
                                                   user))
                             matched-issues)))))))
