#!/usr/bin/env python3

import totalRates as tr
import matplotlib.pyplot as plt
import numpy as np
import Info
import os
import pdb

cwd = os.getcwd()
cuNiPwd = "../CuNi"
niCuPwd = "../NiCu"
d1 = tr.readData(cwd=".")
d2 = tr.readData(cwd="../CuNi")
coverages = d1[3]

#read ref file
inf = Info.Info()
inf.setRefFile()
inf.setParams()
latSize = inf.sizI * int(inf.sizJ/np.sqrt(3)*2)

# plot it
for i in coverages[-101::10][1:]:
    index = Info.getIndexFromCov(coverages, i)
    fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,3.5))
    fig.subplots_adjust(top=0.88,left=0.15,right=0.95,bottom=0.15)
    os.chdir(niCuPwd)
    tr.plotManyTotalRates(axarr, index, d1, latSize)
    os.chdir(cuNiPwd)
    tr.plotManyTotalRates(axarr, index, d2, latSize, annotate=False)
    fig.savefig("TotalRatesBoth_{:d}_{:5f}.svg".format(index,coverages[index]))
    plt.close(fig)
