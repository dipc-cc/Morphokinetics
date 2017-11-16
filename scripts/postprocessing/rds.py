#!/usr/bin/env python3
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
import multiplicitiesPlot as mp
import numpy as np
import operator
import functions as fun

kb = 8.6173324e-5

def plotRds(temperatures,activationEnergyTOF,tempMavg,rates,ratios,omega,minAlfa,maxAlfa,labelAlfa,axarr,ratioEa,multiplicityEa):
    #ratios[:,5] = 2*ratios[:,5] # correct O adsorption
    cm = plt.get_cmap('tab20')
    markers=["o", "s","D","^","d","h","p"]
    one = np.ones(len(temperatures))
    zero = np.zeros(len(temperatures))
    ext = "T"
    minOmega = 1e-8

    #print(ratios[0,24]*tempMavg[-1,0,24],ratios[0,24]*tempMavg[-1,0,24]/rates[-1,0,2],rates[-1,0,2])
    #####
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        if any(abs(omega[-1,:,i]) >= minOmega):
            ax.plot(1/kb/temperatures, (tempMavg[-1,:,i]*ratios[:,i]),label=labelAlfa[a], ls="--", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.plot(1/kb/temperatures, rates[-1,:,2])
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$E^M_\alpha$")
    ax.set_yscale("log")
    ax.set_ylim(1e-7,1e2)
    plt.savefig("rds"+ext+".pdf")#, bbox_inches='tight')
    ##### Relative error (error3)
    error3 = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    #error3[:,:] = abs(one-((tempMavg[-1,:,:]*ratios[:,:])/rates[-1,:,2]))
    figR, ax = plt.subplots(1, figsize=(5,2))
    fig2, ax2 = plt.subplots(1,figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    fig2.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures,zero,ls="-",color="red")
    ax2.plot(1/kb/temperatures,activationEnergyTOF[-1,:],ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        # slopes = fun.timeDerivative(np.log(tempMavg[-1,:,i]*ratios[:,i]),1/kb/temperatures[::-1])
        #slopes = omega[-1,:,i]*(ratioEa[-1,:,i] - multiplicityEa[-1,:,i])
        slopes = (ratioEa[-1,:,i] - multiplicityEa[-1,:,i])
        #slopes = (tempMavg[-1,:,i]*ratios[:,i])
        error3[:,i] =  abs(one-(slopes/activationEnergyTOF[-1,:]))
        #error3[:,i] =  abs(one-(slopes/rates[-1,:,2]))
        if any(abs(omega[-1,:,i]) >= minOmega):
            ax.plot(1/kb/temperatures,error3[:,i],label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
            ax2.plot(1/kb/temperatures,slopes,label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    #ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$|1-\frac{E^{Mk}_{app}}{E^{TOF}_{app}}|$")
    #ax.set_ylabel(r"$E^{Mk}_{app}$")
    ax.set_yscale("log")
    figR.savefig("rdsError3Log.pdf")
    ax.set_yscale("linear")
    ax2.set_ylim(-1,3)
    ax.set_ylim(-0.2,2)
    figR.savefig("rdsError3.pdf")
    fig2.savefig("rdsApp2.pdf")
    ####
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures,activationEnergyTOF[-1,:],ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        if any(abs(omega[-1,:,i]) >= minOmega):
            ax.plot(1/kb/temperatures,slopes,label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    #ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$E^{Mk}_{app}$")
    ax.set_yscale("linear")
    ax.set_ylim(-1,3)
    plt.savefig("rdsApp.pdf")
                                                                                                   
    ##### Relative error (delta)
    delta = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    #delta[:,:] = abs(one-((tempMavg[-1,:,:]*ratios[:,:])/rates[-1,:,2]))
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures,zero,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        delta[:,i] =  abs(one-((tempMavg[-1,:,i]*ratios[:,i])/rates[-1,:,2]))
        if any(abs(omega[-1,:,i]) >= minOmega):
            ax.plot(1/kb/temperatures,delta[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$|1-\frac{M^R_\alpha k}{TOF}|$")
    ax.set_yscale("log")
    plt.savefig("rdsDeltaLog.pdf")
    ax.set_yscale("linear")
    ax.set_ylim(-0.2,2)
    plt.savefig("rdsDelta.pdf")

    #### Dominance
    dominance = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    #dominance[:,:] = abs(one-((tempMavg[-1,:,:]*ratios[:,:])/rates[-1,:,2]))
    #figR, ax = plt.subplots(1, figsize=(5,2))
    #figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    axarr.plot(1/kb/temperatures,one,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        m = []; m.append(one); m.append(delta[:,i]);
        dominance[:,i] =  one-np.amin(m,axis=0)
        if any(abs(omega[-1,:,i]) >= minOmega):
            axarr.plot(1/kb/temperatures,dominance[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    axarr.legend(loc="best", prop={'size':6})
    axarr.set_ylabel(r"$\sigma^{TOF}_\alpha$")
    axarr.set_yscale("log")
    #plt.savefig("rdsDominanceLog.pdf")
    axarr.set_yscale("linear")
    axarr.set_ylim(-0.1,1.1)
    #plt.savefig("rdsDominance.pdf")

    #### Similarity
    similarity = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    ax.plot(1/kb/temperatures,one,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        m = []; m.append(one); m.append(error3[:,i]);
        similarity[:,i] = 1/(1+error3[:,i])
        if any(abs(omega[-1,:,i]) >= minOmega):
            ax.plot(1/kb/temperatures,similarity[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$\varsigma = \frac{1}{1+\delta}$")
    ax.set_yscale("log")
    plt.savefig("rdsSimilarityLog.pdf")
    ax.set_yscale("linear")
    ax.set_ylim(-0.1,1.1)
    plt.savefig("rdsSimilarity.pdf")
    plt.close(figR)
    
    #### Sigma
    sumDominance = np.sum(dominance,axis=1)
    sigma = np.zeros(shape=(len(temperatures),maxAlfa-minAlfa))
    figR, ax = plt.subplots(1, figsize=(5,2))
    figR.subplots_adjust(top=0.95,left=0.15,right=0.95,bottom=0.12)
    #ax.plot(1/kb/temperatures,zero,ls="-",color="red")
    for i,a in enumerate(range(minAlfa,maxAlfa)):
        sigma[:,i] =  dominance[:,i]/sumDominance
        if any(abs(omega[-1,:,i]) >= minOmega):
            ax.plot(1/kb/temperatures,sigma[:,i],
                    label=labelAlfa[a], ls="", color=cm(abs((a%20)/20)),marker=markers[i%7], mec=mp.getMec(i), alpha=0.75)
    ax.legend(loc="best", prop={'size':6})
    ax.set_ylabel(r"$\sigma=\upsilon/\sum \upsilon$")
    ax.set_yscale("log")
    plt.savefig("rdsSigmaLog.pdf")
    ax.set_yscale("linear")
    ax.set_ylim(-0.1,1.1)
    plt.savefig("rdsSigma.pdf")

    #### new sigma
    newSigma = np.zeros(shape=(len(temperatures)))
    myMax = np.argmax(similarity,axis=1)
    print(np.shape(myMax),myMax)
#    for i in range(0,len(myMax)):
#        myMax[i] = 5
    return sigma, myMax
