#!/bin/bash

function getStatistics()
{
    javaFiles=$(find . -name "*java" | wc -l 2>/dev/null)
    javaLines=$(find . -name "*java" | xargs wc -l 2>/dev/null | tail -n 1 | awk '{print $1}')
    shFiles=$(find . -name "*sh" | wc -l) 
    shLines=$(find . -name "*sh" | xargs wc -l | tail -n 1 | awk '{print $1}')
    mFiles=$(find . -name "*.m" | wc -l) 
    mLines=$(find . -name "*.m" | xargs wc -l | tail -n 1 | awk '{print $1}')
    pyFiles=$(find . -name "*py" | wc -l) 
    pyLines=$(find . -name "*py" | xargs wc -l | tail -n 1 | awk '{print $1}')
    echo "$1 $javaFiles $javaLines $shFiles $shLines $mFiles $mLines $pyFiles $pyLines"
}

echo "Date JavaFiles JavaLines BashFiles BashLines MatlabFiles MatlabLines PythonFiles PythonLines"
getStatistics $(date -I)
month=$(date -I | awk '{split($0,a,"-"); print a[2]}')
year=$(date -I | awk '{split($0,a,"-"); print a[1]}')
# Go month by month
for((j=year; j>=2014; j--))
do
    for((i=$month; i>0; i--))
    do
	gitHash=$(printf "git rev-list -n 1 --before=\"%d-%02d-01\" master" $j $i)
	hash=$($gitHash)
	git co $hash > /dev/null 2>/dev/null
	getStatistics $(printf "%d-%02d-01" $j $i)
    done
done

git co  master # revert to last change

