(defproject data-generator "0.0.1"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [mount "0.1.12"]
                 [honeysql "0.9.3"]
                 [org.clojure/test.check "0.10.0-alpha3"]
                 [org.clojure/java.jdbc "0.7.7"]
                 [org.postgresql/postgresql "42.2.4"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.3"]
                 [instaparse "1.4.9"]
                 [aero "1.1.3"]]
  :profiles {:uberjar {:aot :all}
             :repl {:source-paths ["dev"]
                    :dependencies [[org.clojure/tools.namespace "0.2.11"]]
                    :repl-options {:init-ns user.my}
                    :injections [(require 'clojure.tools.namespace.repl)
                                 (require 'user.my)]}})
