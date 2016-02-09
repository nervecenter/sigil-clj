(ns sigil-clj.core)
(use 'ring.adapter.jetty)

(defn sigil-handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello from Ring."})
