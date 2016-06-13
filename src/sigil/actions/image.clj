(ns sigil.actions.image
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.db.tags :as tags]
            [clojure.java.io :as io]
            [sigil.auth :as auth]
            [sigil.views.internal-error :refer [internal-error-handler]]
            [sigil.views.not-found :refer [not-found-handler]]
            [ez-image.core :as ezimg])
  (:import [javax.imageio.ImageIO]
           [java.awt.image.BufferedImage]))

(defn convert-image
  "Takes in the original temp image from the form with the final width and height and save path"
  [org-img w h new-img-str]
  (let [img-type (java.awt.image.BufferedImage/TYPE_INT_ARGB) ;;PNG == ARGB
        new-img (java.awt.image.BufferedImage. w h img-type)
        tmp (.createGraphics new-img)]
    (.drawImage tmp (javax.imageio.ImageIO/read (io/file org-img)) 0 0 w h nil)
    (.dispose tmp)
    (javax.imageio.ImageIO/write new-img "png" (io/file (format new-img-str)))
    true))


(defn update-user-icon
  [req]
  (let [upload-params (:params req)
        user-icon-file (:usericon100 upload-params)
        converted-image (ezimg/convert (:tempfile user-icon-file) [:constrain 100])
        user (auth/user-or-nil req)
        new-file-name (str (:username user) "_100.png")
        db-path (str "/db_imgs/user/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (println converted-image)
    (ezimg/save! converted-image
                 save-path)
    ;;(convert-image (user-icon-file :tempfile) 100 100 save-path)
    (db/db-trans [users/update-user user {:icon_100 db-path}])
    {:status 302
     :headers {"Location" "/settings?success=i"}}))

(defn update-org-icon-30
  [req]
  (let [upload-params (:params req)
        org-icon-file (upload-params :icon-30-upload)
        org (auth/user-org-or-nil (auth/user-or-nil req))
        new-file-name (str (:org_url org) "_30.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (if (not (nil? org))
      (do
        (convert-image (org-icon-file :tempfile) 30 30 save-path)
        (db/db-trans [orgs/update-org org {:icon_30 db-path}])
        {:status 302
         :headers {"Location" "/orgsettings"}}))))

(defn update-org-icon-100
  [req]
  (let [upload-params (:params req)
        org-icon-file (upload-params :icon-100-upload)
        org (auth/user-org-or-nil (auth/user-or-nil req))
        new-file-name (str (:org_url org) "_100.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (do
      (convert-image (org-icon-file :tempfile) 100 100 save-path)
      (db/db-trans [orgs/update-org org {:icon_100 db-path}])
      {:status 302
       :headers {"Location" "/orgsettings"}})))

(defn update-tag-icon-30
  [req]
  (let [user (auth/user-or-nil req)
        upload-params (:params req)
        tag-icon-file (upload-params :icon-30-upload)
        tag (tags/get-tag-by-id (read-string (upload-params :tagid)))
        org (orgs/get-org-by-id (read-string (upload-params :orgid)))
        new-file-name (str (:org_url org) "_" (:tag_id tag) "_30.png")
        db-path (str "/db_imgs/tag/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (if (and (some? user)
             (= (:org_id user) (:org_id org)))
      (do
        (convert-image (tag-icon-file :tempfile) 30 30 save-path)
        (db/db-trans [tags/update-tag tag {:icon_30 db-path}])
        {:status 302
         :headers {"Location" "/orgsettings"}})
      (not-found-handler req "Unauthorized attempt to change tag icon."))))

(defn update-org-banner
  [req]
  (let [upload-params (:params req)
        org-icon-file (upload-params :banner-upload)
        org (auth/user-org-or-nil (auth/user-or-nil req))
        new-file-name (str (:org_url org) "_banner.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (do
      (convert-image (org-icon-file :tempfile) 1000 200 save-path)
      (db/db-trans [orgs/update-org org {:banner db-path}])
      {:status 302
       :headers {"Location" "/orgsettings"}})))
