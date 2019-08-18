(ns

    ^{:doc    "smangler library, core imports."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.core

  (:require [clojure.string  :as            str]
            [smangler.spec   :as              s]
            [orchestra.core  :refer [defn-spec]]))

(defn-spec trim-same-once ::s/phrase
  "Takes a string and trims its first and last character if they are equal.
  Returns a new string or nil when there is nothing to trim or nil was passed
  as an argument instead of a string. For an empty string it also returns nil.

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

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  When the string consist of 2 matching letters the result will be an empty string."
  {:added "1.0.0" :tag java.lang.String}

  ([^CharSequence p ::s/phrase]
   (when-let [l (and (seq p) (unchecked-dec (.length p)))]
     (when (and (> l 0) (= (.charAt p 0) (.charAt p l)))
       (.. p (subSequence 1 l) toString))))

  ([^clojure.lang.IFn matcher ::s/char-matcher
    ^java.lang.CharSequence p ::s/phrase]
   (when-let [l (and (seq p) (unchecked-dec (.length p)))]
     (when (and (> l 0) (= (matcher (.charAt p 0)) (.charAt p l)))
       (.. p (subSequence 1 l) toString))))

  ([^java.lang.Character start ::s/beginning-character
    ^java.lang.Character   end ::s/ending-character
    ^java.lang.CharSequence  p ::s/phrase]
   (when-let [l (and (seq p) (unchecked-dec (.length p)))]
     (when (and (> l 0) (= (.charAt p 0) start) (= (.charAt p l) end))
       (.. p (subSequence 1 l) toString)))))

(defn-spec trim-same ::s/phrase
  "Takes a string and recursively trims its first and last character if they are equal.
  Returns a new string or nil when nil was passed as an argument instead of a
  string. For an empty string it returns an empty string.

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

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  When the string consist of 2 matching letters the result will be an empty string."
  {:added "1.0.0" :tag java.lang.String}

  ([^CharSequence p ::s/phrase]
   (when p
     (loop [l ^int (unchecked-int 0)
            r ^int (unchecked-dec-int (unchecked-int (.length p)))]
       (if (>= l r)
         (if (= l r)
           (.. p (charAt l) toString)
           "")
         (if (= (.charAt p l) (.charAt p r))
           (recur (unchecked-inc-int l) (unchecked-dec-int r))
           (.. p (subSequence l (unchecked-inc-int r)) toString))))))

  ([^clojure.lang.IFn matcher ::s/char-matcher
    ^CharSequence           p ::s/phrase]
   (when p
     (loop [l ^int (unchecked-int 0)
            r ^int (unchecked-dec-int (unchecked-int (.length p)))]
       (if (>= l r)
         (if (= l r)
           (.. p (charAt l) toString)
           "")
         (if (= (matcher (.charAt p l)) (.charAt p r))
           (recur (unchecked-inc-int l) (unchecked-dec-int r))
           (.. p (subSequence l (unchecked-inc-int r)) toString))))))

  ([^Character      start ::s/beginning-character
    ^Character        end ::s/ending-character
    ^CharSequence       p ::s/phrase]
   (when p
     (loop [l ^int (unchecked-int 0)
            r ^int (unchecked-dec-int (unchecked-int (.length p)))]
       (if (>= l r)
         (if (= l r)
           (.. p (charAt l) toString)
           "")
         (if (and (= (.charAt p l) start) (= (.charAt p r) end))
           (recur (unchecked-inc-int l) (unchecked-dec-int r))
           (.. p (subSequence l (unchecked-inc-int r)) toString)))))))

(defn-spec all-suffixes ::s/lazy-seq-of-ne-strings
  "Generates a lazy sequence of all possible suffixes of a given string. Returns nil
  if nil or an empty string was given as an argument instead of a string.

  The resulting sequence will contain the whole string on its first position.

  If two arguments are given the first one should be a predicate function used to
  partition the given string. It should take a single character and if the returned
  value is not nil nor false then all-suffixes will create a boundary (a place where
  it can slice the string during generation of suffixes). Without this predicate each
  character is such a boundary."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String p ::s/phrase]
   (when (seq p)
     (take-while seq (iterate #(subs %1 1) p))))

  ([^clojure.lang.IFn pred ::s/phrase-splitter
    ^String p              ::s/phrase]
   (when-let [p (seq p)]
     (->> p
          (partition-by pred)
          (iterate rest)
          (take-while seq)
          (map #(apply str (apply concat %)))))))

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
  character is such a boundary."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^String p ::s/phrase]
   (when-let [p (seq p)]
     (rest (reductions str "" p))))

  ([^clojure.lang.IFn pred ::s/phrase-splitter
    ^String p              ::s/phrase]
   (when-let [p (seq p)]
     (->> p
          (partition-by pred)
          (reductions concat)
          (map (partial apply str))))))

(defn- for-suffixes
  "Generates all possible suffixes from the given sequence of character sequences."
  {:added "1.0.0" :tag clojure.lang.LazySeq}
  [^clojure.lang.IFn pred]
  (comp
   (comp (partial map (partial apply concat))
         (comp (partial take-while seq)
               (partial iterate rest)))
   (partial partition-by pred)))

(defn-spec all-subs ::s/lazy-seq-of-ne-strings
  "Generates a lazy sequence of all possible substrings (prefixes, suffixes and
  infixes) of a given string. Returns nil if nil or an empty string was given as an
  argument instead of a string.

  The resulting sequence will contain the whole string in its middle. Moreover, the
  substrings will not be unique across the sequence if the characters are repeating
  in the input.

  If two arguments are given the first one should be a predicate function used to
  partition the given string. It should take a single character and if the returned
  value is not nil nor false then all-subs will create a boundary (a place where it
  can slice the string during generation of suffixes). Without this predicate each
  character is such a boundary."
  {:added "1.0.0" :tag clojure.lang.LazySeq}

  ([^clojure.lang.IFn pred ::s/phrase-splitter
    ^java.lang.String p    ::s/phrase]
   (when-let [p (seq p)]
     (->> p
          (partition-by pred)
          (reductions concat)
          (map (for-suffixes pred))
          (apply concat)
          (map str/join))))

  ([^java.lang.String p ::s/phrase]
   (when (seq p)
     (->> p
          all-prefixes
          (map all-suffixes)
          (apply concat)))))
