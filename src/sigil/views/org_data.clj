(ns sigil.views.org-data
  (:require [sigil.auth :refer [user-or-nil]]
            [sigi.helpers :refer [user-is-org-admin?]]
            [sigil.db.orgs :refer [get-org-by-user get-chart-data-by-org]]
            [sigil.db.issues :refer [get-top-issues-by-org]]
            [sigil.views.not-found :refer [not-found-handler]]
            [sigil.views.layout :as layout]
            [clj-time.core :as time]))

(defn org-data-handler [req]
  (let [user (user-or-nil req)]
    (if (user-is-org-admin? user)
      (let [user-org (get-org-by-user user)
            chart-data (get-chart-data-by-org
                        user-org
                        (time/minus (time/now) (time/days 7))
                        (time/now))
            top-issues (get-top-issues-by-org
                        user-org
                        (time/minus (time/now) (time/days 7))
                        (time/now))
            top-unresponded-issues (get-top-unresponded-issues-by-org
                                    user-org
                                    (time/minus (time/now) (time/days 7))
                                    (time/now))
            top-rising-issues (get-top-rising-issues-by-org
                               user-org
                               (time/minus (time/now) (time/days 7))
                               (time/now))]
        (layout/render req
                       user
                       user-org
                       (str "Sigil - " (:org_name org) " Data")
                       (org-data-body (:uri req)
                                      user-org
                                      chart-data
                                      top-issues
                                      top-unresponded-issues
                                      top-rising-issues)))
      (not-found-handler req))))

(defn org-data-page [uri
                     org
                     chart-data
                     top-issues
                     top-unresponded-issues
                     top-rising-issues]
  [:div.container
   [:div.row
    [:div.col-lg-12
     [:div.panel.panel-default
      [:div.panel-body
       [:img#data-controls-hider.pull-left
        {:src "images/heirarchy-extended.png"
         :style "margin-top:23px;margin-right:10px;"}]
       [:h3#data-header.pull-left
        (:org_name org)
        " - Data "
        [:span#data-period "for the past week"]]
       [:div#data-controls
        [:select#selected-data.form-control.pull-left
         {:name "selected-data"}
         [:option {:value "Pick chart data"
                   :selected "selected"} "Pick chart data"]
         [:option {:value "Views"} "Views"]
         [:option {:value "Votes"} "Votes"]
         [:option {:value "Comments"} "Comments"]
         [:option {:value "Follows"} "Follows"]
         [:option {:value "All"} "All"]]
        (text-field {:id "dpstart"
                     :class "form-control pull-left"}
                    "start-date"
                    "Start date")
        (text-field {:id "dpend"
                     :class "form-controll pull-left"}
                    "end-date"
                    "End date")
        [:button#data-button.btn.btn-primary.disabled "Get data"]]
       [:div#chart-panel {:style "clear:both;width:100%;"}
        [:div#org-chart-div {:style "width:100%;height:350px;"}]]
       [:div
        [:h4#data-header "Top issues for selected period"
         [:div#top-issues-parent
          (for [i top-issues]
            (issue-partial uri i user true))]]
        [:h4#data-header "Top issues awaiting responses"
         [:div#top-unresponded-issues-parent
          (for [i top-unresponded-issues]
            (issue-partial uri i user true))]]
        [:h4#data-header "Top new and rising issues"
         [:div#top-rising-issues-parent
          (for [i top-issues]
            (issue-partial uri i user true))]]]]]]]])
