JUNIT_DETAILS=tree
SKIP_FMT=N

JAVAC=javac --enable-preview --release 17
JAVA=java --enable-preview



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
	mlib/result-flow-3.1.0.jar

mlib/compiler-0.9.10.jar: lib/compiler-0.9.10.jar
	cp $? $@

mlib/result-flow-3.1.0.jar: lib/result-flow-3.1.0.jar
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
	${JAVAC} \
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

.PHONY: test
test: compiletests
	ACSREDUX_SALT_FILENAME=./test/salt \
	ACSREDUX_SECRET_FILENAME=./test/encryption-key \
	${JAVA} \
		-cp "lib/*:testclasses:testlib/*:$$(echo classes/*|tr ' ' :)" \
		org.junit.platform.console.ConsoleLauncher \
		--disable-banner \
		--details=$(JUNIT_DETAILS) \
		--fail-if-no-tests \
		--exclude-engine=junit-vintage \
		--scan-classpath

# Manual tests are not run as part of the normal "make test" target.
# For example, a notifier test that actually sends an email.
# Manual tests have class names that start with "ManualTest".
.PHONY: manualtest
manualtest: compiletests
	${JAVA} \
		-cp "testclasses:testlib/*:$$(echo classes/*|tr ' ' :)" \
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
compiletests: compile resourcebundles testlib/com.acsredux.testlib.jar
	${JAVAC} \
		-d testclasses \
		-cp "testlib/*:$$(echo classes/*|tr ' ' :)" \
		$$(find src -name '*.java'|egrep '(/Test|/Mock|/ManualTest)')


testlib/com.acsredux.testlib.jar: \
		./src/com.acsredux.lib.testutil/com/acsredux/lib/testutil/MockProxy.java \
		./src/com.acsredux.lib.testutil/com/acsredux/lib/testutil/TestData.java \
		./src/com.acsredux.lib.testutil/com/acsredux/lib/testutil/MockAdminReader.java \
		./src/com.acsredux.lib.testutil/com/acsredux/lib/testutil/MockMemberService.java \
		./src/com.acsredux.lib.testutil/com/acsredux/lib/testutil/MockAdminService.java
	${JAVAC} \
		-d testclasses \
		-cp "testlib/*:$$(echo classes/*|tr ' ' :)" \
		$$(find src/com.acsredux.lib.testutil -name '*.java')
	jar --create \
		--file=$@ \
		$$(find testclasses/com/acsredux/lib/testutil -name '*.class')


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
		${JAVA} \
			-jar testlib/checkstyle-9.2.1-all.jar \
			-c test/checkstyle.xml \
			$$(find src -name '*.java'|grep -v module-info) ; \
	fi

.PHONY: fmt
fmt:
	if [ "$(SKIP_FMT)" = "N" ]; then \
		npx prettier --print-width 90 --write src ; \
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
