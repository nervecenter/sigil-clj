(ns sigil.actions.tag
  (:require [sigil.db.tags :as tags]
            [sigil.db.core :as db]
            [sigil.auth :as auth]))

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

(defn delete-org-tag
  [req]
  (let [org (auth/user-org-or-nil (auth/user-or-nil req))
        deleted-tag-params (:route-params req)]
    (do
      ;(tags/delete-tag __ true)
      {:status 302
       :headers {"Location" "/orgsettings"}})))
