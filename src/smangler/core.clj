(ns

    ^{:doc    "smangler library, core imports."
      :author "Paweł Wilk"}

    smangler.core

  (:require [parallel.core  :as p]
            [clojure.string :as s]))

(declare trim-both-recur)
(declare trim-both-with-pairs-core)
(declare trim-both-with-fn-core)

(defn- trim-both-core
  "Takes a string and returns its version with first and last character removed if
   they were the same character. Returns nil if there is nothing to trim."
  {:added "1.0.0"
   :tag java.lang.String}
  ([^String w]
   (when-let [l (and (seq w) (dec (.length w)))]
     (when (and (> l 1) (= (.charAt w 0) (.charAt w l)))
       (subs w 1 l))))
  ([^java.lang.Character start ^java.lang.Character end ^String w]
   (when-let [l (and (seq w) (dec (.length w)))]
     (when (and (> l 1) (= (.charAt w 0) start) (= (.charAt w l) end))
       (subs w 1 l))))
  ([^clojure.lang.IFn chars ^String w]
   (when-let [l (and (seq w) (dec (.length w)))]
     (when (and (> l 1) (= (chars (.charAt w 0)) (.charAt w l)))
       (subs w 1 l)))))

(defn trim-both-preserving-orig
  "Takes a string and returns a sequence containing the string and optionally its
  version with first and last character removed if they were the same character."
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  ([^String w]
   (take 2 (trim-both-recur w)))
  ([chars ^String w]
   (take 2 (trim-both-recur chars w)))
  ([^java.lang.Character start ^java.lang.Character end ^String w]
   (take 2 (trim-both-recur start end w))))

(defn- trim-both-with-pairs
  [^clojure.lang.IPersistentMap pairs ^String w]
  (when-let [l (and (seq w) (dec (.length w)))]
    (when (and (> l 1) (= (pairs (.charAt w 0)) (.charAt w l)))
      (subs w 1 l))))

(defn all-suffixes
  [^String w]
  "Generates a sequence of all possible suffixes for a given string."
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  (take-while seq (iterate #(subs %1 1) w)))

(defn all-prefixes
  [^String w]
  "Generates a sequence of all possible prefixes for a given string."
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  (rest (reductions str "" w)))

(defn- for-suffixes
  [^clojure.lang.ISeq s]
  "Generates a sequence of all possible suffixes."
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  (map #(apply concat %) (take-while seq (iterate rest s))))

(defn- all-subs-core
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  ([^clojure.lang.IFn pred ^clojure.lang.ISeq w]
   (->> w
        (partition-by pred)
        (reductions concat)
        (map (comp for-suffixes (partial partition-by pred)))
        (apply concat)
        (map s/join)))
  ([^clojure.lang.ISeq w]
   (->> w
        all-prefixes
        (map all-suffixes)
        (apply concat))))

(defprotocol Mangable

  (trim-both
    [w] [chars w] [start end w])

  (trim-both-recur
    [w] [chars w] [start end w])

  (all-subs
    [w] [pred w]
    "Generates a sequence of all possible prefixes, suffixes and infixes for the
    given string (or other sequence of characters) by partitioning it with boundary
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
    more than once in the input string (including boundary characters themselves)."))

(extend-protocol Mangable

  clojure.lang.Seqable

  (all-subs
    ([w]
     (all-subs-core (apply str w)))
    ([pred ^clojure.lang.ISeq w]
     (if (seq pred) (all-subs-core (set pred) (apply str w)) (cons (apply str w) nil))))

  (trim-both
    ([w]
     (or (trim-both-core (apply str w)) (apply str w)))
    ([chars ^clojure.lang.ISeq w]
     (or (trim-both-core (set chars) (apply str w)) (apply str w))))

  (trim-both-recur
    ([w]
     (if-some [o (seq (take-while seq (iterate trim-both-core (apply str w))))]
       o (take-while some? (cons (apply str w) nil))))
    ([chars ^clojure.lang.ISeq w]
     (if-some [o (seq (take-while seq (iterate (partial trim-both-core chars) (apply str w))))]
       o (take-while some? (cons (apply str w) nil)))))

  java.lang.String

  (all-subs
    ([w]
     (all-subs-core w))
    ([pred ^clojure.lang.ISeq w]
     (if (seq pred) (all-subs-core (set pred) (apply str w)) (cons (apply str w) nil))))

  (trim-both
    ([w]
     (or (trim-both-core w) w))
    ([chars ^clojure.lang.ISeq w]
     (or (trim-both-core (set chars) (apply str w)) (apply str w))))

  (trim-both-recur
    ([w]
     (if-some [o (seq (take-while seq (iterate trim-both-core w)))]
       o (take-while some? (cons w nil))))
    ([chars ^clojure.lang.ISeq w]
     (if-some [o (seq (take-while seq (iterate (partial trim-both-core (set chars)) (apply str w))))]
       o (take-while some? (cons (apply str w) nil)))))

  java.lang.Character

  (all-subs
    ([c]
     (cons (str c) nil))
    ([pred ^clojure.lang.ISeq w]
     (all-subs-core #(= pred %) (apply str w))))

  (trim-both
    ([c]
     (str c))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (or (trim-both-core start end (apply str w)) (apply str w))))

  (trim-both-recur
    ([c]
     (str c))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (if-some [o (seq (take-while seq (iterate (partial trim-both-core start end) (apply str w))))]
       o (take-while some? (cons (apply str w) nil)))))

  java.lang.CharSequence

  (all-subs
    ([w]
     (all-subs-core w))
    ([pred ^clojure.lang.ISeq w]
     (if (seq pred) (all-subs-core (set pred) (apply str w)) (cons (apply str w) nil))))

  (trim-both
    ([w]
     (or (trim-both-core w) w))
    ([chars ^clojure.lang.ISeq w]
     (or (trim-both-core (set chars) (apply str w)) (apply str w))))

  (trim-both-recur
    ([w]
     (if-some [o (seq (take-while seq (iterate trim-both-core w)))]
       o (take-while some? (cons w nil))))
    ([chars ^clojure.lang.ISeq w]
     (if-some [o (seq (take-while seq (iterate (partial trim-both-core (set chars)) (apply str w))))]
       o (take-while some? (cons (apply str w) nil)))))

  clojure.lang.IPersistentSet

  (all-subs
    [pred ^clojure.lang.ISeq w]
    (if (seq pred) (all-subs-core pred (apply str w)) (cons (apply str w) nil)))

  (trim-both
    [chars ^clojure.lang.ISeq w]
    (or (trim-both-core chars (apply str w)) (apply str w)))

  (trim-both-recur
    [chars ^clojure.lang.ISeq w]
    (if-some [o (seq (take-while seq (iterate (partial trim-both-core chars) (apply str w))))]
      o (take-while some? (cons (apply str w) nil))))

  clojure.lang.IPersistentVector

  (all-subs
    [pred ^clojure.lang.ISeq w]
    (if (seq pred) (all-subs-core (set pred) (apply str w)) (cons (apply str w) nil)))

  (trim-both
    [chars ^clojure.lang.ISeq w]
    (or (trim-both-core (set chars) (apply str w)) (apply str w)))

  (trim-both-recur
    [chars ^clojure.lang.ISeq w]
    (if-some [o (seq (take-while seq (iterate (partial trim-both-core (set chars)) (apply str w))))]
      o (take-while some? (cons (apply str w) nil))))

  clojure.lang.IPersistentMap

  (trim-both
    [chars ^clojure.lang.ISeq w]
    (or (trim-both-with-pairs-core chars (apply str w)) (apply str w)))

  (trim-both-recur
    [chars ^clojure.lang.ISeq w]
    (if-some [o (seq (take-while seq (iterate (partial trim-both-with-pairs-core chars) (apply str w))))]
      o (take-while some? (cons (apply str w) nil))))

  clojure.lang.IFn

  (all-subs
    [pred ^clojure.lang.ISeq w]
    (all-subs-core pred (apply str w)))

  (trim-both
    [pred ^clojure.lang.ISeq w]
    (or (trim-both-with-fn-core pred (apply str w)) (apply str w)))

  (trim-both-recur
    [pred ^clojure.lang.ISeq w]
    (if-some [o (seq (take-while seq (iterate (partial trim-both-with-fn-core pred) (apply str w))))]
      o (take-while some? (cons (apply str w) nil))))

  nil

  (all-subs
    ([w]
     (all-subs-core w))
    ([pred ^clojure.lang.ISeq w]
     (cons (apply str w) nil)))

  (trim-both
    ([w]
     w)
    ([chars ^clojure.lang.ISeq w]
     (apply str w))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (apply str w)))

  (trim-both-recur
    ([w]
     w)
    ([chars ^clojure.lang.ISeq w]
     (apply str w))
    ([start ^java.lang.Character end ^clojure.lang.ISeq w]
     (apply str w))))
