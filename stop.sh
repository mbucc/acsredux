#! /bin/sh -e

pid=$(ps |grep java|grep -v grep|awk '{print $1}')
[ "x$pid" != "x" ] && kill $pid || echo already stopped.
