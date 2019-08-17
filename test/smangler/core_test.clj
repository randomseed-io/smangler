(ns

    ^{:doc    "smangler library, core tests."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.core-test

  (:require [clojure.spec.alpha      :as            s]
            [midje.sweet             :refer      :all]
            [midje.experimental      :refer [for-all]]
            [clojure.spec.gen.alpha  :as           sg]
            [clojure.spec.test.alpha :as           st]
            [orchestra.spec.test     :as           ot]
            [expound.alpha           :as           ex]
            [smangler.core           :refer      :all]))

(alter-var-root #'s/*explain-out*
                (constantly
                 (ex/custom-printer {:show-valid-values? false
                                     :print-specs?       true
                                     :theme :figwheel-theme})))
(s/check-asserts true)
;;(st/check (st/enumerate-namespace 'smangler.core))

(facts "about `trim-same-once`"
  (fact "when it returns nil for nil"
    (trim-same-once nil)          => nil
    (trim-same-once #{} nil)      => nil
    (trim-same-once \a \b nil)    => nil)
  (fact "when it returns nil for no-hit"
    (trim-same-once "abcdef")     => nil
    (trim-same-once "")           => nil
    (trim-same-once "b")          => nil
    (trim-same-once #{} "abc")    => nil
    (trim-same-once \a \b "xy")   => nil
    (trim-same-once #{} "aba")    => nil
    (trim-same-once \a \b "xx")   => nil
    (trim-same-once #{\a} "")     => nil
    (trim-same-once #{\a} "")     => nil
    (trim-same-once \a \b "")     => nil)
  (fact "when it returns trimmed string"
    (trim-same-once "barkrab")    => "arkra"
    (trim-same-once "barrab")     => "arra"
    (trim-same-once "aa")         => ""
    (trim-same-once #{\a} "aba")  => "b"
    (trim-same-once #{\a} "aa")   => ""
    (trim-same-once \a \b "abab") => "ba"
    (trim-same-once \b \b "bab")  => "a"
    (trim-same-once \a \a "aa")   => ""
    (trim-same-once \b \b "bab")  => "a"))

(facts "about `trim-same`"
  (fact "when it returns nil for nil"
    (trim-same nil)          => nil
    (trim-same #{} nil)      => nil
    (trim-same \a \b nil)    => nil)
  (fact "when it returns original for no-hit"
    (trim-same "abcdef")     => "abcdef"
    (trim-same "")           => ""
    (trim-same "b")          => "b"
    (trim-same #{} "abc")    => "abc"
    (trim-same \a \b "xy")   => "xy"
    (trim-same #{} "aba")    => "aba"
    (trim-same \a \b "xx")   => "xx"
    (trim-same #{\a} "")     => ""
    (trim-same #{\a} "")     => ""
    (trim-same \a \b "")     => "")
  (fact "when it returns trimmed string"
    (trim-same "barkrab")    => "k"
    (trim-same "barrab")     => ""
    (trim-same "aa")         => ""
    (trim-same #{\a} "aba")  => "b"
    (trim-same #{\a} "aa")   => ""
    (trim-same \a \b "abab") => "ba"
    (trim-same \b \b "bab")  => "a"
    (trim-same \a \a "aa")   => ""
    (trim-same \b \b "bab")  => "a"))

(facts "about `all-prefixes`"
  (fact "when it returns nil for nil"
    (all-prefixes nil)        => nil
    (all-prefixes #{} nil)    => nil
    (all-prefixes #{\a} nil)  => nil)
  (fact "when it returns nil for an empty string"
    (all-prefixes "")         => nil
    (all-prefixes #{} "")     => nil
    (all-prefixes #{\a} "")   => nil)
  (fact "when it returns an original for not-matching slicer"
    (all-prefixes #{} "abc")      => (just ["abc"])
    (all-prefixes #{\q} "abc")    => (just ["abc"])
    (all-prefixes #{\a \b} "xyz") => (just ["xyz"]))
  (fact "when it returns all prefixes for a string"
    (all-prefixes "a")              => (just ["a"])
    (all-prefixes "abc")            => (just ["a", "ab", "abc"])
    (all-prefixes #{\a} "abc")      => (just ["a", "abc"])
    (all-prefixes #{\a \b} "abc")   => (just ["a", "ab", "abc"])
    (all-prefixes #{\a} "abcde")    => (just ["a", "abcde"])
    (all-prefixes #{\a \b} "abcde") => (just ["a", "ab", "abcde"])))

(facts "about `all-suffixes`"
  (fact "when it returns nil for nil"
    (all-suffixes nil)         => nil
    (all-suffixes #{} nil)     => nil
    (all-suffixes #{\a} nil)   => nil)
  (fact "when it returns nil for an empty string"
    (all-suffixes "")          => nil
    (all-suffixes #{} "")      => nil
    (all-suffixes #{\a} "")    => nil)
  (fact "when it returns an original for not-matching slicer"
    (all-suffixes #{} "abc")   => (just ["abc"])
    (all-suffixes #{\q} "abc") => (just ["abc"]))
  (fact "when it returns all suffixes for a string"
    (all-suffixes "a")              => (just ["a"])
    (all-suffixes "abc")            => (just ["abc", "bc", "c"])
    (all-suffixes #{\a} "abc")      => (just ["abc", "bc"])
    (all-suffixes #{\a \b} "abc")   => (just ["abc", "bc", "c"])
    (all-suffixes #{\a} "abcde")    => (just ["abcde", "bcde"])
    (all-suffixes #{\a \b} "abcde") => (just ["abcde", "bcde", "cde"])))

(facts "about `all-subs`"
  (fact "when it returns nil for nil"
    (all-subs nil)        => nil
    (all-subs #{} nil)    => nil
    (all-subs #{\a} nil)  => nil)
  (fact "when it returns nil for an empty string"
    (all-subs "")         => nil
    (all-subs #{} "")     => nil
    (all-subs #{\a} "")   => nil)
  (fact "when it returns an original for not-matching slicer"
    (all-subs #{} "abc")   => (just ["abc"])
    (all-subs #{\q} "abc") => (just ["abc"]))
  (fact "when it returns all infixes for a string"
    (all-subs "a")              => (just ["a"])
    (all-subs "abc")            => (just ["a", "ab", "b", "abc", "bc", "c"])
    (all-subs #{\a} "abc")      => (just ["a", "abc", "bc"])
    (all-subs #{\a \b} "abc")   => (just ["a", "ab", "b", "abc", "bc", "c"])
    (all-subs #{\a} "abcde")    => (just ["a", "abcde", "bcde"])
    (all-subs #{\a \b} "abcde") => (just ["a", "ab", "b" "abcde", "bcde", "cde"])))
