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
