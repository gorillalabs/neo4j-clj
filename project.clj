(defproject neo4j-clj "0.3.0-SNAPSHOT"
  :description "Clojure bindings for Neo4j using the Java driver"
  :url "https://github.com/CYPP/neo4j-clj"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.neo4j/neo4j "3.0.1"]
                 [org.neo4j/neo4j-jdbc-driver "3.0.1"]]

  :profiles {:default [:base :system :user :provided :dev :mirrors]
             :mirrors {:mirrors {"central"  {:name         "Nexus"
                                             :url          "http://172.18.102.210/repository/maven-public/"
                                             :repo-manager true}
                                 #"clojars" {:name         "Nexus"
                                             :url          "http://172.18.102.210/repository/clojars-public/"
                                             :repo-manager true}}}
             :uberjar {:aot :all}
             :dev     {:jvm-opts       ["-Denv=LOCAL" "-Dclojure.spec.check-asserts=true" "-XX:-OmitStackTraceInFastThrow"]
                       :env            {:env "LOCAL"}
                       :resource-paths ["test-resources"]
                       :dependencies [[org.neo4j.test/neo4j-harness "3.0.1"]]}}

  :uberjar-name "neo4j-clj.jar"
  :target-path "target/%s"
  :repositories [["releases" {:url   "http://172.18.102.210/repository/CYPP/"
                              :creds :gpg}]]

  :vcs :git

  ;; make sure you have your ~/.lein/credentials.clj.gpg setup correctly
  :deploy-repositories [["releases" {:url   "http://172.18.102.210/repository/CYPP-builds/"
                                     :creds :gpg}]
                        ["snapshots" {:url   "http://172.18.102.210/repository/CYPP-build-snapshots/"
                                      :creds :gpg}]]

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "backend-develop-v"]
                  ["uberjar"]
                  ["deploy" "releases" "cypp.one/neo4j-clj" :project/version "target/uberjar/neo4j-clj.jar"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]])
