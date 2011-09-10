(ns clojure-nlp.core-test
  (:use [clojure-nlp.core] :reload-all)
  (:use [clojure.test]))

(setup-models)

(deftest tokenize-test
  (let [words (tokenize "Hello World")]
    (is (= (first words) "Hello"))
    (is (= (second words) "World"))))


