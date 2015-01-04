(ns openrada.api.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [openrada.api.http :refer [new-http-server]]
            [openrada.db.component :refer [new-database]]
            [openrada.api.data :refer [new-datacollector]]
            [environ.core :refer [env]]))


(defn rada-system [config-options]
  (let [{:keys [rethinkdb-host port]} config-options]
    (-> (component/system-map
          :config-options config-options
          :db (new-database rethinkdb-host)
          :http (component/using
             (new-http-server port)
             {:database  :db}
          )
          :data-collector (component/using
             (new-datacollector)
             {:database  :db}
          )))))


(def system (rada-system {:rethinkdb-host (env :rethinkdb-host) :port 3000}))

(defn -main [& args]
  (alter-var-root #'system component/start))
