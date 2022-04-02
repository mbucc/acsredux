#! /bin/sh -e

if [ ! -f "siteinfo.json" ]; then
 cat > siteinfo.json << EOF
{
  "siteTitle": "A Test Site",
  "siteDescription": "foobar",
  "siteEmail": "t@t.com",
  "analyticsScriptTag": ""
}
EOF
fi

sh ./stop.sh
make SKIP_FMT=Y
IS_PRODUCTION=Y sh ./start.sh
