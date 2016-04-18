(ns sigil.views.org-settings
  (:require [sigil.auth :refer [user-or-nil user-has-role? user-is-org-admin?]]
            [sigil.db.orgs :refer [get-org-by-id]]
            [sigil.db.tags :refer [get-tags-by-org]]
            [sigil.views.layout :as layout]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to file-upload text-field submit-button]]
            [sigil.views.not-found :refer [not-found-handler]]))

(declare org-settings-handler org-settings-body)

(defn org-settings-handler [req]
  (let [user (user-or-nil req)]
    (if (user-is-org-admin? user)
      (let [org (get-org-by-id (:org_id user))
            tags (get-tags-by-org org)
            ]
        (layout/render
         req
         user
         org
         (str "Sigil - " (:org_name org) " Settings")
         (org-settings-body org tags)))
      (not-found-handler req))))

(defn org-settings-body [org tags]
  (let [org_url (:org_url org)]
      (html
       [:div.container.settings-container
        [:div.row
         [:div.col-lg-12
          [:div.btn-group.btn-group-justified {:style "margin-bottom:20px;"}
           [:a.btn.btn-warning {:href (:org_url org)} (:org_name org) " Main Page"]
           [:a.btn.btn-primary {:href (str (:org_url org) "/data")} (:org_name org) " Data"]]]]]
       [:div.container.settings-container
        [:div.row
         [:div.col-lg-12
          [:div.panel
           [:div.panel-body
            [:img.img-rounded.img-responsive.img-relief
             {:src (str (:banner org) "?3243294")}]
            [:h4 "Banner files: 1000 x 200 px .jpg or .png"]
            [:form {:action "/orgbanner" :method "post" :enctype "multipart/form-data"}
             [:div.form-group
              [:div.input-group
               [:div.input-group-btn
                [:span.btn.btn-default.btn-file "Browse"
                 (file-upload {:id "banner-upload"} "banner-upload")]]
               (text-field {:class "form-control image-input" :readonly ""} "txt-field-banner")]]
             [:div.form-group
              (submit-button {:class "btn btn-default disabled form-control"} "Upload new banner")]]]]]]]
       [:div.container.settings-container
        [:div.row
         [:div.col-lg-6
          [:div.panel {:style "text-align:center;"}
           [:div.panel-body
            [:div.row
             [:div.col-lg-6 [:h4 "Tag name:"]]
             [:div.col-lg-6 [:h4 "Tag URL:"]]]
            [:hr]
            (for [tag tags
                  :let [tag_url (:tag_url tag)]]
           
              [:div.row {:style "margin-bottom:10px;"}
               [:div.col-lg-6 (:tag_name tag)]
               [:div.col-lg-6
                (:tag_url tag)
                [:a {:href (str "/"
                                org_url;(:org_url org)
                                "/"
                                (:tag_url tag) "/")}
                 (str "www.sigil.tech/"
                      org_url "/"
                      (:tag_url tag) "/")]
                [:a {:href (str "/"
                                org_url "/";(:org_url org) "/"
                                (:tag_url tag) "/"
                                "settings")}
                 [:span.glyphicon.glyphicon-wrench]]
                [:a {:href (str "/" org_url "/" (:tag_url tag) "/delete")}
                 [:span.glyphicon.glyphicon-remove-sign]]]])
            (form-to
             [:post "/orgaddtag"]
             [:div.row {:style "margin-top:30px;"}
              [:div.col-lg-6
               [:div.form-group
                (text-field {:class "form-control"
                             :id "tag-name"
                             :placeholder "Tag name"} "tag-name")]]
              [:div.col-lg-6
               [:div.form-group
                (text-field {:class "form-control"
                             :id "tag-url"
                             :placeholder "Tag URL"} "tag-url")]
               [:div.form-group
                (submit-button {:class "btn btn-default disabled form-control"} "Create New Tag")]]])]]]
         [:div.col-lg-6
          [:div.panel
           [:div.panel-body
            [:img.img-rounded.img-responsive.img-relief
             {:src (:icon_100 org)}]
            [:h4 "Large icon files: 100 x 100 px .jpg or .png"]
            [:form {:action "/orgicon100" :method "post" :enctype "multipart/form-data"}
             [:div.form-group
              [:div.input-group
               [:div.input-group-btn
                [:span.btn.btn-default.btn-file "Browse"
                 (file-upload {:id "icon-100-upload"} "icon-100-upload")]]
               (text-field {:class "form-control image-input" :readonly ""} "txt-field-icon100")]]
             [:div.form-group
              (submit-button {:class "btn btn-default disabled form-control"} "Upload new 100px icon")]]]]
          [:div.panel
           [:div.panel-body
            [:img.img-rounded.img-responsive.img-relief
             {:src (:icon_30 org)}]
            [:h4 "Small icon files: 30 x 30 px .jpg or .png"]
            [:form {:action "/orgicon30" :method "post" :enctype "multipart/form-data"}
             [:div.form-group
              [:div.input-group
               [:div.input-group-btn
                [:span.btn.btn-default.btn-file "Browse"
                 (file-upload {:id "icon-30-upload"} "icon-30-upload")]]
               (text-field {:class "form-control image-input" :readonly ""} "txt-field-icon30")]]
             [:div.form-group
              (submit-button {:class "btn btn-default disabled form-control"} "Upload new 30px icon")]]]]]]])))
