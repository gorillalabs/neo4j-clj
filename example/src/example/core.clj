(ns example.core)
(:require [neo4j-clj.core :as db]
  [clojure.pprint])

(def local-db (db/create-connection "bolt://localhost:7687" "neo4j" "eJD,s(3X*vcz"))

(db/defquery create-user "CREATE (u:User {user})")

(db/defquery get-all-users "MATCH (u:User) RETURN u as user")

(defn -main
  "Example usage of neo4j-clj"
  [& args]
  (with-open [session (db/get-session local-db)]
    (db/with-db-transaction
      tx session
      (create-user tx {:user {:first-name "Luke" :last-name "Skywalker"}})))

  (clojure.pprint/pprint
    (with-open [session (db/get-session local-db)]
      (get-all-users session))))
;; => ({:user {:first-name "Luke", :last-name "Skywalker"}})