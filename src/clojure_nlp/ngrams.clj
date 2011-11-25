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
