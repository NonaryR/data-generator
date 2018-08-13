(ns data-generator.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :as h]
            [mount.core :as mount :refer [defstate]]
            [jdbc.pool.c3p0 :as pool]
            [clojure.test.check.generators :as gen]
            [taoensso.timbre :as log]
            [data-generator.utils :as u]
            [clj-time.coerce :as tc]
            [clojure.core.async :as a]))

(defn connect-db []
  (log/info "connect to db")
  (pool/make-datasource-spec u/config))

(defstate db
  :start (connect-db)
  :stop (.close db))

(def ^:private columns [:name :price :created-date
                        :description :in-stock])

(defn write-to-db [db values]
  ;; батч в 1000 записей
  (jdbc/execute! db (-> (h/insert-into :generated-data)
                        (h/values (into [] values))
                        (sql/format))))

(defn data-to-db [db n-samples]
  (let [->>gen (fn [generator] (gen/sample generator n-samples))
        names (->>gen (gen/not-empty gen/string-ascii))
        prices (->>gen (gen/choose 10 100000))
        tms (map #(tc/to-sql-date %) (->>gen (gen/choose 1444433115447 1555533915447)))
        descs (->>gen (gen/fmap #(apply str (repeat 10 %))
                                (gen/not-empty gen/string-ascii)))
        stocks (->>gen gen/boolean)]
    (->> (partition 5 (interleave names prices tms descs stocks))
         (map (partial interleave columns))
         (map (partial apply assoc {}))
         (partition 1000)
         (map (partial write-to-db db)))))
(comment

  (defn f [n]
    (do (println n) (Thread/sleep (* n 1000)) (println "finish")))

  )

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [profile (or (keyword (first args)) :prod)]
    (mount/start-with-args profile)
    (println profile)
    (println u/config)))
