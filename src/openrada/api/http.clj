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


(defn db-conf []
  (println "host" (env :rethinkdb-host))
  {:host (env :rethinkdb-host)})


(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false :escape-slash false))

(defroutes app
  (ANY "/" [] (resource))
  (GET  "/v1/_seed" [] (data/init))
  (GET "/v1/parliament/:convocation/members/:id" [convocation id]
       (resource
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (let [db-conn (db/make-connection (db-conf))]
                        (to-json (db/get-member db-conn id))))))



  (GET "/v1/parliament/:convocation/members" [convocation]
       (resource
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (let [db-conn (db/make-connection (db-conf))]
                        (to-json (db/get-members-from-convocation db-conn (read-string convocation))))))))

(def handler
  (-> app
      wrap-params))



(defrecord HTTPServer [port server]
  component/Lifecycle
  (start [component]
    (println ";; Starting HTTP server")
    (let [server (jetty/run-jetty handler {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (println ";; Stopping HTTP server")
    (.stop server)
    component))

(defn new-http-server
  [port]
  (map->HTTPServer {:port port}))
