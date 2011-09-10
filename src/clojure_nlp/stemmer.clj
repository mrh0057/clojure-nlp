(ns clojure-nlp.stemmer
  (:import [org.tartarus.snowball.ext EnglishStemmer]))

(defprotocol Stemmable
  (stem-snowball [this]))

(extend-type java.lang.String
  Stemmable
  (stem-snowball [this]
    (let [stemmer (new EnglishStemmer)]
      (doto stemmer
        (. setCurrent this)
        (. stem))
      (. stemmer getCurrent))))

(extend-type clojure.lang.ISeq
  Stemmable
  (stem-snowball [seq]
    "Used to stem a sequence of words that are in the seq.
  
  seq - The sequence to stem.
  
  return - A list of the stemmed words."
    (let [stemmer (new EnglishStemmer)]
      (map (fn [word]
             (doto stemmer
               (. setCurrent word)
               (. stem))
             (. stemmer getCurrent))seq))))
