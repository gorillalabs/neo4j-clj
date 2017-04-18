(defproject neo4j-clj "0.1.0-SNAPSHOT"
  :description "Clojure bindings for Neo4j using the Java driver"
  :url "https://github.com/CYPP/neo4j-clj"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.neo4j/neo4j-bolt "3.0.1"]
                 [org.neo4j/neo4j-kernel "3.0.1"]
                 [org.neo4j/neo4j-io "3.0.1"]
                 [org.neo4j/neo4j-dbms "3.0.1"]
                 [org.neo4j/neo4j-graphdb-api "3.0.1"]
                 [org.neo4j/neo4j-jdbc-bolt "3.0.1"]
                 [org.neo4j/neo4j-jdbc-driver "3.0.1"]
                 [org.neo4j/neo4j "3.0.1"]
                 [org.neo4j.test/neo4j-harness "3.0.1"]
                 [junit/junit "4.12"]]
  :dev-dependencies [[org.neo4j/neo4j "3.0.1"]])