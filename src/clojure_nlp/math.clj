(ns clojure-nlp.math
  (:import [org.apache.commons.math.util MathUtils]))

(defn log2 [#^Double x]
  (MathUtils/log 2 x))
