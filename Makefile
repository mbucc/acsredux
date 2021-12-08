CP=classes/com.acsredux.user:classes/com.acsredux.base:classes/com.acsredux.adapters.dbstub:testlib/*
JUNIT_DETAILS=tree
SKIP_FMT=N


# The class path syntax for junit-platform-console-standalone-1.8.2.jar
# is different than javac when including jars; you must list all the jars.
.PHONY: test
test: compiletests
	java -cp testclasses:${CP} org.junit.platform.console.ConsoleLauncher \
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
		-d ./classes \
		-Xlint \
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

