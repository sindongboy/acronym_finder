#!/bin/bash

function usage() {
    echo "usage: $0 [options]"
    echo "-t	type [ url | text | batch ]"
    exit 1
}


while test $# -gt 0; do 
    case "$1" in
        -h)
            shift
            usage
            ;;
        -t)
            shift
            TYPE=$1
            shift ;;
        *)
            break
            ;;
    esac
done

if [[ -z ${TYPE} ]]; then
    usage
fi

if [[ ${TYPE} != url ]] && [[ ${TYPE} != text ]] && [[ ${TYPE} != batch ]]; then
    usage
fi

PROJECT_HOME="/Users/sindongboy/Documents/workspace/ner_mining_from_daily_webnews/trunk"

# env.
CONFIG="/Users/sindongboy/Documents/workspace/news-ne-miner/config"
LOG="/Users/sindongboy/Documents/workspace/news-ne-miner/log"
RESOURCE_ACR="/Users/sindongboy/Documents/workspace/news-ne-miner/resource/acronym"
RESOURCE_BOT="/Users/sindongboy/Documents/workspace/news-ne-miner/resource/nebot"
RESOURCE_SEG="/Users/sindongboy/Documents/workspace/news-ne-miner/resource/segmenter"


# libs.
HNLP="/Users/sindongboy/.m2/repository/com/skplanet/nlp/hnlp/2.0.4-SNAPSHOT/hnlp-2.0.4-SNAPSHOT.jar"
CLI="/Users/sindongboy/.m2/repository/com/skplanet/nlp/cli/1.0.1-SNAPSHOT/cli-1.0.1-SNAPSHOT.jar"
COM_CLI="/Users/sindongboy/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar"
OMP_CONF="/Users/sindongboy/.m2/repository/com/skplanet/nlp/omp-config/1.0.6-SNAPSHOT/omp-config-1.0.6-SNAPSHOT.jar"
LOG4J="/Users/sindongboy/.m2/repository/log4j/log4j/1.2.7/log4j-1.2.7.jar"
JSOUP="/Users/sindongboy/.m2/repository/org/jsoup/jsoup/1.7.2/jsoup-1.7.2.jar"
JSON="/Users/sindongboy/.m2/repository/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar"
TARGET="${PROJECT_HOME}/target/NeHarvester-1.1.0-SNAPSHOT.jar"

CP="${TARGET}:${CONFIG}:${RESOURCE_ACR}:${RESOURCE_BOT}:${RESOURCE_SEG}:${HNLP}:${CLI}:${COM_CLI}:${OMP_CONF}:${LOG4J}:${JSOUP}:${JSON}:${LOG}"

java -Xmx4G -Dfile.encoding=UTF-8 -cp ${CP} com.skplanet.nlp.nebot.NebotTester -t ${TYPE}
