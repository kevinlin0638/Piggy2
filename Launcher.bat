@echo off
title ¤pØp¨¦
set CLASSPATH=.;lib\*
java -Xms8024M -Xmx10024M -server -Dnet.sf.odinms.wzpath=wz  server.Start
pause
