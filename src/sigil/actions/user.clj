(ns sigil.actions.user
  (:require [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.db.core :as db]
            [sigil.auth :as auth]
            [ring.util.response :refer [redirect]]
            [clj-time.jdbc]
            [clj-time.core :as time]
            [clj-time.local :as local-time]
            [buddy.hashers :refer [check]]))

;;----------------------------------
;; user_register_post

(def not-nil? (complement nil?))

(defn register-user [user]
  (db/db-trans
   [users/create-user
    (assoc user :icon_100 (rand-nth db/default_icon_100))]))

(defn change-user-password
  [req]
  (let [user (auth/user-or-nil req)
        form-params (:form-params req)
        old-password (form-params "old-password")
        new-password (form-params "new-password")
        confirm-password (form-params "confirm-new-password")]
    (cond
      (not= new-password confirm-password)
      (redirect "settings?v=m")

      (not (check old-password (:pass_hash user)))
      (redirect "settings?v=b")

      (< (count new-password) 6)
      (redirect "settings?v=c")

      :else
      (do
        ;; Update user password
        (db/db-trans [users/update-user user {:pass_hash (buddy.hashers/encrypt new-password)}])
        ;; Then redirect back to user_settings
        (redirect "/settings?v=p")))))

(defn change-user-zip-code
  [req]
  (let [user (auth/user-or-nil req)
        form-params (:form-params req)
        new-zip-code (read-string (form-params "zip"))]
    (do
      (.println System/out (str new-zip-code " " (type new-zip-code)))
      (if (nil? (:zip_code_last_set user))
        (do (.println System/out "Zip is null")
            (db/db-trans [users/update-user user {:zip_code new-zip-code
                                                :zip_code_last_set (local-time/local-now)}])
            (redirect "/settings?v=z"))
        (if (time/after? (local-time/local-now)
                         (time/plus (:zip_code_last_set user) db/min-time-zip-change))
          (do (db/db-trans [users/update-user user {:zip_code  new-zip-code
                                                    :zip_code_last_set (local-time/local-now)}])
              (redirect "/settings?v=z"))
          (redirect "/settings?v=k"))))))
