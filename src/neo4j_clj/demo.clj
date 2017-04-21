(ns neo4j-clj.demo
  (:require [neo4j-clj.core :as db]))

(db/defquery create-user "CREATE (u:User {user})")

(db/defquery get-all-users
             "MATCH (u:User) RETURN u as user")

(def local-db (db/create-connection "bolt://localhost:7687" "neo4j" "eJD,s(3X*vcz"))

(with-open [session (db/get-session local-db)]
  (create-user session {:user {:first-name "Luke" :last-name "Skywalker"}}))

(with-open [session (db/get-session local-db)]
  (get-all-users session))
;; => ({:user {:first-name "Luke", :last-name "Skywalker"}})
