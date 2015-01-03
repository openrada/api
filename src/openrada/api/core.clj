(ns openrada.api.core
  (:gen-class)
  (:require [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer [defroutes ANY GET]]
            [ring.adapter.jetty :refer [run-jetty]]
            [openrada.db.core :as db]
            [openrada.api.data :as data]
            [cuerdas.core :as str]
            [environ.core :refer [env]]))


(defn db-conf []
  (println "host" (env :rethinkdb-host))
  {:host (env :rethinkdb-host)})


(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false))

(defroutes app
  (ANY "/" [] (resource))
  (GET  "/v1/_seed" [] (data/init))
  (GET "/v1/parliament/:convocation/members/:id" [convocation id]
       (resource
         :available-charsets ["iso-8859-1"]
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (let [db-conn (db/make-connection (db-conf))]
                        (to-json (db/get-member db-conn id))))))



  (GET "/v1/parliament/:convocation/members" [convocation]
       (resource
         :available-charsets ["iso-8859-1"]
         :available-media-types ["application/json"]
         :handle-ok (fn [ctx]
                      (let [db-conn (db/make-connection (db-conf))]
                        (to-json (db/get-members-from-convocation db-conn (read-string convocation))))))))

(def handler
  (-> app
      wrap-params))


(defn -main [& args] (run-jetty handler {:port 3000}))
