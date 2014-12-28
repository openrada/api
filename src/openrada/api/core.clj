(ns openrada.api.core
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            [ring.adapter.jetty :as jetty]
            [openrada.db.core :as db]
            [cuerdas.core :as str]))



(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false))

(defroutes app
  (ANY "/" [] (resource))
  (GET "/v1/parliament/:convocation/members/:id" [convocation id]
       (resource
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (to-json (db/get-member id)))))
  (GET "/v1/parliament/:convocation/members" [convocation]
       (resource
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (to-json (db/get-members-from-convocation (str/parse-int convocation)))))))

(def handler
  (-> app
      wrap-params))


