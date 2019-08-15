(ns

    ^{:doc    "smangler library, internal utilities."
      :author "Paweł Wilk"
      :added  "1.0.0"
      :no-doc true}

    smangler.util)

(defn part-caller-iterate
  "For the given function f and a sequence of items w it calls (iterate f w) and takes
  produced elements until nil is found.

  If there are 3 arguments then the second argument is also passed to the iterated
  function.

  If there are 4 arguments then the second and third are also passed to the iterated
  function.

  Returns a lazy sequence. If the last argument is nil it returns nil."
  {:added "1.0.0" :tag clojure.lang.LazySeq}
  ([^clojure.lang.IFn  f
    ^clojure.lang.ISeq w]
   (when (some? w)
     (take-while some? (iterate f w))))
  ([^clojure.lang.IFn       f
    ^clojure.lang.IFn matcher
    ^clojure.lang.ISeq      w]
   (when (some? w)
     (take-while some? (iterate (partial f matcher) w))))
  ([^clojure.lang.IFn        f
    ^java.lang.Character start
    ^java.lang.Character   end
    ^clojure.lang.ISeq       w]
   (when (some? w)
     (take-while some? (iterate (partial f start end) w)))))

(defmacro some-or [f & more]
  "Takes one argument (which should be a function) and one or more optional arguments.
  Calls the given function with the given arguments, however the last argument will
  be evaluated before calling the function and returned if the function returns
  nil. If the function returns some value other than nil it will be returned.

  Short-circuits nil when given as value of a last argument."
  {:added "1.0.0"}
  `(when-some [v# ~(last more)]
     (if-some [r# (~f ~@(butlast more) v#)] r# v#)))
