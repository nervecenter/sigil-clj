#!/usr/bin/env boot

(set-env! :project 'sigil-clj
          :version "0.5.0"
          :source-paths #{"src"}
          :resource-paths #{"resources"}
          :dependencies '[[org.clojure/clojure "1.8.0"]
                          [org.clojure/tools.namespace "0.2.11"] ;; For REPL reloading and such; MAY END UP TAKING OUT
                          [ring/ring-core "1.4.0"]               ;; Web server
                          [javax.servlet/servlet-api "2.5"]      ;; Either used in Ring or http-kit
                          ;;[ring.middleware.logger "0.5.0"]
                          [http-kit "2.1.19"]                    ;; Replaces jetty as the HTTP endpoint of the server
                          [buddy/buddy-auth "0.9.0"]             ;; Allows us to encrypt and authorize requests
                          [buddy/buddy-hashers "0.11.0"]
                          [compojure "1.5.0"]                    ;; Routing
                          [hiccup "1.0.5"]                       ;; Templating
                          [cheshire "5.5.0"]                     ;; JSON encoding
;                          [korma "0.4.2"]                        ;; A schema and SQL library, possibly use later
                          [org.clojure/java.jdbc "0.4.2"]        ;; SQL querying and transactions
;                          [org.clojure/java.jdbc "0.3.7"]        ;; Korma depends on 0.3.7
                          [org.postgresql/postgresql "9.4.1208"] ;; Postgres driver
                          [clj-time "0.11.0"]                    ;; Simple date and time calculations
                          ;;[speclj "3.3.1"]                     ;; Test and behavior suite, may use later
                          ;;[fresh "1.0.1"]                      ;; Live reloads src files on save, may use later
                          [ez-image "1.0.4"]                     ;; Image conversion and saving
                          ])

(task-options! pom {:project (get-env :project)
                    :version (get-env :version)}
               jar {:main 'sigil.core
                    ;;:manifest {"URL" "http://localhost:3000"}
                    }
               aot {:namespace '#{sigil.core}}
               ;;repl {:init-ns 'sigil.core}
               )

;; Let's define some utilities we can run in REPL
;; First, we include core for access to server and repl
;; for access to namespace reloading stuff
(require 'sigil.db.migrations
         'sigil.db.seed
         '[clojure.tools.namespace.repl :as repl]
         '[sigil.core :refer [server start-server stop-server restart-server]])

;; Define dirs for reloading
(def dirs (get-env :directories))
(apply repl/set-refresh-dirs dirs)

;; Composes stopping server, reloading namespaces, and starting server
(defn reload-server []
  (when-not (nil? @server)
    (do (stop-server) (repl/refresh) (start-server))))

;; (defn rebuild-and-seed
;;   "Drops the current db tables and then rebuilds and seeds."
;;   []
;;   (sigil.db.seed/drop-create-seed))

(defn build-and-seed
  []
  "Builds and seeds database tables"
  (sigil.db.seed/drop-create-seed))

(def default-sigil-map
  {:sigil-root "src/sigil"})

;; Task to build the server jar using "boot build"
(deftask build
  "Build the Sigil web server."
  []
  (comp (aot)
        (pom)
        (uber)
        (jar :file (format "%s-%s-standalone.jar"
                           (get-env :project)
                           (get-env :version)))
        (target)))

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
