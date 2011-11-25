(ns clojure-nlp.document
  (:use clojure-nlp.seq-utils
        [clj-data.core :only [make-dense-double-matrix
                              make-data-set-my-matrix
                              set-value-matrix!]]
        [clojure-nlp.term-weighting :only [calculate-weight]]))

(defrecord CountNumber [value
                        times])

(defn make-count-number [value times]
  (CountNumber. value times))

(defrecord ProcessedDocument [name
                              counts
                              number-of-words])

(defn make-processed-document [name counts number-of-words]
  (ProcessedDocument. name counts number-of-words))

(defprotocol DocumentProtocol
  (contains-word? [this word])
  (unique-words [this])
  (word-count [this word]))

(extend-type ProcessedDocument
  DocumentProtocol
  (contains-word? [this word]
    (contains? (:counts this) word))
  (unique-words [this]
    (count this))
  (word-count [this word]
    (let [counts (:counts this)]
      (if (contains? counts word)
        (get counts word)
        0))))

(defrecord Document [name
                     tokenized-words])

(defprotocol Processable
  (process [this]
    "Used to process a document.

return a DocumentProcessed record"))



(defn make-document [name tokenized-words]
  (Document. name tokenized-words))

(defn- inc-times [count-number]
  (assoc count-number :times (inc (:times count-number))))

(defprotocol CountableSame
  (count-same [this]))

(extend-type clojure.lang.ISeq
  CountableSame
  (count-same [this]
    (if (= (count this) 0)
      this
      (loop [counts '()
             vals (sort this)
             previous (make-count-number (first vals) 0)]
        (if (empty? vals)
          (reverse (cons previous counts))
          (let [current (first vals)]
            (if (= (:value previous) current)
              (recur
               counts
               (rest vals)
               (inc-times previous))
              (recur
               (cons previous counts)
               (rest vals)
               (make-count-number current 1)))))))))

(extend-type nil
  CountableSame
  (count-same [this]
    this))

(extend-type clojure.lang.IPersistentVector
  CountableSame
  (count-same [this]
    (count-same (sort this))))

(defn find-lowest-word [documents]
  (reduce (fn [lowest current]
            (let [current (:value (first current))]
              (if (< (compare current lowest) 0)
                current
                lowest))) (:value (first (first documents))) (rest documents)))

(defn- get-document-counts-for-word 
  "Used to get word counts for a word.

*returns*
 First is the word count second is the updated list of documents"
  [word documents]
  (loop [documents documents
         updated '()
         number 0]
    (let [current-word (first (first documents))]
      (if (empty? documents)
        (list number
              updated)
        (if (= (:value current-word) word)
          (recur
           (rest documents)
           (cons-not-empty (rest (first documents)) updated)
           (+ number (if (:times current-word)
                       (:times current-word)
                       0)))
          (recur
           (rest documents)
           (cons-not-empty (first documents) updated)
           number))))))

(defn count-documents-words 
  "Used to get the word counts of documents

*documents*
  The documents word counts. <br />
*returns*
 a map of word counts."
  [documents]
  (loop [documents documents
         word (find-lowest-word documents)
         word-counts-map {}]
    (if (empty? documents)
      word-counts-map
      (let [word-counts (get-document-counts-for-word word documents)]
        (recur (second word-counts)
               (find-lowest-word (second word-counts))
               (assoc word-counts-map word (first word-counts)))))))

(defn count-document-word
  "Used to get the total count for words in a document."
  [document]
  (reduce (fn [number word]
            (+ number (:times word))) 0 document))

(defn convert-count-value-to-map [document]
  (reduce (fn [map word]
            (assoc map (:value word) (:times word))) {} document))

(defn total-words 
  "Calculates the total word count for the document."
  [word-counts]
  (apply + (vals word-counts)))

(extend-type Document
  Processable
  (process [this]
    (let [word-counts (count-same (:tokenized-words this))]
      (make-processed-document (:name this)
                               (convert-count-value-to-map word-counts)
                               (count-document-word word-counts)))))

(defn process-documents 
  "Used to process a collection of documents.

*document-col*
  The collection of documents. <br />
*returns*
 a list of ProcessedDocument"
  [document-col]
  (map #(process %) document-col))

(defn- build-word-positions 
  "Used to build the words to be put into a matrix.

*words*
  The words."
  [words]
  (second
   (reduce (fn [data word]
             (list (inc (first data))
                   (assoc (second data) word (first data))))
           '(0 {}) (sort words))))

(defn create-document-by-word-dataset
    "Used to create a document b word dataset.

*name*
  The name to give the data set. <br />
*term-weighting* 
  A record that has the term weighint protocol implemented. <br />
*documents* 
  The processed documents. <br />
*words*
  The map of total word counts.  The words are converted into numbers.  Words are in ascending order."
    [name term-weighting documents words]
  (let [matrix (make-dense-double-matrix (count documents) (count words))
        words (sort (keys words))
        word-positions (build-word-positions words)
        row-labels (map #(:name %) documents)]
    (loop [i 0
           documents documents]
      (if (empty? documents)
        (make-data-set-my-matrix name
                                 (map #(str %) (range 0 (count words)))
                                 row-labels
                                 nil
                                 matrix)
        (let [set-word-value (fn [word counts]
                               (if (contains? word-positions word)
                                 (set-value-matrix! matrix (get word-positions word)
                                                    i
                                                    (calculate-weight term-weighting word counts))))]
          (do
            (reduce (fn [matrix word-count]
                      (set-word-value (first word-count)
                                      (second word-count))
                      matrix) matrix (:counts (first documents)))
            (recur (inc i)
                   (rest documents))))))))

