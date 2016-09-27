#!/home/jalberdi004/software/gnuplot/5.0.3/bin/gnuplot

set term png
set output "islandDistribution.png"
plot "islandDistribution/occurrences" w lp, "islandDistribution/average" w vectors nohead