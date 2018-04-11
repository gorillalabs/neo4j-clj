(ns joplin.neo4j.migrators.20171023122731-seedUser
  (:use [joplin.neo4j.database])
  (:require [neo4j-clj.core :refer :all]))

(defquery create-seed-user
  "CREATE (u:SeedUser $user)")

(defquery remove-seed-user
  "MATCH (u:SeedUser) DELETE u")

(def seed-user
  {:name "SeedUser" :role "Seeder"})

(defn up [db]
  (with-connection db session
    (create-seed-user session {:user seed-user})))

(defn down [db]
  (with-connection db session
    (remove-seed-user session)))