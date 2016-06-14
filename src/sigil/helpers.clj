(ns sigil.helpers
  (:require [sigil.db.orgs :as orgs]
            [sigil.db.topics :as topics]
            [sigil.db.tags :as tags]
            [sigil.db.issues :as issues]
            [sigil.db.users :as users]
            [sigil.db.notifications :as notifications]
            [sigil.db.core :as db]
            [sigil.db.comments :as comments]
            [sigil.db.votes :as votes]
            [clojure.string :as str]
            [clj-time.jdbc]
            [clj-time.local :as local-time]
            [clj-time.core :as time]))

(defn get-return [req]
  (if (some? ((:query-params req) "return"))
    ((:query-params req) "return")
    "/"))

(defn redirect [location]
  {:status 302
   :headers {"Location" location}})

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


(defn get-all-users-of-issue
  "Gets the users associated with an issue who voted or commented."
  [issue]
  (let [users-voted (votes/get-users-who-voted-issue issue)
        users-commented (comments/get-users-by-issue-comments issue)]
    (clojure.set/union users-voted users-commented)))

(defn create-note
  [msg url icon too]
  (hash-map :to_user_id (:user_id too)
            :note_message msg
            :url url
            :icon icon))

(defn create-notes
  "Creates a vector of notification maps to be inserted."
  ([msg url icon too]
   (map #(hash-map :to_user_id (:user_id %1)
                   :note_message msg
                   :url url
                   :icon icon) too)))



(defn notify
  [notes]
  (db/db-trans [notifications/create-notification (flatten notes)]))



(defn time-display
  [t]
  (let [time-diff (time/minus (local-time/local-now) t)]
    (cond
      (true) "")))


;; public static string TimeSince(DateTime datePosted) {
;;             DateTime now = DateTime.Now;
;;             TimeSpan since = now - datePosted;

;;             if ( since >= TimeSpan.FromDays( 365.0 ) ) {
;;                 int years = since.Days / 365;
;;                 return ( years > 1 ) ? years.ToString() + " years ago" : "1 year ago";
;;             } else if ( since >= TimeSpan.FromDays( 30.0 ) ) {
;;                 int months = since.Days / 30;
;;                 return ( months > 1 ) ? months.ToString() + " months ago" : "1 month ago";
;;             } else if ( since >= TimeSpan.FromDays( 1.0 ) ) {
;;                 return ( since.Days > 1 ) ? since.Days.ToString() + " days ago" : "1 day ago";
;;             } else if ( since >= TimeSpan.FromHours( 1.0 ) ) {
;;                 return ( since.Hours > 1 ) ? since.Hours.ToString() + " hours ago" : "1 hour ago";
;;             } else if ( since >= TimeSpan.FromMinutes( 1.0 ) ) {
;;                 return ( since.Minutes > 1 ) ? since.Minutes.ToString() + " minutes ago" : "1 minute ago";
;;             } else {
;;                 return "Less than a minute ago";
;;             }

;;         }
