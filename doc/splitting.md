# Splitting

Splitting strings into substrings is realized by these 3 API functions:

* [`all-prefixes`][api-all-prefixes],
* [`all-suffixes`][api-all-suffixes]
* [`all-subs`][api-all-subs].

## Getting all prefixes

To get all possible prefixes of the given string you can call `all-prefixes` function
from the API. In its basic form, it takes one argument which should be a string and
returns a lazy sequence of strings, including the original string in the last
position:

```clojure
(require '[smangler.api :as sa])

(sa/all-prefixes       "")  ; => nil
(sa/all-prefixes "abcdef")  ; => ("a" "ab" "abc" "abcd" "abcde" "abcdef")
(sa/all-prefixes      "a")  ; => ("a")
```

## Coercion to strings

You can pass other types of arguments and they will be coerced to strings. Single
characters and numbers are supported, and so are collections of strings, characters
and numbers:

```clojure
(require '[smangler.api :as sa])

(sa/all-prefixes        12345)  ; => ("1" "12" "123" "1234" "12345")
(sa/all-prefixes           \a)  ; => ("a")
(sa/all-prefixes      [0 1 2])  ; => ("0" "01" "012")
(sa/all-prefixes   [\a \b \c])  ; => ("a" "ab" "abc")
(sa/all-prefixes ["abc" "de"])  ; => ("a" "ab" "abc" "abcd" "abcde")
```

## Custom splitter

Optionally, you can call `all-prefixes` with 2 arguments passed. In this scenario the
first argument should be a function which takes a single character and returns
a character, `false` or `nil`:

```clojure
(fn [character]
  (and (some-lookup character) character)
```

The function is used to partition the string. As a result the prefixes will not be
generated for all characters but for those substrings which are the effect of
splitting the string each time the splitter returns a new value.

```clojure
(require '[smangler.api :as sa])

(sa/all-prefixes #(and (= \a %) %)
                 "abcdef")  ; => ("a" "abcdef")
```

### Sets as splitters

It is common to use sets for partitioning the string. This is possible because in
Clojure sets implement function interface which allows us to perform quick lookup:

```clojure
(require '[smangler.api :as sa])

(sa/all-prefixes #{\a \b} "abcdef")  ; => ("a" "ab" "abcdef")
(sa/all-prefixes #{\a}    "abcdef")  ; => ("a" abcdef")
```

### Coercion to splitter

You can pass other types of arguments and they will be coerced to splitters. Single
characters, strings and numbers are supported, and so are collections of strings,
characters and numbers:

```clojure
(require '[smangler.api :as sa])

(sa/all-prefixes \a        "abcde")  ; => ("a" "abcde")
(sa/all-prefixes 1         "abcde")  ; => ("abcde")
(sa/all-prefixes 12      "12abcde")  ; => ("1" "12" "12abcde")
(sa/all-prefixes [1 2]   "12abcde")  ; => ("1" "12" "12abcde")
(sa/all-prefixes [\a \b]   "abcde")  ; => ("a" "ab" "abcde")
(sa/all-prefixes "ab"      "abcde")  ; => ("a" "ab" "abcde")
```

## Getting all suffixes

Getting all suffixes is possible with [`all-suffixes`][api-all-suffixes]. It takes
the same arguments and returns the same kind of values as `all-prefixes` but (as the
name stands for) generates all possible suffixes for the given string:

```clojure
(require '[smangler.api :as sa])

(sa/all-suffixes                "")  ; => nil
(sa/all-suffixes          "abcdef")  ; => ("abcdef" "bcdef" "cdef" "def" "ef" "f")
(sa/all-suffixes               "a")  ; => ("a")

(sa/all-suffixes             12345)  ; => ("12345" "2345" "345" "45" "5")
(sa/all-suffixes                \a)  ; => ("a")
(sa/all-suffixes           [0 1 2])  ; => ("012" "12" "2")
(sa/all-suffixes        [\a \b \c])  ; => ("abc" "bc" "c")
(sa/all-suffixes      ["abc" "de"])  ; => ("abcde" "bcde" "cde" "de" "e")

(sa/all-suffixes  #(and (= \a %) %)
                    "abcdef")        ; => ("abcdef" "bcdef")

(sa/all-suffixes #{\a \b} "abcdef")  ; => ("abcdef" "bcdef" "cdef")
(sa/all-suffixes #{\a}    "abcdef")  ; => ("abcdef" "bcdef")

(sa/all-suffixes \a        "abcde")  ; => ("abcde" "bcde")
(sa/all-suffixes 1         "abcde")  ; => ("abcde")
(sa/all-suffixes 12      "12abcde")  ; => ("12abcde" "2abcde" "abcde")
(sa/all-suffixes [1 2]   "12abcde")  ; => ("12abcde" "2abcde" "abcde")
(sa/all-suffixes [\a \b]   "abcde")  ; => ("abcde" "bcde" "cde")
(sa/all-suffixes "ab"      "abcde")  ; => ("abcde" "bcde" "cde")
```

## Getting all substrings

You can get all possible substrings of a string by calling [`all-subs`][api-all-subs]
from [`smangler.api`][api]. It works similarly to `all-prefixes` and `all-suffixes`
but returns all prefixes, infixes and suffixes, including the original string:

```clojure
(require '[smangler.api :as sa])

(sa/all-subs                "")  ; => nil
(sa/all-subs             "abc")  ; => ("a" "ab" "b" "abc" "bc" "c")
(sa/all-subs               "a")  ; => ("a")

(sa/all-subs               123)  ; => ("1" "12" "2" "123" "23" "3")
(sa/all-subs                \a)  ; => ("a")
(sa/all-subs           [0 1 2])  ; => ("0" "01" "1" "012" "12" "2")
(sa/all-subs        [\a \b \c])  ; => ("a" "ab" "b" "abc" "bc" "c")
(sa/all-subs        ["ab" "c"])  ; => ("a" "ab" "b" "abc" "bc" "c")

(sa/all-subs  #(and (= \a %) %)
              "abc")             ; => ("a" "abc" "bc")

(sa/all-subs #{\a \b}    "abc")  ; => ("a" "ab" "b" "abc" "bc" "c")
(sa/all-subs #{\a}       "abc")  ; => ("a" "abc" "bc")

(sa/all-subs \a          "abc")  ; => ("a" "abc" "bc")
(sa/all-subs 1           "abc")  ; => ("abc")
(sa/all-subs 12          "12c")  ; => ("1" "12" "2" "12c" "2c" "c")
(sa/all-subs [1 2]       "12c")  ; => ("1" "12" "2" "12c" "2c" "c")
(sa/all-subs [\a \b]     "abc")  ; => ("a" "ab" "b" "abc" "bc" "c")
(sa/all-subs "ab"        "abc")  ; => ("a" "ab" "b" "abc" "bc" "c")
```

## Low-level splitting

Certain applications may require more efficient and/or more strict splitting
functions. It is particularly not recommended but there is [`smangler.core`][core]
namespace which contains splitting operations which are a bit faster than those in
API. They require certain argument types and no coercion is performed:

* [`all-prefixes`][core-all-prefixes],
* [`all-suffixes`][core-all-suffixes]
* [`all-subs`][core-all-subs].

```clojure
(require '[smangler.core :as c])

(c/all-prefixes          nil)  ; => nil
(c/all-prefixes           "")  ; => nil
(c/all-prefixes        "abc")  ; => ("a" "ab" "abc")
(c/all-prefixes  #{\a} "abc")  ; => ("a" "abc")

(c/all-suffixes          nil)  ; => nil
(c/all-suffixes           "")  ; => nil
(c/all-suffixes        "abc")  ; => ("abc" "bc" "c")
(c/all-suffixes  #{\a} "abc")  ; => ("abc" "bc")

(c/all-subs              nil)  ; => nil
(c/all-subs               "")  ; => nil
(c/all-subs            "abc")  ; => ("a" "ab" "b" "abc" "bc" "c")
(c/all-subs      #{\a} "abc")  ; => ("a" "abc" "bc")
```

[api]:                          smangler.api.html
[core]:                         smangler.core.html
[api-all-suffixes]:             smangler.api.html#var-all-suffixes
[api-all-prefixes]:             smangler.api.html#var-all-prefixes
[api-all-subs]:                 smangler.api.html#var-all-subs
[api-trim-both]:                smangler.api.html#var-trim-both
[api-trim-both-seq]:            smangler.api.html#var-trim-both-seq
[api-trim-both-once]:           smangler.api.html#var-trim-both-once
[api-trim-both-once-with-orig]: smangler.api.html#var-trim-both-once-with-orig
[core-trim-both]:               smangler.core.html#var-trim-both
[core-trim-both-once]:          smangler.core.html#var-trim-both-once
[core-all-suffixes]:            smangler.core.html#var-all-suffixes
[core-all-prefixes]:            smangler.core.html#var-all-prefixes
[core-all-subs]:                smangler.core.html#var-all-subs
