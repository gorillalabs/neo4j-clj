(ns joplin.neo4j.seeds
  (:require [neo4j-clj.core :refer :all]))

(defquery create-seed-user
  "CREATE (u:SeedUser $user)")

(def seed-user
  {:name "SeedUser" :role "Seeder"})

(defn run [target & args]
  (let [db (connect (-> target :db :url))]
    (with-open [session (get-session db)]
      (create-seed-user session {:user seed-user}))))
