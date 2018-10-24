#!/usr/bin/env python3

import totalRates as tr
import matplotlib.pyplot as plt

d = readData(cwd=".")
coverages = d[3]
# plot it
for i in coverages[-101::10][1:]:
    index = getIndexFromCov(coverages, i)
    fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,3.5))
    fig.subplots_adjust(top=0.88,left=0.15,right=0.95,bottom=0.15)
    plotManyTotalRates(axarr, index, d)
    fig.savefig("TotalRates_{:d}_{:5f}.svg".format(index,coverages[index]))
    plt.close(fig)
