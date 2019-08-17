(ns

    ^{:doc    "smangler library, API."
      :author "Paweł Wilk"
      :added  "1.0.0"
      :no-doc true}

    smangler.api

  (:require [clojure.string  :as            str]
            [smangler.core   :as             sc]
            [smangler.spec   :as              s]
            [smangler.util   :refer        :all]
            [smangler.proto  :refer        :all]
            [orchestra.core  :refer [defn-spec]]))

(defn-spec trim-same ::s/phrase
  {:added "1.0.0" :tag String}

  ([^String w ::s/stringable]
   (sc/trim-same (->str w)))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^String w                 ::s/stringable]
   (sc/trim-same (->char-match matcher) (->str w)))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^String        w ::s/stringable]
   (sc/trim-same start end (->str w))))

(defn-spec trim-same-once ::s/phrase
  {:added "1.0.0" :tag String}

  ([^String w ::s/stringable]
   (some-or sc/trim-same-once (->str w)))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^String w                 ::s/stringable]
   (some-or sc/trim-same-once (->char-match matcher) (->str w)))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^String        w ::s/stringable]
   (some-or sc/trim-same-once start end (->str w))))

(defn-spec trim-same-seq ::s/lazy-seq-of-strings
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (part-caller-iterate sc/trim-same-once (->str w)))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^String                 w ::s/stringable]
   (part-caller-iterate sc/trim-same-once (->char-match matcher) (->str w)))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^String        w ::s/stringable]
   (part-caller-iterate sc/trim-same-once start end (->str w))))

(defn-spec trim-same-once-with-orig ::s/one-or-two-strings
  "Takes a string and returns a sequence containing the string and optionally its
  version with first and last character removed if they were the same character."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^CharSequence w ::s/stringable]
   (when-some [w (->str w)]
     (cons w (when-some [r (sc/trim-same-once w)]
               (cons r nil)))))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^CharSequence           w ::s/stringable]
   (when-some [w (->str w)]
     (cons w (when-some [r (sc/trim-same-once (->part-pred matcher) w)]
               (cons r nil)))))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^CharSequence  w ::s/stringable]
   (when-some [w (->str w)]
     (cons w (when-some [r (sc/trim-same-once start end w)]
               (cons r nil))))))

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
