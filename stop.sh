#! /bin/sh -e

pid=$(ps -eo pid,args|grep 'java.*com.acsredux.adapter.web'|grep -v grep|awk '{print $1}')
[ "x$pid" != "x" ] && kill $pid || echo already stopped.
sleep 0.2
