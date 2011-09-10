(ns clojure-nlp.seq-utils)

(defn cons-not-empty [val seq]
  (if (empty? val)
    seq
    (cons val seq)))
