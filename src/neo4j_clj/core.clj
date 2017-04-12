(ns neo4j-clj.core
  (:import (org.neo4j.driver.v1 Values GraphDatabase AuthTokens))
  (:require [neo4j-clj.compability :refer [neo4j->clj clj->neo4j]]))

(defn create-connection [url user password]
  (let [auth (AuthTokens/basic user password)]
    (GraphDatabase/driver url auth)))

(defn create-query
  [cypher]
  (letfn [(in-session [conn query params]
            (with-open [sess (.session conn)]
              (neo4j->clj (.run sess query params))))]
    (fn
      ([conn] (in-session conn cypher {}))
      ([conn params] (in-session conn cypher (clj->neo4j params))))))