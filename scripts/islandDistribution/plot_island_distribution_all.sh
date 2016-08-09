#!/bin/bash

# Calculates the island distribution until given coverage, with the
# chosen chunk size, calling repeatedly to
# "plot_island_distribution_from_file.sh" for a fixed temperature. The
# inputs are "occurrence" files of previous script and the output are
# "allDistributions.gp" and its plot "islands.png"
#
# Author: J. Alberdi-Rodriguez

chunk=40
coverage=29
mkdir -p islandDistribution
out=islandDistribution/allDistributions.gp
if [ $# -lt 2 ]
then
    echo "using default chunk size $chunk"
    echo "using default coverage 29%"
else
    chunk=$1
    coverage=$2
fi

echo "set term png" > $out
echo "set output \"islands.png\"" >> $out
echo "plot \\" >> $out
for ((i=1;i<$coverage;i++))
do 
    echo $i
    plot_island_distribution_from_file.sh $chunk $i
    plot_island_distribution_get_island_number.sh $chunk $i
    mv islandDistribution/occurrences islandDistribution/occurrences_$i
    mv islandDistribution/average islandDistribution/average_$i
    mv islandDistribution/execTime islandDistribution/execTime_$i
    mv islandDistribution/times islandDistribution/times_$i
    echo -n "\"islandDistribution/occurrences_$i\" u 4:3 w l t \"$i%\", " >> $out
done
echo "" >> $out
sed -i -e 's/\(.*\),/\1 /' $out
gnuplot $out

