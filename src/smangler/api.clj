(ns

    ^{:doc    "smangler library, API."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.api

  (:require [smangler.core :as sc]))

(declare to-str)
(declare trim-both-recur)

(defn- part-caller
  ([^clojure.lang.IFn f
    ^clojure.lang.IFn pred
    ^clojure.lang.ISeq w]
   (if (seq pred)
     (f pred (to-str w))
     (cons (to-str w) nil)))
  ([^clojure.lang.IFn f
    ^clojure.lang.IFn transformer
    ^clojure.lang.IFn pred
    ^clojure.lang.ISeq w]
   (if (seq pred)
     (f (transformer pred) (to-str w))
     (cons (to-str w) nil))))

(defn- part-caller-iterate
  ([^clojure.lang.IFn f
    ^clojure.lang.ISeq w]
   (if-some [o (seq (take-while seq (iterate f (to-str w))))]
     o (take-while some? (cons (to-str w) nil))))
  ([^clojure.lang.IFn f
    ^clojure.lang.IFn pred
    ^clojure.lang.ISeq w]
   (if-some [o (seq (take-while seq (iterate (partial f pred) (to-str w))))]
     o (take-while some? (cons (to-str w) nil))))
  ([^clojure.lang.IFn f
    ^java.lang.Character start
    ^java.lang.Character end
    ^clojure.lang.ISeq w]
   (if-some [o (seq (take-while seq (iterate (partial f start end) (to-str w))))]
     o (take-while some? (cons (to-str w) nil)))))

(defn trim-both-with-orig
  "Takes a string and returns a sequence containing the string and optionally its
  version with first and last character removed if they were the same character."
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  ([^String w]
   (take 2 (trim-both-recur w)))
  ([^clojure.lang.IFn pred ^String w]
   (take 2 (trim-both-recur pred w)))
  ([^java.lang.Character start ^java.lang.Character end ^String w]
   (take 2 (trim-both-recur start end w))))

(defprotocol Mangleable

  (to-str
    [w]
    ""
    {:added "1.0.0"
     :tag clojure.lang.ISeq})

  (trim-both
    [w] [pred w] [start end w]
    ""
    {:added "1.0.0"
     :tag clojure.lang.ISeq})

  (trim-both-recur
    [w] [pred w] [start end w]
    ""
    {:added "1.0.0"
     :tag clojure.lang.ISeq})

  (all-prefixes
    [w] [pred w]
    ""
    {:added "1.0.0"
     :tag clojure.lang.ISeq})

  (all-suffixes
    [w] [pred w]
    ""
    {:added "1.0.0"
     :tag clojure.lang.ISeq})

  (all-subs
    [w] [pred w]
    "Generates a sequence of all possible prefixes, suffixes and infixes for the
    given string (or other sequence of characters).

    When 2 arguments are given the first one will be used to partition the string
    in a way that the only allowed splitting points will

    by partitioning it with boundary
    characters determined through calling a function passed as a first argument. If
    the first argument is an empty sequence or nil then a sequence with single string
    will be returned (the string being converted input sequence), even if an object
    passed as the argument implements function interface (IFn). If the sequential
    collection is given instead of function then a set will be created out of it and
    used.

    If only one argument is given it should be a string (or other sequence of
    characters) and each character is treated as a separate element. In such case
    partitioning is performed for each character.

    The resulting sequence of strings may contain duplicated words if they appear
    more than once in the input string (including boundary characters themselves)."
    {:added "1.0.0"
     :tag clojure.lang.ISeq}))

(extend-protocol Mangleable

  clojure.lang.Seqable

  (to-str
    [w]
    (apply str w))

  (all-prefixes
    ([w]
     (sc/all-prefixes (to-str w)))
    ([pred ^clojure.lang.ISeq w]
     (part-caller sc/all-prefixes  set pred w)))

  (all-suffixes
    ([w]
     (sc/all-suffixes (to-str w)))
    ([pred ^clojure.lang.ISeq w]
     (part-caller sc/all-suffixes set pred w)))

  (all-subs
    ([w]
     (sc/all-subs (to-str w)))
    ([pred ^clojure.lang.ISeq w]
     (part-caller sc/all-subs set pred w)))

  (trim-both
    ([w]
     (or (sc/trim-both (to-str w)) (to-str w)))
    ([chars ^clojure.lang.ISeq w]
     (or (sc/trim-both (set chars) (to-str w)) (to-str w))))

  (trim-both-recur
    ([w]
     (part-caller-iterate sc/trim-both w))
    ([chars ^clojure.lang.ISeq w]
     (part-caller-iterate sc/trim-both chars w)))

  java.lang.String

  (to-str
    [w]
    w)

  (all-prefixes
    ([w]
     (sc/all-prefixes w))
    ([pred ^clojure.lang.ISeq w]
     (part-caller sc/all-prefixes set pred w)))

  (all-suffixes
    ([w]
     (sc/all-suffixes w))
    ([pred ^clojure.lang.ISeq w]
     (part-caller sc/all-suffixes set pred w)))

  (all-subs
    ([w]
     (sc/all-subs w))
    ([pred ^clojure.lang.ISeq w]
     (part-caller sc/all-subs set pred w)))

  (trim-both
    ([w]
     (or (sc/trim-both w) w))
    ([chars ^clojure.lang.ISeq w]
     (or (sc/trim-both (set chars) (to-str w)) (to-str w))))

  (trim-both-recur
    ([w]
     (part-caller-iterate sc/trim-both w))
    ([chars ^clojure.lang.ISeq w]
     (part-caller-iterate sc/trim-both (set chars) w)))

  java.lang.Character

  (to-str
    [w]
    (str w))

  (all-prefixes
    ([c]
     (cons (str c) nil))
    ([c ^clojure.lang.ISeq w]
     (sc/all-prefixes #(= c %) (to-str w))))

  (all-suffixes
    ([c]
     (cons (str c) nil))
    ([c ^clojure.lang.ISeq w]
     (sc/all-suffixes #(= c %) (to-str w))))

  (all-subs
    ([c]
     (cons (str c) nil))
    ([c ^clojure.lang.ISeq w]
     (sc/all-subs #(= c %) (to-str w))))

  (trim-both
    ([c]
     (str c))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (or (sc/trim-both start end (to-str w)) (to-str w))))

  (trim-both-recur
    ([c]
     (str c))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (part-caller-iterate sc/trim-both start end w)))

  java.lang.CharSequence

  (to-str
    [w]
    (.toString w))

  (all-prefixes
    ([w]
     (sc/all-prefixes w))
    ([chars ^clojure.lang.ISeq w]
     (part-caller sc/all-prefixes set chars w)))

  (all-suffixes
    ([w]
     (sc/all-suffixes w))
    ([chars ^clojure.lang.ISeq w]
     (part-caller sc/all-suffixes set chars w)))

  (all-subs
    ([w]
     (all-subs w))
    ([chars ^clojure.lang.ISeq w]
     (part-caller sc/all-subs set chars w)))

  (trim-both
    ([w]
     (or (sc/trim-both w) w))
    ([chars ^clojure.lang.ISeq w]
     (or (sc/trim-both (set chars) (to-str w)) (to-str w))))

  (trim-both-recur
    ([w]
     (part-caller-iterate sc/trim-both w))
    ([chars ^clojure.lang.ISeq w]
     (part-caller-iterate sc/trim-both (set chars) w)))

  clojure.lang.IPersistentSet

  (to-str
    [w]
    (apply str w))

  (all-prefixes
    [chars ^clojure.lang.ISeq w]
    (part-caller sc/all-prefixes chars w))

  (all-suffixes
    [chars ^clojure.lang.ISeq w]
    (part-caller sc/all-suffixes chars w))

  (all-subs
    [chars ^clojure.lang.ISeq w]
    (part-caller sc/all-subs chars w))

  (trim-both
    [chars ^clojure.lang.ISeq w]
    (or (sc/trim-both chars (to-str w)) (to-str w)))

  (trim-both-recur
    [chars ^clojure.lang.ISeq w]
    (part-caller-iterate sc/trim-both chars w))

  clojure.lang.IPersistentVector

  (to-str
    [w]
    (apply str w))

  (all-prefixes
    [pred ^clojure.lang.ISeq w]
    (part-caller sc/all-prefixes set pred w))

  (all-suffixes
    [pred ^clojure.lang.ISeq w]
    (part-caller sc/all-suffixes set pred w))

  (all-subs
    [pred ^clojure.lang.ISeq w]
    (part-caller sc/all-subs set pred w))

  (trim-both
    [chars ^clojure.lang.ISeq w]
    (or (sc/trim-both (set chars) (to-str w)) (to-str w)))

  (trim-both-recur
    [chars ^clojure.lang.ISeq w]
    (part-caller-iterate sc/trim-both (set chars) w))

  clojure.lang.IPersistentMap

  (trim-both
    [char-pairs ^clojure.lang.ISeq w]
    (or (sc/trim-both char-pairs (to-str w)) (to-str w)))

  (trim-both-recur
    [char-pairs ^clojure.lang.ISeq w]
    (part-caller-iterate sc/trim-both char-pairs w))

  clojure.lang.IFn

  (all-prefixes
    [pred ^clojure.lang.ISeq w]
    (sc/all-prefixes pred (to-str w)))

  (all-suffixes
    [pred ^clojure.lang.ISeq w]
    (sc/all-suffixes pred (to-str w)))

  (all-subs
    [pred ^clojure.lang.ISeq w]
    (sc/all-subs pred (to-str w)))

  (trim-both
    [pred ^clojure.lang.ISeq w]
    (or (trim-both pred (to-str w)) (to-str w)))

  (trim-both-recur
    [pred ^clojure.lang.ISeq w]
    (part-caller-iterate sc/trim-both pred w))

  nil

  (to-str
    [w]
    nil)

  (all-prefixes
    ([w]
     nil)
    ([chars ^clojure.lang.ISeq w]
     (cons (to-str w) nil)))

  (all-suffixes
    ([w]
     nil)
    ([chars ^clojure.lang.ISeq w]
     (cons (to-str w) nil)))

  (all-subs
    ([w]
     nil)
    ([pred ^clojure.lang.ISeq w]
     (cons (to-str w) nil)))

  (trim-both
    ([w]
     w)
    ([chars ^clojure.lang.ISeq w]
     (to-str w))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (if-some [start end]
       (sc/trim-both start end (to-str w))
       (to-str w))))

  (trim-both-recur
    ([w]
     w)
    ([chars ^clojure.lang.ISeq w]
     (to-str w))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (if-some [start end]
       (part-caller-iterate sc/trim-both start end w)
       (to-str w)))))
