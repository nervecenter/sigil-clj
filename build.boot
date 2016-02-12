#!/usr/bin/env boot

(set-env! :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/tools.namespace "0.2.11"]
                          [ring/ring-core "1.4.0"]
                          [ring/ring-jetty-adapter "1.4.0"]
                          [compojure "1.4.0"]
                          [hiccup "1.0.5"]
                          [cheshire "5.5.0"]
                          ;;[korma "0.4.2"]
                          [org.clojure/java.jdbc "0.4.2"]
                          [postgresql/postgresql "9.1-901-1.jdbc4"]])

(task-options! pom {:project 'sigil-clj
                    :version "0.5.0"}
               ;;repl {:init-ns 'sigil.core}
               )

;; Let's define some utilities we can run in REPL
;; First, we include core for access to server and repl
;; for access to namespace reloading stuff
(require 'sigil.core
         '[clojure.tools.namespace.repl :as repl])

;; Define dirs for reloading
(def dirs (get-env :directories))
(apply repl/set-refresh-dirs dirs)

;; Define helpers for REPL
;; Start the Ring Jetty server
(defn start [] (.start sigil.core/server))
;; Stop the server
(defn stop [] (.stop sigil.core/server))
;; Reload dirs with code changes
(defn reload [] (repl/refresh))
;; Do it all!
(defn restart [] (stop) (reload) (start))

;; Task to build the server jar using "boot build"
(deftask build
  "Build the Sigil web server."
  []
  (comp (pom) (jar) (install)))

;; Totally irrelevant, but if you want to test boot, run "boot repl" in the
;; boot.user namespace, and you can run fib; you can also run "./build.boot n"
;; from the terminal, where you want to print the nth Fibonacci number.

(defn fib
  ([n]
   (fib [0 1] n))
  ([pair, n]
   (print (first pair) " ")
   (if (> n 0)
     (fib [(second pair) (apply + pair)] (- n 1))
     (println))))

(defn -main [& args]
  (let [limit (first args)]
    (println "Printing fibonacci sequence up to " limit "numbers...")
    (fib (Integer/parseInt limit))))
