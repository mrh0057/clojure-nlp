(ns clojure-nlp.test.log-entropy-test
  (:use clojure.test
        clojure-nlp.document
        clojure-nlp.term-weighting
        clojure-nlp.log-entropy))

(deftest create-log-entropy-test
  (let [document-word-counts (list
                     (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "three" 3))
                     (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "two" 2))
                     (list (make-count-number "a" 2)
                             (make-count-number "one" 1)
                             (make-count-number "two" 2)))
        word-counts {"a" 6
                     "one" 3
                     "two" 4
                     "three" 3}
        log-entropy (create-log-entropy document-word-counts word-counts)
        g-map (:g-map log-entropy)]
    (is (= (get g-map "a") 2.0))
    (is (= (get g-map "three") 1.0))
    (println (calculate-weight log-entropy "two" 3))))
