(ns user
  (:require
   [clojure.test                 :as                t]
   [clojure.spec.alpha           :as                s]
   [clojure.spec.gen.alpha       :as               sg]
   [orchestra.spec.test          :as               st]
   [expound.alpha                :as               ex]
   [specviz.core                 :as          specviz]
   [clojure.repl                 :refer          :all]
   [clojure.tools.namespace.repl :refer [refresh
                                         refresh-all]]
   [smangler.core                :refer          :all]
   [smangler.api                 :as              api]
   [infra]))

(set! *warn-on-reflection* true)
(alter-var-root #'s/*explain-out*
                (constantly (ex/custom-printer {:show-valid-values? false
                                                :print-specs?       true
                                                :theme :figwheel-theme})))
(s/check-asserts true)

(st/instrument)

(when (System/getProperty "nrepl.load")
(require 'nrepl))

(defn test-all []
(refresh)
(t/run-all-tests #"(smangler).*test$"))

(comment 
(refresh-all)
(test-all)

)
