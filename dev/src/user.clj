(ns user
  (:require
   [smangler.api :as t]
   [clojure.spec.alpha :as s]
   [clojure.tools.namespace.repl :refer [refresh refresh-all]]
   [infra]
   clojure.test))

(set! *warn-on-reflection* true)

(when (System/getProperty "nrepl.load")
  (require 'nrepl))

(defn test-all []
  (refresh)
  (clojure.test/run-all-tests #"(smangler).*test$"))

(comment 
  (refresh-all)
  (test-all)
  
  )
