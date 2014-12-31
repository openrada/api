(defproject openrada/api "0.1.0-SNAPSHOT"
  :description "Openrada API component"
  :url "https://github.com/openrada/api"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [openrada/db "0.1.0-SNAPSHOT"]
                 [openrada/collector "0.1.0-SNAPSHOT"]
                 [ring-server "0.3.1"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.3.1"]
                 [liberator "0.12.2"]
                 [org.clojure/data.json "0.2.5"]
                 [cuerdas "0.1.0"]
                 [environ "1.0.0"]]
  :plugins [[lein-environ "1.0.0"]
            [lein-ring "0.8.13"]
            [com.palletops/uberimage "0.4.1"]]
  :profiles {
     :dev  {:env {
              :rethinkdb-host "127.0.0.1"
              :rethinkdb-port "28015"}}}
  :main openrada.api.core
  :uberjar {:aot :all}
  :jvm-opts ^:replace ["-server"]
  :ring {:handler openrada.api.core/handler
         :init openrada.api.data/init})
