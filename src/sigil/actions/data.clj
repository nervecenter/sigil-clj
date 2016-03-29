(ns sigil.actions.data
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]
            [clj-time.jdbc]
            [clj-time.core :as time]
            [cheshire.core :as json])
  (:use [hiccup.core]))


;; (defn get-chart-data-by-org
;;   [org start stop]
;;   (charts/create-org-data-chart org "Test Title" "Time" "Views" start stop :last_viewed))


(defn views-chart
  [org start stop]
  (map #(hash-map :viewDate (clj-time.coerce/to-long %)
                  :viewCount 1) (filter #(time/within?
                                        (clj-time.coerce/from-long start)
                                        (clj-time.core/plus
                                         (clj-time.coerce/from-long stop)
                                         (clj-time.core/days 1))
                                        (clj-time.coerce/from-sql-time %))
                                    (:views org))))


(defn default-org-chart
  [req]
  (let [org (orgs/get-org-by-url (:org (:params req)))
        start-time (time/minus (time/now) (time/days 7))
        stop-time (time/now)]
    ;(println (views-chart org start-time stop-time))
    (json/generate-string (views-chart org start-time stop-time))))

(defn custom-org-chart
  [req]
  (let [org (orgs/get-org-by-url (:org (:params req)))
        data-tag (:tag (:params req))
        start-time (read-string (:start (:params req)))
        stop-time (read-string (:stop (:params req)))]
    (cond
      (= data-tag "Views") (json/generate-string (views-chart org start-time stop-time)))))


(def sort-by-views-to-votes-ratio (partial sort-by #(/ (count (:views %)) (:total_votes %))))

(defn get-top-issues-by-org
  "Top issues are defined as the issues that have the best views to votes ratio"
  ([org start stop] (get-top-issues-by-org org start stop 10))
  ([org start stop num]
   (let [org-issues (issues/get-issues-by-org org)]
     (sort-by-views-to-votes-ratio (filter #(time/within? (time/interval start stop) (:created_at %)) org-issues))
     )))

(defn get-top-unresponded-issues-by-org
  ([org start stop] (get-top-unresponded-issues-by-org org start stop 10))
  ([org start stop num]
   (let [org-issues (issues/get-issues-by-org org)]
     (sort-by-views-to-votes-ratio (filter #(and (not (:responded %))
                                                 (time/within? (time/interval start stop) (:created_at %)))
                                           org-issues)))))
;;TODO -- NEED TO FIGURE OUT WHAT RISING REALLY MEANS
(defn get-top-rising-issues-by-org
  ([org start stop] (get-top-rising-issues-by-org org start stop 10))
  ([org start stop num]
   (let [org-issues (issues/get-issues-by-org org)]
     (sort-by-views-to-votes-ratio (filter #(time/within? (time/interval start stop) (:created_at %)) org-issues))
     )))

