(ns

    ^{:doc    "smangler library, core imports."
      :author "Paweł Wilk"}

    smangler.core

  (:require [clojure.string  :as            str]
            [smangler.spec   :as              s]
            [orchestra.core  :refer [defn-spec]]))

(defn-spec trim-both ::s/phrase
  ""
  {:added "1.0.0"
   :tag java.lang.String}

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

  ([^clojure.lang.IFn pred ::s/char-matcher
    ^java.lang.String    p ::s/phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 0) (= (pred (.charAt p 0)) (.charAt p l)))
       (subs p 1 l)))))

(defn-spec all-suffixes ::s/non-empty-strings
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

(defn-spec all-prefixes ::s/non-empty-strings
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

(defn-spec all-subs ::s/non-empty-strings
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
