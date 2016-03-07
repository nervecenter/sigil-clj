(ns sigil.actions.search
  (:require [sigil.helpers :refer [search-all]]))



(defn auto-complete-search
  [req]
  (let [matched (search-all (:term (:route-params req)))]
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
