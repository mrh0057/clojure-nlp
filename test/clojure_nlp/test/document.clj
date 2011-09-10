(ns clojure-nlp.test.document
  (:use clojure.test
        clojure-nlp.document
        clojure-nlp.term-weighting
        [clj-data.core :only [count-rows count-cols]])
  (:require [clj-data.core :as data-func]))

(deftest count-same-test
  (let [test-values (list "one" "two" "a" "two" "a")
        test-values-vec ["one" "two" "a" "two" "a"]
        correct-values (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "two" 2))]
    (is (= (count-same test-values) correct-values))
    (is (= (count-same test-values-vec) correct-values))
    (is (= (count-same '()) '()))
    (is (= (count-same nil) nil))))

(deftest processed-document-test
  (let [document (make-processed-document "test"
                                          (hash-map "1" 1 "2" 2 "3" 3)
                                          6)]
    (is (contains-word? document "1"))
    (is (= (unique-words document) 3))
    (is (= (word-count document "1") 1))
    (is (= (word-count document "not-there") 0))))

(deftest document-test
  (let [document (make-document "test"
                                (list "one" "two" "a" "two" "a"))
        document-processed (process document)]
    (is (= (:name document-processed) "test"))
    (is (= (:number-of-words document-processed) 5))
    (is (= (word-count document-processed "one") 1))
    (is (= (word-count document-processed "two") 2))
    (is (= (word-count document-processed "a") 2))))

(deftest find-lowest-word-test
  (let [test-values '((2 3) (1 3) (4 6))
        format-data (fn [data]
                      (map (fn [vals]
                             (map #(make-count-number % 1) vals)) data))]
    (is (= (find-lowest-word (format-data test-values)) 1))
    (is (= (find-lowest-word '()) nil))
    (is (= (find-lowest-word (format-data '((2 3)))) 2))
    (is (= (find-lowest-word (format-data '(("a" "b") ("c" "d")))) "a"))))

(deftest count-documents-words-test
  (let [test-values (list
                     (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "three" 3))
                     (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "two" 2))
                     (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "two" 2)))
        word-map (count-documents-words test-values)]
    (is (= (get word-map "a")) 6)
    (is (= (get word-map "one")) 3)
    (is (= (get word-map "two")) 4)
    (is (= (get word-map "three")) 2)))

(deftest count-document-word-test
  (let [test-values (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "three" 3))]
    (is (= (count-document-word test-values) 6))))

(deftest convert-count-value-to-map-test
  (let [test-values (list (make-count-number "a" 2)
                          (make-count-number "one" 1)
                          (make-count-number "three" 3))
        map (convert-count-value-to-map test-values)]
    (is (= (get map "a") 2))
    (is (= (get map "one") 1))
    (is (= (get map "three") 3))))

(defrecord TermWeightingTest
    [number])

(extend-type TermWeightingTest
  TermWeighting
  (calculate-weight [this term word-count]
    word-count))

(deftest total-words-test
  (let [test-values {:a 1 :b 2 :c 3}]
    (is (= (total-words test-values) 6))))

(deftest create-document-by-word-dataset-test
  (let [term-weighting (new TermWeightingTest 3)
        documents (list
                   (make-processed-document "doc1"
                                            (list (make-count-number "a" 2)
                                                  (make-count-number "one" 1)
                                                  (make-count-number "three" 3))
                                            6)
                     (make-processed-document "doc2"
                                              (list (make-count-number "a" 5)
                                                    (make-count-number "one" 1)
                                                    (make-count-number "two" 2))
                                              8)
                     (make-processed-document "doc3"
                                              (list (make-count-number "a" 5)
                                                    (make-count-number "one" 1)
                                                    (make-count-number "two" 2))
                                              8))
        word-map {"a" 4
                  "one" 3
                  "three" 3
                  "two" 4}
        data-set (create-document-by-word-dataset "something" term-weighting documents word-map)]
    (is (= (count-rows data-set) 3))
    (is (= (count-cols data-set) 4))
    (is (= (data-func/get-value data-set 0 0) 2))
    (is (= (data-func/get-value data-set 1 0) 1))
    (is (= (data-func/get-value data-set 2 0) 3))
    (is (= (data-func/get-value data-set 0 2) 5))
    (is (= (data-func/get-value data-set 1 2) 1))
    (is (= (data-func/get-value data-set 3 2) 2))
    (is (= (:row-labels data-set) ["doc1" "doc2" "doc3"]))
    (is (= (:attributes data-set) ["a" "one" "three" "two"]))))
