(ns

    ^{:doc    "smangler library, API."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.api

  (:require [smangler.core   :as             sc]
            [smangler.spec   :as              s]
            [smangler.util   :refer        :all]
            [smangler.proto  :refer        :all]
            [orchestra.core  :refer [defn-spec]]))

(defn-spec trim-same ::s/phrase
  "Takes a string and recursively trims its first and last character if they are equal.
  Returns a new string or nil when nil was passed as an argument instead of a
  string. For an empty string it returns an empty string.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  When the matcher argument is present it specifies a matching function used to
  decide whether to trim first and last character. The given function should take a
  character and make a lookup to decide whether a character should be trimmed from
  the beginning of a string. Additionally, its returned value (if a character) is
  then used to match the last character of the string. Therefore the returned value
  should be a character (the same or different as passed), nil or false (to indicate
  a failed match). The trimming function will trim a string on both ends if a value
  returned by the matcher is equal to the last character of this string. It's common
  to use a set (to match the same characters on both ends) or a map (to match
  different characters).

  It automatically converts objects of the following types to matchers: characters,
  numbers, strings, sequences of characters, collections of strings, collections of
  characters, collections of numbers. It splits them into single-character elements
  and creates a sets to be used as functions. In case of single characters it creates
  a small predicate function. For nil it will create a function that won't match
  anything. Same for empty collections or strings.

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  When the string consist of 2 matching letters the result will be an empty string."
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
  "Takes a string and trims its first and last character if they are equal.
  Returns a new string or original string if there is nothing to trim. For nil it
  returns nil.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  When the matcher argument is present it specifies a matching function used to
  decide whether to trim first and last character. The given function should take a
  character and make a lookup to decide whether a character should be trimmed from
  the beginning of a string. Additionally, its returned value (if a character) is
  then used to match the last character of the string. Therefore the returned value
  should be a character (the same or different as passed), nil or false (to indicate
  a failed match). The trimming function will trim a string on both ends if a value
  returned by the matcher is equal to the last character of this string. It's common
  to use a set (to match the same characters on both ends) or a map (to match
  different characters).

  It automatically converts objects of the following types to matchers: characters,
  numbers, strings, sequences of characters, collections of strings, collections of
  characters, collections of numbers. It splits them into single-character elements
  and creates a sets to be used as functions. In case of single characters it creates
  a small predicate function. For nil it will create a function that won't match
  anything. Same for empty collections or strings.

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  When the string consist of 2 matching letters the result will be an empty string."
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
  "Takes a string and recursively trims its first and last character if they are
  equal. Returns a lazy sequence of strings for each iteration. For nil it returns
  nil.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  When the matcher argument is present it specifies a matching function used to
  decide whether to trim first and last character. The given function should take a
  character and make a lookup to decide whether a character should be trimmed from
  the beginning of a string. Additionally, its returned value (if a character) is
  then used to match the last character of the string. Therefore the returned value
  should be a character (the same or different as passed), nil or false (to indicate
  a failed match). The trimming function will trim a string on both ends if a value
  returned by the matcher is equal to the last character of this string. It's common
  to use a set (to match the same characters on both ends) or a map (to match
  different characters).

  It automatically converts objects of the following types to matchers: characters,
  numbers, strings, sequences of characters, collections of strings, collections of
  characters, collections of numbers. It splits them into single-character elements
  and creates a sets to be used as functions. In case of single characters it creates
  a small predicate function. For nil it will create a function that won't match
  anything. Same for empty collections or strings.

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  When the last of the processed strings consist of 2 matching letters the resulting
  sequence will contain an empty string."
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
  "Takes a string and trims its first and last character if they are equal.
  Returns a 2-element sequence containing a new string and the original one. If there
  is nothing to trim it returns a sequence with just 1 element. For nil it returns
  nil.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  When the matcher argument is present it specifies a matching function used to
  decide whether to trim first and last character. The given function should take a
  character and make a lookup to decide whether a character should be trimmed from
  the beginning of a string. Additionally, its returned value (if a character) is
  then used to match the last character of the string. Therefore the returned value
  should be a character (the same or different as passed), nil or false (to indicate
  a failed match). The trimming function will trim a string on both ends if a value
  returned by the matcher is equal to the last character of this string. It's common
  to use a set (to match the same characters on both ends) or a map (to match
  different characters).

  It automatically converts objects of the following types to matchers: characters,
  numbers, strings, sequences of characters, collections of strings, collections of
  characters, collections of numbers. It splits them into single-character elements
  and creates a sets to be used as functions. In case of single characters it creates
  a small predicate function. For nil it will create a function that won't match
  anything. Same for empty collections or strings.

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  When the last of the processed strings consist of 2 matching letters the resulting
  sequence will contain an empty string."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^CharSequence w ::s/stringable]
   (when-some [w (->str w)]
     (cons w (when-some [r (sc/trim-same-once w)]
               (cons r nil)))))

  ([^clojure.lang.IFn matcher ::s/char-matchable
    ^CharSequence           w ::s/stringable]
   (when-some [w (->str w)]
     (cons w (when-some [r (sc/trim-same-once (->char-match matcher) w)]
               (cons r nil)))))

  ([^Character start ::s/beginning-character
    ^Character   end ::s/ending-character
    ^CharSequence  w ::s/stringable]
   (when-some [w (->str w)]
     (cons w (when-some [r (sc/trim-same-once start end w)]
               (cons r nil))))))

(defn-spec all-prefixes ::s/lazy-seq-of-ne-strings
  "Generates a lazy sequence of all possible prefixes of a given string. Returns nil
  if nil or an empty string was given as an argument instead of a string.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  The resulting sequence will contain the whole string on its last position.

  If two arguments are given the first one should be a predicate function used to
  partition the given string. It should take a single character and if the returned
  value is not nil nor false then all-prefixes will create a boundary (a place where
  it can slice the string during generation of prefixes). Without this predicate each
  character is such a boundary.

  It automatically converts objects of the following types to slicing predicates:
  characters, numbers, strings, sequences of characters, collections of strings,
  collections of characters, collections of numbers. It splits them into
  single-character elements and creates a sets to be used as functions. In case of
  single characters it creates a small predicate function. For nil it will create a
  function that won't match anything. Same for empty collections or strings."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (sc/all-prefixes (->str w)))

  ([^clojure.lang.IFn pred ::s/phrase-splittable
    ^String              w ::s/stringable]
   (sc/all-prefixes (->part-pred pred) (->str w))))

(defn-spec all-suffixes ::s/lazy-seq-of-ne-strings
  "Generates a lazy sequence of all possible suffixes of a given string. Returns nil
  if nil or an empty string was given as an argument instead of a string.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  The resulting sequence will contain the whole string on its first position.

  If two arguments are given the first one should be a predicate function used to
  partition the given string. It should take a single character and if the returned
  value is not nil nor false then all-suffixes will create a boundary (a place where
  it can slice the string during generation of suffixes). Without this predicate each
  character is such a boundary.

  It automatically converts objects of the following types to slicing predicates:
  characters, numbers, strings, sequences of characters, collections of strings,
  collections of characters, collections of numbers. It splits them into
  single-character elements and creates a sets to be used as functions. In case of
  single characters it creates a small predicate function. For nil it will create a
  function that won't match anything. Same for empty collections or strings."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (sc/all-suffixes (->str w)))

  ([^clojure.lang.IFn pred ::s/phrase-splittable
    ^String w              ::s/stringable]
   (sc/all-suffixes (->part-pred pred) (->str w))))

(defn-spec all-subs ::s/lazy-seq-of-ne-strings
  "Generates a lazy sequence of all possible substrings (prefixes, suffixes and
  infixes) of a given string. Returns nil if nil or an empty string was given as an
  argument instead of a string.

  It automatically converts objects of the following types to strings: characters,
  numbers, sequences of characters, collections of strings, collections of
  characters, collections of numbers. For collections it joins the elements converted
  to strings.

  The resulting sequence will contain the whole string in its middle. Moreover, the
  substrings will not be unique across the sequence if the characters are repeating
  in the input.

  If two arguments are given the first one should be a predicate function used to
  partition the given string. It should take a single character and if the returned
  value is not nil nor false then all-subs will create a boundary (a place where it
  can slice the string during generation of suffixes). Without this predicate each
  character is such a boundary.

  It automatically converts objects of the following types to slicing predicates:
  characters, numbers, strings, sequences of characters, collections of strings,
  collections of characters, collections of numbers. It splits them into
  single-character elements and creates a sets to be used as functions. In case of
  single characters it creates a small predicate function. For nil it will create a
  function that won't match anything. Same for empty collections or strings."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String w ::s/stringable]
   (sc/all-subs (->str w)))

  ([^clojure.lang.IFn pred ::s/phrase-splittable
    ^String              w ::s/stringable]
   (sc/all-subs (->part-pred pred) (->str w))))
