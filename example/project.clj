(defproject example "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [gorillalabs/neo4j-clj "4.0.2"]
                 [joplin.core "0.3.11"]]
  :profiles {:test {:dependencies [#_[org.neo4j.test/neo4j-harness "4.0.0"]]}}

  :main ^:skip-aot example.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
