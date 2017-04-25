import info as inf
import os
import matplotlib.pyplot as plt

def plot():
    p = inf.getInputParameters()
    d = inf.readAverages()
    
    cove = d.getRecomputedCoverage()/p.sizI/p.sizJ
    x = cove
    fig, ax = plt.subplots(1, 1, sharey=True,figsize=(6,5))
    alpha=0.5
    Na = d.cove*p.sizI*p.sizJ
    diff = 1/(4*Na) * d.diff / d.time
    hops = 1/(4*Na) * d.hops / d.time
    diffCm = 1/(4*Na) * d.cmDf / d.time#/Na
    f = Na / d.negS**2
    y = diffCm * f
    ax.plot(x, hops, "s", alpha=alpha)
    ax.plot(x, diff, "x", alpha=alpha)
    ax.plot(x, diffCm, "o", alpha=alpha)
    ax.plot(x, y, "d", alpha=alpha)
    ax.set_yscale("log")
    ax.set_xscale("log")
    fig.savefig("../../../cmDiff"+str(p.temp)+".png")

workingPath = os.getcwd()
fluxes = inf.getFluxes()
print(fluxes)
for f in fluxes:
    os.chdir(workingPath)
    print(f)
    os.chdir(f)
    fPath = os.getcwd()
    for t in inf.getTemperatures():
        os.chdir(fPath)
        try:
            os.chdir(str(t)+"/results")
            print(t)
            plot()
        except FileNotFoundError:
            pass
        
