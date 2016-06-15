(ns sigil.actions.email
  (:require [postal.core :as mailer]))



(def smtp-server "smtp-relay.gmail.com")
(def smtp-port 587)
(def email-user "dominic@sigil.tech")
(def email-password "Sigiltech1027!")
(def from-email "contact@sigil.tech")

(defn send-email
  ([to-email subject body]
   (mailer/send-message {:host smtp-server
                         :user email-user
                         :pass email-password
                         ;:ssl true
                         :tls true
                         :port smtp-port ;; default is 25/no sll 468/yes ssl, tsl?
                         }
                        {:from from-email
                         :to to-email
                         :subject subject
                         :body body})))
