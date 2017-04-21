(ns example.core
  (:require [neo4j-clj.core :as db]
            [clojure.pprint]))

(db/defquery create-user "CREATE (u:User {user})")

(db/defquery get-all-users
             "MATCH (u:User) RETURN u as user")

(def local-db (db/create-connection "bolt://localhost:7687" "neo4j" "eJD,s(3X*vcz"))




;; => ({:user {:first-name "Luke", :last-name "Skywalker"}})



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (clojure.pprint/pprint (with-open [session (db/get-session local-db)]
                           (create-user session {:user {:first-name "Luke" :last-name "Skywalker"}})))

  (clojure.pprint/pprint
    (with-open [session (db/get-session local-db)]
      (get-all-users session))

    ))
