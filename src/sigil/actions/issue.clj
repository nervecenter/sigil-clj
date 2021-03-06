(ns sigil.actions.issue
  (:require [sigil.db.core :as db]
            [sigil.db.issues :as issues]
            [sigil.db.orgs :as orgs]
            [sigil.auth :as auth]
            [sigil.db.votes :as votes]
            [sigil.db.reports :as reports]
            [sigil.views.internal-error :refer [internal-error-handler]]))

(def not-nil? (complement nil?))

(defn add-issue-post
  [req]
  (let [new-issue-data (:form-params req)
        user (auth/user-or-nil req)
        issue-org (orgs/get-org-by-id (long (read-string (new-issue-data "org-id"))))
        new-issue {:title (new-issue-data "title")
                   :text (new-issue-data "text")
                   :tag_id (read-string (new-issue-data "tag"))
                   :user_id (:user_id user)
                   :org_id (:org_id issue-org)
                   ;;:tags [(long (read-string (new-issue-data "tag-select")))]
                   }]
    (if (= :success
           (do
             (db/db-trans [issues/create-issue new-issue])
             (let [created-issue (issues/get-issue-by-user-and-title user (:title new-issue))]
               (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                                :issue_id (:issue_id created-issue)
                                                :org_id (:org_id created-issue)}]))))
      {:status 302
       :headers {"Location" (str "/" (:org_url issue-org)
                                 "/" (:issue_id (issues/get-issue-by-user-and-title user (:title new-issue))))}}
      ;;else redirect and let them know whats wrong....
      (internal-error-handler req "Error uploading user's issue.")
      )))

(defn archive-issue-post
  [req]
  (let [issue-data (:form-params req)
        user (auth/user-or-nil req)
        issue-org (orgs/get-org-by-id (long (read-string (issue-data "org-id"))))
        issue-to-archive (issues/get-issue-by-id (read-string (issue-data "issue-id")))]
    (if (= :success (db/db-trans [issues/archive-issue issue-to-archive]))
      {:status 302
       :headers {"Location" ((:headers req) "referer")}}
      ;;else redirect and let them know whats wrong....
      (internal-error-handler req "Error archiving issue."))))


(defn vote-issue
  [req]
  (let [issue (issues/get-issue-by-id (read-string (:issue_id (:params req))))
        user (auth/user-or-nil req)]
    (if (not (votes/user-voted-on-issue? user issue))
      (do
        (db/db-trans [votes/create-vote {:user_id (:user_id user)
                                         :issue_id (:issue_id issue)
                                         :org_id (:org_id issue)}]
                     [issues/issue-voted issue])
        {:status 200})
      {:status 403})))

(defn unvote-issue
  [req]
  (let [issue (issues/get-issue-by-id (read-string (:issue_id (:params req))))
        user (auth/user-or-nil req)
        vote (votes/get-user-issue-vote user issue)]
    (if (not-nil? vote)
      (do
        (db/db-trans
         [votes/delete-vote vote]
         [issues/issue-unvoted issue])
        {:status 200})
      {:status 403})))


(defn report-issue
  [req]
  (let [issue (issues/get-issue-by-id (read-string (:issue_id (:params req))))
        user (auth/user-or-nil req)]
    (if (some? user)
      (if (not (reports/user-reported-issue? user issue))
        (do
          (db/db-trans [reports/create-report {:user_id (:user_id user)
                                               :issue_id (:issue_id issue)}])
          {:status 200})
      {:status 500
       :headers {"Content-Type" "text/plain"}
       :body "User tried to report issue; report already found."})
      {:status 500
       :headers {"Content-Type" "text/plain"}
       :body "No user logged in, can't report."})))

(defn unreport-issue
  [req]
  (let [issue (issues/get-issue-by-id (read-string (:issue_id (:params req))))
        user (auth/user-or-nil req)
        report (reports/get-report-by-user-and-issue user issue)]
    (if (some? report)
      (do
        (db/db-trans
         [reports/delete-report report])
        {:status 200})
      {:status 500
       :headers {"Content-Type" "text/plain"}
       :body "User tried to unreport issue; no report exists."})))
