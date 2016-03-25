(ns sigil.views.search-page
  (:require [sigil.views.layout :as layout]
            [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [hiccup.core :refer [html]]))

(declare search-page-handler search-page-body)

(defn search-page-handler [req]
  (let [user (user-or-nil req)
        user-org (user-org-or-nil  user)
        search-query ((:query-params req) "search")
        orgs nil;(get-orgs-by-name search-query)
        topics nil;(get-topics-by-name search-query)
        tags nil;(get-tags-with-orgs-by-name search-query)
        ;;users (get-users-by-name search-query)
        ]
    (layout/render req
                   user
                   user-org
                   (str "Sigil - Search for " search-query)
                   (search-page-body (:uri req)
                                     orgs
                                     topics
                                     tags
                                     ;;users
                                     ))))

(defn search-page-body [uri
                        orgs
                        topics
                        tags
                        ;;users
                        ]
  [:div.column.maxw-1000
   [:div.row
    [:div.col-lg-6
     [:h3 "Organizations"]
     [:div.panel.panel-default
      [:div.panel-body
       (if (not-empty orgs)
         (for [o orgs]
           (html [:div.media
              [:div.media-left
               [:img.media-object {:src (str "/" (:icon_100 o))}]]
              [:div.media-body
               [:h3 [:a {:href (:org_url o)} (:org_name o)]]]]
            [:hr]))
         [:p "No organizations, companies or people matching your search."])]]
     [:h3 "Issues"]
     [:div.panel.panel-default
      [:div.panel-body
       ;; (if (not-empty issues)
       ;;   (for [i issues]
       ;;     (html (issue-partial uri i user true)
       ;;          [:hr]))
       ;;   [:p "No issues matching your search."])
       ]]]
    [:div.col-lg-6
     [:h3 "Tags"]
     [:div.panel.panel-default
      [:div.panel-body
       (if (not-empty tags)
         (for [t tags]
           (html [:div.media
              [:div.media-left
               [:img.media-object {:src (str "/" (:icon_100 (:org t)))}]]
              [:div.media-body
               [:h3
                [:a {:href (:org_url (:org t))} (:org_name (:org t))]
                [:span.label.label-default.pull-right
                 [:img {:src (str "/" (:icon_30 t))}]
                 (:tag_name t)]]]]
            [:hr]))
         [:p "No tags matching your search."])]]]]])
