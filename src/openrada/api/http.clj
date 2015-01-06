(ns openrada.api.http
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
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
                            (to-json (db/get-member-full database (read-string convocation) id)))))


      (GET "/v1/parliament/:convocation/members" [convocation]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-members-full database (read-string convocation))))))


      (GET "/v1/parliament/:convocation/factions/:id" [convocation id]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-faction-full database (read-string convocation) id)))))


      (GET "/v1/parliament/:convocation/factions" [convocation]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-factions-full database (read-string convocation))))))

      (GET "/v1/parliament/:convocation/committees/:id" [convocation id]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-committee-full database (read-string convocation) id)))))


      (GET "/v1/parliament/:convocation/committees" [convocation]
           (resource
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-committees-full database (read-string convocation))))))
      )

    (def handler
      (-> app
          (wrap-cors :access-control-allow-origin #"https://api.openrada.com"
                     :access-control-allow-methods [:get])
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
