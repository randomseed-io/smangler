(ns

    ^{:doc    "smangler library, class-based single dispatch."
      :author "Paweł Wilk"
      :added  "1.0.0"
      :no-doc true}

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

  (->character-matcher  [p])
  (->string-partitioner [p]))

(extend-protocol Predicative

  clojure.lang.Sequential

  (->character-matcher  [p] (set (apply str p)))
  (->string-partitioner [p] (set (apply str p)))

  CharSequence

  (->character-matcher  [p] (set p))
  (->string-partitioner [p] (set p))

  clojure.lang.PersistentVector

  (->character-matcher  [p] (set (apply str p)))
  (->string-partitioner [p] (set (apply str p)))

  clojure.lang.IPersistentVector

  (->character-matcher  [p] (set (apply str p)))
  (->string-partitioner [p] (set (apply str p)))

  clojure.lang.IFn

  (->character-matcher  [p] p)
  (->string-partitioner [p] p)

  Character

  (->character-matcher  [p] #(and (= p %) %))
  (->string-partitioner [p] #(and (= p %) %))

  Number

  (->character-matcher  [p] (set (str p)))
  (->string-partitioner [p] (set (str p)))

  nil

  (->character-matcher  [p] (constantly nil))
  (->string-partitioner [p] (constantly nil)))
