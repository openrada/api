(ns openrada.api.core
  (:gen-class)
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            [ring.adapter.jetty :refer [run-jetty]]
            [openrada.db.core :as db]
            [cuerdas.core :as str]
            [environ.core :refer [env]]))





(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false))

(defroutes app
  (ANY "/" [] (resource))
  (GET "/v1/parliament/:convocation/members/:id" [convocation id]
       (resource
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (let [db-conn (db/make-connection {:host (env :rethinkdb-host)
                                                         :port (read-string (env :rethinkdb-port))})]
                        (to-json (db/get-member db-conn id))))))



  (GET "/v1/parliament/:convocation/members" [convocation]
       (resource
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (let [db-conn (db/make-connection {:host (env :rethinkdb-host)
                                                         :port (read-string (env :rethinkdb-port))})]
                        (to-json (db/get-members-from-convocation db-conn (read-string convocation))))))))

(def handler
  (-> app
      wrap-params))


(defn -main [& args] (run-jetty handler {:port 3000}))
