(ns clojure-nlp.test.math
  (:use clojure-nlp.math
        clojure.test))

(deftest log2-test
  (is (= (log2 2) 1))
  (is (= (log2 4) 2)))
