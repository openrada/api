(ns openrada.api.data
  (:require [com.stuartsierra.component :as component]
            [openrada.db.core :as db]
            [openrada.collector.core :as collector]
            [openrada.collector.committees :as committees]))



(defn get-more-data-for-member [db member]
  (println "getting more data for" member)
  (let [full-data (collector/parse-member (:link member))]
      (println "full data" full-data)
      (db/update-member db (:id member) full-data)))



(defn seed-members [db convocation]
  (let [members (db/get-members-from-convocation db convocation)]
    (println "members " convocation " found" (> (count members) 0))
    (if (= (count members) 0)
      (let [members-8 (collector/parse-members-8)]
        (db/save-members db members-8)))))

(defn update-members-with-bios [db convocation]
  (let [members (db/get-members-from-convocation db convocation)]
    (map (fn [member]
             (get-more-data-for-member db member)) members)))


(defn seed-committees [db convocation]
  (let [committees (db/get-committees-from-convocation db convocation)]
    (println "committees " convocation " found" (> (count committees) 0))
    (if (= (count committees) 0)
      (let [committees-8 (committees/parse-committees 8)]
        (db/save-committees db committees-8)))))

(defn update-members-with-committee [db convocation]
  (let [committees (db/get-committees-from-convocation db convocation)]
    (doall
      (map (fn [comm]
             (println "process committe" comm)
             (doall
                (map (fn [mem]
                    (println "process commitee member" mem)
                    (db/update-member-by-full-name db (:member mem)
                                                   {
                                                    :committee_id (:id comm)
                                                    :role (:role mem)
                                                   })

                    ) (:members comm)))

             ) committees))
    ))


(defn init [database]
  (println "init")
  (seed-members database 8)
  (update-members-with-bios database 8)
  (seed-committees database 8)
  (update-members-with-committee database 8)
  (db/remove-field database "committees" "members")
  )
;(committees/parse-committees 8)

;(def db {:connection (db/make-connection "127.0.01")})
;(db/get-committees-from-convocation db 8)

;(update-members-with-committee db 8)
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


