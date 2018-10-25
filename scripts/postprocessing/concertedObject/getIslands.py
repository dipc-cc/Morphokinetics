#!/usr/bin/env python3

import islands as isl
import os
import pdb
import matplotlib.pyplot as plt


def plot(ax, islands, system):
    cm = plt.get_cmap('tab20c')
    
    if system == "CuNi":
        color = [cm(0/20),cm(1/20),cm(2/20)]
        marker = ["o", "2", "^"]
        label = "Cu/Ni"
    else:
        color = [cm(4/20),cm(5/20),cm(6/20)]
        marker = ["d", "+", "<"]
        label = "Ni/Cu"
    ax.plot(islands[:,0], islands[:,1], label=label+" islands", color=color[0], marker=marker[0])
    
cwd = os.getcwd()
cuNiPwd = "../CuNi"
niCuPwd = "../NiCu"

#read islands for both systems

islandsNiCu = isl.getIslandsAt10(niCuPwd)
os.chdir(cwd)
islandsCuNi = isl.getIslandsAt10(cuNiPwd)
os.chdir(cwd)

fig, axarr = plt.subplots(1, 1, sharey=True, figsize=(5,2.5))
fig.subplots_adjust(top=0.99,left=0.15,right=0.9,bottom=0.15)
plot(axarr, islandsCuNi, "CuNi")
plot(axarr, islandsNiCu, "NiCu")

axarr.set_xscale("log")
axarr.set_yscale("log")
axarr.set_xlabel("Temperature (K)")
axarr.set_ylabel(r"Number of islands at $0.1\theta$")
axarr.legend(loc="best", prop={'size':10})
fig.savefig("IslandsBoth_{:d}_{:5f}.svg".format(36,0.1))
plt.close(fig)
