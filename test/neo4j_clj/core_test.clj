(ns neo4j-clj.core-test
  (:require [clojure.test :refer :all]
            [neo4j-clj.core :refer [defquery disconnect session-config get-session execute with-tx with-retry]]
            [neo4j-clj.in-memory :refer [create-in-memory-connection]])
  (:import (org.neo4j.driver.exceptions TransientException)))

(defquery create-database
          "CREATE DATABASE $db")


(defquery create-test-user
  "CREATE (u:TestUser $user)-[:SELF {reason: \"to test\"}]->(u)")

(defquery get-test-users-by-name
  "MATCH (u:TestUser {name: $name}) RETURN u.name as name, u.role as role, u.age as age, u.smokes as smokes")

(defquery get-test-users-relationship
  "MATCH (u:TestUser {name: $name})-[s:SELF]->() RETURN collect(u) as ucoll, collect(s) as scoll")

(defquery delete-test-user-by-name
  "MATCH (u:TestUser {name: $name}) DETACH DELETE u")

(def dummy-user
  {:name "MyTestUser" :role "Dummy" :age 42 :smokes true})

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

    (testing "You can get a relationship"
      (is (= (first (get-test-users-relationship session name-lookup))
             {:ucoll (list dummy-user) :scoll (list {:reason "to test"})})))

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
(deftest transactions-fail-test
  (testing "If an exception is thrown during a transaction, nothing should be committed"
    (is (thrown? TransientException
                 (with-tx [tx (get-session temp-db)]
                                   (execute tx "CREATE (x:test $t)" {:t {:payload 42}})
                                   (throw (TransientException. "" "I fail")))))
    (with-tx [tx (get-session temp-db)]
                      (is (= (execute tx "MATCH (x:test) RETURN x")
                             '())))
    (with-tx [tx (get-session temp-db)]
                      (execute tx "CREATE (x:test $t)" {:t {:payload 42}}))
    (with-tx [tx (get-session temp-db)]
                      (is (= (execute tx "MATCH (x:test) RETURN x")
                             '({:x {:payload 42}}))))))

(deftest transactions-do-commit
  (testing "If using a transaction, writes are persistet"
    (with-tx [tx (get-session temp-db)]
      (execute tx "CREATE (x:test $t)" {:t {:payload 42}})))

  (testing "If using a transaction, writes are persistet"
    (with-tx [tx (get-session temp-db)]
      (is (= (execute tx "MATCH (x:test) RETURN x")
             '({:x {:payload 42}})))))

  (testing "If using a transaction, writes are persistet"
    (with-tx [tx (get-session temp-db)]
      (execute tx "MATCH (x:test) DELETE x" {:t {:payload 42}})))

  (testing "If using a transaction, writes are persistet"
    (with-tx [tx (get-session temp-db)]
      (is (= (execute tx "MATCH (x:test) RETURN x")
             '())))))

;; Retry
(deftest deadlocks-fail
  (testing "When a deadlock occures,"
    (testing "the transaction throws an Exception"
      (is (thrown? TransientException
                   (with-tx [tx (get-session temp-db)]
                     (throw (TransientException. "" "I fail"))))))
    (testing "the retried transaction works"
      (let [fail-times (atom 3)]
        (is (= :result
               (with-retry [temp-db tx]
                 (if (pos? @fail-times)
                   (do (swap! fail-times dec)
                       (throw (TransientException. "" "I fail")))
                   :result))))))
    (testing "the retried transaction throws after max retries"
      (is (thrown? TransientException
                   (with-retry [temp-db tx]
                     (throw (TransientException. "" "I fail"))))))))

;; Using different Databases

#_(deftest separate-databases
  (with-open [session (get-session temp-db)]
    (create-database session {:db "DB1"})
    (create-database session {:db "DB2"}))

  (let [db-1-config (session-config "DB1")
        db-2-config (session-config "DB2")]

    (with-open [session (get-session temp-db db-1-config)]
      (testing "You can create a new user with neo4j"
        (create-test-user session {:user dummy-user}))

      (testing "You can get a created user by name"
        (is (= (get-test-users-by-name session name-lookup)
               (list dummy-user)))))

    (with-open [session (get-session temp-db db-1-config)]
      (testing "You can get a created user by name"
        (is (= (get-test-users-by-name session name-lookup)
               (list dummy-user)))))

    (with-open [session (get-session temp-db db-2-config)]
      (testing "You can get a created user by name"
        (is (= (get-test-users-by-name session name-lookup)
               (list dummy-user)))))

    ))