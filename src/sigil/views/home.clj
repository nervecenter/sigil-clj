(ns sigil.views.home
  (:require [sigil.views.partials.issue :refer [issue-partial]]
            [sigil.views.partials.sidebar :refer [sidebar-partial]]
            [sigil.views.layout :as layout]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.db.issues :as issues]
            [hiccup.core :refer [html]]))

(declare home-body home-handler)

(defn home-handler [req]
  ;; Home page expects user
  (let [user (user-or-nil req)
        user-org (user-org-or-nil user)
        issues (issues/get-user-home-page-issues-and-posters user)]
    (layout/render req
                   user
                   user-org
                   "Sigil"
                   (home-body (:uri req) user issues))))

(defn home-body [uri user issues]
  (html
   [:div.col-md-9.col-lg-9
    [:div.panel.panel-default
     [:div.panel-body
      [:h3 "Welcome, " (:username user) "!"]
      [:br]
      [:p.empty-home-text "Here's the latest feedback on Sigil."]
      [:br]
      [:p.empty-home-text "Want to find companies? Use the " [:b "search bar"] " up top, or just " [:b [:a {:href "/companies"} "browse all companies on Sigil"]] "."]]]
    (for [i issues]
      (issue-partial uri (:issue i) (:poster i) true))]
   (sidebar-partial nil user)))
