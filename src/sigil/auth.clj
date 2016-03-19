(ns sigil.auth
  (:require [clojure.java.jdbc :refer [query]]
            [sigil.db.orgs :as orgs]
            [sigil.db.votes :as votes]
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
  (if (authenticated? req) (user-identity req) nil))

(defn user-org-or-nil [user]
  (let [user-org (:org_id user)]
      (if (not= user-org 0)
        (orgs/get-org-by-id user-org)
        nil)))

(defn is-user-site-admin?
  [user]
  true)

;;TODO::
(defn user-is-admin-of-org?
  [user org]
  true)
