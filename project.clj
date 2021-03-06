(defproject openrada/api "0.1.0-SNAPSHOT"
  :description "Openrada API component"
  :url "https://api.openrada.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :java-agents [[com.newrelic.agent.java/newrelic-agent "2.19.0"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [openrada/db "0.1.0-SNAPSHOT"]
                 [openrada/collector "0.1.0-SNAPSHOT"]
                 [ring-server "0.4.0"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.3.1"]
                 [liberator "0.12.2"]
                 [org.clojure/data.json "0.2.5"]
                 [cuerdas "0.3.0"]
                 [buddy "0.2.3"]
                 [environ "1.0.0"]
                 [com.stuartsierra/component "0.2.2"]
                 [clansi "1.0.0"]
                 [clj-time "0.9.0"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-ring "0.9.1"]]
  :profiles {
     :dev  {:env {
              :rethinkdb-host "127.0.0.1"}}}
  :main openrada.api.core
  :aot [openrada.api.core]
  :jvm-opts ^:replace ["-server"]
  :ring {:handler openrada.api.http/handler})
