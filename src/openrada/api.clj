(ns openrada.api
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            ;[openrada.db :as db]
            ))




(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false))

(defroutes app
  (ANY "/" [] (resource))
 ; (GET "/deputies/:id" [id] (resource
 ;                          :available-media-types ["application/json"]
 ;                          :handle-ok (fn [ctx]
 ;                                       (to-json (db/get-deputy id)))))
  (GET "/deputies" [] (resource
                           :available-media-types ["application/json"]
                           :handle-ok (fn [ctx]
                                        (to-json (db/get-deputies))))))

(def handler
  (-> app
      wrap-params))
