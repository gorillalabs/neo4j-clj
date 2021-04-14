(defproject example "0.1.0-SNAPSHOT"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [gorillalabs/neo4j-clj "4.1.0"]
                 [joplin.core "0.3.11"]]
  :profiles {:test {:dependencies [#_[org.neo4j.test/neo4j-harness "4.0.0"]]}
             :uberjar {:aot :all}}

  :main ^:skip-aot example.core)
