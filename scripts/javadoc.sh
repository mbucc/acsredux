# !/bin/sh -e
# January 16, 2022
# Avoid errors Javadoc prints for test files (even when they are not in sourcefiles).

D=tmp/src


rm -rf tmp
mkdir -p $D

cp -r src/ $D
find $D -type f | egrep '(/Test|/Mock|/ManualTest)' | xargs rm
rm -rf $D/com.acsredux.lib.testutil

javadoc -quiet -Xdoclint:none  -p lib:testlib -d docs --module-source-path $D $(find $D -name '*.java')
