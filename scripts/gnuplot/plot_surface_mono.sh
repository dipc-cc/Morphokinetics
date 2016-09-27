#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
input=${1%.*} # remove the extension (if present)
sed    -e "s/INPUT/$input/g" $script_dir/plot_common_surface_mono > tmp
sed -i -e "s/OUTPUT/$input/g" tmp
/home/jalberdi004/software/gnuplot/5.0.2/bin/gnuplot tmp
