(ns sigil.actions.notifications
  (:require [sigil.db.notifications :as notes]
            [sigil.auth :as auth]))

;;----------------------------------------
;; notification GETs

(def not-nil? (complement nil?))

(defn get-number-user-notifications
  [req]
  (let [user (auth/user-or-nil req)]
    (if (not-nil? user)
      (count (notes/get-user-notifications user))
      0)))


;;TODO:: Need to jsonify the return of notifications
(defn get-user-notifications
  [req]
  (let [user (auth/user-or-nil req)]
    (if (not-nil? user)
      (notes/get-user-notifications user)
      [])))
