#!/bin/sh
JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk.x86_64
WORK_HOME_DIR=/home/hgpark/news_ne_miner/
BIN=$JAVA_HOME/bin/java
LIB=$WORK_HOME_DIR/bin/NeHarvester-1.0.0-SNAPSHOT-jar-with-dependencies.jar
MAIN_CLASS=com.skplanet.nlp.nebot.NebotTester
CONFIG=$WORK_HOME_DIR/bin/property/nebot.properties
OPTION=-b

$BIN -cp $LIB $MAIN_CLASS $CONFIG $OPTION



