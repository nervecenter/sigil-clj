(ns sigil.auth
  (:require [clojure.java.jdbc :refer [query]]
            [sigil.db.orgs :as orgs]
            [sigil.db.votes :as votes]
            [sigil.db.roles :as roles]
            [buddy.sign.jwe :as jwe]
            [buddy.core.keys :refer [public-key private-key]]
            [sigil.db.users :refer [get-user-by-id]]))

(def pubkey (public-key "resources/private/pubkey.pem"))
(def privkey (private-key "resources/private/privkey.pem"))
(def encryption {:alg :rsa-oaep :enc :a128cbc-hs256})

(defn make-user-token [user]
  (jwe/encrypt {:user_id (:user_id user)} pubkey encryption))

(defn extract-user-id [req]
  (:user_id (jwe/decrypt (:value ((:cookies req) "user"))
                         privkey
                         encryption)))

(defn authenticated? [req]
  (if (contains? (:cookies req) "user")
    (if (map? (jwe/decrypt (:value ((:cookies req) "user"))
                           privkey
                           encryption))
      true
      false)
    false))

(defn user-identity [req]
  (get-user-by-id (extract-user-id req)))

(defn user-or-nil [req]
  (if (authenticated? req)
    (user-identity req)
    nil))

(defn user-org-or-nil [user]
  (if (not= (:org_id user) 0)
    (orgs/get-org-by-id (:org_id user))
    nil))

(defn user-has-role? [user role]
  (cond
    (= role :org-admin) (if (contains? (:roles user) (:role_id (roles/get-org-admin-role)))
                          true
                          false)
    (= role :site-admin) (if (contains? (:roles user) (:role_id (roles/get-org-admin-role)))
                           true
                           false)))

(defn user-is-org-admin? [user]
  ;;(and (user-has-role? user :org-admin)
  (not= 0 (:org_id user)))
;;)

(defn user-is-admin-of-org? [user org]
  (= (:org_id org) (:org_id user))
  ;;(and (user-has-role? user :org-admin))
  )

(defn is-user-site-admin? [user]
  (user-has-role? user :site-admin))
