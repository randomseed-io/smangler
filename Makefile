
.PHONY: 		watch default docs deploy test dev-docs-cljs

default:		docs

docs:
			bin/docs

test-clj:
			clojure -Atest -e deprecated

test:
			@$(MAKE) test-clj

pom:
			clojure -Spom && awk 'NF > 0' pom.xml > pom.new.xml && mv -f pom.new.xml pom.xml
deploy:
			mvn deploy

.PHONY: list
list:
		@$(MAKE) -pRrq -f $(lastword $(MAKEFILE_LIST)) : 2>/dev/null | awk -v RS= -F: '/^# File/,/^# Finished Make data base/ {if ($$1 !~ "^[#.]") {print $$1}}' | sort | egrep -v -e '^[^[:alnum:]]' -e '^$@$$' | xargs
