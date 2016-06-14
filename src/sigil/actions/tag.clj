(ns sigil.actions.tag
  (:require [sigil.db.tags :as tags]
            [sigil.db.core :as db]
            [sigil.db.orgs :refer [get-org-by-id]]
            [sigil.auth :refer [user-or-nil user-org-or-nil]]
            [sigil.helpers :refer [redirect error-redirect]]
            [sigil.views.internal-error :refer [internal-error-handler]]))

(defn add-tag
  [req]
  (let [user (user-or-nil req)
        new-tag-form (:params req)
        org (get-org-by-id (read-string (:orgid new-tag-form)))
        new-tag {:tag_name (:tag-name new-tag-form)
                 :icon_30 (rand-nth db/default_icon_30)
                 :org_id (:org_id org)}]
    (println "Org:" (:org_id org) "User:" (:org_id user))
    (if (= (:org_id org) (:org_id user))
      (if (= :success (db/db-trans [tags/create-tag new-tag]))
        (redirect "/orgsettings?v=s")
        (error-redirect "DB failed to make new tag." new-tag user "/orgsettings?v=q"))
      (error-redirect "Could not authorize new tag." new-tag user "/orgsettings?v=c"))))

;; TODO: Get this working using transactions.
(defn delete-org-tag
  [req]
  (let [user (user-or-nil req)
        org (user-org-or-nil user)
        deleted-tag-params (:params req)
        deleted-tag (tags/get-tag-by-id (read-string (:tagid deleted-tag-params)))
        move-to-tag (tags/get-tag-by-id (read-string (:moveto deleted-tag-params)))]
    (if (= deleted-tag move-to-tag)
      (redirect "/orgsettings?v=l")
      (if (= :success (db/db-trans [tags/delete-tag deleted-tag]
                         [tags/move-issues-from-tag-to-tag deleted-tag move-to-tag]))
       
        (redirect "/orgsettings?v=d")
        (error-redirect "Tag failed to delete." 
                        {:deleted deleted-tag
                         :moved-to move-to-tag} 
                        user
                        "/orgsettings?v=p")))))
