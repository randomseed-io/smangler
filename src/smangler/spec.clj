(ns

    ^{:doc    "smangler library, spec definitions."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.spec

  (:require [clojure.spec.alpha  :as              s]
            [expound.alpha       :as             ex]
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
(s/def ::false               false?)
(s/def ::phrase              (s/or :string ::string    :nil ::nil))
(s/def ::lazy-seq-strings    (s/coll-of ::string :min-count 1 :kind? ::lazy-seq))
(s/def ::lazy-seq-ne-strings (s/coll-of ::non-empty-string :min-count 1 :kind? ::lazy-seq))
(s/def ::nil                 nil?)
(s/def ::strings             (s/or :nil ::nil :seq ::lazy-seq-strings))
(s/def ::non-empty-strings   (s/or :nil ::nil :seq ::lazy-seq-ne-strings))

(s/def ::char-matcher
  (s/fspec :args (s/cat :v ::character)
           :ret  (s/or  :character ::character
                        :nothing   ::nothing
                        :false     ::false)))

(s/def ::phrase-splitter
  (s/fspec :args (s/cat :v ::character)
           :ret  any?))

(defmacro should-be
  [k error-message]
  `(ex/defmsg ~k (str "should be " ~error-message)))

(should-be ::string              "a string")
(should-be ::non-empty-string    "a non-empty string")
(should-be ::empty-seq           "an empty sequence")
(should-be ::beginning-character "a character (to match first character)")
(should-be ::ending-character    "a character (to match last character)")
(should-be ::nil                 "nil")
(should-be ::false               "false")
(should-be ::lazy-seq-strings    "a non-empty, lazy sequence of strings")
(should-be ::lazy-seq-ne-strings "a non-empty, lazy sequence of non-empty strings")
(should-be ::character           "a character")
(should-be ::phrase              "a string or nil")
(should-be ::char-matcher        "a function taking a character and returning a character or nothing")
(should-be ::phrase-splitter     "a function taking a character used to partition a string")
