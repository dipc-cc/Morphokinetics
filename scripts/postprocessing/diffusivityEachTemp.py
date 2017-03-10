#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=-1 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data0.txt
import info as inf
import numpy as np
import matplotlib.pyplot as plt
import glob
import os
import functions as fun


def diffusivityDistance(index):
    p = inf.getInputParameters()
    allData = []

    filesN = glob.glob("data[0-9]*.txt")
    for i in range(0,len(filesN)):
        fileName = "data"+str(i)+".txt"
        allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

    cove = np.mean([i[:,0]  for i in allData], axis=0)
    time = np.mean([i[:,1]  for i in allData], axis=0)
    isld = np.mean([i[:,3]  for i in allData], axis=0)
    Ra   = np.mean([i[:,4]  for i in allData], axis=0)
    Rh   = np.mean([i[:,5]  for i in allData], axis=0)
    even = np.mean([i[:,7]  for i in allData], axis=0)
    hops = np.exp(np.mean(np.log([i[:,15] for i in allData]), axis=0))
    diff = np.mean([i[:,12] for i in allData], axis=0)
    neg = []
    neg.append(np.mean([i[:,16] for i in allData], axis=0))
    neg.append(np.mean([i[:,17] for i in allData], axis=0))
    neg.append(np.mean([i[:,18] for i in allData], axis=0))
    neg.append(np.mean([i[:,19] for i in allData], axis=0))
    if p.maxN == 6:
        neg.append(np.mean([i[:,20] for i in allData], axis=0))
        neg.append(np.mean([i[:,21] for i in allData], axis=0))
        neg.append(np.mean([i[:,22] for i in allData], axis=0))

    cove = np.sum(neg[:],axis=0)/p.sizI/p.sizJ

    ratios = 0
    if p.calc == "AgUc":
        ratios = inf.getRatio(p.temp, inf.getHexagonalEnergies())
    if p.calc == "basic":
        if p.rLib == "version2":
            ratios = inf.getRatio(p.temp, inf.getBasic2Energies())
        else:
            ratios = inf.getRatio(p.temp, inf.getBasicEnergies())
    Na = cove * p.sizI * p.sizJ

    plt.clf()
    x = list(range(0,len(time)))
    x = cove
    cm = plt.get_cmap("Accent")
    alpha = 0.5
    mew = 0
    diff = fun.timeDerivative(diff, time)/(4*Na)
    lgR, = plt.loglog(x, diff, label=r"$\frac{1}{2dN_a} \; \frac{d(R^2)}{dt}$",
               marker="s", ls="", mew=mew, markerfacecolor=cm(0/8), ms=8, alpha=alpha)
    hops = fun.timeDerivative(hops, time)/(4*Na)
    lgN, = plt.loglog(x, hops, label=r"$\frac{l^2}{2dN_a} \; \frac{d(N_h)}{dt}$",
               marker="p", ls="", mew=mew, markerfacecolor=cm(7/8), ms=7, alpha=alpha)

    lgRh, = plt.loglog(x, Rh/(4*Na), label=r"$\frac{l^2}{2dN_a} R_{h} $",
               marker="o", ls="", mew=mew, markerfacecolor=cm(1/8), ms=5.5, alpha=alpha)
               
    #coverages
    cm1 = plt.get_cmap("Set1")

    handles = []
    lg, = plt.loglog(x, cove, label=r"$\theta$", color=cm1(1/9))
    handles.append(lg)
    for k in range(0,7):
        if k < 4:
            label=r"${n_"+str(k)+"}$"
            lg, = plt.loglog(x, neg[k]/p.sizI/p.sizJ, label=label, color=cm1((k+2)/9))
            handles.append(lg)
 

    hopsCalc0 = (6 * neg[0] *ratios[0])/(4*Na)
    lgC0, = plt.loglog(x, hopsCalc0, label="hops calc0",
                       marker="", ls=":", mew=mew, color=cm(8/8), ms=4, alpha=1)
    if p.calc == "AgUc":
        hopsCalc = (6 * neg[0]*ratios[0]+2*neg[1]*ratios[8]+2*neg[2]*ratios[15]+0.1*neg[3]*ratios[24])/(4*Na)
        lgC, = plt.loglog(x, hopsCalc, label="hops calc",
                   marker="*", ls="", mew=mew, markerfacecolor=cm(5/8), ms=5, alpha=alpha)
        handles = [lgR, lgN, lgRh, lgC, lgC0] + handles 
    else:
        handles = [lgR, lgN, lgRh, lgC0] + handles
    plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)
    plt.legend(handles=handles, numpoints=1, prop={'size':8}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
    plt.grid()
    plt.title("flux: {:.1e} temperature: {:d}".format(p.flux, int(p.temp)))
    plt.savefig("../../../plot"+str(p.flux)+str(p.temp)+".png")


##########################################################
##########           Main function   #####################
##########################################################

workingPath = os.getcwd()
fluxes = inf.getFluxes()
for f in fluxes:
    temperaturesPlot = []
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for i,t in enumerate(inf.getTemperatures()):
        try:
            os.chdir(str(t)+"/results")
            print("\t",t)
            inf.splitDataFiles()
            diffusivityDistance(i)
        except FileNotFoundError:
            pass
        os.chdir(fPath)
    os.chdir(workingPath)
plt.legend()

