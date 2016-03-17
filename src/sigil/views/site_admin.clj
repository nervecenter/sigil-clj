(ns sigil.views.site-admin
  (:require [sigil.auth :refer [user-or-nil]]
            [sigil.helpers :refer [is-user-site-admin?]]
            [sigil.views.not-found :refer [not-found-handler]]))

(defn site-admin-handler [req]
  (let [user (user-or-nil req)]
    (if (is-user-site-admin? user)
      
      (not-found-handler req))))
