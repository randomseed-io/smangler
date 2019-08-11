(ns

    ^{:doc    "smangler library, class-based single dispatch."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.proto

  (:require [clojure.string  :as            str]
            [smangler.core   :as             sc]
            [smangler.spec   :as              s]
            [orchestra.core  :refer [defn-spec]]))

(defprotocol Stringable

  (->str [w]))

(extend-protocol Stringable

  String

  (->str [w] w)

  clojure.lang.Seqable

  (->str [w] (apply str w))

  Character

  (->str [w] (.toString w))

  Number

  (->str [w] (str w))

  nil

  (->str [w] nil))

(defprotocol Predicative

  (->char-pred [p])
  (->part-pred [p]))

(extend-protocol Predicative

  clojure.lang.Seqable

  (->char-pred [p] (set p))
  (->part-pred [p] (set p))

  clojure.lang.IPersistentVector

  (->char-pred [p] (set p))
  (->part-pred [p] (set p))

  clojure.lang.IFn

  (->char-pred [p] p)
  (->part-pred [p] p)

  Character

  (->char-pred [p] #(and (= p %)))
  (->part-pred [p] #(= p %))

  Number

  (->char-pred [p] (->char-pred (str p)))
  (->part-pred [p] (->part-pred (str p)))

  nil

  (->char-pred [p] (constantly nil))
  (->part-pred [p] (constantly nil)))
