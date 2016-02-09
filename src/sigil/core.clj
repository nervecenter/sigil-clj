(ns sigil.core
  (:gen-class))
(use 'ring.adapter.jetty)

(defn sigil-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello from Ring."})

(defn start-server [] (run-jetty sigil-handler {:port 3000}))
