VER=1

jars: test
	mkdir -p mlib
	jar --create \
		--file=mlib/com.acsredux.server@${VER}.jar \
		--module-version ${VER} \
		-C ./classes/com.acsredux.server/ \
		.

compile: fmt
	javac -d ./classes --module-source-path src $$(find src -name '*.java')

.PHONY: test
test: compiletests
	java -jar ./testlib/junit-platform-console-standalone-1.8.2.jar \
		-cp "mlib/*:testlib/*:testclasses" \
		--disable-banner \
		--fail-if-no-tests \
		--exclude-engine=junit-vintage \
		--details=tree \
		--scan-classpath

compiletests: compile
	javac -cp "testlib/*" -d testclasses $$(find tests -name '*.java')


fmt:
	npx prettier --write "**/*.java"

clean:
	rm -rf mlib
	rm -rf classes
	rm -rf testclasses
	rm -f test/*.out

