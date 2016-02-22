(ns sigil.db.seed
  (:require [clojure.java.jdbc :as sql]
            [sigil.db.core :as db])
  (:use [sigil.db.issues]
        [sigil.db.orgs]
        [sigil.db.comments]
        [sigil.db.tags]
        [sigil.db.comments]
        [sigil.db.officialresponses]))


(def org_seed [["sigil" "Sigil" "beta.sigil.tech"]
               ["test" "Test Org" "test.com"]])


(def user_seed [])

(defn seed-db
  []
  (do
    (map #(create-org (get %1 0) (get %1 1) (get %1 2)) org_seed)))

