(ns openrada.api.data
  (:require [openrada.db.core :as db]
            [openrada.collector.core :as collector]))



;(openrada.collector.core/parse-deputies-8)

;(def members-8 (collector/parse-deputies-8))





;(db/save-members members-8)

;(db/get-deputies)

;(collect/parse-deputy "http://gapp.rada.gov.ua/mps/info/page/8718")
;      (db/update-deputy "027fa5df-cbd4-4138-8b7b-77078d2e7f28"
;         (collect/parse-deputy "http://gapp.rada.gov.ua/mps/info/page/8718"))

(defn get-more-data-for-deputy [deputy]
      (db/update-member (:id deputy)
         (collect/parse-deputy (:link deputy))))

;(def ds (db/get-deputies))

(;def d (second ds))

;(:link d)
;(collect/parse-deputy (:link d))
;(get-more-data-for-deputy (second (db/get-deputies)))
;(map get-more-data-for-deputy (db/get-deputies))
