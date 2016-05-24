(ns sigil.views.org-data
  (:require [sigil.auth :refer [user-or-nil user-is-org-admin? user-org-or-nil]]
            ;[sigil.helpers :refer [user-is-org-admin?]]
            ;[sigil.db.orgs :refer [get-org-by-user]]
;            [sigil.db.issues]
            [sigil.views.not-found :refer [not-found-handler]]
            [sigil.views.layout :as layout]
            [sigil.actions.data :as data]
            [clj-time.core :as time]
            [sigil.views.partials.issue :refer [issue-partial]])
  (:use [hiccup.form]
        [hiccup.core]
        [hiccup.page]))
        

(declare org-data-page)

(defn org-data-handler [req]
  (let [user (user-or-nil req)]
    (if (user-is-org-admin? user)
      (let [user-org (user-org-or-nil user)
            ;chart-data () ;; (data/get-chart-data-by-org
                       ;;  user-org
                       ;;  (time/minus (time/now) (time/days 7))
                       ;;  (time/now))
            top-issues (data/get-top-issues-by-org
                        user-org
                        (time/minus (time/now) (time/days 7))
                        (time/now))
            top-unresponded-issues (data/get-top-unresponded-issues-by-org
                                    user-org
                                    (time/minus (time/now) (time/days 7))
                                    (time/now))
            top-rising-issues (data/get-top-rising-issues-by-org
                               user-org
                               (time/minus (time/now) (time/days 7))
                               (time/now))]
        (layout/render req
                       user
                       user-org
                       (str "Sigil - " (:org_name user-org) " Data")
                       (org-data-page (:uri req)
                                      user
                                      user-org
                                      ;chart-data
                                      top-issues
                                      top-unresponded-issues
                                      top-rising-issues)))
      (not-found-handler req "Non-org-admin user attempted to access org data page."))))

(defn org-data-page [uri
                     user
                     org
                     ;chart-data
                     top-issues
                     top-unresponded-issues
                     top-rising-issues]
  ;(include-js "https://www.google.com/jsapi" "/js/graph.js")
  (html
   [:style "
    #dpstart, #dpend, #selected-data, #data-button {
        width: 22.5%;
        margin-left: 2%;
    }

    #selected-data {
        width: 22.5%;
        margin-left: 2%;
    }

    #data-controls {
        border-radius: 3px;
        background-color: grey;
        height: 60px;
        padding-top: 7px;
        clear: both;
    }

    #top-issues {
        font-size: 18px;
    }

    .container {
        max-width: 1000px;
    }

    #top-issues-parent, #top-unresponded-issues-parent, #top-rising-issues-parent {
        width: 90%;
        margin: 0 auto;
    }"]
   [:div.container
    [:div.row
     [:div.col-lg-12
      [:div.panel.panel-default
       [:div.panel-body
        [:img#data-controls-hider.pull-left
         {:src "/images/minus.png"
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
            [:option {:value "Comments"} "Comments"]]
            ;[:option {:value "Follows"} "Follows"]
            ;[:option {:value "All"} "All"]
          
         (text-field {:id "dpstart"
                      :class "form-control pull-left"}
                     "start-date"
                     "Start date")
         (text-field {:id "dpend"
                      :class "form-control pull-left"}
                     "end-date"
                     "End date")
         [:button#data-button.btn.btn-primary.disabled "Get data"]]
        [:div#chart-panel {:style "clear:both;width:100%;"}
         [:div#org_chart_div {:style "width:100%;height:350px;"}]]
        [:div
         [:h4#data-header "Top issues for selected period"]
         [:div#top-issues-parent
          (for [i top-issues]
            (issue-partial uri i user))]
         [:h4#data-header "Top issues awaiting responses"]
         [:div#top-unresponded-issues-parent
          (for [i top-unresponded-issues]
            (issue-partial uri i user))]
         [:h4#data-header "Top new and rising issues"]
         [:div#top-rising-issues-parent
          (for [i top-rising-issues]
            (issue-partial uri i org user))]]]]]]]))
   ;(include-js "/js/graph.js")
   
