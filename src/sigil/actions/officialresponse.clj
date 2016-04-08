(ns sigil.actions.officialresponse
  (:require [sigil.db.officialresponses :as offrep]
            [sigil.auth :as auth]
            [sigil.db.issues :as issues]
            [sigil.db.core :as db]
            [sigil.helpers :as help]))

(defn post-official-response
  [req]
  (let [new-official-data (:form-params req)
        user (auth/user-or-nil req)
        user-org (auth/user-org-or-nil user)
        issue (issues/get-issue-by-id (read-string (new-official-data "issue-id")))
        return (str ((:headers req) "referer"))
        new-official {:user_id (:user_id user)
                      :issue_id (:issue_id issue)
                      :org_id (:org_id user-org)
                      :text (new-official-data "response")}]
    (if (= :success
           (db/db-trans [offrep/create-official-response new-official]
                        [issues/issue-set-responded issue]))
      (do (help/notify (help/create-notes "An issue you have shown interest in has been responsed to." return (:icon_100 user) (vec (help/get-all-users-of-issue issue))))
        {:status 302
         :headers {"Location" return}}))))

(defn vote-helpful
  [req]
  ())

(defn unvote-helpful
  [req]
  ())

(defn vote-unhelpful
  [req]
  ())

(defn unvote-unhelpful
  [req]
  ())
