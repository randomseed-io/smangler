# Trimming

String trimming is handled by the API function [`trim-same`][api-trim-same] from the
namespace [`smangler.api`][api]. In its single-argument version it trims a given
string on both ends if the characters are the same. It repeats this operation until
there is nothing to trim.

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same "abba")     ; => ""
(sa/trim-same "some")     ; => "some"
(sa/trim-same "barkrab")  ; => "k"
```

## Coercion to strings

You can pass other types of arguments and they will be coerced to strings. Single
characters and numbers are supported, and so are collections of strings, characters
and numbers:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same 1111)          ; => ""
(sa/trim-same 12345)         ; => "12345"
(sa/trim-same 12021)         ; => "0"
(sa/trim-same \a)            ; => "a"
(sa/trim-same [1 0 1])       ; => "0"
(sa/trim-same [\a \b \a])    ; => "b"
(sa/trim-same ["abc" "ba"])  ; => "c"
```

## Custom matcher

Optionally, you can call `trim-same` with 2 arguments passed. In this scenario the
first argument should be a function that takes a single character and returns either
some character, `nil` or `false`. This function, called the matcher, is used on
a first character of a string to determine whether it should be removed (along with
a last character).

If the matcher returns `nil` or `false` then no trimming occurs. If the matcher
returns a character it is compared with the right-most character of the trimmed
string and if they're equal then trimming is performed.

```clojure
(require '(smangler [api :as sa]))

;; Only 'a' will be trimmed since the matcher
;; checks if it is this letter.
(sa/trim-same #(and (= \a %) %) "abxba")   ; => "bxb"

;; Beginning 'a' and ending 'z' will be trimmed
;; because matcher requires that 'a' must have corresponding 'z'.
(sa/trim-same #(and (= \a %) \z) "abcdz")  ; => "bcd"
```

### Sets as matchers

It is common to use sets for matching the same characters on both ends. This is
possible because in Clojure sets implement function interface that allows us to
perform quick lookup:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same #{\a \b} "abxba")  ; => "x"
(sa/trim-same #{\a}    "abxba")  ; => "bxb"
```

### Maps as matchers

Due to the characteristics of a matching function it is possible to use maps as since
they also implement function interface. That will allow us to match both ends of
a string in an easy way:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same {\a \a} "abxba")  ; => "bxb"
(sa/trim-same {\a \z} "abcdz")  ; => "bcd"
```

### Coercion to matcher

You can pass other types of arguments and they will be coerced to matchers. Single
characters, strings and numbers are supported, and so are collections of strings,
characters and numbers:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same \a        "abxba")  ; => "bxb"
(sa/trim-same 1         "1abc1")  ; => "abc"
(sa/trim-same 12      "21abc12")  ; => "abc"
(sa/trim-same [1 2]   "21abc12")  ; => "abc"
(sa/trim-same [\a \b]   "abxba")  ; => "x"
(sa/trim-same "ab"      "abxba")  ; => "x"
```

## Two characters and a string

In its 3-argument version the function simply takes 2 characters and a string. The
given characters must match first and last characters of the string for trimming to
be performed:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same \a \a "abxba")  ; => "bxb"
(sa/trim-same \a \a "aaxaa")  ; => "x"
(sa/trim-same \1 \1 "1abc1")  ; => "abc"
(sa/trim-same \a \z "axz")    ; => "x"
(sa/trim-same \a \z "aaxzz")  ; => "x"
```

## Trimming once

If you want to perform trimming just once, use [`trim-same-once`][api-trim-same-once]
API function. It works the same way as `trim-same` but stops after first operation
(if any):

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same-once "abba")              ; => "bb"
(sa/trim-same-once "some")              ; => "some"
(sa/trim-same-once "barkrab")           ; => "arkra"

(sa/trim-same-once "ab"     "barkrab")  ; => "arkra"
(sa/trim-same-once #{\a \b} "barkrab")  ; => "arkra"

(sa/trim-same-once \a \a   "aaxaa")     ; => "axa"
```

## Trimming once with preservation

To make certain operations easy there is
[`trim-same-once-with-orig`][api-trim-same-once-with-orig] function that returns
a sequence of 1 or 2 elements, the first being always an original string and the
second being its trimmed version. If there is nothing to trim, only one element will
be present in the resulting sequence:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same-once-with-orig "abba")              ; => ("abba", "bb")
(sa/trim-same-once-with-orig "some")              ; => ("some")
(sa/trim-same-once-with-orig "barkrab")           ; => ("barkrab" "arkra")

(sa/trim-same-once-with-orig "ab"     "barkrab")  ; => ("barkrab" "arkra")
(sa/trim-same-once-with-orig #{\a \b} "barkrab")  ; => ("barkrab" "arkra")

(sa/trim-same-once-with-orig \a \a   "aaxaa")     ; => ("aaxaa" "axa")
```

## Sequence of trimming

If there is a need for keeping all subsequent steps of trimming you can use the
function [`trim-same-seq`][api-trim-same-seq]. It works the same as `trim-same` but
returns a lazy sequence of strings, each being the result of next step of iterative
trimming:

```clojure
(require '(smangler [api :as sa]))

(sa/trim-same-seq "abba")              ; => ("abba", "bb", "")
(sa/trim-same-seq "some")              ; => ("some")
(sa/trim-same-seq "barkrab")           ; => ("barkrab" "arkra" "rkr" "k")

(sa/trim-same-seq "ab"     "barkrab")  ; => ("barkrab" "arkra" "rkr")
(sa/trim-same-seq #{\a \b} "barkrab")  ; => ("barkrab" "arkra" "rkr")

(sa/trim-same-seq \a \a   "aaxaa")     ; => ("aaxaa" "axa" "x")
```

## Low-level trimming

Certain applications may require more efficient and/or more strict trimming
functions. It is particularly not recommended but there is a [`smangler.core`][core]
namespace that contains trimming operations that are a bit faster than those in API.
They require certain argument types. Also the returned values may differ when it
comes to handling corner cases (`nil` or empty values, not finding a match, etc.):

* [`trim-same`][core-trim-same],
* [`trim-same-once`][core-trim-same-once].



[api]:                          smangler.api.html
[core]:                         smangler.core.html
[api-trim-same]:                smangler.api.html#var-trim-same
[api-trim-same-seq]:            smangler.api.html#var-trim-same-seq
[api-trim-same-once]:           smangler.api.html#var-trim-same-once
[api-trim-same-once-with-orig]: smangler.api.html#var-trim-same-once-with-orig
[core-trim-same]:               smangler.core.html#var-trim-same
[core-trim-same-once]:          smangler.core.html#var-trim-same-once
