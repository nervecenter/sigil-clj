(ns sigil.views.usertest
  (:require [sigil.auth :refer [authenticated? extract-user-id]]
            [sigil.db.users :refer [get-user-by-id]])
  (:use hiccup.page))

(defn usertest-handler [req]
  (html5
   (if (authenticated? req)
     [:p (get-user-by-id (extract-user-id req))]
     [:p "No user. :("])))
