(defproject neo4j-clj "0.3.0"
  :description "Clojure bindings for Neo4j using the Java driver"
  :url "https://github.com/CYPP/neo4j-clj"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.neo4j/neo4j "3.0.1"]
                 [org.neo4j/neo4j-jdbc-driver "3.0.1"]]
  :profiles {:dev {:dependencies [[org.neo4j.test/neo4j-harness "3.0.1"]]}}

  ;; make sure you have your ~/.lein/credentials.clj.gpg setup correctly
  :deploy-repositories [["releases" {:url   "http://172.18.102.210/repository/CYPP-builds/"
                                     :creds :gpg}]
                        ["snapshots" {:url   "http://172.18.102.210/repository/CYPP-build-snapshots/"
                                      :creds :gpg}]])
