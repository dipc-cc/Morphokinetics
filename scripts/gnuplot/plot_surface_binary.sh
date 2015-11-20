#!/bin/bash

if [ $# -lt 1 ]
then
    echo "You have to provide a surface (without extension)"
    exit -1
fi

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
sed    -e "s/INPUT/$1/g" $script_dir/plot_common_surface_binary > tmp
max=$(hexdump -s 0x17 -n 1 -e '1/1 "%4i\n"' $1.mko)
sed -i -e "s/MAX/$max/g" tmp
sed -i -e "s/OUTPUT/$1/g" tmp
gnuplot tmp
