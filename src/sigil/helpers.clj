(ns sigil.helpers)

(defn get-return [req]
  (if (some? ((:query-params req) "return"))
    ((:query-params req) "return")
    "/"))
