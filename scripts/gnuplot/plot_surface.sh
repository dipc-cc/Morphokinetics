#!/bin/bash

script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
input=${1%.*} # remove the extension (if present)
sed    -e "s/INPUT/$input/g" $script_dir/plot_common_surface > tmp
sed -i -e "s/OUTPUT/$input/g" tmp
gnuplot tmp  # it doesn't work with version 5.0.3 (it does some kind of rotation) ??
