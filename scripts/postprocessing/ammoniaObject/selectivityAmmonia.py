#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import info as inf
import glob
import os
import numpy as np

def getOneProduction(fileNumber):
    name = "dataCatalysis"
    data = np.loadtxt(fname=name+"{:03d}".format(fileNumber)+".txt")

    production = np.zeros(3)
    try:
        production[0:3] = data[-1,21:24] # get last productions
    except IndexError: # probably the file is empty
        pass;
    return production

def getAvgProduction():
    p = inf.getInputParameters()
    files = glob.glob("dataCatalysis*")
    filesNumber = len(files)
    production = np.zeros(3)
    for i in range(0,filesNumber):
        try:
            production += getOneProduction(i)
        except FileNotFoundError:
            filesNumber -= 1 
    production = production / filesNumber
    return production

def getProduction(p,temperatures,workingPath):
    pressures = np.zeros(len(temperatures))
    production = np.zeros(shape=(len(temperatures),3))
    for i,t in enumerate(temperatures):
        print(t*1.0132499658281449)
        pressures[i] = t*1.0132499658281449
        os.chdir(workingPath)
        try:
            os.chdir(str(t)+"/results")
            runFolder = glob.glob("*/");
            runFolder.sort()
            os.chdir(runFolder[-1])
        except FileNotFoundError:
            continue
        production[i,:] = getAvgProduction()
    return pressures, production

def plotRefProduction(ax):
    data = np.loadtxt("refSelectivity.txt")
    labels=[r"$NO$",r"$N_2$"]
    for i in range(1,3):
        ax.plot(data[:,0]/1e10, data[:,i], "-", label=labels[i-1])
    ax.plot(data[:,0]/1e10, (data[:,1]+data[:,2]), "-",label=labels[0])

    # data = np.loadtxt("refSelectivityWrong.txt")
    # for i in range(1,3):
    #     ax.plot(data[:,0]/1e10, data[:,i], "x", label=labels[i-1])
    # ax.plot(data[:,0]/1e10, (data[:,1]+data[:,2]), "x",label=labels[0])

        
def plotProduction(x,production):
    markers=["o", "s","D","^","d","h","p"]
    cm = plt.get_cmap('tab20')
    fig, ax = plt.subplots(1, figsize=(5,4))
    fig.subplots_adjust(top=0.95,left=0.15, right=0.95)
    labels = [r"$NO$",r"$N_2$",r"$H_2O$"]
    lines = ["-",":"]
    for i in range(0,2):
        ax.plot(x, production[:,i]/(production[:,0]+production[:,1]), marker=markers[i],label=labels[i],color=cm(i), linestyle=lines[i%2])
    ax.legend(loc="best", prop={'size':6})
    #ax.set_yscale("log")
    ax.set_ylabel(r"$\theta$")
    #ax.set_xlabel(r"$1/k_BT$")
    ax.set_xlabel(r"O pressure (bar)")
    ax.set_ylim(0,1.1)
    plotRefProduction(ax)
    fig.savefig("avgProduction.png")
    
temperatures = inf.getTemperatures("float")
maxRanges = len(temperatures)
kb = 8.6173324e-5
p = inf.getInputParameters(glob.glob("*/output*")[0])
workingPath = os.getcwd()
pressures, production = getProduction(p,temperatures,workingPath)
os.chdir(workingPath)
plotProduction(pressures,production)
