(ns

    ^{:doc    "smangler library, spec definitions."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.spec

  (:require [clojure.spec.alpha              :as    s]
            [expound.alpha                   :as   ex]
            [smangler.proto                  :as    p]
            [clojure.test.check.generators   :as  gen]
            [orchestra.core      :refer    [defn-spec]]))

;; Basic specs.

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
(s/def ::character-set           (s/coll-of ::character :kind set?))
(s/def ::char-sequence           (s/or :string string? :char-seq (s/coll-of ::character)))
(s/def ::characters              (s/or :char-sequence ::char-sequence :char-coll (s/coll-of ::character)))
(s/def ::lazy-seq-of-strings     (s/nilable (s/coll-of ::string :min-count 1 :kind? seq?)))
(s/def ::one-or-two-strings      (s/nilable (s/coll-of ::string :min-count 1 :max-count 2 :kind seq?)))
(s/def ::lazy-seq-of-ne-strings  (s/nilable (s/coll-of ::non-empty-string :min-count 1 :kind? seq?)))
(s/def ::nil                     nil?)
(s/def ::some                    some?)
(s/def ::number                  number?)


;; Function specs (matcher and partitioning predicate).

(s/def ::char-matcher
  (s/with-gen
    (s/fspec :args (s/cat :v ::character)
             :ret  (s/or  :character ::character
                          :nil       ::nil
                          :false     ::false)
             :fn   (s/and
                    #(s/valid? ::character (:v (:args %)))
                    (s/or
                     :nil       #(= :nil       (first (:ret %)))
                     :false     #(= :false     (first (:ret %)))
                     :character #(= :character (first (:ret %))))))
    #(gen/return (fn [c] (rand-nth [nil false c (char (rand-int 428))])))))

(s/def ::phrase-splitter
  (s/with-gen
    (s/fspec :args (s/cat :v ::character)
             :ret  any?
             :fn   #(s/valid? ::character (:v (:args %))))
    #(gen/return (fn [c] (rand-nth [true false nil c (char (rand-int 1024))])))))


(s/def ::stringable
  (s/or
   :nil             ::nil
   :character       ::character
   :char-sequence   ::char-sequence
   :number          ::number
   :collection      (s/coll-of (s/or :character   ::character
                                     :number      ::number
                                     :string      ::string
                                     :char-seq    ::char-sequence))))

;; Compound specs for either a function or an object that can be converted to a function.

(s/def ::convertable-to-predicate
  (s/or
   :character       ::character
   :char-sequence   ::char-sequence
   :character-pairs ::character-pairs
   :number          ::number
   :collection      (s/coll-of (s/or :character ::character
                                     :number    ::number
                                     :string    ::string
                                     :char-seq  ::char-sequence))))

(s/def ::char-matcher-only    (s/and ::plain-matcher  ::char-matcher))
(s/def ::phrase-splitter-only (s/and ::plain-splitter ::phrase-splitter))

(s/def ::char-matchable
  (s/or :nil          ::nil
        :convertable  ::convertable-to-predicate
        :function     ::char-matcher))

(s/def ::phrase-splittable
  (s/or :nil          ::nil
        :convertable  ::convertable-to-predicate
        :function     ::phrase-splitter))

;; Messaging (handled by Expound).

(defmacro should-be
  [k error-message]
  `(ex/defmsg ~k (str "should be " ~error-message)))

(defmacro should-not-be
  [k error-message]
  `(ex/defmsg ~k (str "should not be " ~error-message)))

(should-be ::string                   "a string")
(should-be ::char-sequence            "a sequence of characters (string and similar)")
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
(should-be ::digits                   "a collection of digits")
(should-be ::number                   "a number")
(should-be ::char-matcher             "a function matching a character and returning it or nothing")
(should-be ::phrase-splitter          "a function taking a character used to partition a string")
(should-be ::char-matcher-only        "a function matching a character and returning it or nothing")
(should-be ::phrase-splitter-only     "a function taking a character used to partition a string")
(should-be ::plain-matcher            "a function matching a character and returning it or nothing")
(should-be ::plain-splitter           "a function taking a character used to partition a string")
(should-be ::convertable-to-predicate "an object that can be converted to a matcher or a predicate")
(should-be ::predicate-convertables   "a collection of objects that can be converted to a matcher or a predicate")
