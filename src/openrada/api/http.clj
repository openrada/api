(ns openrada.api.http
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [liberator.core :refer [resource defresource]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.cors :refer [wrap-cors]]
            [compojure.core :refer [defroutes ANY GET]]
            [openrada.db.core :as db]
            [openrada.api.data :as data]
            [cuerdas.core :as str]
            [clansi :refer [style]]
            [environ.core :refer [env]]))


(defn to-json [data]
  (clojure.data.json/write-str data :escape-unicode false :escape-slash false))





(defrecord HTTPServer [port database server]
  component/Lifecycle
  (start [component]
    (println (style ";; Starting HTTP server" :green))

    (defroutes app
      (ANY "/" [] (resource))


      ;;;;
      ;; @api {get} /parliament/:convocation/members Get all members of the parliament from the specific convocation.
      ;; @apiName GetMembers
      ;; @apiGroup Members
      ;; @apiParam {Number} convocation Parliament convocation. Current one is 8th.
      ;; @apiExample {shell} Example usage:
      ;;    curl -i https://api.openrada.com/v1/parliament/8/memebers
      ;; @apiSuccess (200) {Object[]} members List of parliament members.
      ;; @apiSuccess (200) {String} members.full_name Member's full name.
      ;; @apiSuccess (200) {String} members.short_name Member's short name.
      ;; @apiSuccess (200) {Date} members.dob Member's date of birth.
      ;; @apiSuccess (200) {Date} members.member_since Date of becoiming a parliamnet member.
      ;;;;

      (GET "/v1/parliament/:convocation/members" [convocation]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-members-full database (read-string convocation))))))

      ;;;;
      ;; @api {get} /parliament/:convocation/members/:id Get full information for the specific parliament member.
      ;; @apiName GetMember
      ;; @apiGroup Members
      ;; @apiParam {Number} convocation Parliament convocation. Current one is 8th.
      ;; @apiParam {Number} id Parliament member id.
      ;; @apiExample {shell} Example usage:
      ;;    curl -i https://api.openrada.com/v1/parliament/8/memebers/005ded9a-18c4-4f34-806c-80a82e9a7a26
      ;;;;

      (GET "/v1/parliament/:convocation/members/:id" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-member-full database (read-string convocation) id)))))


      (GET "/v1/parliament/:convocation/factions" [convocation]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-factions-full database (read-string convocation))))))

      (GET "/v1/parliament/:convocation/factions/:id" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-faction-full database (read-string convocation) id)))))

      (GET "/v1/parliament/:convocation/committees" [convocation]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-committees-full database (read-string convocation))))))

      (GET "/v1/parliament/:convocation/committees/:id" [convocation id]
           (resource
             :allowed-methods [:get]
             :available-media-types ["application/json"]
             :handle-ok (fn [ctx]
                            (to-json (db/get-committee-full database (read-string convocation) id)))))



      )

    (def handler
      (-> app
          (wrap-cors :access-control-allow-origin #"https://api.openrada.com"
                     :access-control-allow-methods [:get])
          wrap-params))
    (let [server (jetty/run-jetty handler {:port port :join? false})]
      (assoc component :server server)))
  (stop [component]
    (println (style ";; Stopping HTTP server" :green))
    (.stop server)
    component))

(defn new-http-server
  [port]
  (map->HTTPServer {:port port}))
