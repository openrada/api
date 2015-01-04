(ns openrada.api.data
  (:require [openrada.db.core :as db]
            [openrada.collector.core :as collector]
            [environ.core :refer [env]]))


(defn db-conf []
  (println "host" (env :rethinkdb-host))
  {:host (env :rethinkdb-host)})

(defn get-more-data-for-member [db-conn member]
  (println "getting more data for" member)
  (let [full-data (collector/parse-member (:link member))]
      (println "full data" full-data)
      (db/update-member db-conn (:id member) full-data)))



(defn seed-8-members []
  (let [db-conn (db/make-connection (db-conf))
        members-8 (db/get-members-from-convocation db-conn 8)]
    (println "members 8 found" (> (count members-8) 0))
    (if (> (count members-8) 0)
      (map (fn [member]
             (get-more-data-for-member db-conn member)) members-8)
      (let [members-8 (collector/parse-members-8)]
        (db/save-members db-conn members-8)
        (map (fn [member]
             (get-more-data-for-member db-conn member)) (db/get-members-from-convocation db-conn 8))))))


(defn init []
  (println "init")
  (seed-8-members))
