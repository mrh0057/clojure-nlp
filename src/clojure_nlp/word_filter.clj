(ns clojure-nlp.word-filter)

(defprotocol FilterWords
  (filter-not-letters [this]))

(defprotocol CharacterProtocol
  (letter? [this])
  (digit? [this]))

(extend-type java.lang.Character
  CharacterProtocol
  (letter? [this] (Character/isLetter this))
  (digit? [this] (Character/isDigit this)))

(extend-type java.lang.String
  FilterWords
  (filter-not-letters [this]
    (let [str-builder (new StringBuilder)]
      (reduce (fn [last val]
                (if (letter? val)
                  (. str-builder append val))) nil this)
      (. str-builder toString))))

(extend-type clojure.lang.ISeq
  FilterWords
  (filter-not-letters [this]
    (map #(filter-not-letters %) this)))

(extend-type nil
  FilterWords
  (filter-not-letters [this]
    nil))
