(ns sigil.actions.data
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]
            [clj-time.jdbc]
            [clj-time.core :as time])
  (:use [hiccup.core]))


(defn get-chart-data-by-org
  [org start stop]
  [:svg {:height 500
         :width 500
         :id "graph"
         :style {:outline "2px solid black"
                 :background-color "#fff"}}
   [:rect {:x 10
           :y 10
           :height 20
           :width 20
           :key 1
           :style {:fill "black" :stroke "black" :stroke-width 1}}]
   ])


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

