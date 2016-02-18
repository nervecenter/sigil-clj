(ns sigil.views.partial.footer)

(def footer
  [:div.footer
   [:div.container
    [:div.row.footer-row
     [:div.col-sm-6.col-md-4
      [:ul.footer-list
       [:li
        [:a {:href "/"}
         [:img {:src "images/logo-small.png"}]]]
       [:li
        [:a.footer-link {:href ""} "A focal point for feedback."]]]]
     [:div.col-sm-6.col-md-4
      [:ul.footer-list
       [:li
        [:a.footer-link {:href "404"} "About"]]
       [:li
        [:a.footer-link {:href "legal"} "Legal"]]
       [:li
        [:a.footer-link {:href "mailto:contact@sigil.tech"} "Advertise"]]
       [:li
        [:a.footer-link {:href "https://www.facebook.com/Sigil-Technologies-Inc-1617420208509874/"} "Facebook"]]
       [:li
        [:a.footer-link {:href "404"} "Twitter"]]
       [:li
        [:a.footer-link {:href "mailto:contact@sigil.tech"} "Support"]]]]
     [:div.col-sm-6.col-md-4 {:style "font-size:12px;"}
      [:ul.footer-list
       [:li "© 2016 Sigil Technologies Inc. All rights reserved."]
       [:li "Sigil and the Sigil logo are trademarks of Sigil Technologies Inc."]
       [:li [:br]]
       [:li "Powered by Clojure and PostgreSQL."]]]]]])
