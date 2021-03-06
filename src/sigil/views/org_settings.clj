(ns sigil.views.org-settings
  (:require [sigil.auth :refer [user-or-nil user-has-role? user-is-org-admin?]]
            [sigil.db.orgs :refer [get-org-by-id]]
            [sigil.db.tags :refer [get-tags-by-org]]
            [sigil.views.layout :as layout]
            [hiccup.core :refer [html]]
            [hiccup.form :refer [form-to file-upload text-field submit-button hidden-field]]
            [sigil.views.not-found :refer [not-found-handler]]))

(declare org-settings-handler org-settings-body)

(defn org-settings-handler [req]
  (let [user (user-or-nil req)]
    (if (user-is-org-admin? user)
      (let [org (get-org-by-id (:org_id user))
            tags (get-tags-by-org org)
            validation ((:query-params req) "v")]
        (layout/render
         req
         user
         org
         (str "Sigil - " (:org_name org) " Settings")
         (org-settings-body org tags validation)))
      (not-found-handler req "Non-org-admin user attempted to access org settings."))))

(defn org-settings-body [org tags validation]
  (html
    [:div.container.settings-container
     [:div.row
      [:div.col-lg-12
       [:div.btn-group.btn-group-justified {:style "margin-bottom:20px;"}
        [:a.btn.btn-warning {:href (:org_url org)} (:org_name org) " Main Page"]
        [:a.btn.btn-primary {:href (str (:org_url org) "/data")} (:org_name org) " Data"]]]]]
    [:div.container.settings-container
     [:div.row
      (condp = validation
        ;; banner upload failed
        "u" [:h3.validation {:style "color:red"} "Banner upload failed; we'll look into it."]
        ;; banner image incorrect
        "k" [:h3.validation {:style "color:red"} "Banner should be a 1000 x 200 pixel jpg or png."]
        ;; banner successfully changed
        "b" [:h3.validation {:style "color:green"} "Successfully changed banner."]
        ;; icon100 upload failed
        "y" [:h3.validation {:style "color:red"} "Icon upload failed; we'll look into it."]
        ;; icon100 image incorrect
        "u" [:h3.validation {:style "color:red"} "Icon should be a 100 x 100 pixel jpg or png."]
        ;; icon100 successfully changed
        "i" [:h3.validation {:style "color:green"} "Successfully changed icon."]
        ;; icon30 upload failed
        "e" [:h3.validation {:style "color:red"} "Icon upload failed; we'll look into it."]
        ;; icon30 image incorrect
        "a" [:h3.validation {:style "color:red"} "Icon should be a 30 x 30 pixel jpg or png."]
        ;; icon30 successfully changed
        "t" [:h3.validation {:style "color:green"} "Successfully changed icon."]
        ;; tagicon upload failed
        "m" [:h3.validation {:style "color:red"} "Tag icon upload failed; we'll look into it."]
        ;; tagicon image incorrect
        "r" [:h3.validation {:style "color:red"} "Tag icon should be a 30 x 30 pixel jpg or png."]
        ;; tagicon successfully changed
        "g" [:h3.validation {:style "color:green"} "Successfully changed tag icon."]
        ;; tag successfully added
        "s" [:h3.validation {:style "color:green"} "Successfully added tag."]
        ;; tag add failed
        "q" [:h3.validation {:style "color:red"} "Adding new tag failed; we'll look into it."]
        ;; tag name incorrect
        "c" [:h3.validation {:style "color:red"} "Incorrect tag name."]
        ;; tag successfully deleted
        "d" [:h3.validation {:style "color:green"} "Successfully deleted tag."]
        ;; tag delete failed
        "p" [:h3.validation {:style "color:red"} "Deleting tag failed; we'll look into it."]
        ;; Can't delete last tag
        "l" [:h3.validation {:style "color:red"} "Can't delete your last tag!"]
        ;; zip successfully added
        "z" [:h3.validation {:style "color:green"} "Successfully added zip code."]
        ;; zip add failed
        "o" [:h3.validation {:style "color:red"} "Deleting zip failed; we'll look into it."]
        ;; zip successfully deleted
        "w" [:h3.validation {:style "color:green"} "Successfully deleted zip code"]
        ;; zip delete failed
        "n" [:h3.validation {:style "color:red"} "Deleting zip failed; we'll look into it."]
        nil)
      [:div.col-lg-12
       [:div.panel
        [:div.panel-body
         [:img.img-rounded.img-responsive.img-relief
          {:src (:banner org)}]
         [:h4 "Banner files: 1000 x 200 px .jpg or .png"]
         [:form {:action "/orgbanner" :method "post" :enctype "multipart/form-data"}
          [:div.form-group
           [:div.input-group
            [:div.input-group-btn
             [:span.btn.btn-default.btn-file "Browse"
              (file-upload {:id "banner-upload"} "banner-upload")]]
            (text-field {:class "form-control image-input" :readonly ""} "txt-field-banner")]]
          [:div.form-group
           (submit-button {:class "btn btn-default disabled form-control"
                           :disabled "disabled"} "Upload new banner")]]]]]]]
    [:div.container.settings-container
     [:div.row
      [:div.col-lg-6
       [:h3 "Tags"]
       (for [tag tags]
         [:div.panel {:style "text-align:center;"}
          [:div.panel-body
           [:img.pull-left {:src (:icon_30 tag)
                            :style "height:40px;"}]
           [:div.btn-group.pull-right
            [:a.btn.btn-sm.btn-default.change-tag-icon
             {:data-tagid (:tag_id tag)
              :data-orgid (:org_id tag)}
             "Change icon"]
            [:a.btn.btn-sm.btn-danger.delete-tag
             {:data-tagid (:tag_id tag)}
             "Delete"]]
           [:h4 (:tag_name tag)]]
          ])
       [:div.panel {:style "text-align:center;"}
        [:div.panel-body
         [:h4 "Add a new tag"]
         (form-to
           [:post "/addtag"]
           (hidden-field {:value (:org_id org)} "orgid")
           [:div.row {:style "margin-top:30px;"}
            [:div.col-lg-12
               [:div.form-group
                (text-field {:class "form-control"
                             :id "tag-name"
                             :placeholder "Tag name"} "tag-name")]
               [:div.form-group
                (submit-button {:class "form-control btn btn-primary disabled"
                                :id "new-tag-submit"
                                :disabled "disabled"} "Submit new tag")]]])]]]
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
              (submit-button {:class "btn btn-default disabled form-control"
                              :disabled "disabled"} "Upload new 100px icon")]]]]
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
              (submit-button {:class "btn btn-default disabled form-control"
                              :disabled "disabled"} "Upload new 30px icon")]]]]]]]
      [:div#delete-tag-modal.modal.fade {:tabindex "-1"
                                         :role "dialog"
                                   :aria-labelledby "delete-tag-modal-label"}
    [:div.modal-dialog.modal-sm {:role "document"}
     [:div.modal-content
      [:div.modal-header
        [:button.close {:type "button"
                        :data-dismiss "modal"
                        :aria-label "Close"}
          [:span {:aria-hidden "true"} "x"]]
        [:h4#delete-tag-modal-label.modal-title "Delete tag"]]
      [:div.modal-body
        "Move these issues to:"
        [:form#delete-tag-form {:method "post"
                                :action "/deletetag"}
         (hidden-field {:id "tagid-field"} "tagid")
         [:div.form-group
          [:select.form-control {:name "moveto"}
           (for [tag tags]
             [:option.to-tag {:id (str "to-" (:tag_id tag))
                              :value (:tag_id tag)} (:tag_name tag)])]]]]
      [:div.modal-footer
        [:button.btn.btn-default {:type "button" :data-dismiss "modal"} "Cancel"]
        [:button.btn.btn-danger {:type "submit" :form "delete-tag-form"} "Delete"]]]]]))
