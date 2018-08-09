(ns data-generator.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [mount.core :as mount :refer [defstate]]
            [jdbc.pool.c3p0 :as pool]
            [clojure.test.check.generators :as gen]
            [data-generator.utils :as u]))

(defstate db
  :start (pool/make-datasource-spec u/config)
  :stop (.close db))

(defn q [db]
  (jdbc/query db (sql/format {:select [:*]
                              :from [:random-data]})))

(defn u [db]
  (jdbc/execute! db (-> (h/insert-into :random-data)
                        (h/columns :name :price :description)
                        (h/values
                         [["nvidia" 100 "videocard"]
                          ["asus" 100 "motherboard"]])
                        (sql/format))))

(comment
  (def ids (gen/sample gen/nat 5))
  (def names (gen/sample (gen/such-that not-empty gen/string 10)))
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
