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

(defn user-is-org-admin? [user]
  (and (user-has-role? user :org-admin) (not= 0 (:org_id user))))

(defn user-is-admin-of-org? [user org]
  (and (user-has-role? user :org-admin) (= (:org_id org) (:org_id user))))
