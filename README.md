# neo4j-clj
Clojure bindings for the Java Neo4j driver

## Neo4j survival guide

The easiest way to get started with Neo4j is by running it in a docker container

```sh
docker run \
    --publish=7474:7474 \
    --volume=$HOME/neo4j/data:/data \
    neo4j:2.3
```

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
(ns example.core)
(:require [neo4j-clj.core :as db]
          [clojure.pprint])

(def local-db (db/create-connection "bolt://localhost:7687" "neo4j" "eJD,s(3X*vcz"))

(db/defquery create-user "CREATE (u:User {user})")

(db/defquery get-all-users "MATCH (u:User) RETURN u as user")

(defn -main
  "Example usage of neo4j-clj"
  [& args]
  (with-open [session (db/get-session local-db)]
    (create-user session {:user {:first-name "Luke" :last-name "Skywalker"}}))

  (clojure.pprint/pprint
    (with-open [session (db/get-session local-db)]
      (get-all-users session))))
  ;; => ({:user {:first-name "Luke", :last-name "Skywalker"}})
```

For the parameters you have two options:
```clojure
;; Wrapped object
(db/defquery create-user "CREATE (u:User {user})")
(create-user session {:user {:first-name ....}}

(db/defquery get-users "MATCH (u:User) RETURN u as user")
(get-users session)
;; => {:user {...}}

;; Extracted parameters
(db/defquery create-user "CREATE (u:User {name: {name}, age: {age}})")
(create-user session {:name "..." :age 42})

(db/defquery get-users "MATCH (u:User) RETURN u.name as name, u.age as age")
(get-users session)
;; => ({:name "..." :age 42}, ...)
```