(ns neo4j-clj.core
  "This namespace contains the logic to connect to Neo4j instances,
  create and run queries as well as creating an in-memory database for
  testing."
  (:require [neo4j-clj.compability :refer [neo4j->clj clj->neo4j]])
  (:import (org.neo4j.driver.v1 Values GraphDatabase AuthTokens)
           (org.neo4j.graphdb.factory GraphDatabaseSettings$BoltConnector
                                      GraphDatabaseFactory)
           (java.net ServerSocket)
           (java.io File)))

(defn create-connection
  "Returns a connection map from an url. Uses BOLT as the only communication
  protocol."
  ([url user password]
   (let [auth (AuthTokens/basic user password)
         db   (GraphDatabase/driver url auth)]
     {:url url, :user user, :password password, :db db}))
  ([url]
   (let [db (GraphDatabase/driver url)]
     {:url url, :db db})))

(defn- get-free-port []
  (.getLocalPort (ServerSocket. 0)))

(defn- create-temp-uri
  "In-memory databases need an uri to communicate with the bolt driver.
  Therefore, we need to get a free port."
  []
  (str "bolt://localhost:" (get-free-port)))

(defn- in-memory-db
  "In order to store temporary large graphs, the embedded Neo4j database uses a
  directory and binds to an url. We use the temp directory for that."
  [url]
  (let [bolt (GraphDatabaseSettings$BoltConnector. "0")
        temp (System/getProperty "java.io.tmpdir")]
    (-> (GraphDatabaseFactory.)
        (.newEmbeddedDatabaseBuilder (File. (str temp (System/currentTimeMillis))))
        ;; Configure db to use bolt
        (.setConfig (.type bolt) "BOLT")
        (.setConfig (.enabled bolt) "true")
        (.setConfig (.address bolt) url)
        (.newGraphDatabase))))

(defn create-in-memory-connection
  "To make the local db visible under the same interface/map as remote
  databases, we connect to the local url. To be able to shutdown the local db,
  we merge a destroy function into the map that can be called after testing.

  _All_ data will be wiped after shutting down the db!"
  []
  (let [url (create-temp-uri)
        db (in-memory-db url)]
    (merge (create-connection url)
           {:destroy-fn (fn [] (.shutdown db))})))

(defn destroy-in-memory-connection [connection]
  ((:destroy-fn connection)))

(defn get-session [connection]
  (.session (:db connection)))

(defn- run-query [sess query params]
  (neo4j->clj (.run sess query params)))

(defn create-query
  "Convenience function. Takes a cypher query as input, returns a function that
  takes a session (and parameter as a map, optionally) and return the query
  result as a map."
  [cypher]
  (fn
    ([sess] (run-query sess cypher {}))
    ([sess params] (run-query sess cypher (clj->neo4j params)))))

(defmacro defquery "Shortcut macro to define a named query."
  [name query]
  `(def ~name (create-query ~query)))