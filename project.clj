(defproject openrada.api "0.1.0-SNAPSHOT"
  :description "API collection component"
  :url "https://github.com/openrada/api"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring-server "0.3.1"]
                 [ring/ring-jetty-adapter "1.2.1"]
                 [compojure "1.1.8"]
                 [liberator "0.12.2"]
                 [org.clojure/data.json "0.2.5"]]
  :target-path "target/%s")
