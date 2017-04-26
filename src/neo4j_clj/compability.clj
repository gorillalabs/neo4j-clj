(ns neo4j-clj.compability
  "Neo4j communicates with Java via custom data structures. Those are
  can contain lists, maps, nulls, values or combinations. This namespace
  has functions to help to convert between Neo4j's data structures and Clojure"
  (:require [clojure.walk])
  (:import (org.neo4j.driver.v1 Values)
           (org.neo4j.driver.internal InternalRecord InternalPair
                                      InternalStatementResult InternalNode)
           (org.neo4j.driver.internal.value NodeValue ScalarValueAdapter
                                            NullValue ListValue MapValue)
           (org.neo4j.cypher.internal.javacompat ExecutionResult)
           (java.util Map List)))

(defn clj->neo4j
  "## Convert to Neo4j

  Neo4j expects a map of key/value pairs. The map has to be constructed as
  a `Values.parameters` instance which expects the values as an `Object` array"
  [val]
  (->> val
       clojure.walk/stringify-keys
       (mapcat identity)
       (into-array Object)
       Values/parameters))

(defmulti neo4j->clj
          "## Convert from Neo4j

          Neo4j returns results as `StatementResults`, which contain `InternalRecords`,
          which contain `InternalPairs` etc. Therefore, this multimethod recursively
          calls itself with the extracted content of the data structure until we have
          values, lists or `nil`."
          class)



(defn transform [m]
  (let [f (fn [[k v]]
            [(if (string? k) (keyword k) k) (neo4j->clj v)]
            )]
    ;; only apply to maps
    (clojure.walk/postwalk
      (fn [x]
        (if (or (map? x) (instance? Map x))
          (with-meta (into {} (map f x))
                     (meta x))
          x))
      m)))

(defmethod neo4j->clj InternalStatementResult [record]
  (map neo4j->clj (iterator-seq record)))

(defmethod neo4j->clj InternalRecord [record]
  (apply merge (map neo4j->clj (.fields record))))

(defmethod neo4j->clj InternalPair [^InternalPair pair]
  {(-> pair .key keyword) (-> pair .value neo4j->clj)})

(defmethod neo4j->clj NodeValue [^NodeValue value]
  (transform (into {} (.asMap value))))

(defmethod neo4j->clj ScalarValueAdapter [^ScalarValueAdapter v]
  (.asObject v))

(defmethod neo4j->clj ListValue [^ListValue l]
  (map neo4j->clj (.asList l)))

(defmethod neo4j->clj MapValue [^MapValue l]
  (transform (into {} (.asMap l))))

(defmethod neo4j->clj InternalNode [^InternalNode n]
  (with-meta (transform (into {} (.asMap n)))
             {:labels (.labels n)
              :id     (.id n)}))

(defmethod neo4j->clj NullValue [n]
  nil)

(defmethod neo4j->clj List [^List l]
  (transform (into [] l)))

(defmethod neo4j->clj :default [x]
  x)
