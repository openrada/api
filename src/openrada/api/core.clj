(ns openrada.api.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [openrada.api.http :refer [new-http-server]]))


(def http-server (new-http-server 3000))

(defn -main [& args]
  (alter-var-root #'http-server component/start))
