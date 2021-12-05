CP=testlib/*:classes/com.acsredux.auth:classes/com.acsredux.base
JUNIT_DETAILS=tree
SKIP_FMT=N


# The class path syntax for junit-platform-console-standalone-1.8.2.jar
# is different than javac when including jars; you must list all the jars.
.PHONY: test
test: compiletests
	java -jar ./testlib/junit-platform-console-standalone-1.8.2.jar \
		-cp "testlib/mockito-core-4.1.0.jar:testlib/byte-buddy-1.12.3.jar:testlib/byte-buddy-agent-1.12.3.jar:testlib/objenesis-3.2.jar:classes/com.acsredux.auth:classes/com.acsredux.base:testclasses" \
		--disable-banner \
		--details=$(JUNIT_DETAILS) \
		--fail-if-no-tests \
		--exclude-engine=junit-vintage \
		--scan-classpath

.PHONY: compiletests
compiletests: compile
	javac \
		-d testclasses \
		-cp "${CP}" \
		$$(find src -name '*.java'|grep Test\.java)

.PHONY: compile
compile: fmt
	javac \
		-Xlint \
		-d ./classes \
		--module-source-path src \
		$$(find src -name '*.java'|grep -v Test\.java)

.PHONY: fmt
fmt:
	[ "$(SKIP_FMT)" = "N" ] \
		&& npx prettier --print-width 90 --write . \
		|| printf ""

.PHONY: clean
clean:
	rm -rf mlib
	rm -rf classes
	rm -rf testclasses

