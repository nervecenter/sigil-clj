(ns sigil.views.login.render
  (:require [sigil.views.layout :as layout])
  (:use hiccup.core
        hiccup.page
        hiccup.form))

(def not-nil? (complement nil?))

(defn body [return & validation-messages]
  [:div.container.maxw-400
   [:h2 "Log in to Sigil"]
   [:div.row
    [:div.col-lg-12
     [:div.panel-panel-default
      [:div.panel-body
       (if (not-nil? validation-messages)
         (for [m validation-messages]
           [:h4 m])
         nil)
       (form-to
        [:post "/login"]
        (hidden-field {:id "return"} "return" return)

        [:div.form-group
         (label "email" "Email")
         (text-field {:id "email" :placeholder "Email"} "email")]

        [:div.form-group
         (label "email" "Email")
         (password-field {:id "password" :placeholder "password"} "password")]

        [:div.login-page-remember {:style "margin-bottom:10px;"}
         (check-box {:id "remember"} "checkbox" false "true")
         (label "remember" "Remember me?")]

        [:div.btn-group.btn-group-justified
         [:div.btn-group
          (submit-button {:class "btn btn-primary"} "Log In")]
         [:div.btn-group
          [:a.btn.btn-info {:href "register"} "Sign Up"]]])]]]]])

(defn page
  "Render the login page. Takes in a string representing the domain URI for the view to be returned to after login success, and an optional collection of validation messages to be rendered at the top of the form."
  [return & validation-messages]
  (layout/render "Sigil - Log In" (body return validation-messages)))
