(ns sigil.db.users
  (:require [clojure.java.jdbc :as j])
  (:use sigil.db.core))

(defn get-user-by-id [id]
  (first (j/query db [(str "SELECT * FROM users WHERE user_id = " id ";")])))


(defn get-user-by-email [email]
  (first (j/query db [(str "SELECT * FROM users WHERE email = '" email "';")])))
