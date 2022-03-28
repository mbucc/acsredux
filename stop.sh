#! /bin/sh -e

pid=$(ps -a -o pid,args|grep 'java.*com.acsredux.adapter.web'|grep -v grep|awk '{print $1}')
[ "x$pid" != "x" ] && kill $pid || echo already stopped.
