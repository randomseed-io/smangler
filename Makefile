
.PHONY: 		watch default docs deploy test test-clj sig jar pom clean

default:		docs

docs:
			bin/docs

test-clj:
			bin/test --no-profiling

test:
			@$(MAKE) test-clj

pom: pom.xml
			clojure -Spom && awk 'NF > 0' pom.xml > pom.new.xml && mv -f pom.new.xml pom.xml
			rm -f pom.xml.asc

smangler.jar: pom.xml
			clojure -Apack -m mach.pack.alpha.skinny --no-libs --project-path smangler.jar

jar: smangler.jar

sig: pom.xml
			rm -f pom.xml.asc
			gpg2 --armor --detach-sig pom.xml

deploy:
			@$(MAKE) clean
			@$(MAKE) pom
			@$(MAKE) jar
			@$(MAKE) sig
			mvn deploy:deploy-file -Dfile=smangler.jar -DrepositoryId=clojars -Durl=https://clojars.org/repo -DpomFile=pom.xml

clean:
			rm -f smangler.jar pom.xml.asc

.PHONY: list
list:
		@$(MAKE) -pRrq -f $(lastword $(MAKEFILE_LIST)) : 2>/dev/null | awk -v RS= -F: '/^# File/,/^# Finished Make data base/ {if ($$1 !~ "^[#.]") {print $$1}}' | sort | egrep -v -e '^[^[:alnum:]]' -e '^$@$$' | xargs
