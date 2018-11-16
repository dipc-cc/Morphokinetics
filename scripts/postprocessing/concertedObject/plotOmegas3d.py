#!/usr/bin/env python3

from mpl_toolkits.mplot3d import Axes3D
import numpy as np
import matplotlib.pyplot as plt
from matplotlib import cm
import Label
import os
import pdb

kb = 8.617332e-5
maxA = 206
minCov = 18
minLog = -3

def plot(x,y,z,a, log):
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    if log:
        zz = np.log(omegas[a])/np.log(10)
        z = np.ma.masked_where(zz<minLog, zz).filled(np.nan)#.compressed()
    else:
        z = omegas[a]
    x = x[minCov:,]
    y = y[minCov:,]
    z = z[minCov:,]

    ax.plot_wireframe(x, y, z, rcount=25, ccount=25, color="C"+str(a%10), label=label[a])
    ax.plot_surface(x, y, z, color="C"+str(a%10),alpha=0.2)
    
    ax.set_xlabel(r"coverage $\theta$")
    ax.set_ylabel(r"$1/k_BT$")
    ax.view_init(15, 15)
    ax.legend(loc="best", prop={'size':16})
    if log:
        ax.set_zlim(minLog,0.1)
        logN = "_log"
        ax.set_zlabel(r"$\log(\omega)$")
    else:
        ax.set_zlim(0,1.1)
        logN = ""
        ax.set_zlabel(r"$\omega$")
    ax.invert_xaxis()
    fig.savefig("plotOmegas/omegas3d"+logN+"_{:03d}".format(a)+".svg")

def delete(a,cov):
    try:
        for cov in range(0,127):
            os.remove("omegas/omegas_{:03d}".format(a)+"_{:03d}".format(cov)+".txt")
    except FileNotFoundError:
        pass    
# read data
temperatures = np.loadtxt("temperatures.txt")
coverages = np.loadtxt("coverages.txt")

#prepare x, y
x = np.zeros((len(coverages),len(temperatures)))
y = np.zeros((len(coverages),len(temperatures)))
omegas = np.zeros((maxA,len(coverages),len(temperatures)))

l = len(temperatures)

for i in range(0,len(coverages)):
    for j in range(0,len(temperatures)):
        x[i,j] = coverages[i]
        y[i,j] = 1/kb/temperatures[j]


for a in range(0,maxA):
    for cov in range(0,127):
        try:
            omegas[a,cov] = np.loadtxt("omegas/omegas_{:03d}".format(a)+"_{:03d}".format(cov)+".txt")
        except IOError:
            break
     
label = Label.Label().getLabels()
for a in range(0,maxA):
    if np.any(omegas[a] > 10**minLog):
        plot(x,y,omegas,a, True)
    else:
        delete(a,cov)


