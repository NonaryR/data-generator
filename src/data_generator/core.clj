(ns data-generator.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [mount.core :as mount :refer [defstate]]
            [jdbc.pool.c3p0 :as pool]
            [clojure.test.check.generators :as gen]
            [taoensso.timbre :as log]
            [data-generator.utils :as u]))

(defn connect-db []
  (log/info "connect to db")
  (pool/make-datasource-spec u/config))

(defstate db
  :start (connect-db)
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

(def ^:private columns [:id :name :price :created-date
                        :description :in-stock])

(defn write-to-db [db values]
  (jdbc/execute! db (-> (h/insert-into :generated-data)
                        (h/columns columns)
                        (h/values [values])
                        (sql/format))))

(comment

  (def names (gen/sample (gen/not-empty gen/string) 10))
  (def prices (gen/sample (gen/choose 10 100000) 10))
  (def dates  (gen/sample (gen/choose 1433115447 1533915447) 10))
  (def descs (gen/sample (gen/fmap #(apply str (repeat 10 %))
                                   (gen/not-empty gen/string)) 10))
  (def stocks (gen/sample gen/boolean 10))
  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [profile (or (keyword (first args)) :prod)]
    (mount/start-with-args profile)
    (println profile)
    (println u/config)))
