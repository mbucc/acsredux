TESTCP=classes/com.acsredux.adapter.web:classes/com.acsredux.members:classes/com.acsredux.base:classes/com.acsredux.adapter.stub:testlib/*:lib/*
JUNIT_DETAILS=tree
SKIP_FMT=N


jars: mlib/com.acsredux.adapter.web@1.jar \
	mlib/com.acsredux.members@1.jar \
	mlib/com.acsredux.base@1.jar \
	mlib/com.acsredux.adapter.stub@1.jar \
	mlib/compiler-0.9.10.jar \
	mlib/result-flow-2.0.0.jar

mlib/compiler-0.9.10.jar: lib/compiler-0.9.10.jar
	cp $? $@

mlib/result-flow-2.0.0.jar: lib/result-flow-2.0.0.jar
	cp $? $@

mlib/com.acsredux.adapter.web@1.jar: compile resourcebundles
	@mkdir -p mlib
	jar --create \
		--file=$@ \
		--main-class com/acsredux/adapter/web/Main \
		--module-version 1 \
		-C classes/$$(echo "$@"|cut -d/ -f2|cut -d@ -f1) \
		.

mlib/%@1.jar: compile resourcebundles
	@mkdir -p mlib
	jar --create \
		--file=$@ \
		--module-version 1 \
		-C classes/$$(echo "$@"|cut -d/ -f2|cut -d@ -f1) \
		.

.PHONY: test
test: compiletests
	java \
		-cp testclasses:${TESTCP} \
		org.junit.platform.console.ConsoleLauncher \
		--disable-banner \
		--details=$(JUNIT_DETAILS) \
		--fail-if-no-tests \
		--exclude-engine=junit-vintage \
		--scan-classpath

.PHONY: compiletests
compiletests: compile resourcebundles
	javac \
		-d testclasses \
		-cp "${TESTCP}" \
		$$(find src -name '*.java'|egrep '(Test\.java|/Mock)')

.PHONY: resourcebundles
resourcebundles: \
		classes/com.acsredux.members/MemberErrorMessages.properties

classes/com.acsredux.members/MemberErrorMessages.properties: src/com.acsredux.members/MemberErrorMessages.properties
	cp $? $@

.PHONY: compile
compile: fmt
	javac \
		-p lib \
		-d classes \
		-Xlint \
		-Xlint:-requires-automatic \
		--module-source-path src \
		$$(find src -name '*.java'|egrep -v '(Test\.java|/Mock)')

.PHONY: checkstyle
checkstyle:
	java \
		-jar testlib/checkstyle-9.2.1-all.jar \
		-c test/checkstyle.xml \
		$$(find src -name '*.java'|grep -v module-info)

.PHONY: fmt
fmt: checkstyle
	[ "$(SKIP_FMT)" = "N" ] \
		&& npx prettier --print-width 90 --write . \
		|| printf ""

.PHONY: clean
clean:
	rm -rf mlib
	rm -rf classes
	rm -rf testclasses
	find ./test -name '*.actual' | xargs rm
