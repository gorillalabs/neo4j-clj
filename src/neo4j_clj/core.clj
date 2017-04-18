(ns neo4j-clj.core
  (:require [neo4j-clj.compability :refer [neo4j->clj clj->neo4j]])
  (:import (org.neo4j.driver.v1 Values GraphDatabase AuthTokens)
           (org.neo4j.graphdb.factory GraphDatabaseSettings$BoltConnector GraphDatabaseFactory)
           (java.net ServerSocket)
           (java.io File)))

(defn create-connection
  ([url user password]
   (let [auth (AuthTokens/basic user password)
         db   (GraphDatabase/driver url auth)]
     {:url      url
      :user     user
      :password password
      :db       db}))
  ([url]
   (let [db (GraphDatabase/driver url)]
     {:url url
      :db  db})))

(defn- find-free-port []
  (let [socket (ServerSocket. 0)]
    (.getLocalPort socket)))

(defn- create-temp-uri []
  (str "bolt://localhost:" (find-free-port)))

(defn- in-memory-db [uri]
  (let [bolt (GraphDatabaseSettings$BoltConnector. "0")
        temp (System/getProperty "java.io.tmpdir")]
    (-> (GraphDatabaseFactory.)
        (.newEmbeddedDatabaseBuilder (File. temp))
        (.setConfig (.type bolt) "BOLT")
        (.setConfig (.enabled bolt) "true")
        (.setConfig (.address bolt) uri)
        (.newGraphDatabase))))

(defn create-in-memory-connection []
  (let [url (create-temp-uri)
        db (in-memory-db url)]
    {:url url
     :db (:db (create-connection url))
     :destroy-fn (fn [] (.shutdown db))}))

(defn destroy-in-memory-connection [connection]
  ((:destroy-fn connection)))

(defn get-session [connection]
  (.session (:db connection)))

(defn- run-query [sess query params]
  (neo4j->clj (.run sess query params)))

(defn create-query [cypher]
  (fn
    ([sess] (run-query sess cypher {}))
    ([sess params] (run-query sess cypher (clj->neo4j params)))))