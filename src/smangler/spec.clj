(ns

    ^{:doc    "smangler library, spec definitions."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.spec

  (:require [clojure.spec.alpha  :as              s]
            [expound.alpha       :refer    [defmsg]]
            [orchestra.core      :refer [defn-spec]]))

(def ^{:added   "1.0.0"
       :const   true
       :tag     java.lang.Boolean}
  lazy-seq?
  (partial instance? clojure.lang.LazySeq))

(s/def ::empty-seq           (s/and seq? empty?))
(s/def ::lazy-seq            lazy-seq?)
(s/def ::string              string?)
(s/def ::non-empty-string    (s/and string? seq))
(s/def ::character           char?)
(s/def ::beginning-character char?)
(s/def ::ending-character    char?)
(s/def ::nothing             (s/or :empty  ::empty-seq :nil ::nil))
(s/def ::phrase              (s/or :string ::string    :nil ::nil))
(s/def ::lazy-seq-strings    (s/coll-of ::string :min-count 1 :kind? ::lazy-seq))
(s/def ::lazy-seq-ne-strings (s/coll-of ::non-empty-string :min-count 1 :kind? ::lazy-seq))
(s/def ::nil                 nil?)
(s/def ::strings             (s/or :nil ::nil :seq ::lazy-seq-strings))
(s/def ::non-empty-strings   (s/or :nil ::nil :seq ::lazy-seq-ne-strings))

(s/def ::char-matcher
  (s/fspec :args (s/cat :v ::character)
           :ret  (s/or  :character ::character
                        :nothing   ::nothing)))

(s/def ::phrase-splitter
  (s/fspec :args (s/cat :v ::character)
           :ret  any?))

(defmsg ::string              "should be a string")
(defmsg ::non-empty-string    "should be a non-empty string")
(defmsg ::empty-seq           "should be an empty sequence")
(defmsg ::beginning-character "should be a character (to match first character)")
(defmsg ::ending-character    "should be a character (to match last character)")
(defmsg ::nil                 "should be nil")
(defmsg ::lazy-seq-strings    "should be a non-empty, lazy sequence of strings")
(defmsg ::lazy-seq-ne-strings "should be a non-empty, lazy sequence of non-empty strings")
(defmsg ::character           "should be a character")
(defmsg ::phrase              "should be a string or nil")
(defmsg ::char-matcher        "should be a function taking a character and returning a character or nothing")
(defmsg ::phrase-splitter     "should be a function taking a character used to partition a string")
