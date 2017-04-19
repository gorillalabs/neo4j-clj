(ns neo4j-clj.demo
  (:require [neo4j-clj.core :as db]))

(def create-user
  (db/create-query "CREATE (u:User {user})"))

(def get-all-users
  (db/create-query "MATCH (u:User) RETURN u as user"))

(def local-db (db/create-connection "bolt://localhost:7687" "neo4j" "password"))

(with-open [session (db/get-session local-db)]
  (create-user session {:user {:first-name "Luke" :last-name "Skywalker"}}))

(with-open [session (db/get-session local-db)]
  (get-all-users session))
;; => ({:user {:first-name "Luke", :last-name "Skywalker"}})
