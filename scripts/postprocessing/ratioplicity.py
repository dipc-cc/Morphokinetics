import info as inf
import functions as fun
import os
import matplotlib.pyplot as plt
import numpy as np

def compute(i, j, mij, ratios, maxK):
    numerator = np.zeros(126)
    for coverage in range(0,126):
        numerator[coverage] = (mij[coverage,j,i] * ratios[j,i]) / (mij[coverage,i,j]*ratios[i,j])
    W = []
    div = [None] * maxK
    denominator = np.zeros(126*maxK).reshape(126,maxK)
    for k in range(0,maxK):
        for coverage in range(0,126):
            div = (mij[coverage,j,k] * ratios[j,k]) / (mij[coverage,k,j]*ratios[k,j])
            if not np.isnan(div) and not np.isinf(div):
                denominator[coverage,k] = denominator[coverage,k] + div
    W = numerator/np.sum(denominator, axis=1)
    return W
        
def ratioplicity(ax):
    d = inf.readAverages()
    p = inf.getInputParameters()
    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    x = cove
    maxI = 7
    maxJ = 7
    maxK = 4
    
    handles = []
    Malpha, Mij = inf.readInstantaneous(False)
    Mij = Mij.reshape(len(Mij),7,7)
    #mAlpha, trash = inf.readDiscrete()
    trash, Mij = inf.readPossibles()
    m = []
    Mij = Mij.reshape(len(Mij),7,7)
    for i in range(0,7):
        for j in range(0,7):
            Mij[:,i,j] = fun.timeDerivative(Mij[:,i,j], d.time)

    
    mij = np.zeros(len(Mij)*49).reshape(len(Mij),7,7)
    for i in range(0,4):
        m.append(Malpha[i]/d.negs[i])
        label=r"$m_"+str(i)+"}$"
        lg, = plt.loglog(x, Malpha[i]/d.negs[i], label=label);    handles.append(lg) # Around 6,2,2,0.1
        #lg, = plt.loglog(x, m[i]/(mAlpha[i]/Malpha[i]), label=str(i));    handles.append(lg)
    # for i in range(0,7):
    #     for j in range(0,7):
    #         label=r"$m_{"+str(i)+str(j)+"}$"
    #         lg, = plt.loglog(x, Mij[:,i,j]/d.negs[i], label=label);    handles.append(lg) # Around 6,2,2,0.1
            
        
    for i in range(0,maxI):
        for j in range(0,maxJ):
            #mij[:,i,j] = Mij[:,i,j]/(d.negs[i] + 1e-16)
            for coverage in range(0,len(Mij)):
                if d.negs[i][coverage] == 0:
                    mij[coverage,i,j] = 0
                else:
                    mij[coverage,i,j] = Mij[coverage,i,j]/d.negs[i][coverage]
            label=r"$m_{"+str(i)+str(j)+"}$"
            #lg, = plt.loglog(x, mij[:,i,j], ls=":", label=label);    handles.append(lg) # Around 6,2,2,0.1
                    
            #lg, = plt.loglog(x, mij[:,i,j], "o",label=r"$m_ij "+str(i));    handles.append(lg)

    #mij = fun.timeAverage(mij, d.time)
        
        

    ##########################################################################################
    ratios = p.getRatios().reshape(7,7)
    numerator = np.zeros(len(Mij))
    
    W = []
    i = 0
    j = 1
    print(np.shape(mij))
    print("kk",mij[:,0,:])
    
    W.append(compute(i, j, mij, ratios, maxK))

    i = 1
    j = 0
    W.append(compute(i, j, mij, ratios, maxK))

    i = 2
    j = 1
    W.append(compute(i, j, mij, ratios, maxK))

    i = 3
    j = 2
    W.append(compute(i, j, mij, ratios, maxK))
    ########################################################################################
    cm1 = plt.get_cmap("Set1")
    bbox_props = dict(boxstyle="round", fc="w", ec="1", alpha=0.7, pad=0.1)
    
    ax.loglog(x,np.ones(len(x)), color="black")
    markers=["x", "s","o","^","h","p", "d"]
    sum = d.negs[0]/cove/p.sizI/p.sizJ
    for k in range(0,4):
        label = r"$\theta_"+str(k)+r"\cdot 10^4$"
        ax.loglog(x, d.negs[k]/cove/p.sizI/p.sizJ, label=label, ms=3, lw=1, ls="-", color=cm1(k/8))
        index = np.where(d.negs[k] > 0)[0][2]
        ax.text(x[index],d.negs[k][index]/cove[index]/p.sizI/p.sizJ, r"$W_{"+str(k)+r"}$", color=cm1(k/8), bbox=bbox_props)
        sum += d.negs[k]/cove/p.sizI/p.sizJ
    #ax.loglog(x, sum, "x")
    for i in range(0,maxK):
        lg, = ax.loglog(x, W[i], ls="--", marker=markers[i], label="W temp"+str(i))
        handles.append(lg)
        #ax.text(x[index],d.negs[k][index]/cove[index], r"$W_{"+str(k)+r"^\nu}$", color=cm1(k/8), bbox=bbox_props)
    #W = np.array(W)
    #print(np.shape(W))
    #lg, = ax.loglog(x, np.sum(W, axis=0), "+", label="W sum"); handles.append(lg)
    
    plt.legend(handles=handles, loc="best")
    
fig = plt.figure(figsize=(15,15))
ax = fig.gca()
workingPath = os.getcwd()
for f in inf.getFluxes():
    os.chdir(workingPath)
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for t in inf.getTemperatures():
        print("\t",t)
        try:
            os.chdir(str(t)+"/results")
            fig.clf()
            ax = fig.gca()
            ratioplicity(ax)
            #ax.set_ylim(1e-4,1e1)
            #ax.set_yscale("linear")
            #ax.set_ylim(0,6)
            fig.savefig("ratioplicity"+str(t)+".pdf", bbox_inches='tight')
        except FileNotFoundError:
            pass            
        os.chdir(fPath)

ratios = p.getRatios().reshape(7,7)
mu = []                                         
for i in range(0,7):          
    for j in range(0,7):
        mu.append(Mij[:,i,j]/d.negs[i]*ratios[i,j])
mu = np.transpose(mu).reshape(len(Mij),7,7)

W = []
for k in range(0,4):
    W.append(d.negs[k]/cove/p.sizI/p.sizJ)

     
i = 3
for i in range(0,4):
    loglog(d.cove, W[i]*np.sum(mu[:,i,:], axis=1))
    sum = np.zeros(len(Mij))
    for j in range(0,4):
        sum = sum + W[j]*mu[:,j,i]
    loglog(d.cove, sum, "3")


figure(1001)
i = 0
sum = np.zeros(len(Mij))
for j in range(3,-1,-1):
    loglog(d.cove, W[i]*np.sum(mu[:,i,j:4], axis=1))
    #for j in range(0,4):
    sum = sum + W[j]*mu[:,j,i]
    loglog(d.cove, sum, "3")


    
trash, Mij = inf.readDiscrete()
Mij = Mij.reshape(len(Mij),7,7)
for i in range(0,7):
    for j in range(0,7):
        Mij[:,i,j] = fun.timeDerivative(Mij[:,i,j], d.time)
i = 0
sum = np.zeros(len(Mij))
sumkj = np.zeros(len(Mij))
sumki = np.zeros(len(Mij))
figure(1002)
sumki =  p.flux/(1-d.cove)/p.sizI/p.sizJ * np.sum(Mij[:,:,i], axis=1)
for j in range(3,-1,-1):
    sumkj = np.sum(Mij[:,:,j],axis=1)
    fL = W[i]*mu[:,i,j] + p.flux/(1-d.cove)/p.sizI/p.sizJ * sumkj
    loglog(d.cove, fL)
    sum = W[j]*mu[:,j,i]
    sumki= 0
    fR = sum + sumki
    loglog(d.cove, fR, "3")





    
trash, Mij = inf.readDiscrete()
Mij = Mij.reshape(len(Mij),7,7)
for i in range(0,7):
    for j in range(0,7):
        Mij[:,i,j] = fun.timeDerivative(Mij[:,i,j], d.time)
i = 0
sum = np.zeros(len(Mij))
sumkj = np.zeros(len(Mij))
sumki = np.zeros(len(Mij))
figure(1002)
sumki =  p.flux/(1-d.cove)/p.sizI/p.sizJ * np.sum(Mij[:,:,i], axis=1)
for j in range(3,-1,-1):
    sumkj = np.sum(Mij[:,:,j],axis=1)
    fL = W[i]*mu[:,i,j] + p.flux/(1-d.cove)/p.sizI/p.sizJ * sumkj
    loglog(d.cove, fL)
    sum = W[j]*mu[:,j,i]
    sumki= 0
    fR = sum + sumki
    loglog(d.cove, fR, "3")



    
trash, Mij = inf.readDiscrete()
Mij = Mij.reshape(len(Mij),7,7)
for i in range(0,7):
    for j in range(0,7):
        Mij[:,i,j] = fun.timeDerivative(Mij[:,i,j], d.time)
i = 3
sum = np.zeros(len(Mij))
sumkj = np.zeros(len(Mij))
sumki = np.zeros(len(Mij))
figure(1003)
sumki =  p.flux/(1-d.cove)/p.sizI/p.sizJ * np.sum(Mij[:,:,i], axis=1)
fL = 0
fR = 0
sumL = 1/d.cove * (fun.timeDerivative(d.negs[i],d.time))/p.sizI/p.sizJ
#sumL = fun.timeDerivative(W[i],d.time)
for j in range(3,-1,-1):
    #if i == j:
    #    continue
    sumkj =  p.flux/(1-d.cove)/p.sizI/p.sizJ * np.sum(Mij[:,:,j], axis=1)
    fL = fL + W[i]*mu[:,i,j]
    fR = fR + W[j]*mu[:,j,i]
fL += sumL
loglog(d.cove, fL)
loglog(d.cove, fR, "3")
fR = fR + sumki
loglog(d.cove, fR, "+")




i = 3
sum = np.zeros(len(Mij))
sumki = np.zeros(len(Mij))
figure(1004)
sumki =  p.flux/(1-d.cove)/p.sizI/p.sizJ * d.empt[i] #np.sum(Mij[:,:,i], axis=1)
fL = 0
fR = 0
sumL = 1/d.cove * (fun.timeDerivative(d.negs[i],d.time))/p.sizI/p.sizJ
#sumL = fun.timeDerivative(W[i],d.time)
for j in range(3,-1,-1):
    fL = fL + W[i]*mu[:,i,j]
    fR = fR + W[j]*mu[:,j,i]
fL += sumL
loglog(d.cove, fL)
loglog(d.cove, fR, "3")
fR = fR + sumki
loglog(d.cove, fR, "+")
