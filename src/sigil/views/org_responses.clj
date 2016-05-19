(ns sigil.views.org-responses
  (:require [sigil.auth :as auth]
            [sigil.db.orgs :as orgs]
            [sigil.db.issues :as issues]
            [sigil.views.layout :as layout]
            [sigil.views.not-found :refer [not-found-handler]]
            [hiccup.core :refer [html]]))

(declare org-responses-body org-responses-handler)

(defn org-responses-handler [req]
  (let [user (auth/user-or-nil req)
        user-org (auth/user-org-or-nil user)
        org (orgs/get-org-by-url (:org_url (:route-params req)))]
    (if (some? org)
      (let [issues (issues/get-responded-issues-by-org org)]
        (layout/render req
                       user
                       user-org
                       (str "Sigil - " (:org_name org) " Responses")
                       (org-responses-body (:uri req) user org issues)))
      (not-found-handler req "No such org found."))))

(defn org-responses-body [uri user org issues]
  (html
   [:div#main-col.col-md-9.col-lg-9
    [:img.img-rounded.img-responsive.org-banner-small
     {:src (str (:banner org))}]
    [:div.btn-group.btn-group-sm.btn-group-justified
     {:style "margin-bottom:20px;"}
     [:a.btn.btn-warning
      {:href (str "/" (:org_url org))}
      "Main feed"]
     [:a.btn.btn-default.active "Responses"]]
    [:div#issues
     (if (not-empty issues)
       (for [i issues]
         (sigil.views.partials.issue/issue-partial uri i org user true))
       [:h4 (:org_name org) " hasn't responded to any issues yet. They'll get to it soon!"])]]
   (sigil.views.partials.sidebar/sidebar-partial org user)))
