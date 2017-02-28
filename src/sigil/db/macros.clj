(ns sigil.db.macros
  (:require [clojure.java.jdbc :as jdbc]
            [sigil.db.core :as db]))

(extend-protocol clojure.java.jdbc/ISQLParameter
  clojure.lang.IPersistentVector
  (set-parameter [v ^java.sql.PreparedStatement stmt ^long i]
    (let [conn (.getConnection stmt)
          meta (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta i)]
      (if-let [elem-type (when (= (first type-name)
                                  \_)
                           (apply str (rest type-name)))]
        (.setObject stmt i (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt i v)))))

(defmacro db-transaction [& operations]
  (concat (list 'clojure.java.jdbc/with-db-transaction ['conn db/spec])
          operations))

(defmacro create-users [& users]
  (concat (list 'clojure.java.jdbc/insert!
                db/spec
                :users)
          users))
