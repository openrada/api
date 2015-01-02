(ns openrada.api.data
  (:require [openrada.db.core :as db]
            [openrada.collector.core :as collector]
            [environ.core :refer [env]]))


(defn db-conf []
  {:host (env :rethinkdb-host)
   :port 28015})

(def db-conn (db/make-connection (db-conf)))

(defn get-more-data-for-member [member]
  (println "getting more data for" member)
  (let [full-data (collector/parse-member (:link member))]
      (println "full data" full-data)
      (db/update-member db-conn (:id member) full-data)))



;(db/update-member "0194b04d-4496-41b4-8458-ff34705e66c2" (collector/parse-member "http://gapp.rada.gov.ua/mps/info/page/15818"))

(defn fetch-members-8-data []
  (println "init")
  (let [members-8 (db/get-members-from-convocation db-conn 8)]
    (println "members 8 found" (> (count members-8) 0))
    (if (> (count members-8) 0)
      (map get-more-data-for-member members-8)
      (let [members-8 (collector/parse-members-8)]
        (db/save-members db-conn members-8)
        (map get-more-data-for-member (db/get-members-from-convocation db-conn 8))))))


(defn init []
  (fetch-members-8-data))
