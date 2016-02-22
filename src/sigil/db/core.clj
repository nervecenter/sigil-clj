(ns sigil.db.core
  (:require [clojure.java.jdbc :as j]))

(def postgres-debug-db {:subprotocol "postgresql"
                        :classname "org.postgresql.Driver"
                        :subname "//localhost:5432/sigildb"
                        :user "Chris"})

(def db postgres-debug-db)

