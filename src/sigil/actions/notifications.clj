(ns sigil.actions.notifications
  (:require [sigil.db.notifications :as notes]
            [sigil.auth :as auth]
            [hiccup.core :refer [html]]))

;;----------------------------------------
;; notification GETs

(def not-nil? (complement nil?))

(defn get-number-user-notifications
  [req]
  (let [user (auth/user-or-nil req)]
    (if (not-nil? user)
      (count (notes/get-user-notifications user))
      0)))

(defn get-user-notifications
  [req]
  (let [user (auth/user-or-nil req)
        notifications (notes/get-user-notifications user)]
    (html
     (for [n notifications]
       [:div.media
        [:a.media-left
         [:img.media-object.notification-icon
          {:src (:icon n)}]]
        [:div.media-body
         [:a {:href (:url n)} (:text n)]]
        [:div.media-right
         [:a {:href (str "/deletenote/" (:note_id n))}
          [:span.glyphicon.glyphicon-remove-sign]]]]))))

(defn delete-notification
  [req])
