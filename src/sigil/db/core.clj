(ns sigil.db.core
  (:require [clojure.java.jdbc :as j]
            ;;[korma.db :as kdb]
            ;;[korma.core :as kc]
            ))

(def postgres-debug-db {:subprotocol "postgresql"
                        :classname "org.postgresql.Driver"
                        :subname "//localhost:5432/SigilDB"
                        :user "Chris"})

(def db postgres-debug-db)

;;(kdb/defdb sigil-db
;;  (kdb/postgres {:db "SigilDB"
;;                :user "Chris"
;;                :host "localhost"
;;                :port "5432"}))

;;(declare user issue)

;;(kc/defentity user
;;  (kc/pk "user-id")
;;  (kc/table :Users)
;;  (kc/has-many issue))

;;(kc/defentity issue
;;  (kc/pk "issue-id")
;;  (kc/table :Issues)
;;  (kc/belongs-to user))

