(ns clojure-nlp.test.word-filter
  (:use clojure.test
        clojure-nlp.word-filter))

(deftest CharacterProtocol-test
  (is (letter? (first "h")))
  (is (not (letter? (first "1"))))
  (is (digit? (first "1")))
  (is (not (digit? (first "n")))))

(deftest filter-words-string-test
  (is (= "hello" (filter-not-letters "he1.2llo"))))

(deftest filter-words-sequence-test
  (is (= '("hello" "world" "vals") (filter-not-letters (list "hello" "world12" "vals323")))))
