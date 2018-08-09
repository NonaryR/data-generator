(ns user.migrator
  (:require [ragtime.jdbc :as jdbc]
            [ragtime.repl :as repl]
            [data-generator.utils :as u]
            [clj-time.core :as t]))

(defn load-config []
  {:datastore  (jdbc/sql-database u/config)
   :migrations (jdbc/load-resources "migrations")})

(defn create-migration []
  (let [path (format "resources/migrations/%s.edn" (t/now))
        template {:up [""] :down [""]}]
    (spit path template)))

(defn migrate []
  (repl/migrate (load-config)))

(defn rollback []
  (repl/rollback (load-config)))
