#!/bin/bash

# Processes activation energy data from output.* file of every
# execution to get only the last values. Get the last data table to be
# processed later.
#
# Author: J. Alberdi-Rodriguez

function getMatrix()
{
    # Grep we want to find in output, skip the first column with awk and get number of columns (NF in awk, columns in bash).
    columns=$(grep $1 output.*  -n | awk '{$1=""; print > "tmpFile"; print NF-1 }' | tail -n 1)
    # Print last columns
    tail -n $columns tmpFile
    rm tmpFile
}

# fluxes
for i in $(ls -d */);
do
    echo $i;
    pushd $i;
    # temperatures
    for j in $(ls -d */);
    do
	echo $j;
	pushd $j
	getMatrix "AeRatioTimesPossible" > AeRatioTimesPossible
	getMatrix "AePossibleFromList" > AePossibleFromList
	popd;
    done
    popd;
done
