(defproject data-generator "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [mount "0.1.12"]
                 [honeysql "0.9.3"]
                 [org.clojure/test.check "0.10.0-alpha3"]
                 [org.clojure/java.jdbc "0.7.7"]
                 [org.postgresql/postgresql "42.2.4"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]
                 [aero "1.1.3"]
                 [ragtime "0.7.2"]
                 [clj-time "0.14.4"]
                 [com.taoensso/timbre "4.10.0"]
                 [org.clojure/core.async "0.4.474"]]
  :plugins [[lein-cljfmt "0.6.0"]]
  :profiles {:repl {:source-paths ["dev"]
                    :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                    :repl-options {:init-ns user.my}
                    :injections [(require 'clojure.tools.namespace.repl)
                                 (require 'user.my)]}
             :uberjar {:aot :all}}

  :main data-generator.core
  :source-paths ["src"]
  :aliases {"migrate" ["run" "-m" "user.migrator/migrate"]
            "rollback" ["run" "-m" "user.migrator/rollback"]
            "create-migration" ["run" "-m" "user.migrator/create-migration"]})
