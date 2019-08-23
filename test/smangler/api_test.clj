(ns

    ^{:doc    "smangler library, API tests."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.api-test

  (:require [clojure.spec.alpha            :as            s]
            [midje.sweet                   :refer      :all]
            [midje.experimental            :refer [for-all]]
            [clojure.spec.gen.alpha        :as           sg]
            [clojure.spec.test.alpha       :as           st]
            [clojure.test.check.generators :as          gen]
            [orchestra.spec.test           :as           ot]
            [expound.alpha                 :as           ex]
            [smangler.api                  :refer      :all]))

(alter-var-root #'s/*explain-out*
                (constantly
                 (ex/custom-printer {:show-valid-values? false
                                     :print-specs?       true
                                     :theme :figwheel-theme})))
(s/check-asserts true)

(facts "about `trim-both-once`"
  (fact "when it returns nil for nil"
    (trim-both-once nil)          => nil
    (trim-both-once #{} nil)      => nil
    (trim-both-once #{\a} nil)    => nil
    (trim-both-once \a \b nil)    => nil
    (trim-both-once nil nil)      => nil
    (trim-both-once "" nil)       => nil
    (trim-both-once "ab" nil)     => nil
    (trim-both-once "xx" nil)     => nil)
  (fact "when it returns an original for no-hit"
    (trim-both-once "abcdef")     => "abcdef"
    (trim-both-once "")           => ""
    (trim-both-once nil "")       => ""
    (trim-both-once "b")          => "b"
    (trim-both-once nil "b")      => "b"
    (trim-both-once #{} "abc")    => "abc"
    (trim-both-once \a \b "xy")   => "xy"
    (trim-both-once #{} "aba")    => "aba"
    (trim-both-once nil "aba")    => "aba"
    (trim-both-once "" "aba")     => "aba"
    (trim-both-once "xyz" "aba")  => "aba"
    (trim-both-once "xxx" "aba")  => "aba"
    (trim-both-once \a \b "xx")   => "xx"
    (trim-both-once #{\a} "")     => ""
    (trim-both-once #{\a} "")     => ""
    (trim-both-once \a \b "")     => ""
    (trim-both-once nil "")       => ""
    (trim-both-once "" "")        => "")
  (fact "when it returns a trimmed string"
    (trim-both-once "barkrab")    => "arkra"
    (trim-both-once "barrab")     => "arra"
    (trim-both-once "aa")         => ""
    (trim-both-once "abc" "aba")  => "b"
    (trim-both-once #{\a} "aba")  => "b"
    (trim-both-once #{\a} "aa")   => ""
    (trim-both-once "a" "aa")     => ""
    (trim-both-once "abc" "aa")   => ""
    (trim-both-once \a \b "abab") => "ba"
    (trim-both-once \b \b "bab")  => "a"
    (trim-both-once \a \a "aa")   => ""
    (trim-both-once \b \b "bab")  => "a")
  (fact "when it trims fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (trim-both-once s) => (if (and (some? (second s)) (= (first s) (last s)))
                             (apply str (butlast (rest s)))
                             s))))

(facts "about `trim-both`"
  (fact "when it returns nil for nil"
    (trim-both nil)          => nil
    (trim-both #{} nil)      => nil
    (trim-both \a \b nil)    => nil
    (trim-both nil nil)      => nil
    (trim-both "" nil)       => nil
    (trim-both "ab" nil)     => nil)
  (fact "when it returns an original for no-hit"
    (trim-both "abcdef")     => "abcdef"
    (trim-both "")           => ""
    (trim-both "b")          => "b"
    (trim-both nil "")       => ""
    (trim-both nil "b")      => "b"
    (trim-both "" "")        => ""
    (trim-both "" "b")       => "b"
    (trim-both #{} "abc")    => "abc"
    (trim-both nil "abc")    => "abc"
    (trim-both \a \b "xy")   => "xy"
    (trim-both #{} "aba")    => "aba"
    (trim-both nil "aba")    => "aba"
    (trim-both "" "aba")     => "aba"
    (trim-both "x" "aba")    => "aba"
    (trim-both "xx" "aba")   => "aba"
    (trim-both \a \b "xx")   => "xx"
    (trim-both #{\a} "")     => ""
    (trim-both #{\a} "")     => ""
    (trim-both \a \b "")     => "")
  (fact "when it returns a trimmed string"
    (trim-both "barkrab")    => "k"
    (trim-both "barrab")     => ""
    (trim-both "aa")         => ""
    (trim-both #{\a} "aba")  => "b"
    (trim-both #{\a} "aa")   => ""
    (trim-both "a" "aba")    => "b"
    (trim-both "a" "aa")     => ""
    (trim-both "ax" "aba")  => "b"
    (trim-both "axx" "aa")   => ""
    (trim-both \a \b "abab") => "ba"
    (trim-both \b \b "bab")  => "a"
    (trim-both \a \a "aa")   => ""
    (trim-both \b \b "bab")  => "a")
  (fact "when it trims fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (trim-both s) => (last
                       (take-while
                        some?
                        (iterate #(if (and (some? (second %)) (= (first %) (last %)))
                                    (apply str (butlast (rest %)))
                                    nil) s))))))

(facts "about `trim-both-seq`"
  (fact "when it returns nil for nil"
    (trim-both-seq nil)          => nil
    (trim-both-seq #{} nil)      => nil
    (trim-both-seq nil nil)      => nil
    (trim-both-seq "" nil)       => nil
    (trim-both-seq "x" nil)      => nil
    (trim-both-seq "xx" nil)     => nil
    (trim-both-seq \a \b nil)    => nil)
  (fact "when it returns an original for no-hit"
    (trim-both-seq "abcdef")     => (just ["abcdef"])
    (trim-both-seq "")           => (just [""])
    (trim-both-seq "b")          => (just ["b"])
    (trim-both-seq #{} "abc")    => (just ["abc"])
    (trim-both-seq \a \b "xy")   => (just ["xy"])
    (trim-both-seq #{} "aba")    => (just ["aba"])
    (trim-both-seq nil "aba")    => (just ["aba"])
    (trim-both-seq "" "aba")     => (just ["aba"])
    (trim-both-seq "x" "aba")    => (just ["aba"])
    (trim-both-seq "xx" "aba")   => (just ["aba"])
    (trim-both-seq \a \b "xx")   => (just ["xx"])
    (trim-both-seq #{\a} "")     => (just [""])
    (trim-both-seq "a" "")       => (just [""])
    (trim-both-seq nil "")       => (just [""])
    (trim-both-seq "" "")        => (just [""])
    (trim-both-seq \a \b "")     => (just [""]))
  (fact "when it returns  a trimmed string"
    (trim-both-seq "barkrab")    => (just ["barkrab", "arkra", "rkr", "k"])
    (trim-both-seq "barrab")     => (just ["barrab", "arra", "rr", ""])
    (trim-both-seq "aa")         => (just ["aa", ""])
    (trim-both-seq #{\a} "aba")  => (just ["aba", "b"])
    (trim-both-seq #{\a} "aa")   => (just ["aa", ""])
    (trim-both-seq "a" "aba")    => (just ["aba", "b"])
    (trim-both-seq "axa" "aa")   => (just ["aa", ""])
    (trim-both-seq "a" "aa")     => (just ["aa", ""])
    (trim-both-seq \a \b "abab") => (just ["abab", "ba"])
    (trim-both-seq \b \b "bab")  => (just ["bab", "a"])
    (trim-both-seq \a \a "aa")   => (just ["aa", ""])
    (trim-both-seq \b \b "bab")  => (just ["bab", "a"]))
  (fact "when it trims fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (trim-both-seq s) => (->> s
                               (iterate #(if (and (some? (second %)) (= (first %) (last %)))
                                           (apply str (butlast (rest %)))
                                           nil))
                               (take-while some?)))))

(facts "about `trim-both-once-with-orig`"
  (fact "when it returns nil for nil"
    (trim-both-once-with-orig nil)          => nil
    (trim-both-once-with-orig #{} nil)      => nil
    (trim-both-once-with-orig \a \b nil)    => nil
    (trim-both-once-with-orig nil nil)      => nil
    (trim-both-once-with-orig "" nil)       => nil
    (trim-both-once-with-orig "ab" nil)     => nil)
  (fact "when it returns an original for no-hit"
    (trim-both-once-with-orig "abcdef")     => (just ["abcdef"])
    (trim-both-once-with-orig "")           => (just [""])
    (trim-both-once-with-orig "b")          => (just ["b"])
    (trim-both-once-with-orig #{} "abc")    => (just ["abc"])
    (trim-both-once-with-orig \a \b "xy")   => (just ["xy"])
    (trim-both-once-with-orig #{} "aba")    => (just ["aba"])
    (trim-both-once-with-orig \a \b "xx")   => (just ["xx"])
    (trim-both-once-with-orig #{\a} "")     => (just [""])
    (trim-both-once-with-orig #{\a} "")     => (just [""])
    (trim-both-once-with-orig \a \b "")     => (just [""])
    (trim-both-once-with-orig (char 207) 0) => (just ["0"])
    (trim-both-once-with-orig \\ 0)         => (just ["0"])
    (trim-both-once-with-orig " " 0)        => (just ["0"]))
  (fact "when it returns a trimmed string"
    (trim-both-once-with-orig "barkrab")    => (just ["barkrab", "arkra"])
    (trim-both-once-with-orig "barrab")     => (just ["barrab", "arra"])
    (trim-both-once-with-orig "aa")         => (just ["aa", ""])
    (trim-both-once-with-orig #{\a} "aba")  => (just ["aba", "b"])
    (trim-both-once-with-orig #{\a} "aa")   => (just ["aa", ""])
    (trim-both-once-with-orig \a \b "abab") => (just ["abab", "ba"])
    (trim-both-once-with-orig \b \b "bab")  => (just ["bab", "a"])
    (trim-both-once-with-orig \a \a "aa")   => (just ["aa", ""])
    (trim-both-once-with-orig \b \b "bab")  => (just ["bab", "a"]))
  (fact "when it trims fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (trim-both-once-with-orig s) => (->> s
                                          (iterate #(if (and (some? (second %)) (= (first %) (last %)))
                                                      (apply str (butlast (rest %)))
                                                      nil))
                                          (take-while some?)
                                          (take 2)))))

(facts "about `all-prefixes`"
  (fact "when it returns nil for nil"
    (all-prefixes nil)        => nil
    (all-prefixes #{} nil)    => nil
    (all-prefixes nil nil)    => nil
    (all-prefixes "" nil)     => nil
    (all-prefixes #{\a} nil)  => nil
    (all-prefixes "ab" nil)   => nil)
  (fact "when it returns nil for an empty string"
    (all-prefixes "")         => nil
    (all-prefixes #{} "")     => nil
    (all-prefixes nil "")     => nil
    (all-prefixes "" "")      => nil
    (all-prefixes #{\a} "")   => nil
    (all-prefixes "ab" "")    => nil)
  (fact "when it returns an original for not-matching slicer"
    (all-prefixes nil "ab")    => (just ["ab"])
    (all-prefixes "" "ab")     => (just ["ab"])
    (all-prefixes #{} "abc")   => (just ["abc"])
    (all-prefixes "x" "abc")   => (just ["abc"])
    (all-prefixes "xx" "abc")  => (just ["abc"])
    (all-prefixes #{\q} "abc") => (just ["abc"])
    (all-prefixes "abc" "xyz") => (just ["xyz"])
    (all-prefixes \a "xyz")    => (just ["xyz"]))
  (fact "when it returns all prefixes for a string"
    (all-prefixes "a")              => (just ["a"])
    (all-prefixes "abc")            => (just ["a", "ab", "abc"])
    (all-prefixes #{\a} "abc")      => (just ["a", "abc"])
    (all-prefixes #{\a \b} "abc")   => (just ["a", "ab", "abc"])
    (all-prefixes #{\a} "abcde")    => (just ["a", "abcde"])
    (all-prefixes #{\a \b} "abcde") => (just ["a", "ab", "abcde"])
    (all-prefixes "a" "abc")        => (just ["a", "abc"])
    (all-prefixes "ab" "abc")       => (just ["a", "ab", "abc"])
    (all-prefixes "a" "abcde")      => (just ["a", "abcde"])
    (all-prefixes "ab" "abcde")     => (just ["a", "ab", "abcde"])
    (all-prefixes \a "abcde")       => (just ["a", "abcde"])
    (all-prefixes "bx" "abcde")     => (just ["a", "ab", "abcde"]))
  (fact "when it returns all prefixes for fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (all-prefixes s) => (seq (rest (reductions str "" s))))))

(facts "about `all-suffixes`"
  (fact "when it returns nil for nil"
    (all-suffixes nil)         => nil
    (all-suffixes #{} nil)     => nil
    (all-suffixes nil nil)     => nil
    (all-suffixes "" nil)      => nil
    (all-suffixes #{\a} nil)   => nil
    (all-suffixes "ab" nil)    => nil)
  (fact "when it returns nil for an empty string"
    (all-suffixes "")          => nil
    (all-suffixes #{} "")      => nil
    (all-suffixes nil "")      => nil
    (all-suffixes "" "")       => nil
    (all-suffixes #{\a} "")    => nil
    (all-suffixes "ab" "")     => nil)
  (fact "when it returns an original for not-matching slicer"
    (all-suffixes nil "ab")    => (just ["ab"])
    (all-suffixes "" "ab")     => (just ["ab"])
    (all-suffixes #{} "abc")   => (just ["abc"])
    (all-suffixes "x" "abc")   => (just ["abc"])
    (all-suffixes "xx" "abc")  => (just ["abc"])
    (all-suffixes #{\q} "abc") => (just ["abc"])
    (all-suffixes "abc" "xyz") => (just ["xyz"])
    (all-suffixes \a "xyz")    => (just ["xyz"]))
  (fact "when it returns all suffixes for a string"
    (all-suffixes "a")              => (just ["a"])
    (all-suffixes "abc")            => (just ["abc", "bc", "c"])
    (all-suffixes #{\a} "abc")      => (just ["abc", "bc"])
    (all-suffixes #{\a \b} "abc")   => (just ["abc", "bc", "c"])
    (all-suffixes #{\a} "abcde")    => (just ["abcde", "bcde"])
    (all-suffixes #{\a \b} "abcde") => (just ["abcde", "bcde", "cde"])
    (all-suffixes "a" "abc")        => (just ["abc", "bc"])
    (all-suffixes "ab" "abc")       => (just ["abc", "bc", "c"])
    (all-suffixes "a" "abcde")      => (just ["abcde", "bcde"])
    (all-suffixes "ab" "abcde")     => (just ["abcde", "bcde", "cde"])
    (all-suffixes \a "abcde")       => (just ["abcde", "bcde"])
    (all-suffixes "bx" "abcde")     => (just ["abcde", "bcde", "cde"]))
  (fact "when it returns all suffixes for fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (all-suffixes s) => (seq (take-while seq (iterate #(subs %1 1) s))))))

(facts "about `all-subs`"
  (fact "when it returns nil for nil"
    (all-subs nil)        => nil
    (all-subs #{} nil)    => nil
    (all-subs nil nil)    => nil
    (all-subs "" nil)     => nil
    (all-subs #{\a} nil)  => nil
    (all-subs "ab" nil)   => nil)
  (fact "when it returns nil for an empty string"
    (all-subs "")         => nil
    (all-subs #{} "")     => nil
    (all-subs nil "")     => nil
    (all-subs "" "")      => nil
    (all-subs #{\a} "")   => nil
    (all-subs "ab" "")    => nil)
  (fact "when it returns an original for not-matching slicer"
    (all-subs nil "ab")    => (just ["ab"])
    (all-subs "" "ab")     => (just ["ab"])
    (all-subs #{} "abc")   => (just ["abc"])
    (all-subs "x" "abc")   => (just ["abc"])
    (all-subs "xx" "abc")  => (just ["abc"])
    (all-subs #{\q} "abc") => (just ["abc"])
    (all-subs "abc" "xyz") => (just ["xyz"])
    (all-subs \a "xyz")    => (just ["xyz"]))
  (fact "when it returns all infixes for a string"
    (all-subs "a")              => (just ["a"])
    (all-subs "abc")            => (just ["a", "ab", "b", "abc", "bc", "c"])
    (all-subs #{\a} "abc")      => (just ["a", "abc", "bc"])
    (all-subs #{\a \b} "abc")   => (just ["a", "ab", "b", "abc", "bc", "c"])
    (all-subs #{\a} "abcde")    => (just ["a", "abcde", "bcde"])
    (all-subs #{\a \b} "abcde") => (just ["a", "ab", "b" "abcde", "bcde", "cde"])
    (all-subs "a" "abc")        => (just ["a", "abc", "bc"])
    (all-subs "ab" "abc")       => (just ["a", "ab", "b", "abc", "bc", "c"])
    (all-subs "a" "abcde")      => (just ["a", "abcde", "bcde"])
    (all-subs "ab" "abcde")     => (just ["a", "ab", "b", "abcde", "bcde", "cde"])
    (all-subs \a "abcde")       => (just ["a", "abcde", "bcde"])
    (all-subs "bx" "abcde")     => (just ["a", "ab", "b", "abcde", "bcde", "cde"]))
  (fact "when it returns all infixes for fuzzy strings"
    (for-all
     {:max-size 24, :num-tests 50}
     [s gen/string]
     (all-subs s) => (->> s
                          (all-prefixes)
                          (map all-suffixes)
                          (apply concat)
                          seq))))
