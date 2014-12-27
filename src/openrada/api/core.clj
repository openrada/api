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
  (GET "/deputies/:id" [id] (resource
                           :available-media-types ["application/json"]
                           :handle-ok (fn [ctx]
                                        (to-json (db/get-deputy id)))))
  (GET "/deputies" [] (resource
                           :available-media-types ["application/json"]
                           :handle-ok (fn [ctx]
                                        (to-json (db/get-deputies))))))

(def handler
  (-> app
      wrap-params))



(defn -main []
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))]
    (jetty/run-jetty
      handler
      {:port port})))
