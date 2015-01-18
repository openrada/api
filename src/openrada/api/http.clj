(ns openrada.api.http
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            [openrada.db.core :as db]
            [openrada.api.data :as data]
            [cuerdas.core :as str]
            [clansi :refer [style]]
            [environ.core :refer [env]]))


(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false :escape-slash false))




(defn json-success [ctx]
  (liberator.representation/ring-response
                 {:headers {"Access-Control-Allow-Origin" "https://api.openrada.com"
                            "Access-Control-Allow-Methods" "GET"
                            "Content-Type" "application/json;charset=utf-8"}
                  :body (to-json (get ctx :result))}))



(defrecord HTTPServer [port database server]
  component/Lifecycle
  (start [component]
    (println (style ";; Starting HTTP server" :green))

    (defroutes app
      (ANY "/" [] (resource))


      (GET "/v1/parliament/:convocation/members" [convocation]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-members-full database (read-string convocation))})
             :handle-ok json-success))


      (GET "/v1/parliament/:convocation/members/:id" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-member-full database (read-string convocation) id)})
             :handle-ok json-success))

      (GET "/v1/parliament/:convocation/members/:id/registrations" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-registrations-for-member database id)})
             :handle-ok json-success))

      (GET "/v1/parliament/:convocation/factions" [convocation]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-factions-full database (read-string convocation))})
             :handle-ok json-success))

      (GET "/v1/parliament/:convocation/factions/:id" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-faction-full database (read-string convocation) id)})
             :handle-ok json-success))

      (GET "/v1/parliament/:convocation/committees" [convocation]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-committees-full database (read-string convocation))})
             :handle-ok json-success))

      (GET "/v1/parliament/:convocation/committees/:id" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :malformed? (fn [_] (not (number? (read-string convocation))))
             :exists? (fn [ctx]
                        {:result (db/get-committee-full database (read-string convocation) id)})
             :handle-ok json-success))



      )

    (let [handler(wrap-params app)
          server (jetty/run-jetty handler {:port port :join? false})]
          (assoc component :server server)))
  (stop [component]
    (println (style ";; Stopping HTTP server" :green))
    (.stop server)
    component))

(defn new-http-server
  [port]
  (map->HTTPServer {:port port}))
