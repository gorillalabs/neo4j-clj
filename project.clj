(defproject gorillalabs/neo4j-clj "0.0.0"
  :description "Clojure bindings for Neo4j using the Java driver"

  :url "https://github.com/gorillalabs/neo4j-clj"
  :license {:name "MIT License"}
  :deploy-repositories [["releases" :clojars]]

  :plugins [[com.roomkey/lein-v "6.3.0"]]
  :middleware [leiningen.v/version-from-scm
               leiningen.v/add-workspace-data]

  :dependencies [[org.neo4j/neo4j "3.3.5"]
                 [org.neo4j/neo4j-cypher "3.3.5"]
                 [org.neo4j.driver/neo4j-java-driver "1.6.0-beta01"]
                 [clj-time "0.14.3"]]

  :profiles {:provided     {:dependencies [[org.clojure/clojure "1.9.0"]
                                           [joplin.core "0.3.10"]
                                           [org.neo4j.test/neo4j-harness "3.3.5"]]}
             :default      [:base :system :user :provided :dev]
             :dev          [:project/dev :profiles/dev]
             :profiles/dev {}
             :project/dev  {:jvm-opts     ["-Dclojure.spec.check-asserts=true" "-XX:-OmitStackTraceInFastThrow"]
                            :dependencies []}}

  :scm {:name "git"
        :url  "https://github.com/gorillalabs/neo4j-clj"}

  ;; make sure you have your ~/.lein/credentials.clj.gpg setup correctly

  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"]
                  ["deploy" "releases"]
                  ["vcs" "push"]])
