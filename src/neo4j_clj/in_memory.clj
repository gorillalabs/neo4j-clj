(ns neo4j-clj.in-memory
  "This namespace contains the logic to connect to JVM-local in-memory Neo4j
  instances, esp. for testing."
  (:require [neo4j-clj.core :refer [connect]]
            [neo4j-clj.compatibility :refer [neo4j->clj clj->neo4j]]
            [clojure.java.io :as io])
  (:import (java.net ServerSocket)
           (org.neo4j.driver.internal.logging ConsoleLogging)
           (java.util.logging Level)
           (org.neo4j.harness Neo4jBuilders Neo4j)))

;; In-memory for testing

(defn- get-free-port []
  (let [socket (ServerSocket. 0)
        port (.getLocalPort socket)]
    (.close socket)
    port))

(defn- create-temp-uri
  "In-memory databases need an uri to communicate with the bolt driver.
  Therefore, we need to get a free port."
  []
  (str "localhost:" (get-free-port)))

(defn- in-memory-db
  "In order to store temporary large graphs, the embedded Neo4j database uses a
  directory and binds to an url. We use the temp directory for that."
  []
  (.build (Neo4jBuilders/newInProcessBuilder)))

(defn create-in-memory-connection
  "To make the local db visible under the same interface/map as remote
  databases, we connect to the local url. To be able to shutdown the local db,
  we merge a destroy function into the map that can be called after testing.

  _All_ data will be wiped after shutting down the db!"
  []
  (let [url (create-temp-uri)
        ^Neo4j db (in-memory-db)]
    (merge (connect (.boltURI db) {:logging (ConsoleLogging. Level/WARNING)})
           {:destroy-fn #(.close db)})))
