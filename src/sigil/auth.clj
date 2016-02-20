(ns sigil.auth
  (:require [clojure.java.jdbc :refer [query]]
            [buddy.sign.jwe :as jwe]
            [buddy.core.keys :refer [public-key private-key]]
            [sigil.db.users :refer [get-user-by-id]]))

(def pubkey (public-key "resources/private/pubkey.pem"))
(def privkey (private-key "resources/private/privkey.pem"))
(def encryption {:alg :rsa-oaep :enc :a128cbc-hs256})

(defn make-user-token [user]
  (jwe/encrypt {:user_id (:user_id user)} pubkey encryption))

(defn extract-user-id [req]
  (:user-id (jwe/decrypt (:value (:user (:cookies req)))
                         privkey
                         encryption)))

(defn authenticated? [req]
  (if (and (contains? (:cookies req) :user)
           (map? (jwe/decrypt (:value (:user (:cookies req)))
                              privkey
                              encryption)))
    true
    false))

(defn identity [req]
  (get-user-by-id (extract-user-id req)))
