(ns data-generator.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [mount.core :as mount :refer [defstate]]
            [jdbc.pool.c3p0 :as pool]
            [clojure.test.check.generators :as gen]
            [taoensso.timbre :as log]
            [data-generator.utils :as u]
            [clj-time.coerce :as tc]))

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
                          #_["asus" 100 "motherboard"]])
                        (sql/format))))

(def ^:private columns [:id :name :price :created-date
                        :description :in-stock])

(defn write-to-db [db values]
  (jdbc/execute! db (-> (h/insert-into :generated-data)
                        (h/columns :name :price :created-date
                                   :description :in-stock)
                        (h/values [(into [] values)])
                        (sql/format))))

#_(write-to-db db "asus" 20044 (tc/to-sql-date  1444433115447 ) "motherboard" true)

(defn data-to-db [n-samples]
  (let [f (fn [generator] (gen/sample generator n-samples))
        names (f (gen/not-empty gen/string))
        prices (f (gen/choose 10 100000))
        tms (map #(tc/to-sql-date %) (f (gen/choose 1444433115447 1555533915447)))
        descs (f (gen/fmap #(apply str (repeat 10 %))
                           (gen/not-empty gen/string)))
        stocks (f gen/boolean)]
    (->> (partition 5 (interleave names prices tms descs stocks))
         (map (partial write-to-db db)))))


(comment

  (def names (gen/sample (gen/not-empty gen/string) 10))
  (def prices (gen/sample (gen/choose 10 100000) 10))
  (def dates  (gen/sample (gen/choose 1433115447 1533915447) 10))
  (def descs (gen/sample (gen/fmap #(apply str (repeat 10 %))
                                   (gen/not-empty gen/string)) 10))
  (def stocks (gen/sample gen/boolean 10))


  (defn f [n] (do (println "start") (Thread/sleep (* n 1000)) (println "finish")))

  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [profile (or (keyword (first args)) :prod)]
    (mount/start-with-args profile)
    (println profile)
    (println u/config)))
