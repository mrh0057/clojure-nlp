(ns clojure-nlp.test.stemmer
  (:use clojure.test
        clojure-nlp.stemmer))

(deftest stem-seq-test
  (let [test-sequence '("hello" "worlds")]
    (is (= (stem-snowball test-sequence) (list  "hello" "world")))
    (is (= (stem-snowball "hellos") "hello"))))
