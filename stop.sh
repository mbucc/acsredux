#! /bin/sh -e

pid=$(jps|grep web|cut -d' ' -f1)
[ "x$pid" != "x" ] && kill $pid || echo already stopped.
