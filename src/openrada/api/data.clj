(ns openrada.api.data
  (:require [openrada.db.core :as db]
            [openrada.collector.core :as collector]))


(defn get-more-data-for-member [member]
    (db/update-member (:id member)
       (collector/parse-member (:link member))))


(defn fetch-members-8-data []
  (let [members-8 (collector/parse-members-8)]
    ;(db/save-members members-8)
    ;(map get-more-data-for-member (db/get-members-from-convocation 8))
    )
  )

