(ns sigil.actions.tag
  (:require [sigil.db.tags :as tags]
            [sigil.db.core :as db]
            [sigil.auth :as auth]))



(defn add-org-tag
  [req]
  (let [org (auth/user-org-or-nil (auth/user-or-nil req))
        new-tag-form (:form-params req)
        new-tag {:tag_url (new-tag-form "tag-url")
                 :tag_name (new-tag-form "tag-name")
                 :icon_30 (rand-nth sigil.db.orgs/default_org_icon_20)
                 :org_id (:org_id org)}]
    (do
      (db/db-trans [tags/create-tag new-tag])
      {:status 302
       :headers {"Location" "/orgsettings"}})))

