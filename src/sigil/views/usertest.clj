(ns sigil.views.usertest
  (:require [sigil.auth :refer [authenticated? identity]])
  (:use hiccup.page))

(defn usertest-handler [req]
  (html5
   (if (authenticated? req)
     [:p {} (identity req)]
     [:p "No user. :("])))
