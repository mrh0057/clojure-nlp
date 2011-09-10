(ns clojure-nlp.term-weighting)

(defprotocol TermWeighting
  (calculate-weight [this term word-count]))
