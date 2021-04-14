(ns neo4j-clj.compatibility
  "Neo4j communicates with Java via custom data structures. Those are
  can contain lists, maps, nulls, values or combinations. This namespace
  has functions to help to convert between Neo4j's data structures and Clojure"
  (:require [clojure.walk])
  (:import (org.neo4j.driver Values)
           (org.neo4j.driver.internal
             InternalRecord
             InternalPair
             InternalRelationship
             InternalNode InternalResult)
           (org.neo4j.driver.internal.value
             NodeValue
             NullValue
             ListValue
             MapValue
             RelationshipValue
             StringValue
             BooleanValue
             NumberValueAdapter
             ObjectValueAdapter)
           (java.util Map List)
           (clojure.lang ISeq)))

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
            [(if (string? k) (keyword k) k) (neo4j->clj v)])]

    ;; only apply to maps
    (clojure.walk/postwalk
      (fn [x]
        (if (or (map? x) (instance? Map x))
          (with-meta (into {} (map f x))
                     (meta x))
          x))
      m)))

(defmethod neo4j->clj InternalResult [record]
  (map neo4j->clj (iterator-seq record)))

(defmethod neo4j->clj InternalRecord [record]
  (apply merge (map neo4j->clj (.fields record))))

(defmethod neo4j->clj InternalPair [^InternalPair pair]
  (let [k (-> pair .key keyword)
        v (-> pair .value neo4j->clj)]
    {k v}))

(defmethod neo4j->clj NodeValue [^NodeValue value]
  (transform (into {} (.asMap value))))

(defmethod neo4j->clj RelationshipValue [^RelationshipValue value]
  (transform (into {} (.asMap (.asRelationship value)))))

(defmethod neo4j->clj StringValue [^StringValue v]
  (.asObject v))

(defmethod neo4j->clj ObjectValueAdapter [^ObjectValueAdapter v]
  (.asObject v))

(defmethod neo4j->clj BooleanValue [^BooleanValue v]
  (.asBoolean v))

(defmethod neo4j->clj NumberValueAdapter [^NumberValueAdapter v]
  (.asNumber v))

(defmethod neo4j->clj ListValue [^ListValue l]
  (map neo4j->clj (into [] (.asList l))))

(defmethod neo4j->clj ISeq [^ISeq s]
  (map neo4j->clj s))

(defmethod neo4j->clj MapValue [^MapValue l]
  (transform (into {} (.asMap l))))

(defmethod neo4j->clj InternalNode [^InternalNode n]
  (with-meta (transform (into {} (.asMap n)))
             {:labels (.labels n)
              :id     (.id n)}))

(defmethod neo4j->clj InternalRelationship [^InternalRelationship r]
  (neo4j->clj (.asValue r)))

(defmethod neo4j->clj NullValue [n]
  nil)

(defmethod neo4j->clj List [^List l]
  (map neo4j->clj (into [] l)))

(defmethod neo4j->clj Map [^Map m]
  (transform (into {} m)))

(defmethod neo4j->clj :default [x]
  x)
