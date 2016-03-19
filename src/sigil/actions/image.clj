(ns sigil.actions.image
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [clojure.java.io :as io]
            [sigil.auth :as auth]))



(defn update-user-icon
  [req]
  (let [upload-params (:params req)
        user-icon-file (upload-params :usericon100)
        user (auth/user-or-nil req)
        new-file-name (str (:username user) "_100.png")
        new-file-path (str "db_imgs/user/" new-file-name)]
    (do
      (println "This is real time.")
      (io/copy (user-icon-file :tempfile) (io/file (format new-file-path)))
      (db/db-trans [users/update-user user {:icon_100 new-file-path}])
      {:status 302
       :headers {"Location" "/usersettings"}}))
)

