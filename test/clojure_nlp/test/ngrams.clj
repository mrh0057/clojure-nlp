(ns clojure-nlp.test.ngrams
  (:use clojure-nlp.ngrams
        clojure-nlp.document
        clojure.test))

(deftest create-ngrams-test
  (let [test-val ["a" "b" "c" "d" "e"]
        correct '(("a" "b") ("b" "c") ("c" "d") ("d" "e"))]
    (is (= correct (create-ngrams test-val 2)))
    (is (= nil (create-ngrams '() 2)))
    (is (= nil (create-ngrams ["a" "b"] 3)))))

(deftest n-grams-count-test
  (let [test-vals [["a" "b" "c"] ["b" "c"]]
        vals {'("a" "b") 1
              '("b" "c") 2}
        vals2 {'("a" "b") 1
               '("a" "b" "c") 1
               '("b" "c") 2}]
    (is (= (n-grams-count [2] test-vals) vals))
    (is (= (n-grams-count [2 3] test-vals) vals2))
    (is (= (n-grams-count [2 4] test-vals) vals))))

(deftest add-ngrams-to-document-test
  (let [document ["a" "b" "c" "b" "c"]
        processed-document (make-processed-document "doc4"
                                                    (hash-map "a" 1
                                                              "b" 2
                                                              "c" 2)
                                                    5)
        correct (make-processed-document "doc4"
                                         (hash-map "a" 1
                                                   "b" 2
                                                   "c" 2
                                                   '("b" "c") 2)
                                         7)]
    (is (= (add-ngrams-to-document [2 3 4 5]
                                   document
                                   processed-document
                                   (hash-map '("b" "c") 10))
           correct))))
