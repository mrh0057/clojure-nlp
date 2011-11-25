(ns clojure-nlp.core
  (:use opennlp.nlp
        opennlp.treebank
        [clojure.contrib.io :only [file-str]]))

(def get-sentences)

(def tokenize)

(def pos-tag)

(def chunker)

(defn setup-models ([] (setup-models "models/"))
  ([base-dir]
     (def get-sentences (make-sentence-detector (file-str (str base-dir "en-sent.bin"))))
     (def tokenize (make-tokenizer (file-str (str base-dir "en-token.bin"))))
     (def pos-tag (make-pos-tagger (file-str (str base-dir "en-pos-maxent.bin"))))
     (def chunker (make-treebank-chunker (file-str (str base-dir "en-chunker.bin"))))))
