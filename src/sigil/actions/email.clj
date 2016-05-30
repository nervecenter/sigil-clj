(ns sigil.actions.email
  (:require [postal.core :as mailer]))



(def smtp-server "")
(def smtp-port "")
(def default-from-email "")
(def default-email-password "")


(defn send-email
  ([to-email subject body]
   (mailer/send-message {:host smtp-server
                         :user default-from-email
                         :pass default-email-password
                         :ssl true
                         ;:port 465 ;; default is 25/no sll 468/yes ssl, tsl?
                         }
                        {:from default-from-email
                         :to to-email
                         :subject subject
                         :body body})))




