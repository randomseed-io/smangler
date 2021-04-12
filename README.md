# smangler – String Mangling Library

[![Smangler on Clojars](https://img.shields.io/clojars/v/io.randomseed/smangler.svg)](https://clojars.org/io.randomseed/smangler)

Another Clojure library for manipulating strings.

* It **trims** the same or different characters from both ends of a string:

```clojure
(require '[smangler.api :as s])

;; Trim the same letters from both ends.
(s/trim-both "madam")
; => "d"

;; Trim the same letters but only if they are 'm' or 'n'.
(s/trim-both "mn" "madam")
; => "ada"

;; Trim the same letters once.
(s/trim-both-once "madam")
; => "ada"

;; Trim the same letters and generate a result of each step.
(s/trim-both-seq "madam")
; => ("madam" "ada" "d")
```

* It generates all **prefixes** and **suffixes** of a string:

```clojure
(require '[smangler.api :as s])

;; Generate all prefixes.
(s/all-prefixes "madam")
; => ("m" "ma" "mad" "mada" "madam")

;; Generate all prefixes with 'd' as a boundary character.
(s/all-prefixes \d "madam")
; => ("ma" "mad" "madam")

;; Generate all prefixes using dot, hyphen and comma as boundary characters.
(s/all-prefixes ".,-" "ma-dam")
; => ("ma" "ma-" "ma-dam")

;; Generate all suffixes using dot as boundary.
(s/all-suffixes "." "a.b.c")
; => ("a.b.c" ".b.c" "b.c" ".c" "c")
```

* It generates all possible prefixes, infixes and suffixes (**substrings**) of a string:

```clojure
(require '[smangler.api :as s])

;; Generate all prefixes, suffixes and infixes.
(s/all-subs "abcd")
; => ("a" "ab" "b" "abc" "bc" "c")

;; Generate all prefixes, suffixes and infixes using colon, dot and star
;; as boundary characters.
(api/all-subs ":.* " "use:*123*")
; => ("use"       "use:"     ":"     "use:*" ":*"
      "*"         "use:*123" ":*123" "*123"  "123"
      "use:*123*" ":*123*"   "*123*" "123*"  "*")
```

Recommended way is to use functions defined in `smangler.api` namespace. They coerce
last arguments into strings and predicate arguments into functions.

Use of `smangler.core` may be helpful in special cases, where littile performance
gain is very important. Functions from this namespace are very strict about input
data types and  may return values of different types than their equivalents in API.

## Installation

To use smangler in your project, add the following to dependencies section of
`project.clj` or `build.boot`:

```clojure
[io.randomseed/smangler "1.0.3"]
```

For `deps.edn` add the following as an element of a map under `:deps` or
`:extra-deps` key:

```clojure
io.randomseed/smangler {:mvn/version "1.0.3"}
```

You can also download JAR from [Clojars](https://clojars.org/io.randomseed/smangler).

## Documentation

Full documentation including usage examples is available at:

* https://randomseed.io/software/smangler/

## License

Copyright © 2019 Paweł Wilk

Smangler is copyrighted software owned by Paweł Wilk (pw@gnu.org). You may
redistribute and/or modify this software as long as you comply with the terms of
the [GNU Lesser General Public License][LICENSE] (version 3).

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

## Development

[![CircleCI](https://circleci.com/gh/randomseed-io/smangler.svg?style=svg)](https://circleci.com/gh/randomseed-io/smangler)

### Building docs

```bash
make docs
```

### Building JAR

```bash
make jar
```

### Rebuilding POM

```bash
make pom
```

### Signing POM

```bash
make sig
```

### Deploying to Clojars

```bash
make deploy
```

### Interactive development

```bash
bin/repl
```

Starts REPL and nREPL server (port number is stored in `.nrepl-port`).

[LICENSE]:    https://github.com/randomseed-io/smangler/blob/master/LICENSE
