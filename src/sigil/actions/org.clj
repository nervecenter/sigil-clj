(ns sigil.actions.org
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.auth :as auth]
            [sigil.views.internal-error :refer [internal-error-handler]]))

(defn add-org-zip-code
  [req]
  (let [upload-params (:params req)
        org-new-zip (upload-params :zip-code)
        org (auth/user-org-or-nil (auth/user-or-nil req))]
    (do
      (db/db-trans [orgs/update-org org {:zip_codes (set (conj (:zip_codes org) org-new-zip))}])
      {:status 302
       :headers {"Location" "/orgsettings"}})))


(defn delete-org-zip-code
  [req]
  (let [upload-params (:params req)
          deleted-org-zip (:zipcode upload-params)
          org (auth/user-org-or-nil (auth/user-or-nil req))]
      (do
        (db/db-trans [orgs/update-org org {:zip_codes (set (disj (set (:zip_codes org)) deleted-org-zip))}])
        {:status 302
         :headers {"Location" "/orgsettings"}})))
