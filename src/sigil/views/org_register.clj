(ns sigil.views.org-register)

(defn org-register-body []
  [:div.container.maxw-1000
   [:div.row
    [:div.col-lg-12
     [:h2 "Create a Sigil page for your company"]
     [:div.panel.panel-default
      [:div.panel-body
       (form-to
        [:post "/orgregister"]

        [:div.form-group
         (label "org-name" "The name of your company.")
         (text-field {:id "org-name"
                      :placeholder "Company name"
                      :class "form-control"} "org-name")]

        [:div.form-group
         (label "org-url" "The URL handle for your company on Sigil, i.e. http://sigil.tech/<your company>. Must be lowercase with no spaces.")
         (text-field {:id "org-url"
                      :placeholder "Company URL handle"
                      :class "form-control"} "org-url")]

        [:div.form-group
         (label "website" "The URL of your company website.")
         (text-field {:id "website"
                      :placeholder "Company website"
                      :class "form-control"} "website")]

        [:div.form-group
         (label "website" "The URL of your company website.")
         (text-field {:id "website"
                      :placeholder "Company website"
                      :class "form-control"} "website")]

        [:div.form-group
         (label "website" "The URL of your company website.")
         (text-field {:id "website"
                      :placeholder "Company website"
                      :class "form-control"} "website")]

        [:h4 "Create the Sigil account for your company's first administrator."]
        [:div.form-group
         (label "email" "Email")
         (text-field {:id "email"
                      :placeholder "Email"
                      :class "form-control"} "email")]

        [:div.form-group
         (label "username" "Username")
         (text-field {:id "username"
                      :placeholder "Username"
                      :class "form-control"} "username")]

        [:div.form-group
         (label "password" "Password")
         (password-field {:id "password"
                          :placeholder "Password"
                          :class "form-control"} "password")]

        [:div.form-group
         (label "confirm-password" "Confirm password")
         (password-field {:id "confirm-password"
                          :placeholder "Confirm password"
                          :class "form-control"} "confirm-password")]

        [:div.checkbox
         (label "policy-accept"
                (html
                 (check-box {:id "policy-accept"}
                            "policy-accept"
                            false
                            "true")
                 "I agree to Sigil's "
                 [:a {:href "/terms" :target "_blank"}
                  "Terms of Use"]
                 ", "
                 [:a {:href "/acceptableuse" :target "_blank"}
                  "Acceptable Use"]
                 ", and "
                 [:a {:href "/privacy" :target "_blank"}
                  "Privacy"]
                 " policies."))]

        [:div.btn-group.btn-group-justified
         [:div.btn-group
          (submit-button {:class "btn btn-primary disabled"}
                         "Create your company page")]])]]]]])
