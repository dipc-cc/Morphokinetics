#!/bin/bash -e

# Calculates the island distribution with the given chunk size and
# coverage from the input file
# "dataEvery1percentAndNucleation.txt". The output is "occurrences"
# and the plot "islandDistribution.png" It also calculates the average
# island size (regardless of the chunk size) to the same files.
#
# Additionally, also computes the average simulated time.
#
# Author: J. Alberdi-Rodriguez


script_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
chunk=1
coverage=5
mkdir -p islandDistribution
if [ $# -lt 2 ]
then
    echo "using default chunk size $chunk"
    echo "using default coverage 5%"
else
    chunk=$1
    coverage=$2
fi

#clean data output file and save it to temporary file
#get histogram values and clean them from "]", "[" and ","
grep -P "^0\.[0]?$coverage\t" -B 2 dataEvery1percentAndNucleation.txt | grep histogram | sed "s/\(,\|\[\)\|]/ /g" > islandDistribution/histograms

#get the times
grep -P "^0\.[0]?$coverage\t" dataEvery1percentAndNucleation.txt > islandDistribution/times

#count the occurrences of each atom number per island
awk -v chk=$chunk -v coverage=$coverage '
{
split($0,islands," ")
for (i=3;i<length($0);i++) 
{
  if (islands[i] != 0) 
  {
    sum += islands[i];
    e=e+1;
    bin=int((islands[i]+(chk-0.001))/chk);
    histog[bin]++;
  }
}
}
END{
avg=sum/e
print "#average size of islands = " avg " | sum = "sum " number of elements "e
print "#number_of_atoms number_of_occurrences (n_of_occurrences*avg^2)/coverage j*chunk/avg"
print "#                                      normalised number of occurrences & normalised number of atoms"
for (j in histog)
{
  printf("%d\t%d\t%.2f\t%.6f \n", j*chk, histog[j], (histog[j]*avg*avg)/coverage, j*chk/avg); 
  if (max < histog[j])
  {
    max = histog[j];
  }
}
#print the average to another file
print sum/e" 0 0 "max+max/10 > "islandDistribution/average";
}' islandDistribution/histograms > islandDistribution/occ

#skip the 0 line and sort the output
grep -v ^0 islandDistribution/occ | sort -n > islandDistribution/occurrences

#plot the output
gnuplot $script_dir/plot_histogram.gp

#get avg time for current coverage
time=$(awk '{sum+=$2; e++}END{print sum/e}' islandDistribution/times)
echo "$coverage $time" > islandDistribution/execTime

#cleaning
rm islandDistribution/histograms islandDistribution/occ
