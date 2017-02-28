(ns sigil.actions.logout
  (:require [sigil.helpers :refer [get-return]]))

(defn logout-handler [req]
  (let [return (get-return req)]
    {:status 302
     :headers {"Location" return}
     :body ""
     :cookies {:user {:value ""
                      :max-age 0}}}))
