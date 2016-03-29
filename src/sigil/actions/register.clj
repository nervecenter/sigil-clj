(ns sigil.actions.register
  (:require [sigil.db.core :as db]
            [sigil.db.orgs :as orgs]
            [sigil.db.users :as users]))


;;---------------------------------
;; org_register_post

(defn register-org-and-admin
  [org admin]
  (if (= :success (db/db-trans [orgs/create-org (assoc org
                                                       :icon_20 (rand-nth db/default_icon_30)
                                                       :icon_100 (rand-nth db/default_icon_100)
                                                       :banner (rand-nth db/default_banner))]
                            [users/create-user (assoc admin :icon_100 (rand-nth db/default_icon_100))]))
    (let [userid (:user_id (users/get-user-by-email (:email admin)))
          orgid (:org_id (orgs/get-org-by-url (:org_url org)))]
      (db/db-trans [users/update-user  [userid {:org_id orgid}]]))))

;;-------------------------------------
; user-register-post

(defn register-user [user]
  (db/db-trans
   [users/create-user
    (assoc user :icon_100 (rand-nth db/default_icon_100))]))

