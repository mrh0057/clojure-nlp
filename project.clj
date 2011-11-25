(defproject clojure-nlp "0.0.1-SNAPSHOT"
  :description "FIXME: write"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.apache.lucene/lucene-snowball "3.0.3"]
                 [clj-data "0.0.1-SNAPSHOT"]
                 [org.apache.commons/commons-math "2.2"]
                 [clojure-opennlp "0.1.5"]]
  :dev-dependencies [[swank-clojure "1.3.1"]
                     [lein-midje "1.0.3"]
                     [lein-marginalia "0.6.0"]
                     [midje "1.1.1" :exclusions [org.clojure/clojure
                                             org.clojure.contrib/core]]])
