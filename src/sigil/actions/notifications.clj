(ns sigil.actions.notifications
  (:require [sigil.db.notifications :as notes]
            [sigil.auth :as auth]
            [hiccup.core :refer [html]]
            [cheshire.core :as json]))

;;----------------------------------------
;; notification GETs

(defn number-notes-handler
  [req]
  (let [user (auth/user-or-nil req)]
    (json/generate-string
     {:numnotes (if (some? user)
                  (notes/get-number-notifications-by-user user)
                  0)})))

(defn check-notes-handler
  [req]
  (let [user (auth/user-or-nil req)
        num-notes (notes/get-number-notifications-by-user user)]
    (if (= 0 num-notes)
      (json/generate-string [0])
      (let [notifications (notes/get-user-notifications user)]
        (json/generate-string (for [n notifications]
                                {:id (:note_id n)
                                 :icon (:icon n)
                                 :url (:url n)
                                 :message (:message n)})))
      ;; (let [notifications (notes/get-user-notifications user)]
      ;;   (html
      ;;    (for [n notifications]
      ;;      [:div.media
      ;;       [:a.media-left
      ;;        [:img.media-object.notification-icon
      ;;         {:src (:icon n)}]]
      ;;       [:div.media-body
      ;;        [:a {:href (:url n)} (:message n)]]
      ;;       [:div.media-right
      ;;        [:a {:href (str "/deletenote/" (:note_id n))}
      ;;         [:span.glyphicon.glyphicon-remove-sign]]]])))
      )))

(defn delete-notification-handler
  [req]
  (let [user (auth/user-or-nil req)
        notification (notes/get-notification-by-id (read-string (:id (:params req))))]
    (if (not= (:user_id user) (:user_id notification))
      {:status 403}
      (do
        (notes/delete-notification notification)
        {:status 200}))))
