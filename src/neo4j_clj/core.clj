(ns neo4j-clj.core
  (:require [epicypher.compability :refer [neo4j->clj clj->neo4j]])
  (:import (org.neo4j.driver.v1 Values GraphDatabase AuthTokens)))

(defn create-connection [url user password]
  (let [auth (AuthTokens/basic user password)]
    (GraphDatabase/driver url auth)))

(defn create-query
  [cypher]
  (fn
    ([conn] (with-open [session (.session conn)] (neo4j->clj (.run session cypher))))
    ([conn params] (with-open [session (.session conn)] (neo4j->clj (.run session cypher (clj->neo4j params)))))))

;; Example
(def create-user (create-query "CREATE (a:User {user})"))
(def all-users (create-query "MATCH (a:User) RETURN a.firstname as firstname, a.lastname as lastname, labels(a) as labels"))
(def users-by-name (create-query "MATCH (user:User {firstname: {firstname}}) RETURN user"))

(with-open [conn (create-connection "bolt://localhost:7687" "neo4j" "220992")]
  ;;(create-user conn {:user {:firstname "asdf" :lastname "fdas"}}))
  (users-by-name conn {:firstname "Peter"}))
;;(all-users conn))