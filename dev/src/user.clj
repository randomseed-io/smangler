(ns user
  (:require
   [clojure.spec.alpha           :as               cs]
   [clojure.spec.gen.alpha       :as               sg]
   [clojure.spec.test.alpha      :as               st]
   [midje.repl                   :refer          :all]
   [midje.experimental           :refer     [for-all]]
   [kaocha.repl                  :as               kr]
   [orchestra.spec.test          :as               ot]
   [expound.alpha                :as               ex]
   [specviz.core                 :as          specviz]
   [clojure.repl                 :refer          :all]
   [clojure.tools.namespace.repl :refer [refresh
                                         refresh-all]]
   [smangler.core                :refer          :all]
   [smangler.api                 :as              api]
   [smangler.spec                :as                s]
   [infra]))

(set! *warn-on-reflection* true)
(alter-var-root #'cs/*explain-out*
                (constantly (ex/custom-printer {:show-valid-values? false
                                                :print-specs?       true
                                                :theme :figwheel-theme})))
(cs/check-asserts true)
(ot/instrument)

(when (System/getProperty "nrepl.load")
(require 'nrepl))

(defn test-all []
  (refresh)
  (load-facts :print-facts))

(comment 
(refresh-all)
(test-all)

)
