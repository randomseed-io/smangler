(ns

    ^{:doc    "smangler library, core imports."
      :author "Paweł Wilk"}

    smangler.core

  (:require [clojure.string  :as            str]
            [smangler.spec   :as              s]
            [orchestra.core  :refer [defn-spec]]))

(defn-spec trim-both ::s/phrase
  "Takes a string and trims its first and last character if they are the
  same. Returns new string or nil when there is nothing to trim.

  When the matcher argument is used then it specifies a matching function used to
  decide whether to trim first and last character of a string. The given function
  should take a character and make a lookup to decide whether this character should
  be trimmed from the beginning of a string. The returned value is then used to match
  the last character of the same string. It should be a character (the same or
  different as passed), nil or false. The trimming function will trim the string on
  both ends if a value returned by this function match the last character of a string
  and if the character supplied as a first argument to this function matches the
  first character of a string. It's common to use a set (to match the same characters
  on both ends) or a map (to match different characters).

  When 3 arguments are given the first two should be characters used to match first
  and last character of a trimmed string (given as third argument).

  If nil is given instead of string the returned value is nil."
  {:added "1.0.0" :tag java.lang.String}

  ([^String p ::s/phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 0) (= (.charAt p 0) (.charAt p l)))
       (subs p 1 l))))

  ([^java.lang.Character start ::s/beginning-character
    ^java.lang.Character   end ::s/ending-character
    ^java.lang.String        p ::s/phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 0) (= (.charAt p 0) start) (= (.charAt p l) end))
       (subs p 1 l))))

  ([^clojure.lang.IFn matcher ::s/char-matcher
    ^java.lang.String       p ::s/phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 0) (= (matcher (.charAt p 0)) (.charAt p l)))
       (subs p 1 l)))))

(defn-spec all-suffixes ::s/lazy-seq-of-ne-strings
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}

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
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}

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
  "Generates all possible suffixes from the given sequence of objects"
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}
  [^clojure.lang.IFn pred]
  (comp
   (comp (partial map (partial apply concat))
         (comp (partial take-while seq)
               (partial iterate rest)))
   (partial partition-by pred)))

(defn-spec all-subs ::s/lazy-seq-of-ne-strings
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}

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
