# neo4j-clj
Clojure bindings for the Java Neo4j driver

[![Clojars Project](https://img.shields.io/clojars/v/gorillalabs/neo4j-clj.svg)](https://clojars.org/gorillalabs/neo4j-clj)
[![Build Status](https://travis-ci.org/gorillalabs/neo4j-clj.svg)](https://travis-ci.org/gorillalabs/neo4j-clj)
[![Dependencies Status](https://versions.deps.co/gorillalabs/neo4j-clj/status.svg)](https://versions.deps.co/gorillalabs/neo4j-clj)
[![Downloads](https://versions.deps.co/gorillalabs/neo4j-clj/downloads.svg)](https://versions.deps.co/gorillalabs/neo4j-clj)

## Neo4j survival guide

The easiest way to get started with Neo4j is by running it in a docker container

```sh
docker run \
    --publish=7474:7474 \
    --publish=7687:7687 \
    --volume=$HOME/neo4j/data:/data \
    neo4j:3.2
```

__You have to login once and change the password! Default is neo4j/neo4j__

A complete guide for all kinds of scenarios can be found in the 
[docs](http://neo4j.com/docs/operations-manual/current/installation/docker/).

The Neo4j instance can be accessed under [localhost:7474](http://localhost:7474). The
web interface is great and provides a shell, example queries, overviews, settings etc.

### Trouble-shooting

One thing that might occur often: If you don't provide a SSL cert, Neo4j creates it's
own every restart. This leads to problems if you restart the server often (you'll get
a Java exception). You can force the Neo4j driver to forget the old container and 
accept the new one by

```sh
rm ~/.neo4j/known_hosts
```

## Overview

This library provides functions to interact with the database over the binary bolt
protocol. Furthermore, it provides an in-memory database for tests.

### Example usage

After starting a new Neo4j instance (see docker command), you have to visit 
[localhost:7474](http://localhost:7474) to set a new password. The default user has
neither read nor write roles.

```clojure
(ns example.core
  (:require [neo4j-clj.core :as db]))

(def local-db
  (db/connect "bolt://localhost:7687" "neo4j" "password"))

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
  (with-transaction local-db tx
    (get-all-users tx)) ;; => ({:user {:first-name "Luke", :last-name "Skywalker"}}))
```

For the parameters you have two options:
```clojure
;; Wrapped object
(db/defquery create-user "CREATE (u:User $user)")
(create-user tx {:user {:first-name ....}}

(db/defquery get-users "MATCH (u:User) RETURN u as user")
(get-users tx)
;; => ({:user {...}})

;; Extracted parameters
(db/defquery create-user "CREATE (u:User {name: $name, age: $age})")
(create-user tx {:name "..." :age 42})

(db/defquery get-users "MATCH (u:User) RETURN u.name as name, u.age as age")
(get-users tx)
;; => ({:name "..." :age 42}, ...)
```

## Testing

The test semantics are the same. Just use

```clojure
(def test-db
  (neo4j/create-in-memory-connection))
;; instead of (neo4j/connect url user password)
```

## Joplin integration

neo4j-clj comes equipped with support for [Joplin](https://github.com/juxt/joplin)
for datastore migration and seeding.

As we do not force our users into Joplin dependencies, you have to add [joplin.core "0.3.10"]
to your projects dependencies yourself.

