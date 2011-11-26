(ns clojure-nlp.ngrams)

(defn create-ngrams
  "Used to create ngrams from a seq.

*seq* 
  The sequence to create name grams from <br />
*number*
  The number of words to be in the ngram <br />
*returns*
  A lazy sequence of the ngrams"
  [seq number]
  (if (>= (count seq) number)
    (lazy-seq (cons (take number seq) (create-ngrams (rest seq) number)))))

(defn n-grams-count
  "Used to create n-grams with the specified numbers

*numbers*
  A seq of numbers for the ngrams to create. <br />
*documents*
  The documents to process with the ngrams <br />
*returns*
  A map of the ngrams"
  [numbers documents]
  (reduce (fn [counts number]
             (reduce (fn [counts document]
                       (reduce (fn [counts freqs]
                                 (let [ngram (first freqs)]
                                   (if-let [val  (counts ngram)]
                                     (assoc counts ngram (+ val (second freqs)))
                                     (assoc counts ngram (second freqs)))))
                               counts (frequencies (create-ngrams document number))))
                     counts documents)) {} numbers))

(defn add-ngrams-to-document
  "Used to add ngrams to the document.

*numbers*
  The number of ngrams to create. <br />
*document*
  The document to process. <br />
*processed-document*
  The processed-document to add the ngrams too. <br />
*ngrams-count*
  The ngrams map. <br />
*return*
  The processed document with the added ngrams"
  [numbers document processed-document ngrams-count]
  (reduce (fn [processed-document ngram]
            (if (contains? ngrams-count (first ngram))
              (assoc processed-document :counts
                (assoc (:counts processed-document) (first ngram) (second ngram))
                :number-of-words (+ (:number-of-words processed-document) (second ngram)))
              processed-document)) processed-document (n-grams-count numbers (list document))))
