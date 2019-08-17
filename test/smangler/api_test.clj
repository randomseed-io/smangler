(ns

    ^{:doc    "smangler library, API tests."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.api-test

  (:require [clojure.spec.alpha      :as            s]
            [midje.sweet             :refer      :all]
            [midje.experimental      :refer [for-all]]
            [clojure.spec.gen.alpha  :as           sg]
            [clojure.spec.test.alpha :as           st]
            [orchestra.spec.test     :as           ot]
            [expound.alpha           :as           ex]
            [smangler.api           :refer      :all]))

(alter-var-root #'s/*explain-out*
                (constantly
                 (ex/custom-printer {:show-valid-values? false
                                     :print-specs?       true
                                     :theme :figwheel-theme})))
(s/check-asserts true)

(facts "about `trim-same-once`"
  (fact "when it returns nil for nil"
    (trim-same-once nil)          => nil
    (trim-same-once #{} nil)      => nil
    (trim-same-once #{\a} nil)    => nil
    (trim-same-once \a \b nil)    => nil
    (trim-same-once nil nil)      => nil
    (trim-same-once "" nil)       => nil
    (trim-same-once "ab" nil)     => nil
    (trim-same-once "xx" nil)     => nil)
  (fact "when it returns an original for no-hit"
    (trim-same-once "abcdef")     => "abcdef"
    (trim-same-once "")           => ""
    (trim-same-once nil "")       => ""
    (trim-same-once "b")          => "b"
    (trim-same-once nil "b")      => "b"
    (trim-same-once #{} "abc")    => "abc"
    (trim-same-once \a \b "xy")   => "xy"
    (trim-same-once #{} "aba")    => "aba"
    (trim-same-once nil "aba")    => "aba"
    (trim-same-once "" "aba")     => "aba"
    (trim-same-once "xyz" "aba")  => "aba"
    (trim-same-once "xxx" "aba")  => "aba"
    (trim-same-once \a \b "xx")   => "xx"
    (trim-same-once #{\a} "")     => ""
    (trim-same-once #{\a} "")     => ""
    (trim-same-once \a \b "")     => ""
    (trim-same-once nil "")       => ""
    (trim-same-once "" "")        => "")
  (fact "when it returns a trimmed string"
    (trim-same-once "barkrab")    => "arkra"
    (trim-same-once "barrab")     => "arra"
    (trim-same-once "aa")         => ""
    (trim-same-once "abc" "aba")  => "b"
    (trim-same-once #{\a} "aba")  => "b"
    (trim-same-once #{\a} "aa")   => ""
    (trim-same-once "a" "aa")     => ""
    (trim-same-once "abc" "aa")   => ""
    (trim-same-once \a \b "abab") => "ba"
    (trim-same-once \b \b "bab")  => "a"
    (trim-same-once \a \a "aa")   => ""
    (trim-same-once \b \b "bab")  => "a"))

(facts "about `trim-same`"
  (fact "when it returns nil for nil"
    (trim-same nil)          => nil
    (trim-same #{} nil)      => nil
    (trim-same \a \b nil)    => nil
    (trim-same nil nil)      => nil
    (trim-same "" nil)       => nil
    (trim-same "ab" nil)     => nil)
  (fact "when it returns an original for no-hit"
    (trim-same "abcdef")     => "abcdef"
    (trim-same "")           => ""
    (trim-same "b")          => "b"
    (trim-same nil "")       => ""
    (trim-same nil "b")      => "b"
    (trim-same "" "")        => ""
    (trim-same "" "b")       => "b"
    (trim-same #{} "abc")    => "abc"
    (trim-same nil "abc")    => "abc"
    (trim-same \a \b "xy")   => "xy"
    (trim-same #{} "aba")    => "aba"
    (trim-same nil "aba")    => "aba"
    (trim-same "" "aba")     => "aba"
    (trim-same "x" "aba")    => "aba"
    (trim-same "xx" "aba")   => "aba"
    (trim-same \a \b "xx")   => "xx"
    (trim-same #{\a} "")     => ""
    (trim-same #{\a} "")     => ""
    (trim-same \a \b "")     => "")
  (fact "when it returns a trimmed string"
    (trim-same "barkrab")    => "k"
    (trim-same "barrab")     => ""
    (trim-same "aa")         => ""
    (trim-same #{\a} "aba")  => "b"
    (trim-same #{\a} "aa")   => ""
    (trim-same "a" "aba")    => "b"
    (trim-same "a" "aa")     => ""
    (trim-same "ax" "aba")  => "b"
    (trim-same "axx" "aa")   => ""
    (trim-same \a \b "abab") => "ba"
    (trim-same \b \b "bab")  => "a"
    (trim-same \a \a "aa")   => ""
    (trim-same \b \b "bab")  => "a"))

(facts "about `trim-same-seq`"
  (fact "when it returns nil for nil"
    (trim-same-seq nil)          => nil
    (trim-same-seq #{} nil)      => nil
    (trim-same-seq nil nil)      => nil
    (trim-same-seq "" nil)       => nil
    (trim-same-seq "x" nil)      => nil
    (trim-same-seq "xx" nil)     => nil
    (trim-same-seq \a \b nil)    => nil)
  (fact "when it returns an original for no-hit"
    (trim-same-seq "abcdef")     => (just ["abcdef"])
    (trim-same-seq "")           => (just [""])
    (trim-same-seq "b")          => (just ["b"])
    (trim-same-seq #{} "abc")    => (just ["abc"])
    (trim-same-seq \a \b "xy")   => (just ["xy"])
    (trim-same-seq #{} "aba")    => (just ["aba"])
    (trim-same-seq nil "aba")    => (just ["aba"])
    (trim-same-seq "" "aba")     => (just ["aba"])
    (trim-same-seq "x" "aba")    => (just ["aba"])
    (trim-same-seq "xx" "aba")   => (just ["aba"])
    (trim-same-seq \a \b "xx")   => (just ["xx"])
    (trim-same-seq #{\a} "")     => (just [""])
    (trim-same-seq "a" "")       => (just [""])
    (trim-same-seq nil "")       => (just [""])
    (trim-same-seq "" "")        => (just [""])
    (trim-same-seq \a \b "")     => (just [""]))
  (fact "when it returns  atrimmed string"
    (trim-same-seq "barkrab")    => (just ["barkrab", "arkra", "rkr", "k"])
    (trim-same-seq "barrab")     => (just ["barrab", "arra", "rr", ""])
    (trim-same-seq "aa")         => (just ["aa", ""])
    (trim-same-seq #{\a} "aba")  => (just ["aba", "b"])
    (trim-same-seq #{\a} "aa")   => (just ["aa", ""])
    (trim-same-seq "a" "aba")    => (just ["aba", "b"])
    (trim-same-seq "axa" "aa")   => (just ["aa", ""])
    (trim-same-seq "a" "aa")     => (just ["aa", ""])
    (trim-same-seq \a \b "abab") => (just ["abab", "ba"])
    (trim-same-seq \b \b "bab")  => (just ["bab", "a"])
    (trim-same-seq \a \a "aa")   => (just ["aa", ""])
    (trim-same-seq \b \b "bab")  => (just ["bab", "a"])))

(facts "about `trim-same-once-with-orig`"
  (fact "when it returns nil for nil"
    (trim-same-once-with-orig nil)          => nil
    (trim-same-once-with-orig #{} nil)      => nil
    (trim-same-once-with-orig \a \b nil)    => nil
    (trim-same-once-with-orig nil nil)      => nil
    (trim-same-once-with-orig "" nil)       => nil
    (trim-same-once-with-orig "ab" nil)     => nil)
  (fact "when it returns an original for no-hit"
    (trim-same-once-with-orig "abcdef")     => (just ["abcdef"])
    (trim-same-once-with-orig "")           => (just [""])
    (trim-same-once-with-orig "b")          => (just ["b"])
    (trim-same-once-with-orig #{} "abc")    => (just ["abc"])
    (trim-same-once-with-orig \a \b "xy")   => (just ["xy"])
    (trim-same-once-with-orig #{} "aba")    => (just ["aba"])
    (trim-same-once-with-orig \a \b "xx")   => (just ["xx"])
    (trim-same-once-with-orig #{\a} "")     => (just [""])
    (trim-same-once-with-orig #{\a} "")     => (just [""])
    (trim-same-once-with-orig \a \b "")     => (just [""]))
  (fact "when it returns a trimmed string"
    (trim-same-once-with-orig "barkrab")    => (just ["barkrab", "arkra"])
    (trim-same-once-with-orig "barrab")     => (just ["barrab", "arra"])
    (trim-same-once-with-orig "aa")         => (just ["aa", ""])
    (trim-same-once-with-orig #{\a} "aba")  => (just ["aba", "b"])
    (trim-same-once-with-orig #{\a} "aa")   => (just ["aa", ""])
    (trim-same-once-with-orig \a \b "abab") => (just ["abab", "ba"])
    (trim-same-once-with-orig \b \b "bab")  => (just ["bab", "a"])
    (trim-same-once-with-orig \a \a "aa")   => (just ["aa", ""])
    (trim-same-once-with-orig \b \b "bab")  => (just ["bab", "a"])))

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
    (all-prefixes "bx" "abcde")     => (just ["a", "ab", "abcde"])))

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
    (all-suffixes "bx" "abcde")     => (just ["abcde", "bcde", "cde"])))

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
    (all-subs "bx" "abcde")     => (just ["a", "ab", "b", "abcde", "bcde", "cde"])))
