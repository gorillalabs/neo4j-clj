(ns neo4j-clj.core-test
  (:require [clojure.test :refer :all]
            [neo4j-clj.core :refer :all]))

(defquery create-test-user
  "CREATE (u:TestUser $user)")

(defquery get-test-users-by-name
  "MATCH (u:TestUser {name: $name}) RETURN u.name as name, u.role as role")

(defquery delete-test-user-by-name
  "MATCH (u:TestUser {name: $name}) DELETE u")

(def dummy-user
  {:name "MyTestUser" :role "Dummy"})

(def name-lookup
  {:name (:name dummy-user)})

(defn with-temp-db [tests]
  (def temp-db (create-in-memory-connection))
  (tests)
  (disconnect temp-db))

(use-fixtures :once with-temp-db)

;; Simple CRUD
(deftest create-get-delete-user
  (with-open [session (get-session temp-db)]
    (testing "You can create a new user with neo4j"
      (create-test-user session {:user dummy-user}))

    (testing "You can get a created user by name"
      (is (= (get-test-users-by-name session name-lookup)
             (list dummy-user))))

    (testing "You can remove a user by name"
      (delete-test-user-by-name session name-lookup))

    (testing "Removed users can't be retrieved"
      (is (= (get-test-users-by-name session name-lookup)
             (list))))))

;; Cypher exceptions
(deftest invalid-cypher-does-throw
  (with-open [session (get-session temp-db)]
    (testing "An invalid cypher query does trigger an exception"
      (is (thrown? Exception (execute session "INVALID!!§$/%&/("))))))

;; Transactions
(deftest transactions-do-commit
  (testing "If using a transaction, writes are persistet"
    (with-transaction temp-db tx
      (execute tx "CREATE (x:test $t)" {:t {:payload 42}})))

  (testing "If using a transaction, writes are persistet"
    (with-transaction temp-db tx
      (is (= (execute tx "MATCH (x:test) RETURN x")
             '({:x {:payload 42}})))))

  (testing "If using a transaction, writes are persistet"
    (with-transaction temp-db tx
      (execute tx "MATCH (x:test) DELETE x" {:t {:payload 42}})))

  (testing "If using a transaction, writes are persistet"
    (with-transaction temp-db tx
      (is (= (execute tx "MATCH (x:test) RETURN x")
             '())))))
