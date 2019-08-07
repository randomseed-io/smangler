(ns

    ^{:doc    "smangler library, API tests."
      :author "Paweł Wilk"
      :added  "1.0.0"}

    smangler.api-test

  (:require [clojure.spec.alpha      :as   s]
            [clojure.test            :as   t]
            [clojure.spec.gen.alpha  :as  sg]
            [orchestra.spec.test     :as  st]
            [expound.alpha           :as  ex]
            [smangler.core           :as  sc]))

(alter-var-root #'s/*explain-out*
                (constantly
                 (ex/custom-printer {:show-valid-values? false
                                     :print-specs?       true
                                     :theme :figwheel-theme})))
(s/check-asserts true)
