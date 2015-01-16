(ns openrada.api.data
  (:require [com.stuartsierra.component :as component]
            [openrada.db.core :as db]
            [openrada.collector.members :as members-coll]
            [openrada.collector.committees :as committees-coll]
            [openrada.collector.factions :as factions-coll]
            [openrada.collector.registrations :as registrations-coll]
            [clansi :refer [style]]))



(defn update-member-with-bio [db member]
  (println "getting more data for" member)
  (let [full-data (members-coll/parse-member (:link member))]
    (println "full data" full-data)
    (db/update-member db (:id member) full-data)))



(defn seed-members [db convocation]
  (let [members (db/get-members-from-convocation db convocation)]
    (println "members" convocation "found" (> (count members) 0))
    (if (= (count members) 0)
      (let [members-8 (members-coll/parse-members 8)]
        (db/save-members db members-8)))))

(defn update-members-with-bios [db convocation]
  (let [members (db/get-members-from-convocation db convocation)]
    (doall (map #(update-member-with-bio db %) members))))


(defn seed-committees [db convocation]
  (let [committees (db/get-committees-from-convocation db convocation)]
    (println "committees" convocation "found" (> (count committees) 0))
    (if (= (count committees) 0)
      (let [committees-8 (committees-coll/parse-committees 8)]
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
                                                    :committee_role (:role mem)
                                                   })
                    ) (:members comm)))

             ) committees))
    ))


(defn seed-factions [db convocation]
  (let [factions (db/get-factions-from-convocation db convocation)]
    (println "factions" convocation "found" (> (count factions) 0))
    (if (= (count factions) 0)
      (let [factions-8 (factions-coll/parse-factions 8)]
        (db/save-factions db factions-8)))))

(defn update-members-with-faction [db convocation]
  (let [factions (db/get-factions-from-convocation db convocation)]
    (doall
      (map (fn [comm]
             (println "process faction" comm)
             (doall
                (map (fn [mem]
                    (println "process faction member" mem)
                    (db/update-member-by-full-name db (:member mem)
                                                   {
                                                    :faction_id (:id comm)
                                                    :faction_role (:role mem)
                                                   })

                    ) (:members comm)))

             ) factions))
    ))




(defn update-member-registrations [db member all-regs]
  (let [current-regs (db/get-registrations-for-member db (:id member))
        regs-to-insert (remove (fn [reg]
                                 (some (fn [curr-reg]
                                         (and
                                          (= (:type reg) (:type curr-reg))
                                          (= (:reg_type reg) (:reg_type curr-reg))
                                          (= (:date reg) (:date curr-reg)))
                                         ) current-regs)
                                 ) all-regs)
        to-insert (map (fn [reg]
                         (assoc reg :member_id (:id member))
                         ) regs-to-insert)
        ]
    (db/save-registrations db to-insert)))

(defn update-registrations [db convocation]
  (let [members (db/get-members-from-convocation db convocation)]
    (doall
     (map (fn [member]
            (let [online-regs (registrations-coll/parse-member-online-registrations
                               (:online_registrations_link member))
                  offline-regs (registrations-coll/parse-member-offline-registrations
                               (:offline_registrations_link member))
                  all-regs (concat online-regs offline-regs)
                  ]
              (update-member-registrations db member all-regs)
              )
            ) members))))



(defn init [database]
  (println (style "init start" :green))
  ; members
  (seed-members database 8)
  (update-members-with-bios database 8)
  (println (style "saved members" :green))
  ; committees
  (seed-committees database 8)
  (update-members-with-committee database 8)
  (db/remove-field database "committees" "members")
  (println (style "saved committees" :green))
  ; factions
  (seed-factions database 8)
  (update-members-with-faction database 8)
  (db/remove-field database "factions" "members")
  (println (style "saved factions" :green))
  ; registrations
  (update-registrations database 8)
  (println (style "saved registrations" :green))
  (println (style "init finish" :green)))



(defrecord DataCollector [database]
  component/Lifecycle

  (start [this]
    (println (style ";; Starting datacollector" :green))
      (init database)
      this)

  (stop [this]
    (println (style ";; Stopping datacollector" :green))
    this))


(defn new-datacollector
  []
  (map->DataCollector {}))


