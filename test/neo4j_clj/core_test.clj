(ns neo4j-clj.core-test
  (:require [clojure.test :refer :all]
            [neo4j-clj.core :refer :all]))

(def create-test-user
  "CREATE (u:TestUser {user})")

(def get-test-users-by-name
  "MATCH (u:TestUser {name: {name}}) RETURN u.name as name, u.role as role")

(def delete-test-user-by-name
  "MATCH (u:TestUser {name: {name}}) DELETE u")

(def dummy-user
  {:name "MyTestUser" :role "Dummy"})

(def name-lookup
  {:name (:name dummy-user)})

(defn in-session [query params]
  (with-open [conn (create-connection "bolt://localhost:7687" "neo4j" "220992")]
    (let [do-query (create-query query)]
      (do-query conn params))))

(deftest create-get-delete-user
  (testing "You can create a new user with neo4j"
    (in-session create-test-user {:user dummy-user}))

  (testing "You can get a created user by name"
    (is (= (in-session get-test-users-by-name name-lookup)
           (list dummy-user))))

  (testing "You can remove a user by name"
    (in-session delete-test-user-by-name name-lookup))

  (testing "Removed users can't be retrieved"
    (is (= (in-session get-test-users-by-name name-lookup)))))