#!/bin/bash

for i in $(ls -d */);
do
    echo $i;
    pushd $i/results
    for j in $(ls -d */);
    do
	pushd $j
	for k in $(ls surface*txt)
	do
	    if [ ! -e ${k%.*}.png ]
	    then
		plot_surface.sh $k
	    fi
	done
	popd
    done
    popd
done
