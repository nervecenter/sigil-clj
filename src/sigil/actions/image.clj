(ns sigil.actions.image
  (:require [sigil.db.core :as db]
            [sigil.db.users :as users]
            [sigil.db.orgs :as orgs]
            [sigil.db.tags :as tags]
            [clojure.java.io :as io]
            [sigil.auth :as auth]
            [sigil.helpers :refer [redirect error-redirect]]
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
        user (auth/user-or-nil req)
        new-file-name (str (:username user) "_100.png")
        db-path (str "/db_imgs/user/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (if (= :success (db/db-trans [users/update-user user {:icon_100 db-path}]))
      (try
        (ezimg/save! (ezimg/convert (:tempfile user-icon-file) [:distort 100])
                     save-path)
        (catch Exception e
          (error-redirect "Couldn't save user icon."
                          (.getMessage e)
                          user
                          "/settings?v=l"))
        (finally (redirect "/settings?v=i")))
      (redirect "/settings?v=d"))))

(defn update-org-icon-30
  [req]
  (let [upload-params (:params req)
        user (auth/user-or-nil req)
        org-icon-file (:icon-30-upload upload-params)
        org (auth/user-org-or-nil user)
        new-file-name (str (:org_url org) "_30.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (if (some? org)
      (if (= :success (db/db-trans [orgs/update-org org {:icon_30 db-path}]))
        (try
          (ezimg/save! (ezimg/convert (:tempfile org-icon-file) [:distort 30])
                       save-path)
          (catch Exception e
            (error-redirect "Couldn't save org icon30; image incorrect?"
                            (.getMessage e)
                            user
                            "/orgsettings?v=a"))
          (finally (redirect "/orgsettings?v=t")))
        (error-redirect "DB couldn't change org icon30."
                        db-path
                        user
                        "/orgsettings?v=e"))
      (error-redirect "User not authorized to change org icon30."
                      org
                      user
                      "/orgsettings?v=e"))))

(defn update-org-icon-100
  [req]
  (let [upload-params (:params req)
        user (auth/user-or-nil req)
        org-icon-file (upload-params :icon-100-upload)
        org (auth/user-org-or-nil user)
        new-file-name (str (:org_url org) "_100.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (if (some? org)
      (if (= :success (db/db-trans [orgs/update-org org {:icon_100 db-path}]))
        (try
          (ezimg/save! (ezimg/convert (:tempfile org-icon-file) [:distort 100])
                       save-path)
          (catch Exception e
            (error-redirect "Couldn't save org icon100; image incorrect?"
                            (.getMessage e)
                            user
                            "/orgsettings?v=u"))
          (finally (redirect "/orgsettings?v=i")))
        (error-redirect "DB couldn't change org icon100."
                        db-path
                        user
                        "/orgsettings?v=y"))
      (error-redirect "User not authorized to change org icon100."
                      org
                      user
                      "/orgsettings?v=y"))))

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
    (if (some? org)
      (if (= :success (db/db-trans [tags/update-tag tag {:icon_30 db-path}]))
        (try
          (ezimg/save! (ezimg/convert (:tempfile tag-icon-file) [:distort 30])
                       save-path)
          (catch Exception e
            (error-redirect "Couldn't save tag icon30; image incorrect?"
                            (.getMessage e)
                            user
                            "/orgsettings?v=r"))
          (finally (redirect "/orgsettings?v=g")))
        (error-redirect "DB couldn't change tag icon30."
                        db-path
                        user
                        "/orgsettings?v=m"))
      (error-redirect "User not authorized to change tag icon30."
                      {:tag tag :org org}
                      user
                      "/orgsettings?v=m"))))

(defn update-org-banner
  [req]
  (let [upload-params (:params req)
        user (auth/user-or-nil req)
        org-banner-file (upload-params :banner-upload)
        org (auth/user-org-or-nil user)
        new-file-name (str (:org_url org) "_banner.png")
        db-path (str "/db_imgs/org/" new-file-name)
        save-path (str "resources/public/" db-path)]
    (if (some? org)
      (if (= :success (db/db-trans [orgs/update-org org {:banner db-path}]))
        (try
          (ezimg/save! (ezimg/convert (:tempfile org-banner-file) [:distort 1000 200])
                       save-path)
          (catch Exception e
            (error-redirect "Couldn't save org banner; image incorrect?"
                            (.getMessage e)
                            user
                            "/orgsettings?v=k"))
          (finally (redirect "/orgsettings?v=b")))
        (error-redirect "DB couldn't change org banner."
                        db-path
                        user
                        "/orgsettings?v=u"))
      (error-redirect "User not authorized to change org banner."
                      org
                      user
                      "/orgsettings?v=u"))))
