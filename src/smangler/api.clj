(ns

    ^{:doc    "smangler library, API."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.api

  (:require [clojure.string  :as            str]
            [smangler.core   :as             sc]
            [smangler.spec   :as              s]
            [smangler.proto  :refer        :all]
            [orchestra.core  :refer [defn-spec]]))

(defn- part-caller-iterate
  {:added "1.0.0" :tag clojure.lang.LazySeq}
  ([^clojure.lang.IFn  f
    ^clojure.lang.ISeq w]
   (when (some? w)
     (take-while some? (iterate f w))))
  ([^clojure.lang.IFn       f
    ^clojure.lang.IFn matcher
    ^clojure.lang.ISeq      w]
   (when (some? w)
     (take-while some? (iterate (partial f matcher) w))))
  ([^clojure.lang.IFn        f
    ^java.lang.Character start
    ^java.lang.Character   end
    ^clojure.lang.ISeq       w]
   (when (some? w)
     (take-while some? (iterate (partial f start end) w)))))

(defmacro some-or [f & more]
  `(when-some [v# ~(last more)]
     (if-some [r# (~f ~@(butlast more) v#)] r# v#)))

(defn-spec trim-same-once ::s/phrase
  {:added "1.0.0" :tag String}

  ([^String w ::s/stringable]
   (let [w (->str w)]
     (some-or sc/trim-same-once w)))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^String w                 ::s/stringable]
   (let [w (->str w)]
     (some-or sc/trim-same-once (->char-match matcher) w)))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^String        w ::s/stringable]
   (let [w (->str w)]
     (some-or sc/trim-same-once start end w))))

(defn-spec trim-same-seq ::s/lazy-seq-of-strings
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (let [w (->str w)]
     (part-caller-iterate sc/trim-same-once w)))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^String                 w ::s/stringable]
   (let [w (->str w)]
     (part-caller-iterate sc/trim-same-once (->char-match matcher) w)))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^String        w ::s/stringable]
   (let [w (->str w)]
     (part-caller-iterate sc/trim-same-once start end w))))

(defn-spec trim-same-once-with-orig ::s/one-or-two-strings
  "Takes a string and returns a sequence containing the string and optionally its
  version with first and last character removed if they were the same character."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^CharSequence w ::s/stringable]
   (take 2 (trim-same-seq w)))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^CharSequence           w ::s/stringable]
   (take 2 (trim-same-seq matcher w)))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^CharSequence  w ::s/stringable]
   (take 2 (trim-same-seq start end w))))

(defn-spec all-prefixes ::s/lazy-seq-of-ne-strings
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (sc/all-prefixes (->str w)))

  ([^clojure.lang.IFn pred ::s/phrase-splittable
    ^String              w ::s/stringable]
   (sc/all-prefixes (->part-pred pred) (->str w))))

(defn-spec all-suffixes ::s/lazy-seq-of-ne-strings
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (sc/all-suffixes (->str w)))

  ([^clojure.lang.IFn pred ::s/phrase-splittable
    ^String w              ::s/stringable]
   (sc/all-suffixes (->part-pred pred) (->str w))))

(defn-spec all-subs ::s/lazy-seq-of-ne-strings
  {:added "1.0.0" :tag clojure.lang.LazySeq}
  ([^String w ::s/stringable]
   (sc/all-subs (->str w)))
  ([^clojure.lang.IFn pred ::s/phrase-splittable
    ^String              w ::s/stringable]
   (sc/all-subs (->part-pred pred) (->str w))))
