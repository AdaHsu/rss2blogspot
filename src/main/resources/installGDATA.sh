#!/bin/bash

MVN=$(which mvn)

VER=1.38

for FILE in $(ls *.jar);
do
  T=$(echo $FILE | sed -e 's/\.jar$//')
  V=$(echo $T | gawk -F- '{ print $NF }')
  F=$(echo $T | sed -e 's/'$V'//' | sed -e 's/-$//')
 
  $MVN install:install-file -DgroupId=com.google.gdata \
        -DartifactId=${F} -Dversion=${V}-${VER} -Dfile=${FILE} -Dpackaging=jar \
        -DgeneratePom=true
done;