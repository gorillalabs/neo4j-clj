(defproject gorillalabs/neo4j-clj "0.3.1-SNAPSHOT"
  :description "Clojure bindings for Neo4j using the Java driver"

  :url "https://github.com/gorillalabs/neo4j-clj"
  :license {:name "MIT License"}
  :deploy-repositories [["releases" :clojars]]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.neo4j/neo4j "3.1.3"]
                 [org.neo4j.driver/neo4j-java-driver "1.2.1"]]



  :profiles {:default [:base :system :user :provided :dev #_:mirrors]
             :dev     {:jvm-opts     ["-Dclojure.spec.check-asserts=true" "-XX:-OmitStackTraceInFastThrow"]
                       :dependencies [[org.neo4j.test/neo4j-harness "3.1.3"]]}}


  :scm {:name "git"
        :url  "https://github.com/gorillalabs/neo4j-clj"}

  ;; make sure you have your ~/.lein/credentials.clj.gpg setup correctly

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "backend-develop-v"]
                  ["uberjar"]
                  ["deploy" "releases" "cypp.one/neo4j-clj" :project/version "target/uberjar/neo4j-clj.jar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])
