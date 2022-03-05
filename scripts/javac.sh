#! /bin/sh
# Both stop on error and suppress the pattern matching preview warnings.
# March 6, 2022

${JAVAC} \
		-p lib \
		-d classes \
		-Xlint \
		-Xlint:-requires-automatic \
		--module-source-path src \
		$(find src -name '*.java'|egrep -v '(/Test|/Mock|/ManualTest)') \
		> compile.out 2>&1

rval=$?

awk '/ warning: \[preview] patterns/{getline;getline;next} 1' < compile.out

rm -f compile.out

exit $rval
