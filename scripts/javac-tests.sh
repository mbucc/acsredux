#! /bin/sh
# Both stop on error and suppress the pattern matching preview warnings.
# March 6, 2022

${JAVAC} \
		-d testclasses \
		-cp "lib/*:testlib/*:$(echo classes/*|tr ' ' :)" \
		$(find src -name '*.java'|egrep '(/Test|/Mock|/ManualTest|Test\.java)') \
		> compile.out 2>&1

rval=$?

grep -v "Note:" < compile.out

rm -f compile.out

exit $rval
