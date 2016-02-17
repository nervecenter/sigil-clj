(ns sigil.views.login.render
  (require [sigil.views.layout :as layout]))

(defn body [return & validation-messages]
  [:div.container.maxw-400
   [:h2 "Log in to Sigil"]
   [:div.row
    [:div.col-lg-12
     [:div.panel-panel-default
      [:div.panel-body
       (form-to {:role "form" :method "post" :action "/login"}
                [:input {:type "hidden" :name "return" :id "return" :value (return)}]
                (for [m validation-messages]
                  [:h4 m])

                [:div.form-group
                 [:label {:for "email"} "Email"]
                 [:textbox.form-control {:name "email"
                                         :id "email"
                                         :type "text"
                                         :placeholder "Email"}]]

                [:div.form-group
                 [:label {:for "password"} "Password"]
                 [:textbox.form-control {:name "password"
                                         :id "password"
                                         :type "password"
                                         :placeholder "Password"}]]

                [:div.login-page-remember {:style "margin-bottom:10px;"}
                 [:input {:name "remember"
                          :id "remember"
                          :type "checkbox"
                          :value "true"}]
                 [:label {:for "remember"} "Remember me?"]]

                [:div.btn-group.btn-group-justified
                 [:div.btn-group
                  [:button.btn.btn-primary {:type "submit"} "Log In"]]
                 [:div.btn-group
                  [:a.btn.btn-info {:href "register"} "Sign Up"]]])]]]]])

(defn page
  "Render the login page. Takes in a string representing the domain URI for the view to be returned to after login success, and an optional collection of validation messages to be rendered at the top of the form."
  [return & validation-messages]
  (layout/render "Sigil - Log In" (body return validation-messages) ))
