(ns sigil.db.tentative-funcs
  (:require [sigil.db.core :refer [db]])
  (:use clojure.java.jdbc))

(def test-issue-one {:user_id 1 :title "This is the first test issue."})
(def test-issue-two {:user_id 2 :title "This is the second test issue."})

(defn prepend [coll x]
  (cons x coll))

(defn insert-issue-quote [issue]
  (concat '(~@insert! ~@db :issues)
          (list issue)))

(defn insert-issue [issue]
  (insert! db :issues issue))

(defn concat-eval-to-changes [& changes]
  (map #(list 'eval %) changes))

(defn realize-transaction [& changes]
  (-> (map #(list 'eval %) changes)
      (prepend ['tconn ~@db])
      (prepend ~@with-db-transaction)))

(defn db-trans [& changes]
  (eval
   (-> (map #(list 'eval %) changes)
       (prepend ['tconn ~@db])
       (prepend ~@with-db-transaction))))
