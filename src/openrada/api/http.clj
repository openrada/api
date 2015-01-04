(ns openrada.api.http
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            [openrada.db.core :as db]
            [openrada.api.data :as data]
            [cuerdas.core :as str]
            [environ.core :refer [env]]))


(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false :escape-slash false))





(defrecord HTTPServer [port database server]
  component/Lifecycle
  (start [component]
    (println ";; Starting HTTP server")

    (defroutes app
      (ANY "/" [] (resource))
      (GET "/v1/parliament/:convocation/members/:id" [convocation id]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-member database id)))))


      (GET "/v1/parliament/:convocation/members" [convocation]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-members-from-convocation database (read-string convocation)))))))

    (def handler
      (-> app
          wrap-params))
    (let [server (jetty/run-jetty handler {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (println ";; Stopping HTTP server")
    (.stop server)
    component))

(defn new-http-server
  [port]
  (map->HTTPServer {:port port}))
