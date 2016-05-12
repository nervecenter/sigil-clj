(ns sigil.actions.officialresponse
  (:require [sigil.db.officialresponses :refer [create-official-response]]
            [sigil.auth :as auth]
            [sigil.db.issues :refer [get-issue-by-id issue-set-responded]]
            [sigil.db.core :as db]
            [sigil.helpers :as help]
            [sigil.views.internal-error :refer [internal-error-handler]]))

(defn post-official-response
  [req]
  (let [user (auth/user-or-nil req)
        user-org (auth/user-org-or-nil user)
        new-official-data (:form-params req)
        issue (get-issue-by-id
               (read-string (new-official-data "issue-id")))
        return (str ((:headers req) "referer"))
        new-official {:user_id (:user_id user)
                      :issue_id (:issue_id issue)
                      :org_id (:org_id user-org)
                      :text (new-official-data "response")}]
    (if (= :success
           (db/db-trans [create-official-response new-official]
                        [issue-set-responded issue]))
      (do
        (help/notify
         (help/create-notes "An issue you have shown interest in has been responsed to."
                            return
                            (:icon_100 user)
                            (vec (help/get-all-users-of-issue issue))))
        {:status 302
         :headers {"Location" return}})
      (internal-error-handler req "There was an error uploading your official response."))))

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
