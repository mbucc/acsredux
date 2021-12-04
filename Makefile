CP=testlib/*:classes/com.acsredux.auth:classes/com.acsredux.base


.PHONY: test
test: compiletests
	java -jar ./testlib/junit-platform-console-standalone-1.8.2.jar \
		-cp "${CP}:testclasses" \
		--disable-banner \
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
	npx prettier --print-width 90 --write .

.PHONY: clean
clean:
	rm -rf mlib
	rm -rf classes
	rm -rf testclasses

