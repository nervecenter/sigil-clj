#!/usr/bin/env boot

(set-env! :source-paths #{"src"}
          :resource-paths #{"src"}
          :dependencies '[[ring/ring-core "1.4.0"]
                          [ring/ring-jetty-adapter "1.4.0"]
                          [compojure "1.4.0"]
                          [hiccup "1.0.5"]])

(task-options! pom {:project 'sigil-clj
                    :version "0.5.0"}
               repl {:init-ns sigil-clj.core})

(deftask build
  "Build the Sigil web server."
  []
  (comp (pom) (jar) (install)))

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
