(ns

    ^{:doc    "smangler library, spec definitions."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.spec

  (:require [clojure.spec.alpha  :as              s]
            [expound.alpha       :as             ex]
            [smangler.proto      :as              p]
            [orchestra.core      :refer [defn-spec]]))

(s/def ::empty-seq               (s/and seq? empty?))
(s/def ::lazy-seq                (partial instance? clojure.lang.LazySeq))
(s/def ::string                  string?)
(s/def ::non-empty-string        (s/and string? seq))
(s/def ::character               char?)
(s/def ::beginning-character     char?)
(s/def ::ending-character        char?)
(s/def ::nothing                 (s/or :empty ::empty-seq :nil ::nil))
(s/def ::false                   false?)
(s/def ::phrase                  (s/nilable ::string))
(s/def ::character-pairs         (s/map-of ::character ::character))
(s/def ::char-sequence           #(instance? CharSequence %))
(s/def ::characters              (s/or :char-sequence ::char-sequence :char-coll    (s/coll-of ::character)))
(s/def ::lazy-seq-of-strings     (s/nilable (s/coll-of ::string :min-count 1 :kind? ::lazy-seq)))
(s/def ::one-or-two-strings      (s/nilable (s/coll-of ::string :min-count 1 :max-count 2 :kind ::lazy-seq)))
(s/def ::lazy-seq-of-ne-strings  (s/nilable (s/coll-of ::non-empty-string :min-count 1 :kind? ::lazy-seq)))
(s/def ::nil                     nil?)
(s/def ::some                    some?)
(s/def ::stringable              #(satisfies? p/Stringable %))
(s/def ::predicative             #(satisfies? p/Predicative %))
(s/def ::phraseable              (s/nilable ::stringable))
(s/def ::number                  number?)

(s/def ::char-matcher
  (s/fspec :args (s/cat :v ::character)
           :ret  (s/or  :character ::character
                        :nil       ::nil
                        :false     ::false)
           :fn   (s/and
                  #(s/valid? ::character (:v (:args %)))
                  (s/or
                   :nil       #(= :nil       (first (:ret %)))
                   :false     #(= :false     (first (:ret %)))
                   :character (s/and
                               #(= :character (first (:ret %)))
                               #(= (:v (:args %)) (second (:ret %))))))))

(s/def ::phrase-splitter
  (s/fspec :args (s/cat :v ::character)
           :ret  any?
           :fn   #(s/valid? ::character (:args :v %))))

(s/def ::convertable-to-predicate
  (s/or
   :character       ::character
   :characters      ::characters
   :character-pairs ::character-pairs
   :number          ::number))

(s/def ::predicate-convertables
  (s/coll-of ::convertable-to-predicate))

(s/def ::char-matchable
  (s/and
   ::predicative
   (s/or :nil          ::nil
         :convertable  ::convertable-to-predicate
         :convertables ::predicate-convertables
         :function     ::char-matcher)))

(s/def ::phrase-splittable
  (s/and
   ::predicative
   (s/or :nil          ::nil
         :convertable  ::convertable-to-predicate
         :convertables ::predicate-convertables
         :function     ::phrase-splitter)))

(defmacro should-be
  [k error-message]
  `(ex/defmsg ~k (str "should be " ~error-message)))

(defmacro should-not-be
  [k error-message]
  `(ex/defmsg ~k (str "should not be " ~error-message)))

(should-be ::string                   "a string")
(should-be ::stringable               "an object that can be converted to string")
(should-be ::non-empty-string         "a non-empty string")
(should-be ::empty-seq                "an empty sequence")
(should-be ::beginning-character      "a character (to match first character)")
(should-be ::ending-character         "a character (to match last character)")
(should-be ::nil                      "nil")
(should-be ::false                    "false")
(should-be ::lazy-seq-of-strings      "a non-empty, lazy sequence of strings")
(should-be ::lazy-seq-of-ne-strings   "a non-empty, lazy sequence of non-empty strings")
(should-be ::character                "a character")
(should-be ::characters               "a sequence of characters (like string)")
(should-be ::character-pairs          "a map of characters (to match first with last)")
(should-be ::phrase                   "a string or nil")
(should-be ::phraseable               "an object that can be converted to string or nil")
(should-be ::digits                   "a collection of digits")
(should-be ::number                   "a number")
(should-be ::predicative              "an object that can be converted to a predicate function")
(should-be ::char-matcher             "a function taking a character and returning it or nothing")
(should-be ::phrase-splitter          "a function taking a character used to partition a string")
(should-be ::convertable-to-predicate "an object that can be converted to a matcher or a predicate")
(should-be ::predicate-convertables   "collection of objects that can be converted to a matcher or a predicate")
