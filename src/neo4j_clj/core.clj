(ns neo4j-clj.core
  "This namespace contains the logic to connect to Neo4j instances,
  create and run queries as well as creating an in-memory database for
  testing."
  (:require [neo4j-clj.compability :refer [neo4j->clj clj->neo4j]]
            [clojure.java.io :as io])
  (:import (org.neo4j.driver.v1 Values GraphDatabase AuthTokens Transaction Config)
           (org.neo4j.driver.v1.exceptions TransientException)
           (org.neo4j.graphdb.factory GraphDatabaseSettings$BoltConnector
                                      GraphDatabaseFactory)
           (java.net ServerSocket)
           (java.io File)
           (org.neo4j.driver.internal.logging ConsoleLogging)
           (java.util.logging Level)))

;; Connecting to dbs

(defn connect
  "Returns a connection map from an url. Uses BOLT as the only communication
  protocol."
  ([url user password]
   (let [auth   (AuthTokens/basic user password)
         config (-> (Config/build)
                    (.withLogging (ConsoleLogging. Level/ALL))
                    (.toConfig))
         db     (GraphDatabase/driver url auth config)]
     {:url        url, :user user, :password password, :db db
      :destroy-fn #(.close db)}))
  ([url]
   (let [config (-> (Config/build)
                    (.withLogging (ConsoleLogging. Level/ALL))
                    (.toConfig))
         db     (GraphDatabase/driver url config)]
     {:url url, :db db, :destroy-fn #(.close db)})))

(defn disconnect [db]
  "Disconnect a connection"
  ((:destroy-fn db)))

;; In-memory for testing

(defn- get-free-port []
  (let [socket (ServerSocket. 0)
        port   (.getLocalPort socket)]
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
  [url]
  (let [bolt   (GraphDatabaseSettings$BoltConnector. "0")
        temp   (System/getProperty "java.io.tmpdir")
        millis (str (System/currentTimeMillis))
        folder (File. (.getPath (io/file temp millis)))]
    (println "temp: " temp)
    (println "folder: " folder)
    (-> (GraphDatabaseFactory.)
        (.newEmbeddedDatabaseBuilder folder)
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
        db  (in-memory-db url)]
    (merge (connect (str "bolt://" url))
           {:destroy-fn #(.shutdown db)})))

;; Sessions and transactions

(defn get-session [connection]
  (.session (:db connection)))

(defn- make-success-transaction [tx]
  (proxy [org.neo4j.driver.v1.Transaction] []
    (run
      ([q] (.run tx q))
      ([q p] (.run tx q p)))
    (success [] (.success tx))
    (failure [] (.failure tx))

    ;; We only want to auto-success to ensure persistence
    (close []
      (.success tx)
      (.close tx))))

(defn get-transaction [session]
  (make-success-transaction (.beginTransaction session)))

;; Executing cypher queries

(defn execute
  ([sess query params]
   (neo4j->clj (.run sess query (clj->neo4j params))))
  ([sess query]
   (neo4j->clj (.run sess query))))

(defn create-query
  "Convenience function. Takes a cypher query as input, returns a function that
  takes a session (and parameter as a map, optionally) and return the query
  result as a map."
  [cypher]
  (fn
    ([sess] (execute sess cypher))
    ([sess params] (execute sess cypher params))))

(defmacro defquery
  "Shortcut macro to define a named query."
  [name ^String query]
  `(def ~name (create-query ~query)))

(defn retry-times [times body]
  (let [res (try
              {:result (body)}
              (catch TransientException e#
                (if (zero? times)
                  (throw e#)
                  {:exception e#})))]
    (if (:exception res)
      (recur (dec times) body)
      (:result res))))

(defmacro with-transaction [connection tx & body]
  `(with-open [~tx (get-transaction (get-session ~connection))]
     ~@body))

(defmacro with-retry [[connection tx & {:keys [max-times] :or {max-times 1000}}] & body]
  `(retry-times ~max-times
                (fn []
                  (with-transaction ~connection ~tx ~@body))))
