(ns sigil.actions.db
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.db.issues :as issues]
            [sigil.db.comments :as comments]
            [sigil.db.officialresponses :as official]
            [sigil.db.tags :as tags]
            [sigil.db.topics :as topics]
            [sigil.db.votes :as votes]))


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