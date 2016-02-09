(ns sigil.views.landing.logic
  (:require [hiccup.core :as h]))

(def page
  (h/html
   [:head
    [:title "Sigil"]]
   [:body.page
    [:div.page-header.page-header-landing
     [:img {:src "sample.png"}]]]))
