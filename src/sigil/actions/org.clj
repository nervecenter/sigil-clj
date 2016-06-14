(ns sigil.actions.org
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.auth :as auth]
            [sigil.helpers :refer [redirect error-redirect]]
            [sigil.views.internal-error :refer [internal-error-handler]]))

(defn add-org-zip-code
  [req]
  (let [upload-params (:params req)
        org-new-zip (upload-params :zip-code)
        user (auth/user-or-nil req)
        org (auth/user-org-or-nil user)]
    (if (= :success
           (db/db-trans [orgs/update-org org {:zip_codes (set (conj (:zip_codes org) org-new-zip))}]))
      (redirect "/orgsettings?v=z")
      (error-redirect "Error adding new zip code."
                      {:org org :zip org-new-zip}
                      user
                      "/orgsettings?v=o"))))


(defn delete-org-zip-code
  [req]
  (let [upload-params (:params req)
        deleted-org-zip (:zipcode upload-params)
        user (auth/user-or-nil req)
        org (auth/user-org-or-nil user)]
    (if (= :success
           (db/db-trans [orgs/update-org org {:zip_codes (set (disj (set (:zip_codes org)) deleted-org-zip))}]))
      (redirect "/orgsettings?v=w")
      (error-redirect "Error deleting zip code."
                      {:org org :zip deleted-org-zip}
                      user
                      "/orgsettings?v=n"))))
