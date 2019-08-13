(ns

    ^{:doc    "smangler library, class-based single dispatch."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.proto)

(defprotocol Stringable

  (->str [w]))

(extend-protocol Stringable

  String

  (->str [w] w)

  clojure.lang.Seqable

  (->str [w] (apply str w))

  CharSequence

  (->str [w] (apply str w))

  Character

  (->str [w] (.toString w))

  Number

  (->str [w] (str w))

  nil

  (->str [w] nil))

(defprotocol Predicative

  (->char-match [p])
  (->part-pred  [p]))

(extend-protocol Predicative

  clojure.lang.Sequential

  (->char-match [p] (set (apply str p)))
  (->part-pred  [p] (set (apply str p)))

  CharSequence

  (->char-match [p] (set p))
  (->part-pred  [p] (set p))

  clojure.lang.IPersistentVector

  (->char-match [p] (set (apply str p)))
  (->part-pred  [p] (set (apply str p)))

  clojure.lang.IFn

  (->char-match [p] p)
  (->part-pred  [p] p)

  Character

  (->char-match [p] #(and (= p %) %))
  (->part-pred  [p] #(= p %))

  Number

  (->char-match [p] (set (str p)))
  (->part-pred  [p] (set (str p)))

  nil

  (->char-match [p] (constantly nil))
  (->part-pred  [p] (constantly nil)))
