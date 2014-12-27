(ns openrada.api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            [ring.adapter.jetty :as jetty]
            [openrada.db.core :as db]))




(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false))

(defroutes app
  (ANY "/" [] (resource))
  (GET "/v1/parliament/:parliament/:convocation/members" [id] (resource
                           :available-media-types ["application/json"]
                           :handle-ok (fn [ctx]
                                        (to-json (db/get-member id)))))
  (GET "/v1/parliament/:parliament/:convocation/members" [] (resource
                           :available-media-types ["application/json"]
                           :handle-ok (fn [ctx]
                                        (to-json (db/get-members))))))

(def handler
  (-> app
      wrap-params))



(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))]
    (jetty/run-jetty
      handler
      {:port port})))
