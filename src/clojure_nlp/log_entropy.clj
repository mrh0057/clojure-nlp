(ns clojure-nlp.log-entropy
  (:use clojure-nlp.document
        clojure-nlp.term-weighting
        clojure-nlp.seq-utils
        clojure-nlp.math))

(defrecord LogEntropyRecord [g-map])

(extend-type LogEntropyRecord
  TermWeighting
  (calculate-weight [this term word-count]
    (if (contains? (:g-map this) term)
      (* (get (:g-map this) term) (log2 (+ 1 word-count )))
      0)))

(defn make-log-entropy-record [g-map]
  (LogEntropyRecord. g-map))

(defn- calculate-entropy [documents word document-count documents-word-count-total]
  (let [total-count (get documents-word-count-total word)
        log2m (log2 document-count)]
    (if (= total-count 0)
      (println "My word is: " word))
    (loop [documents documents
           updated '()
           e 0]
      (if (empty? documents)
        [e updated]
        (let [current-word (first (first documents))]
          (if (= (:value current-word) word)
            (let [pij (/ (:times current-word) total-count)]
              (recur
               (rest documents)
               (cons-not-empty (rest (first documents)) updated)
               (+ e (/ (* pij (log2 pij)) log2m))))
            (recur (rest documents)
                   (cons (first documents) updated)
                   e)))))))

(defn create-log-entropy [documents-word-counts documents-word-count-total]
  "Used to calculate the value of g for each word.

*documents-word-counts*
  The document word counts for each word. <br />
*documents-word-count-total*
 All of the words and the number of times they appear in all of the documents.  Expected to be a map. <br />
*returns*
 A map of g values for each word."
  (let [document-count (count documents-word-counts)]
    (loop [documents documents-word-counts
           g-map {}]
      (if (empty? documents)
        (make-log-entropy-record g-map)
        (let [word (find-lowest-word documents)
              entropy (calculate-entropy documents word
                                         document-count documents-word-count-total)]
          (recur (second entropy)
                 (assoc g-map word (- 1 (first entropy)))))))))
