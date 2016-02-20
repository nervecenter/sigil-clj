(ns sigil.logout)

(defn logout-handler [req]
  (let [return ((:query-params req) "return")]
    {:status 302
     :headers {"Location" return}
     :body ""
     :cookies {:user {:value ""
                      :max-age 0}}}))
