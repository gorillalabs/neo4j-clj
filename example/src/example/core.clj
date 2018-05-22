(ns example.core
  (:require [neo4j-clj.core :as db]))

(def local-db
  (db/connect "bolt://localhost:7687" "neo4j" "password"))

(db/defquery create-user
  "CREATE (u:user $user)")

(db/defquery get-all-users
  "MATCH (u:user) RETURN u as user")

(defn -main
  "Example usage of neo4j-clj"
  [& args]

  ;; Using a session
  (with-open [session (db/get-session local-db)]
    (create-user session {:user {:first-name "Luke" :last-name "Skywalker"}}))

  ;; Using a transaction
  (db/with-transaction local-db tx
    (get-all-users tx)) ;; => ({:user {:first-name "Luke", :last-name "Skywalker"}})
)
