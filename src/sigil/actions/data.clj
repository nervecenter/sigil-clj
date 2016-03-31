(ns sigil.actions.data
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]
            [sigil.db.votes :as votes]
            [sigil.db.comments :as comments]
            [clj-time.jdbc]
            [clj-time.core :as time]
            [clj-time.periodic :as ptime]
            [cheshire.core :as json]
            [sigil.auth :as auth]
            [sigil.views.partials.issue :refer [issue-partial]])
  (:use [hiccup.core]))


;; (defn get-chart-data-by-org
;;   [org start stop]
;;   (charts/create-org-data-chart org "Test Title" "Time" "Views" start stop :last_viewed))


(defn date-formula
  "Used to covert dates into 'days' for comparison. http://mathforum.org/library/drmath/view/66857.html"
  [d]
  (if (< (time/month d) 3)
    (let [year (- (time/year d) 1)
          month (+ (time/month d) 12)]
      (+ (- (+ (* 365 year) (Math/floor (/ year 4))) (/ year 100)) (Math/floor (/ year 400)) (time/day d) (Math/floor (/ (+ (* 153 month) 8) 5))))
    (let [year (time/year d)
          month (time/month d)]
      (+ (- (+ (* 365 year) (Math/floor (/ year 4))) (/ year 100)) (Math/floor (/ year 400)) (time/day d) (Math/floor (/ (+ (* 153 month) 8) 5))))))


(defn diff-between-dates
  "Returns the difference between two dates, a and b, where a < b, in days. "
  [a b]
  (- (date-formula b) (date-formula a)))


(defn strip-time
  [d]
  (time/date-time (time/year d) (time/month d) (time/day d)))


(defn views-chart
  [org start stop]
  (let [start-date (clj-time.coerce/from-long start)
        end-date (clj-time.core/plus (clj-time.coerce/from-long stop) (clj-time.core/days 2)) ;;need to add day to end to make inclusive.
        num-days (diff-between-dates start-date end-date)
        time-period (take num-days (ptime/periodic-seq start-date (time/days 1)))]
    (map #(hash-map :viewDate (first (first %))
                    :viewCount (second (first %)))
         (flatten (conj (flatten (map #(hash-map (clj-time.coerce/to-long (strip-time %)) 0) time-period))

                        
                        (frequencies (map #(clj-time.coerce/to-long (strip-time
                                                                     (clj-time.coerce/to-date-time  %)))
                                          (filter #(time/within?
                                                    start-date
                                                    end-date
                                                    (clj-time.coerce/from-sql-time %))
                                                  (:views org)))))))))


(defn votes-chart
  [org start stop]
  (let [start-date (clj-time.coerce/from-long start)
        end-date (clj-time.core/plus (clj-time.coerce/from-long stop) (clj-time.core/days 2)) ;;need to add day to end to make inclusive.
        num-days (diff-between-dates start-date end-date)
        time-period (take num-days (ptime/periodic-seq start-date (time/days 1)))
        org-votes (votes/get-votes-by-org org)]
    (map #(hash-map :viewDate (first (first %))
                    :viewCount (second (first %)))
         (flatten (conj (flatten (map #(hash-map (clj-time.coerce/to-long (strip-time %)) 0) time-period))

                        
                        (frequencies (map #(clj-time.coerce/to-long (strip-time
                                                                     (clj-time.coerce/to-date-time (:created_at %))))
                                          (filter #(time/within?
                                                    start-date
                                                    end-date
                                                    (:created_at %))
                                                  org-votes))))))))

(defn comment-chart
  [org start stop]
  (let [start-date (clj-time.coerce/from-long start)
        end-date (clj-time.core/plus (clj-time.coerce/from-long stop) (clj-time.core/days 2)) ;;need to add day to end to make inclusive.
        num-days (diff-between-dates start-date end-date)
        time-period (take num-days (ptime/periodic-seq start-date (time/days 1)))
        org-comments (comments/get-org-comments org)]
    (map #(hash-map :viewDate (first (first %))
                    :viewCount (second (first %)))
         (flatten (conj (flatten (map #(hash-map (clj-time.coerce/to-long (strip-time %)) 0) time-period))

                        
                        (frequencies (map #(clj-time.coerce/to-long (strip-time
                                                                     (clj-time.coerce/to-date-time (:created_at %))))
                                          (filter #(time/within?
                                                    start-date
                                                    end-date
                                                    (:created_at %))
                                                  org-comments))))))))

(def sort-by-views-to-votes-ratio (partial sort-by #(/ (count (:views %)) (:total_votes %))))

(defn get-top-issues-by-org
  "Top issues are defined as the issues that have the best views to votes ratio"
  ([org start stop] (get-top-issues-by-org org start stop 10))
  ([org start stop num]
   (let [org-issues (issues/get-issues-by-org org)]
     (sort-by-views-to-votes-ratio (filter #(time/within? (time/interval start stop) (:created_at %)) org-issues)))))

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
      (= data-tag "Views") (json/generate-string (views-chart org start-time stop-time))
      (= data-tag "Votes") (json/generate-string (votes-chart org start-time stop-time))
      (= data-tag "Comments") (json/generate-string (comment-chart org start-time stop-time)))))



(defn custom-top-issues
  [req]
  (let [org (orgs/get-org-by-url (:org (:params req)))
        start-time (clj-time.coerce/from-long (read-string (:start (:params req))))
        stop-time (time/plus (clj-time.coerce/from-long (read-string (:stop (:params req)))) (time/days 1))
        user (auth/user-or-nil req)]
    (html [:div (map #(issue-partial (:uri req) % user true) (get-top-issues-by-org org start-time stop-time))])))

(defn custom-unresponded-issues
  [req]
  (let [org (orgs/get-org-by-url (:org (:params req)))
        start-time (clj-time.coerce/from-long (read-string (:start (:params req))))
        stop-time (time/plus (clj-time.coerce/from-long (read-string (:stop (:params req)))) (time/days 1))
        user (auth/user-or-nil req)]
   (html [:div (map #(issue-partial (:uri req) % user true) (get-top-unresponded-issues-by-org org start-time stop-time))])))

(defn custom-underdog-issues
  [req]
  (let [org (orgs/get-org-by-url (:org (:params req)))
        start-time (clj-time.coerce/from-long (read-string (:start (:params req))))
        stop-time (time/plus (clj-time.coerce/from-long (read-string (:stop (:params req)))) (time/days 1))
        user (auth/user-or-nil req)]
   (html [:div (map #(issue-partial (:uri req) % user true) (get-top-rising-issues-by-org org start-time stop-time))])))
