#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

psdFile=""

# check if any input PSD exists
if [ -e psdAvgRaw.txt ]
then
    psdFile="psdAvgRaw.txt"
fi
if [ -e psd ]
then
    psdFile="psd"
fi
if [ $# -eq 1 ]
then
    psdFile=$1
fi

# do the image, if found
if [ $psdFile != "" ]
then
    echo "making graphics of PSD file $psdFile..."
    cp $script_dir/plot_common_psd .
    sed -i -e "s/INPUT/$psdFile/g"  plot_common_psd
    gnuplot plot_common_psd
    convert psd.png -trim psd.png
else
    echo "not able to find any input file (psd, psdAvgRaw.txt or command line)"
fi
