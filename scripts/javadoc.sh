# !/bin/sh -e
# January 16, 2022

javadoc \
   --enable-preview --release 17 \
   -quiet \
   -Xdoclint:none  \
   -p lib \
   -d docs \
   --module-source-path src $(find src -name '*.java'|egrep -v '(com.acsredux.lib.testutil|/tests/)')

#rm -rf tmp
