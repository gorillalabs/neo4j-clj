# example project for the usage of the neo4j-clj library


## Installation

Clone this example project

```
git clone git@github.com:gorillalabs/neo4j-clj.git
```

## Prepare Neo4j

We provide a nix flake development environment. So, within the `neo4j-clj/` directory, run `nix develop`. This will setup OpenJDK, Clojure, Leiningen and Neo4j.

You can then start Neo4j using `neo4j console`. 

When starting for the first time, you need to change the default password.

Use `open http://localhost:7474/` in a second shell / terminal tab to open up the Neo4j web UI. Log in using the default credentials `neo4j`/`neo4j`, and following the dialog, change the password to whatever you like, e.g. `YA4jI)Y}D9a+y0sAj]T5s|C5qX!w.T0#u<be5w6X[p`.

## Usage

Change into the examples project using

```
cd neo4j-clj/example
```

and run

```
NEO4J_PASSWORD='YA4jI)Y}D9a+y0sAj]T5s|C5qX!w.T0#u<be5w6X[p' lein run
```
    
to run the example associated.


## License

Copyright © 2020-2025 gorillalabs

Distributed under the MIT License.
