(ns sigil.views.org-responses)

(defn org-responses-handler [req]
  (let [user (user-or-nil req)
        user-org (get-or-by-user-id (:user_id user))
        org (get-org-by-url (:org_url (:route-params req)))
        issues (get-responded-issues-by-org-id (:org_id org))]
    (layout/render req
                   user
                   user-org
                   (str "Sigil - " (:org_name org) " Responses")
                   (org-responses-body (:uri req) user org issues))))

(defn org-responses-body [uri user org issues]
  [:div#main-col.col-md-9.col-lg-9
   [:img.img-rounded.img-responsive.org-banner-small
    {:src (:banner org)}]
   [:div.btn-group.btn-group-sm.btn-group-justified
    {:style "margin-bottom:20px;"}
    [:a.btn.btn-warning {:href (:org_url org)} "Main feed"]
    [:a.btn.btn-default.active "Responses"]
    [:a.btn.btn-default "Favorite"]]
   [:div#issues
    (if (not-empty issues)
      (for [i issues]
        (issue-partial uri i user true))
      [:h4 (:org_name org) " hasn't responded to any issues yet. They'll get to it soon!"])]]
  (sidebar-partial org user))
