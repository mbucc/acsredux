JUNIT_DETAILS=tree
SKIP_FMT=N




###########################################################################
#
#
#		Build jars
#
#
###########################################################################

jars: \
	mlib/com.acsredux.adapter.web@1.jar \
	mlib/com.acsredux.core.members@1.jar \
	mlib/com.acsredux.core.admin@1.jar \
	mlib/com.acsredux.core.base@1.jar \
	mlib/com.acsredux.adapter.stub@1.jar \
	mlib/com.acsredux.adapter.mailgun@1.jar \
	mlib/com.acsredux.lib.env@1.jar \
	mlib/compiler-0.9.10.jar \
	mlib/result-flow-3.0.0.jar

mlib/compiler-0.9.10.jar: lib/compiler-0.9.10.jar
	cp $? $@

mlib/result-flow-3.0.0.jar: lib/result-flow-3.0.0.jar
	cp $? $@

mlib/com.acsredux.adapter.web@1.jar: compile resourcebundles
	@mkdir -p mlib
	jar --create \
		--file=$@ \
		--main-class com/acsredux/adapter/web/Main \
		--module-version 1 \
		-C classes/$$(echo "$@"|cut -d/ -f2|cut -d@ -f1) \
		.

mlib/com.acsredux.lib.env@1.jar: compile
	@mkdir -p mlib
	jar --create \
		--file=$@ \
		--main-class com.acsredux.lib.env/Main \
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



###########################################################################
#
#
#		Classes and resource bundles
#
#
###########################################################################

.PHONY: resourcebundles
resourcebundles: \
		classes/com.acsredux.core.members/MemberErrorMessages.properties

classes/%Messages.properties: src/%Messages.properties
	cp $? $@

.PHONY: compile
compile: checkstyle
	javac \
		-p lib \
		-d classes \
		-Xlint \
		-Xlint:-requires-automatic \
		--module-source-path src \
		$$(find src -name '*.java'|egrep -v '(/Test|/Mock|/ManualTest)')




###########################################################################
#
#
#		Automated tests
#
#
###########################################################################

TESTCP=classes/com.acsredux.core.admin:classes/com.acsredux.adapter.mailgun:classes/com.acsredux.lib.env:classes/com.acsredux.adapter.web:classes/com.acsredux.core.members:classes/com.acsredux.core.base:classes/com.acsredux.adapter.stub:testlib/*:lib/*

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

# Manual tests are not run as part of the normal "make test" target.
# For example, a notifier test that actually sends an email.
# Manual tests have class names that start with "ManualTest".
.PHONY: test
manualtest: compiletests
	java \
		-cp testclasses:${TESTCP} \
		org.junit.platform.console.ConsoleLauncher \
		--disable-banner \
		--details=$(JUNIT_DETAILS) \
		--fail-if-no-tests \
		--exclude-engine=junit-vintage \
		--include-classname=".*ManualTest.*" \
		--scan-classpath

# Use classpath not module lib for compiling and running tests.
# Keeps test files out of modules.
# Let's us use testutil without putting it in module-infos.
.PHONY: compiletests
compiletests: compile resourcebundles
	javac \
		-d testclasses \
		-cp "${TESTCP}" \
		$$(find src/com.acsredux.lib.testutil -name '*.java')
	javac \
		-d testclasses \
		-cp "${TESTCP}" \
		$$(find src -name '*.java'|egrep '(/Test|/Mock|/ManualTest)')


###########################################################################
#
#
#		Formatting & static analysis
#
#
###########################################################################

.PHONY: checkstyle
checkstyle: fmt
	if [ "$(SKIP_FMT)" = "N" ]; then \
		java \
			-jar testlib/checkstyle-9.2.1-all.jar \
			-c test/checkstyle.xml \
			$$(find src -name '*.java'|grep -v module-info) ; \
	fi

.PHONY: fmt
fmt:
	if [ "$(SKIP_FMT)" = "N" ]; then \
		npx prettier --print-width 90 --write src web/template ; \
	fi

.PHONY: javadoc
javadoc:
	./scripts/javadoc.sh



###########################################################################
#
#
#		Delete generated files
#
#
###########################################################################

.PHONY: clean
clean:
	rm -rf mlib
	rm -rf classes
	rm -rf testclasses
	rm -rf tmp
	find ./test -name '*.actual' | xargs rm
