#!/bin/sh
#DATE=$(date | sed "s/ //g" | awk -F"." '{print $1$2$3}')
DATE=$(date +"%Y%m%d")
_BLOG=ne_blog_
_NEWS=ne_news_
_TAB=.tab
_GEN=.gen
_ACR=.acro
_SORT=.sort
_EXCEL=.tab
_EXCEL_NLP=.nlp.tab

MAIL_OUTPUT_PATH=/home/hgpark/news_ne_miner/output/mail/

NE_NEWS_NAME=$_NEWS$DATE$_TAB$_GEN
NE_BLOG_NAME=$_BLOG$DATE$_TAB$_GEN
ACR_NEWS_NAME=$_NEWS$DATE$_TAB$_ACR
ACR_BLOG_NAME=$_BLOG$DATE$_TAB$_ACR

ATTACH_FILE_NEWS_GEN=$MAIL_OUTPUT_PATH$NE_NEWS_NAME$_SORT$_EXCEL
ATTACH_FILE_BLOG_GEN=$MAIL_OUTPUT_PATH$NE_BLOG_NAME$_SORT$_EXCEL
ATTACH_FILE_NEWS_ACR=$MAIL_OUTPUT_PATH$ACR_NEWS_NAME$_SORT$_EXCEL
ATTACH_FILE_NEWS_ACR_NLP=$MAIL_OUTPUT_PATH$ACR_NEWS_NAME$_SORT$_EXCEL_NLP
ATTACH_FILE_BLOG_ACR=$MAIL_OUTPUT_PATH$ACR_BLOG_NAME$_SORT$_EXCEL


COUNT_NEWS_GEN=$(wc -l $MAIL_OUTPUT_PATH$NE_NEWS_NAME$_SORT$_EXCEL | awk '{print $1}')
COUNT_BLOG_GEN=$(wc -l $MAIL_OUTPUT_PATH$NE_BLOG_NAME$_SORT$_EXCEL | awk '{print $1}')
COUNT_NEWS_ACR=$(wc -l $MAIL_OUTPUT_PATH$ACR_NEWS_NAME$_SORT$_EXCEL | awk '{print $1}')
COUNT_NEWS_ACR_NLP=$(wc -l $MAIL_OUTPUT_PATH$ACR_NEWS_NAME$_SORT$_EXCEL_NLP | awk '{print $1}')

SUBJ="NE_LIST_OF_$DATE"
MSG="고유명사_뉴스 $NE_NEWS_NAME$_SORT$_EXCEL : 단어수 $COUNT_NEWS_GEN \n\n축약어_뉴스 $ACR_NEWS_NAME$_SORT$_EXCEL : 단어수 $COUNT_NEWS_ACR_NLP\n" 

_TO=jukwanlee@sk.com
_CC1=skp.5000918@sk.com
_CC2=skp.5000917@sk.com
_CC3=heegeun.park@sk.com
_CC4=donghun.shin@sk.com
_BC1=youngsook.hwang@sk.com
echo -e "sending mail....\n"
echo -e $MSG | mutt -s $SUBJ -c $_CC1 -c $_CC2 -c $_CC3 -c $_CC4 -b $_BC1 -a $ATTACH_FILE_NEWS_GEN $ATTACH_FILE_NEWS_ACR_NLP -- $_TO
echo "sent successfully!"


