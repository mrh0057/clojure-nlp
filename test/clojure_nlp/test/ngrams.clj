(ns clojure-nlp.test.ngrams
  (:use clojure-nlp.ngrams
        clojure.test))

(deftest create-ngrams-test
  (let [test-val ["a" "b" "c" "d" "e"]
        correct '(("a" "b") ("b" "c") ("c" "d") ("d" "e"))]
    (is (= correct (create-ngrams test-val 2)))
    (is (= nil (create-ngrams '() 2)))
    (is (= nil (create-ngrams ["a" "b"] 3)))))
