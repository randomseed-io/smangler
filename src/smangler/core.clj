(ns

    ^{:doc    "smangler library, core imports."
      :author "Paweł Wilk"}

    smangler.core

  (:require [clojure.string      :as            str]
            [clojure.spec.alpha  :as              s]
            [expound.alpha       :refer    [defmsg]]
            [orchestra.core      :refer [defn-spec]]))

;; Basic specs.

(s/def ::empty-seq
  (s/and seq? empty?))

(s/def ::nothing
  #(or (nil? %)
       (and (seq? %) (empty? %))))

(s/def ::phrase
  (s/nilable string?))

(s/def ::character
  char?)

(s/def ::l-character
  char?)

(s/def ::r-character
  char?)

(s/def ::char-matcher
  (s/fspec :args (s/cat :v ::character)
           :ret  (s/or  :character ::character
                        :nothing   ::nothing)
           :fn   #(let [r (second (:ret %))
                        v (:v (:args %))]
                    (or (= r v) (s/valid? ::nothing r)))))

(defmsg ::empty-seq    "should be an empty sequence")
(defmsg ::l-character  "should be most-left character")
(defmsg ::r-character  "should be most-right character")
(defmsg ::phrase       "should be a string or nil")
(defmsg ::nothing      "should be an empty sequence or nil")
(defmsg ::char-matcher "should be a function taking a character and returning it or nothing")

;; Functions' definitions.

(defn-spec trim-both ::phrase
  ""
  {:added "1.0.0"
   :tag java.lang.String}

  ([^String p ::phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 1) (= (.charAt p 0) (.charAt p l)))
       (subs p 1 l))))

  ([^java.lang.Character start ::l-character
    ^java.lang.Character   end ::r-character
    ^java.lang.String        p ::phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 1) (= (.charAt p 0) start) (= (.charAt p l) end))
       (subs p 1 l))))

  ([^clojure.lang.IFn pred ::char-matcher
    ^java.lang.String    p ::phrase]
   (when-let [l (and (seq p) (dec (.length p)))]
     (when (and (> l 1) (= (pred (.charAt p 0)) (.charAt p l)))
       (subs p 1 l)))))

(defn trim-both-with-pairs
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  [^clojure.lang.IPersistentMap pairs ^String p]
  (when-let [l (and (seq p) (dec (.length p)))]
    (when (and (> l 1) (= (pairs (.charAt p 0)) (.charAt p l)))
      (subs p 1 l))))

(defn all-suffixes
  {:added "1.0.0"
   :tag clojure.lang.ISeq}

  ([^String p]
   (take-while seq (iterate #(subs %1 1) p)))

  ([^clojure.lang.IFn pred ^String p]
   (->> p
        (partition-by pred)
        (iterate rest)
        (take-while seq)
        (map #(apply str (apply concat %))))))

(defn all-prefixes
  {:added "1.0.0"
   :tag clojure.lang.ISeq}

  ([^String p]
   (rest (reductions str "" p)))

  ([^clojure.lang.IFn pred ^String p]
   (->> p
        (partition-by pred)
        (reductions concat)
        (map (partial apply str)))))

(defn- for-suffixes
  [^clojure.lang.ISeq s]
  "Generates a sequence of all possible suffixes."
  {:added "1.0.0"
   :tag clojure.lang.ISeq}
  (map #(apply concat %) (take-while seq (iterate rest s))))

(defn all-subs
  {:added "1.0.0"
   :tag clojure.lang.ISeq}

  ([^clojure.lang.IFn pred ^clojure.lang.ISeq s]
   (->> s
        (partition-by pred)
        (reductions concat)
        (map (comp for-suffixes (partial partition-by pred)))
        (apply concat)
        (map str/join)))

  ([^clojure.lang.ISeq s]
   (->> s
        all-prefixes
        (map all-suffixes)
        (apply concat))))
