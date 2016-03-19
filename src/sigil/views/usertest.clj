(ns sigil.views.usertest
  (:require [sigil.auth :refer [authenticated? user-identity]])
  (:use hiccup.page))

(defn usertest-handler [req]
  (html5
   (if (authenticated? req)
     [:p {} (user-identity req)]
     [:p "No user. :("])))
