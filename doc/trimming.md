# Trimming

Basic string trimming is handled by the API function [`trim-both`][api-trim-both]
from the namespace [`smangler.api`][api]. In its single-argument version it trims
a given string on both ends if the characters are the same. It repeats this operation
until there is nothing to trim.

```clojure
(require '[smangler.api :as sa])

(sa/trim-both "abba")     ; => ""
(sa/trim-both "some")     ; => "some"
(sa/trim-both "barkrab")  ; => "k"
```

## Coercion to strings

You can pass other types of arguments and they will be coerced to strings. Single
characters and numbers are supported, and so are collections of strings, characters
and numbers:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both 1111)          ; => ""
(sa/trim-both 12345)         ; => "12345"
(sa/trim-both 12021)         ; => "0"
(sa/trim-both \a)            ; => "a"
(sa/trim-both [1 0 1])       ; => "0"
(sa/trim-both [\a \b \a])    ; => "b"
(sa/trim-both ["abc" "ba"])  ; => "c"
```

## Custom matcher

Optionally, you can call `trim-both` with 2 arguments passed. In this scenario the
first argument should be a function which takes a single character and returns either
some character, `nil` or `false`. This function, called the matcher, is used on
the first character of a string to determine whether it should be removed (along with
the last character):

```clojure
(fn [character]
  (and (some-lookup character)  ; some-lookup checks if a character should be trimmed
       character))              ; important to return the character, nil or false
```

If the matcher returns `nil` or `false` then no trimming occurs. If the matcher
returns a character it is compared by `trim-both` with the right-most character of
the trimmed string and if they're equal then trimming is performed.

```clojure
(require '[smangler.api :as sa])

;; Only 'a' will be trimmed since the matcher
;; checks if it is this letter.
(sa/trim-both #(and (= \a %) %) "abxba")   ; => "bxb"

;; Beginning 'a' and ending 'z' will be trimmed
;; because matcher requires that 'a' must have corresponding 'z'.
(sa/trim-both #(and (= \a %) \z) "abcdz")  ; => "bcd"
```

### Sets as matchers

It is common to use sets for matching the same characters on both ends. This is
possible because in Clojure sets implement function interface which allows us to
perform quick lookup:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both #{\a \b} "abxba")  ; => "x"
(sa/trim-both #{\a}    "abxba")  ; => "bxb"
```

### Maps as matchers

Due to the characteristics of a matching function it is possible to use maps as since
they also implement function interface. That will allow us to match both ends of
a string in an easy way:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both {\a \a} "abxba")  ; => "bxb"
(sa/trim-both {\a \z} "abcdz")  ; => "bcd"
```

### Coercion to matcher

You can pass other types of arguments and they will be coerced to matchers. Single
characters, strings and numbers are supported, and so are collections of strings,
characters and numbers:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both \a        "abxba")  ; => "bxb"
(sa/trim-both 1         "1abc1")  ; => "abc"
(sa/trim-both 12      "21abc12")  ; => "abc"
(sa/trim-both [1 2]   "21abc12")  ; => "abc"
(sa/trim-both [\a \b]   "abxba")  ; => "x"
(sa/trim-both "ab"      "abxba")  ; => "x"
```

## Two characters and a string

In its 3-argument version the function simply takes 2 characters and a string. The
given characters must match first and last characters of the string for trimming to
be performed:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both \a \a "abxba")  ; => "bxb"
(sa/trim-both \a \a "aaxaa")  ; => "x"
(sa/trim-both \1 \1 "1abc1")  ; => "abc"
(sa/trim-both \a \z "axz")    ; => "x"
(sa/trim-both \a \z "aaxzz")  ; => "x"
```

## Trimming once

If you want to perform trimming just once, use [`trim-both-once`][api-trim-both-once]
API function. It works the same way as `trim-both` but stops after first operation
(if any):

```clojure
(require '[smangler.api :as sa])

(sa/trim-both-once             "abba")  ; => "bb"
(sa/trim-both-once             "some")  ; => "some"
(sa/trim-both-once          "barkrab")  ; => "arkra"

(sa/trim-both-once "ab"     "barkrab")  ; => "arkra"
(sa/trim-both-once #{\a \b} "barkrab")  ; => "arkra"

(sa/trim-both-once \a \a      "aaxaa")     ; => "axa"
```

## Trimming once with preservation

To make certain operations easy there is
[`trim-both-once-with-orig`][api-trim-both-once-with-orig] function which returns
a sequence of 1 or 2 elements, the first being always the original string with the
latter being its trimmed version. If there is nothing to trim, only one element will
be present in the resulting sequence:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both-once-with-orig             "abba")  ; => ("abba", "bb")
(sa/trim-both-once-with-orig             "some")  ; => ("some")
(sa/trim-both-once-with-orig          "barkrab")  ; => ("barkrab" "arkra")

(sa/trim-both-once-with-orig "ab"     "barkrab")  ; => ("barkrab" "arkra")
(sa/trim-both-once-with-orig #{\a \b} "barkrab")  ; => ("barkrab" "arkra")

(sa/trim-both-once-with-orig \a \a      "aaxaa")  ; => ("aaxaa" "axa")
```

## Sequence of trimming steps

If there is a need for keeping all subsequent steps of trimming you can use the
function [`trim-both-seq`][api-trim-both-seq]. It works the same as `trim-both` but
returns a lazy sequence of strings, each being the result of next step of iterative
trimming:

```clojure
(require '[smangler.api :as sa])

(sa/trim-both-seq                nil)  ; => nil
(sa/trim-both-seq             "abba")  ; => ("abba", "bb", "")
(sa/trim-both-seq             "some")  ; => ("some")
(sa/trim-both-seq          "barkrab")  ; => ("barkrab" "arkra" "rkr" "k")

(sa/trim-both-seq "ab"     "barkrab")  ; => ("barkrab" "arkra" "rkr")
(sa/trim-both-seq #{\a \b} "barkrab")  ; => ("barkrab" "arkra" "rkr")

(sa/trim-both-seq \a \a      "aaxaa")  ; => ("aaxaa" "axa" "x")
```

## Low-level trimming

Certain applications may require more efficient and/or more strict trimming
functions. It is particularly not recommended but there is a [`smangler.core`][core]
namespace which contains trimming operations which are a bit faster than those in API.
They require certain argument types and no coercion is performed. The returned values
may also differ when it comes to handling corner cases (`nil` or empty values, not
finding a match, etc.):

* [`trim-both`][core-trim-both],
* [`trim-both-once`][core-trim-both-once].

The function [`trim-both`][core-trim-both] from the namespace [`smangler.core`][core]
takes a string as its last (or only) argument and trims its characters on both ends
are the same:

```clojure
(require '[smangler.core :as c])

(c/trim-both     nil)  ; => nil
(c/trim-both  "abcd")  ; => "abcd"
(c/trim-both  "abca")  ; => "bc"
(c/trim-both "aabaa")  ; => "b"
```

Optionally it can also take a matching function as the first argument. The function
should take a character and return a character, `nil` or `false`. If a character
passed as an argument to this matcher will be the first character of the passed
string and if a value returned by this match will be equal to the last character of
the passed string trimming will occur:

```clojure
(require '[smangler.core :as c])

(c/trim-both #{\a \b}             nil)  ; => nil
(c/trim-both #{\a \b}          "abba")  ; => ""
(c/trim-both #(and (= % \a) %) "abba")  ; => "bb"
(c/trim-both {\a \z}           "abbz")  ; => "bb"
```

Similarly to its counterpart from `smangler.api` the function can handle 3
arguments. In this case the first two should be characters matching the beginning and
the end of a string:

```clojure
(require '[smangler.core :as c])

(c/trim-both \a \z    nil)  ; => nil
(c/trim-both \a \z "abbz")  ; => "bb"
```

The function [`trim-both-once`][core-trim-both-once] from the namespace
[`smangler.core`][core] works the same way as `trim-both` but trims the given string
only once. However, it will return `nil` instead of the original string if there is
nothing to trim:

```clojure
(require '[smangler.core :as c])

(c/trim-both-once nil)                   ; => nil
(c/trim-both-once "")                    ; => nil
(c/trim-both-once "aa")                  ; => ""
(c/trim-both-once "abba")                ; => "bb"
(c/trim-both-once "some")                ; => nil
(c/trim-both-once "barkrab")             ; => "arkra"

(c/trim-both-once (set "ab") "barkrab")  ; => "arkra"
(c/trim-both-once #{\a \b}   "barkrab")  ; => "arkra"
(c/trim-both-once (set "abcd")   "xyz")  ; => nil

(c/trim-both-once \a \a        "aaxaa")  ; => "axa"
```

[api]:                          smangler.api.html
[core]:                         smangler.core.html
[api-trim-both]:                smangler.api.html#var-trim-both
[api-trim-both-seq]:            smangler.api.html#var-trim-both-seq
[api-trim-both-once]:           smangler.api.html#var-trim-both-once
[api-trim-both-once-with-orig]: smangler.api.html#var-trim-both-once-with-orig
[core-trim-both]:               smangler.core.html#var-trim-both
[core-trim-both-once]:          smangler.core.html#var-trim-both-once
