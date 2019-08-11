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
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}
  ([^clojure.lang.IFn  f
    ^clojure.lang.ISeq w]
   (when (some? w)
     (if-some [o (seq (take-while seq (iterate f w)))]
       o (take-while some? (cons w nil)))))
  ([^clojure.lang.IFn     f
    ^clojure.lang.IFn  pred
    ^clojure.lang.ISeq    w]
   (when (some? w)
     (if-some [o (seq (take-while seq (iterate (partial f pred) w)))]
       o (take-while some? (cons w nil)))))
  ([^clojure.lang.IFn        f
    ^java.lang.Character start
    ^java.lang.Character   end
    ^clojure.lang.ISeq       w]
   (when (some? w)
     (if-some [o (seq (take-while seq (iterate (partial f start end) w)))]
       o (take-while some? (cons w nil))))))

(defmacro some-or [f & more]
  `(let [v# ~(last more)]
     (if-some [r# (~f ~@(butlast more) v#)] r# v#)))

(defn trim-both
  {:added "1.0.0"
   :tag String}
  ([^String w]
   (let [w (->str w)]
     (some-or sc/trim-both w)))
  ([^clojure.lang.IFn pred
    ^String              w]
   (let [w (->str w)]
     (some-or sc/trim-both (->char-pred pred) w)))
  ([^Character start
    ^Character   end
    ^String        w]
   (let [w (->str w)]
     (some-or sc/trim-both start end w))))

(defn trim-both-recur
  ([^String w]
   (let [w (->str w)]
     (part-caller-iterate sc/trim-both w)))
  ([^clojure.lang.IFn pred
    ^String              w]
   (let [w (->str w)]
     (part-caller-iterate sc/trim-both (->char-pred pred) w)))
  ([^Character start
    ^Character   end
    ^String        w]
   (let [w (->str w)]
     (part-caller-iterate sc/trim-both start end w))))

(defn trim-both-with-orig
  "Takes a string and returns a sequence containing the string and optionally its
  version with first and last character removed if they were the same character."
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}
  ([^String w]
   (take 2 (trim-both-recur w)))
  ([^clojure.lang.IFn pred
    ^String              w]
   (take 2 (trim-both-recur w)))
  ([^Character start
    ^Character   end
    ^String        w]
   (take 2 (trim-both-recur start end w))))

(defn all-prefixes
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}
  ([^String w]
   (sc/all-prefixes (->str w)))
  ([^clojure.lang.IFn pred
    ^String w]
   (sc/all-prefixes (->part-pred pred) (->str w))))

(defn all-suffixes
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}
  ([^String w]
   (sc/all-suffixes (->str w)))
  ([^clojure.lang.IFn pred
    ^String w]
   (sc/all-suffixes (->part-pred pred) (->str w))))

(defn all-subs
  {:added "1.0.0"
   :tag clojure.lang.LazySeq}
  ([^String w]
   (sc/all-subs (->str w)))
  ([^clojure.lang.IFn pred
    ^String w]
   (sc/all-subs (->part-pred pred) (->str w))))
