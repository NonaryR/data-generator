(ns data-generator.utils
  (:require [aero.core :refer [read-config]]))

(def config (dissoc (read-config "config.edn" {:profile :test}) :secrets))

