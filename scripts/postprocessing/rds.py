#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import multiplicitiesPlot as mp
import numpy as np

kb = 8.6173324e-5

def plotRds(temperatures,tempMavg,rates,ratios,omega,minAlfa,maxAlfa,labelAlfa):

    ratios[:,5] = 2*ratios[:,5] # correct O adsorption
    cm = plt.get_cmap('tab20')
    markers=["o", "s","D","^","d","h","p"]
    one = np.ones(len(temperatures))
    zero = np.zeros(len(temperatures))
    ext = "T"
    
    #####
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures, rates[-1,:,2])
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        if any(abs(omega[-1,:,i]) >= 1e-4):
            ax.plot(1/kb/temperatures, (tempMavg[-1,:,i]*ratios[:,i]),label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$E^M_\alpha$")
    ax.set_yscale("log")
    plt.savefig("rds"+ext+".pdf")#, bbox_inches='tight')

    ##### Relative error (epsilon)
    epsilon = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    #epsilon[:,:] = abs(one-((tempMavg[-1,:,:]*ratios[:,:])/rates[-1,:,2]))
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures,zero,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        epsilon[:,i] =  abs(one-((tempMavg[-1,:,i]*ratios[:,i])/rates[-1,:,2]))
        if any(abs(omega[-1,:,i]) >= 1e-8):
            ax.plot(1/kb/temperatures,epsilon[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$|1-\frac{M^R_\alpha k}{TOF}|$")
    ax.set_yscale("log")
    plt.savefig("rdsRelLog.pdf")
    ax.set_yscale("linear")
    ax.set_ylim(-0.2,2)
    plt.savefig("rdsRel.pdf")

    #### Dominance
    dominance = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    #dominance[:,:] = abs(one-((tempMavg[-1,:,:]*ratios[:,:])/rates[-1,:,2]))
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures,one,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        m = []; m.append(one); m.append(epsilon[:,i]);
        dominance[:,i] =  one-np.amin(m,axis=0)
        if any(abs(omega[-1,:,i]) >= 1e-8):
            ax.plot(1/kb/temperatures,dominance[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$1-\min(1,\epsilon)$")
    ax.set_yscale("log")
    plt.savefig("rdsDominanceLog.pdf")
    ax.set_yscale("linear")
    ax.set_ylim(-0.1,1.1)
    plt.savefig("rdsDominance.pdf")
    
    #### Sigma
    sumDominance = np.sum(dominance,axis=1)
    sigma = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    #ax.plot(1/kb/temperatures,zero,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        sigma[:,i] =  dominance[:,i]/sumDominance
        if any(abs(omega[-1,:,i]) >= 1e-8):
            ax.plot(1/kb/temperatures,sigma[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$1-\min(1,\epsilon)$")
    ax.set_yscale("log")
    plt.savefig("rdsSigmaLog.pdf")
    ax.set_yscale("linear")
    ax.set_ylim(-0.1,1.1)
    plt.savefig("rdsSigma.pdf")
    
    return sigma
