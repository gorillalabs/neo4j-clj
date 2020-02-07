(ns joplin.neo4j.database
  (:require [clj-time
             [coerce :as tc]
             [core :as t]]
            [neo4j-clj.core :as neo4j]
            [joplin.core :as j]
            [ragtime.protocols :refer [DataStore]]))

;; Connecting with the database on-demand rather than in new-database
;; makes for a prettier log output by joplin. Keep it namespace-independent
;; because this macro is used by migrations as well.
(defmacro with-connection
  "Wraps expr in Neo4j connection & transaction."
  [db tx-identifier & expr]
  `(let [connection# (if (and (:username ~db) (:password ~db))
                       (neo4j-clj.core/connect (:url ~db) (:username ~db) (:password ~db))
                       (neo4j-clj.core/connect (:url ~db)))]
     (neo4j-clj.core/with-transaction connection# ~tx-identifier ~@expr)))

(defn run-query
  "Shortcut function to make creating ad-hoc (string-based) queries easier."
  [tx query & [args]]
  ((neo4j/create-query query) tx args))

(defn run-queries
  "Shortcut function for running multiple queries."
  [tx & queries]
  (doall (map #(run-query tx (first %) (second %)) queries)))

;; ============================================================================
;; Ragtime interface

(defrecord Database [url username password]
  DataStore

  (add-migration-id [db id]
    (with-connection db tx
      (run-query tx
                 "CREATE (n:migration {id: $id, created: $created})"
                 {:id id :created (tc/to-long (t/now))})))

  (remove-migration-id [db id]
    (with-connection db tx
      (run-query tx
                 "MATCH (n:migration {id: $id}) DETACH DELETE n"
                 {:id id})))

  (applied-migration-ids [db]
    (with-connection db tx
                     (doall (map :n.id (run-query tx "MATCH (n:migration) RETURN n.id ORDER BY n.id"))))))

(defmethod print-method Database [v ^java.io.Writer w]
  (.write w (str "#joplin.neo4j.database.Database{:url " (.url v) ", :username " (.username v) "}")))

;; ============================================================================
;; Joplin interface

(defn- list-migrations [target]
  (j/get-migrations (:migrator target)))

(defn- new-database [target]
  (let [db (:db target)]
    (Database. (:url db) (:username db) (:password db))))

(defmethod j/migrate-db :neo4j [target & args]
  (apply j/do-migrate (list-migrations target) (new-database target) args))

(defmethod j/rollback-db :neo4j [target amount-or-id & args]
  (apply j/do-rollback (list-migrations target) (new-database target) amount-or-id args))

(defmethod j/seed-db :neo4j [target & args]
  (apply j/do-seed-fn (list-migrations target) (new-database target) target args))

(defmethod j/pending-migrations :neo4j [target & args]
  (j/do-pending-migrations (new-database target) (list-migrations target)))

(defmethod j/create-migration :neo4j [target id & args]
  (j/do-create-migration target id "commons.neo4j.joplin"))

