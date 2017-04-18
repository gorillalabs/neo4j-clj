(ns neo4j-clj.compability
  "Neo4j communicates with Java via custom data structures. Those are
  can contain lists, maps, nulls, values or combinations. This namespace
  has functions to help to convert between Neo4j's data structures and Clojure"
  (:import (org.neo4j.driver.v1 Values)
           (org.neo4j.driver.internal InternalStatementResult InternalRecord InternalPair)
           (org.neo4j.driver.internal.value NodeValue ScalarValueAdapter NullValue ListValue)
           (org.neo4j.cypher.internal.javacompat ExecutionResult)))

(defn clj->neo4j
  "## Convert to Neo4j

  Neo4j expects a map of key/value pairs. The map has to be constructed as
  a ```Values.parameters``` instance which expects the values as an ```Object``` array"
  [val]
  (->> val
       clojure.walk/stringify-keys
       (mapcat identity)
       (into-array Object)
       Values/parameters))

(defmulti neo4j->clj class)

(defmethod neo4j->clj InternalStatementResult [record] (map neo4j->clj (iterator-seq record)))
(defmethod neo4j->clj InternalRecord [record] (apply merge (map neo4j->clj (.fields record))))
(defmethod neo4j->clj InternalPair [pair] {(-> pair .key keyword) (-> pair .value neo4j->clj)})
(defmethod neo4j->clj NodeValue [value] (clojure.walk/keywordize-keys (into {} (.asMap value))))
(defmethod neo4j->clj ScalarValueAdapter [v] (.asObject v))
(defmethod neo4j->clj ListValue [l] (.asList l))
(defmethod neo4j->clj NullValue [n] nil)

(defmethod neo4j->clj ExecutionResult [r] (iterator-seq r))
