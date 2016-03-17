(ns sigil.actions.db
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.db.issues :as issues]
            [sigil.db.comments :as comments]
            [sigil.db.officialresponses :as official]
            [sigil.db.tags :as tags]
            [sigil.db.topics :as topics]
            [sigil.db.votes :as votes]
            [sigil.db.notifications :as notes])
  (:use [sigil.auth]
        [hiccup.form]))


(def not-nil? (complement nil?))

;;----------------------------------
;; user_register_post

(defn register-user [user]
  (db/db-trans
   [users/create-user
    (assoc user :icon_100 (rand-nth orgs/default_org_icon_100))]))


;;---------------------------------
;; org_register_post

(defn register-org-and-admin
  [org admin]
  (if (= :success (db/db-trans [orgs/create-org (assoc org
                                                       :icon_20 (rand-nth orgs/default_org_icon_20)
                                                       :icon_100 (rand-nth orgs/default_org_icon_100)
                                                       :banner (rand-nth orgs/default_org_banner))]
                            [users/create-user (assoc admin :icon_100 (rand-nth orgs/default_org_icon_100))]))
    (let [userid (:user_id (users/get-user-by-email (:email admin)))
          orgid (:org_id (orgs/get-org-by-url (:org_url org)))]
      (db/db-trans [users/update-user  [userid {:org_id orgid}]]))))



;;----------------------------------------
;; notification GETs

(defn get-number-user-notifications
  [req]
  (let [user (user-or-nil req)]
    (if (not-nil? user)
      (count (notes/get-user-notifications user))
      0)))


;;TODO:: Need to jsonify the return of notifications
(defn get-user-notifications
  [req]
  (let [user (user-or-nil req)]
    (if (not-nil? user)
      (notes/get-user-notifications user)
      [])))



;;----------------------------------------
; Add-issue-post

;;TODO::Validate new issue and add redirect on fail
(defn add-issue-post
  [req]
  (let [new-issue-data (:form-params req)
        return (new-issue-data "return")
        new-issue (zipmap [:org_id :user_id :title :text]
                          (map #(new-issue-data %) ["org-id" "user-id" "title" "text"]))]
    (if (= :success (db/db-trans [issues/create-issue new-issue]))
      {:status 302
       :headers {"Location" (str "return=" return "/" (issues/get-issue-insert-id new-issue))}}
      ;;else redirect and let them know whats wrong....
      )))


