#!/bin/bash

if [ $# -lt 1 ]
then
    echo "You have to provide a surface (without extension)"
    exit -1
fi

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
sed    -e "s/INPUT/$1/g" $script_dir/plot_common_surface_binary > tmp
#Old way, doesn't work with 16 bit unsigned integers
#maxX=$(hexdump -s 0x1a -n 1 -e '1/2 "%4i\n"' $1.mko)
#maxY=$(hexdump -s 0x17 -n 1 -e '1/2 "%4i\n"' $1.mko)
#New way. #http://stackoverflow.com/questions/22190902/cut-or-awk-command-to-print-first-string-of-first-row
maxX=$(cat $1.mko | dd conv=swab | od -j 0x1a -N 2 -t d | awk 'NR==1{print $2}') >/dev/null 2>/dev/null
maxY=$(cat $1.mko | dd conv=swab | od -j 0x16 -N 2 -t d | awk 'NR==1{print $2}') >/dev/null 2>/dev/null
sed -i -e "s/MAX_X/$maxX/g" tmp
sed -i -e "s/MAX_Y/$maxY/g" tmp
sed -i -e "s/OUTPUT/$1/g" tmp
gnuplot tmp
convert -trim -rotate 90 $1.png $1.png
