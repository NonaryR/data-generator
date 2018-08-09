(ns data-generator.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [mount.core :as mount :refer [defstate]]
            [aero.core :refer [read-config]]
            [jdbc.pool.c3p0 :as pool])
  (:gen-class))

(defstate db
  :start (pool/make-datasource-spec (read-config "config.edn" {:profile :prod}))
  :stop (.close db))

(defn q [db]
  (jdbc/query db (sql/format {:select [:*]
                              :from [:random-data-2]})))

(defn u [db]
  (jdbc/execute! db (-> (h/insert-into :random-data-2)
                        (h/columns :id :name-of-thing :price)
                        (h/values
                         [[1 "nvidia" 100]
                          [2 "asus" 100]])
                        (sql/format))))
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
