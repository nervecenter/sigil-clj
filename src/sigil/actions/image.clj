(ns sigil.actions.image
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [clojure.java.io :as io]
            [sigil.auth :as auth])
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
    (javax.imageio.ImageIO/write new-img "png" (io/file (format new-img-str)))))


(defn update-user-icon
  [req]
  (let [upload-params (:params req)
        user-icon-file (upload-params :usericon100)
        user (auth/user-or-nil req)
        new-file-name (str (:username user) "_100.png")
        db-path (str "/db_imgs/user/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (do
      (convert-image (user-icon-file :tempfile) 100 100 save-path)
      (db/db-trans [users/update-user user {:icon_100 db-path}])
      {:status 302
       :headers {"Location" "/settings"}})))

(defn update-org-icon-30
  [req]
  (let [upload-params (:params req)
        org-icon-file (upload-params :icon-30-upload)
        org (auth/user-org-or-nil (auth/user-or-nil req))
        new-file-name (str (:org_name org) "_30.png")
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
        new-file-name (str (:org_name org) "_100.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (do
      (convert-image (org-icon-file :tempfile) 100 100 save-path)
      (db/db-trans [orgs/update-org org {:icon_100 db-path}])
      {:status 302
       :headers {"Location" "/orgsettings"}})))

(defn update-org-banner
  [req]
  (let [upload-params (:params req)
        org-icon-file (upload-params :banner-upload)
        org (auth/user-org-or-nil (auth/user-or-nil req))
        new-file-name (str (:org_name org) "_banner.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (do
      (convert-image (org-icon-file :tempfile) 1000 1000 save-path)
      (db/db-trans [orgs/update-org org {:banner db-path}])
      {:status 302
       :headers {"Location" "/orgsettings"}})))
