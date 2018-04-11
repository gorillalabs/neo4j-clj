(ns joplin.neo4j.migrators.20171023123241-renameSeedUser
  (:use [joplin.neo4j.database])
  (:require [neo4j-clj.core :refer :all]))

(defquery alter-seed-user-name
  "MATCH (u:SeedUser)
           SET u.name=$name")

(defn up [db]
  (with-connection db session
    (alter-seed-user-name session {:name "MigratedSeeder"})))

(defn down [db]
  (with-connection db session
    (alter-seed-user-name session {:name "SeedUser"})))