(ns sigil.actions.tag
  (:require [sigil.db.tags :as tags]
            [sigil.db.core :as db]
            [sigil.auth :as auth]
            [sigil.views.internal-error :refer [internal-error-handler]]))

(defn add-org-tag
  [req]
  (let [org (auth/user-org-or-nil (auth/user-or-nil req))
        new-tag-form (:form-params req)
        new-tag {:tag_name (new-tag-form "tag-name")
                 :icon_30 (rand-nth db/default_icon_30)
                 :org_id (:org_id org)}]
    (do
      (db/db-trans [tags/create-tag new-tag])
      {:status 302
       :headers {"Location" "/orgsettings"}})))

;; TODO: Get this working using transactions.
(defn delete-org-tag
  [req]
  (let [org (auth/user-org-or-nil (auth/user-or-nil req))
        deleted-tag-params (:params req)
        deleted-tag (tags/get-tag-by-id (read-string (:tagid deleted-tag-params)))
        move-to-tag (tags/get-tag-by-id (read-string (:moveto deleted-tag-params)))]
    (if (= deleted-tag move-to-tag)
      (internal-error-handler req "Can't delete your last tag!")
      (do
        (tags/delete-tag deleted-tag)
        (tags/move-issues-from-tag-to-tag deleted-tag move-to-tag)
        {:status 302
         :headers {"Location" "/orgsettings"}}))))
