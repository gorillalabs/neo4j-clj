(ns joplin.neo4j.database-test
  (:require [clojure.test :refer :all]
            [neo4j-clj.core :refer :all]
            [joplin.core :as joplin]
            [joplin.neo4j.seeds]
            [joplin.neo4j.database]))

(defn with-temp-db [tests]
  (def temp-db (create-in-memory-connection))
  (tests)
  (disconnect temp-db))

(use-fixtures :each with-temp-db)

(defquery get-seed-users-by-name
  "MATCH (u:SeedUser {name: $name}) RETURN u.name as name, u.role as role")

(defquery get-seed-users-by-label
  "MATCH (u:SeedUser) RETURN u.name as name, u.role as role")

(defquery get-all
  "MATCH (n) RETURN n")

(def name-lookup
  {:name (:name joplin.neo4j.seeds/seed-user)})

(deftest seed-test
  (joplin/seed-db
   {:db   {:type :neo4j,
           :url  (:url temp-db)}
    :seed "joplin.neo4j.seeds/run"})
  (with-open [session (get-session temp-db)]
    (is (= (get-seed-users-by-name session name-lookup)
           (list joplin.neo4j.seeds/seed-user)))))

(deftest migrate-test
  (let [target {:db       {:type :neo4j,
                           :url  (:url temp-db)}
                :migrator "test/joplin/neo4j/migrators"}]
    (joplin/migrate-db target))
  (with-open [session (get-session temp-db)]
    (is (= (get-seed-users-by-label session)
           (list (assoc joplin.neo4j.seeds/seed-user
                        :name "MigratedSeeder"))))))

(deftest rollback-test
  (let [target {:db       {:type :neo4j,
                           :url  (:url temp-db)}
                :migrator "test/joplin/neo4j/migrators"}]
    (joplin/migrate-db target)
    (joplin/rollback-db target 1))
  (with-open [session (get-session temp-db)]
    (is (= (get-seed-users-by-name session name-lookup)
           (list joplin.neo4j.seeds/seed-user)))))