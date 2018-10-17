#!/usr/bin/env python3

import operator
import os
import glob
import numpy as np
import matplotlib.pyplot as plt
import pdb
import info as inf
import functions as fun


kb = 8.6173324e-5

def plotAndAverage(doPlot,x,y,i,t):
    #pdb.set_trace()
    maxIndex, maxValue = max(enumerate(y[:,0]+y[:,1]), key=operator.itemgetter(1))
    agDistance = 2.892 #A
    agUm = agDistance * 1e-4
    totalArea = agUm * 200 * agUm *100
    agArea = agUm * 5 * agUm * 2

    for a in range(0,4):
        y[:,a] *= agArea # scale
    
    lastIndex = int(len(y[:,0])*0.8)
    
    aveMA = np.mean(y[maxIndex:lastIndex,2])
    aveMB = np.mean(y[maxIndex:lastIndex,3])
    if doPlot:
        fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,4))
        fig.subplots_adjust(top=0.85, bottom=0.15, left=0.15, right=0.95, hspace=0.25, wspace=0.35)
        ax.plot(x,y[:,0]+y[:,1], label=r"$\theta$", ls="--")
        ax.plot(x,y[:,0], label=r"$\theta_\alpha$")
        ax.plot(x,y[:,1], label=r"$\theta_\beta$")
        ax.plot(x,y[:,2], label=r"$\theta^M_\alpha$", ls="--")
        ax.plot(x,y[:,3], label=r"$\theta^M_\beta$", ls="--")
        ax.plot(x,y[:,0]-y[:,2], label=r"$\theta^I_\alpha$", ls=":")
        ax.plot(x,y[:,1]-y[:,3], label=r"$\theta^I_\beta$", ls=":")
        ax.plot(x,np.ones(len(x))*aveMA)
        ax.plot(x,np.ones(len(x))*aveMB)
        #ax.set_yscale("log")
        #ax.set_ylim(0,1e-5)
        ax.legend(loc="best")
        fig.savefig("plot"+str(t)+"_"+str(i)+".svg")
        plt.close(fig)

    # diff
    last = np.argmax(x)
    e = int(last/100) # e = every
    if e == 0:
        e = 1
    #repeat maxIndex
    maxIndexO = maxIndex
    maxIndex, maxValue = max(enumerate(y[::e,0]+y[::e,1]), key=operator.itemgetter(1))
    maxIndex+=1
    linA = np.gradient(y[:lastIndex:e,0])/np.gradient(x[:lastIndex:e])
    aveA = np.mean(linA[maxIndex:])
    linB = np.gradient(y[:lastIndex:e,1])/np.gradient(x[:lastIndex:e])
    aveB = np.mean(linB[maxIndex:])
    if doPlot:
        fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,4))
        fig.subplots_adjust(top=0.85, bottom=0.15, left=0.20, right=0.95, hspace=0.25, wspace=0.35)
        #ax.plot(x[::e],np.gradient(y[::e,1]-y[::e,0])/np.gradient(x[::e]))
        ax.plot(x[:lastIndex:e][maxIndex:],linA[maxIndex:], label=r"$v_\alpha$")
        ax.plot(x[maxIndex::e], np.ones(len(x[maxIndex::e]))*aveA)
        ax.plot(x[:lastIndex:e][maxIndex:],linB[maxIndex:], label=r"$v_\beta$")
        ax.plot(x[maxIndex::e], np.ones(len(x[maxIndex::e]))*aveB)
        ax.plot(x[::e],np.gradient(y[::e,2])/np.gradient(x[::e]), label=r"$v^M_\alpha$", ls="--")
        ax.plot(x[::e],np.gradient(y[::e,3])/np.gradient(x[::e]), label=r"$v^M_\beta$", ls="--")
        #ax.set_ylim(-0.2,0.2)
        #ax.set_yscale("log")
        ax.set_ylabel(r"$\mu m^2/s$")
        ax.legend(loc="best")
        fig.savefig("gradient"+str(t)+"_"+str(i)+".svg")
        plt.close(fig)

    return [aveA, aveB, aveMA, aveMB]

def getTransitions(t):
    files = glob.glob("dataBda*.txt")
    coverages = np.zeros(6) # Coverage alpha, coverage beta, monomers a, monomers b, island a, island b

    average = np.zeros(4) # AVERAGE loose of alpha, gain of beta, coverage alpha, coverage beta
    for i,f in enumerate(sorted(files)[:-1]):
        data = np.loadtxt(f)
        l = plotAndAverage(True,data[:,1], data[:,2:6],i,t)
        for i in range(0,4):
            average[i] += l[i]

    for i in range(0,4):
        average[i] /= len(files)-1

    print(t, average)
    return average

def plotTotalTransitionRate(x,y, title):
    fig, ax = plt.subplots(1, 1, sharey=True, figsize=(5,4))
    fig.subplots_adjust(top=0.85, bottom=0.15, left=0.20, right=0.95, hspace=0.25, wspace=0.35)
    ax.plot(x,y,".-", label="data")
    [a,b] = fun.linearFit(x,y,0,len(x))
    ax.plot(x,fun.exp(x,a,b), label=r"$f(x) = "+str(a)+r"x^{"+str(b)+r"}$")
    ax.set_title(title)
    ax.set_xlabel(r"$1/k_BT$")
    ax.set_yscale("log")
    ax.legend(loc="best")
    fig.savefig(title+".svg")
    plt.close(fig)

def readData():
    data = np.loadtxt("cachedTransitions.txt")
    return data

def writeData(x,y1, y2):
    np.savetxt("cachedTransitions.txt", np.stack((x,y1,y2),1))

    
workingPath = os.getcwd()
x = []
yTransitions = []
yAlphaMonomers = []
temperatures = True

try:
    data = readData()
    x = data[:,0]
    yTransitions = data[:,1]
    yAlphaMonomers = data[:,2]

except IOError:
    try:
        iterOver = inf.getTemperatures()
    except ValueError:
        iterOver = inf.getPressures()
    
    for f in iterOver:
        if f< -380:
            continue
        print(f)
        try:
            os.chdir(str(f)+"/results")
            runFolder = glob.glob("*/");
            runFolder.sort()
            os.chdir(runFolder[-1])
        except FileNotFoundError:
            pass
    
        average = getTransitions(f)
        x.append(1/kb/f)
        yTransitions.append(average[1])
        yAlphaMonomers.append(average[2])
    
        os.chdir(workingPath)
    writeData(x,yTransitions,yAlphaMonomers)
plotTotalTransitionRate(x,yTransitions, r"alpha to beta transition rate")
plotTotalTransitionRate(x,yAlphaMonomers, r"alpha monomers M")

for i in range(1,len(yTransitions)-1):
    print(i,i+1)
    try:
        plotTotalTransitionRate(x[i:i+2],yTransitions[i:i+2], "t"+str(i))
        plotTotalTransitionRate(x[i:i+2],yAlphaMonomers[i:i+2], "m"+str(i))
    except RuntimeError:
        pass

#plotTotalTransitionRate(x[4:-1],yTransitions[4:-1], "2t")
#plotTotalTransitionRate(x[4:-1],yAlphaMonomers[4:-1], "2m")
