(ns neo4j-clj.compability
  "Deprecated, use `neo4j-clj.compatibility`."
  (:require [neo4j-clj.compatibility]))

(def clj->neo4j
  "## Convert to Neo4j

  Neo4j expects a map of key/value pairs. The map has to be constructed as
  a `Values.parameters` instance which expects the values as an `Object` array"
  neo4j-clj.compatibility/clj->neo4j)

(def neo4j->clj
          "## Convert from Neo4j

           Neo4j returns results as `StatementResults`, which contain `InternalRecords`,
           which contain `InternalPairs` etc. Therefore, this multimethod recursively
           calls itself with the extracted content of the data structure until we have
           values, lists or `nil`."
          neo4j-clj.compatibility/neo4j->clj)