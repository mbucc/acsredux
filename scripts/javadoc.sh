# !/bin/sh -e
# January 16, 2022

D=tmp/src
rm -rf tmp
mkdir -p $D

#
#		javadoc prints errors for test files even
#		when they are excluded from the list of
#		sourcefiles passed to the command.
#
#		So we use a temporary directory and
#		"manually" remove the test files.
#
cp -r src/ $D
find $D -type f | egrep '(/Test|/Mock|/ManualTest)' | xargs rm
rm -rf $D/com.acsredux.lib.testutil

javadoc \
   -quiet \
   -Xdoclint:none  \
   -p lib:testlib \
   -d docs \
   --module-source-path $D $(find $D -name '*.java')

rm -rf tmp
