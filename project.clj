(defproject memgraph/neo4j-clj "1.0.0"
  :description "Clojure bindings for Memgraph/Neo4j using the Java driver"

  :url "https://github.com/memgraph/neo4j-clj"
  :license {:name "MIT License"}

  :plugins [[com.roomkey/lein-v "7.2.0"]]

  :middleware [leiningen.v/version-from-scm
               leiningen.v/dependency-version-from-scm
               leiningen.v/add-workspace-data]
               
  :dependencies [[org.neo4j.driver/neo4j-java-driver "5.19.0"]
                 [clj-time "0.15.2"]]

  :profiles {:provided     {:dependencies [[org.clojure/clojure "1.11.2"]
                                           [joplin.core "0.3.11"]
                                           [org.neo4j.test/neo4j-harness "5.10.0"]]}
             :default      [:base :system :user :provided :dev]
             :dev          [:project/dev :profiles/dev]
             :profiles/dev {}
             :project/dev  {:jvm-opts     ["-Dclojure.spec.check-asserts=true" "-XX:-OmitStackTraceInFastThrow"]
                            :dependencies []}}

  :pom-addition ([:developers
                  [:developer
                   [:id "as51340"]
                   [:name "Andi Skrgat"]
                   [:url "https://github.com/as51340"]
                   [:roles
                    [:role "developer"]
                    [:role "maintainer"]]]])

  :classifiers [["sources" {:source-paths      ^:replace []
                            :java-source-paths ^:replace ["src/java"]
                            :resource-paths    ^:replace ["javadoc"]}]
                ["javadoc" {:source-paths      ^:replace []
                            :java-source-paths ^:replace []
                            :resource-paths    ^:replace ["javadoc"]}]]

  :scm {:name "git"
        :url  "https://github.com/memgraph/neo4j-clj"}

  ;; make sure you have your ~/.lein/credentials.clj.gpg setup correctly

  :release-tasks [["vcs" "assert-committed"]
                  ["v" "update"]
                  ["deploy" "clojars"]
                  ["vcs" "push"]])
