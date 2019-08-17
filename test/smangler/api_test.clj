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

(facts "about `trim-same-seq`"
  (fact "when it returns nil for nil"
    (trim-same-seq nil)          => nil
    (trim-same-seq #{} nil)      => nil
    (trim-same-seq \a \b nil)    => nil)
  (fact "when it returns original for no-hit"
    (trim-same-seq "abcdef")     => (just ["abcdef"])
    (trim-same-seq "")           => (just [""])
    (trim-same-seq "b")          => (just ["b"])
    (trim-same-seq #{} "abc")    => (just ["abc"])
    (trim-same-seq \a \b "xy")   => (just ["xy"])
    (trim-same-seq #{} "aba")    => (just ["aba"])
    (trim-same-seq \a \b "xx")   => (just ["xx"])
    (trim-same-seq #{\a} "")     => (just [""])
    (trim-same-seq #{\a} "")     => (just [""])
    (trim-same-seq \a \b "")     => (just [""]))
  (fact "when it returns trimmed string"
    (trim-same-seq "barkrab")    => (just ["barkrab" "arkra" "rkr" "k"])
    (trim-same-seq "barrab")     => (just ["barrab" "arra" "rr" ""])
    (trim-same-seq "aa")         => (just ["aa" ""])
    (trim-same-seq #{\a} "aba")  => (just ["aba" "b"])
    (trim-same-seq #{\a} "aa")   => (just ["aa" ""])
    (trim-same-seq \a \b "abab") => (just ["abab" "ba"])
    (trim-same-seq \b \b "bab")  => (just ["bab" "a"])
    (trim-same-seq \a \a "aa")   => (just ["aa" ""])
    (trim-same-seq \b \b "bab")  => (just ["bab" "a"]))


  )
