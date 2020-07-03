(ns example.core
  (:require [neo4j-clj.core :as db])
  (:import (java.net URI)))

(def local-db
  (db/connect (URI. "bolt://localhost:7687")
              "neo4j"
              "YA4jI)Y}D9a+y0sAj]T5s|C5qX!w.T0#u<be5w6X[p"))

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
    ;; print, as query result has to be consumed inside session
    (println (get-all-users tx))))
