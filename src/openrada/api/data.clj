(ns openrada.api.data
  (:require [com.stuartsierra.component :as component]
            [openrada.db.core :as db]
            [openrada.collector.core :as collector]))



(defn get-more-data-for-member [db member]
  (println "getting more data for" member)
  (let [full-data (collector/parse-member (:link member))]
      (println "full data" full-data)
      (db/update-member db (:id member) full-data)))



(defn seed-8-members [db]
  (let [members-8 (db/get-members-from-convocation db 8)]
    (println "members 8 found" (> (count members-8) 0))
    (if (> (count members-8) 0)
      (map (fn [member]
             (get-more-data-for-member db member)) members-8)
      (let [members-8 (collector/parse-members-8)]
        (db/save-members db members-8)
        (map (fn [member]
             (get-more-data-for-member db member)) (db/get-members-from-convocation db 8))))))


(defn init [database]
  (println "init")
  (seed-8-members database))




(defrecord DataCollector [database]
  component/Lifecycle

  (start [this]
    (println ";; Starting datacollector")
      (init database)
      this)

  (stop [this]
    (println ";; Stopping datacollector")
    this))


(defn new-datacollector
  []
  (map->DataCollector {}))

