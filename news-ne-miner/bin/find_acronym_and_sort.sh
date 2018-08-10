#!/bin/sh
JAVA_HOME=/usr/lib/jvm/java-1.7.0-openjdk.x86_64
WORK_HOME_DIR=/home/hgpark/news_ne_miner/
BIN=$JAVA_HOME/bin/java
LIB=/home/hgpark/news_ne_miner/bin/NeAcronymFinder-1.0.0-SNAPSHOT-jar-with-dependencies.jar
MAIN_CLASS=com.skplanet.nlp.acronym.NeAcronymFinder
CONFIG=/home/hgpark/news_ne_miner/bin/property/neacronym.properties
INPUT_FILE_PATH=/home/hgpark/news_ne_miner/output/ne_raw/

#DATE=$(date | sed "s/ //g" | awk -F"." '{print $1$2$3}')
DATE=$(date +"%Y%m%d")
_BLOG=ne_blog_
_NEWS=ne_news_
_TAB=.tab
_GEN=.gen
_ACR=.acro
_SORT=.sort
_EXCEL=.tab


NEWS_INPUT=$INPUT_FILE_PATH$_NEWS$DATE$_TAB
BLOG_INPUT=$INPUT_FILE_PATH$_BLOG$DATE$_TAB

$BIN -cp $LIB $MAIN_CLASS $CONFIG $NEWS_INPUT
$BIN -cp $LIB $MAIN_CLASS $CONFIG $BLOG_INPUT


MAIL_OUTPUT_PATH=/home/hgpark/news_ne_miner/output/mail/


NE_OUTPUT_PATH=/home/hgpark/news_ne_miner/output/NE/
NE_NEWS_NAME=$_NEWS$DATE$_TAB$_GEN
NE_BLOG_NAME=$_BLOG$DATE$_TAB$_GEN
cat $NE_OUTPUT_PATH$NE_NEWS_NAME | awk -F "\t" '{print $3"\t"$7"\t"$11"\t"$13"\t"$14}' | sed "s/ ||//g" | sort -k 1 -nr > $MAIL_OUTPUT_PATH$NE_NEWS_NAME$_SORT$_EXCEL
cat $NE_OUTPUT_PATH$NE_BLOG_NAME | awk -F "\t" '{print $3"\t"$7"\t"$11"\t"$13"\t"$14}' | sed "s/ ||//g" | sort -k 1 -nr > $MAIL_OUTPUT_PATH$NE_BLOG_NAME$_SORT$_EXCEL

ACRO_OUTPUT_PATH=/home/hgpark/news_ne_miner/output/acronym/
ACRO_NEWS_NAME=$_NEWS$DATE$_TAB$_ACR
ACRO_BLOG_NAME=$_BLOG$DATE$_TAB$_ACR
cat $ACRO_OUTPUT_PATH$ACRO_NEWS_NAME | awk -F "\t" '{print $3"\t"$7"\t"$11"\t"$12"\t"$13"\t"$14}' | sort -k 1 -nr > $MAIL_OUTPUT_PATH$ACRO_NEWS_NAME$_SORT$_EXCEL
cat $ACRO_OUTPUT_PATH$ACRO_BLOG_NAME | awk -F "\t" '{print $3"\t"$7"\t"$11"\t"$12"\t"$13"\t"$14}' | sort -k 1 -nr > $MAIL_OUTPUT_PATH$ACRO_BLOG_NAME$_SORT$_EXCEL

