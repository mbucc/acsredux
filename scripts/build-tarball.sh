#! /bin/sh -e
# Build deploy tarball.

sha1=$(git rev-parse HEAD|cut -c -1-8)
fn=deploy.$sha1.tgz
[ -f $fn ] && echo $fn already exists >&2 && exit 1

make clean
make

mkdir bin
cp start.sh stop.sh env.sh ./bin
cat scripts/env-setup|sed 's;./env.sh;env.sh;' > ./bin/env-setup

tar czvf $fn ./mlib ./web ./bin

rm -rf bin
