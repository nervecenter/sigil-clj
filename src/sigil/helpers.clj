(ns sigil.helpers)

(defn get-return [req]
  (if (some? ((:query-params req) "return"))
    ((:query-params req) "return")
    "/"))

(defn user-has-role? [user role]
  (cond
    (= role :org-admin) (if (contains? (:roles user) "org-admin")
                          true
                          false)
    (= role :site-admin) (if (contains? (:roles user) "site-admin")
                          true
                          false)))
