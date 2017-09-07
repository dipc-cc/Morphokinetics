import os
import info as inf
import glob
import numpy as np
import matplotlib.pyplot as plt
import shutil
import errno

def plotCoverages():
    covFiles = glob.glob("dataCatalysis0*.txt")
    covFiles.sort()
    f, axarr = plt.subplots(3, 4)
    # Fine-tune figure; hide x ticks for top plots and y ticks for right plots
    i = 0
    j = 0
    for t in covFiles:
        data = np.loadtxt(t)
        labels=[r"$CO^{br}$",r"$CO^{cus}$",r"$O^{br}$",r"$O^{cus}$"]
        if len(data) > 0:
            try:
                for k in range(1,5):
                    axarr[i,j].plot(data[:,0],data[:,k], label=labels[k-1])
                    axarr[i,j].set_ylim(-0.1,1.1)
                i += 1
                if i == 3:
                    i = 0
                    j += 1
            
            except IndexError:
                continue
    plt.setp([a.get_xticklabels() for a in axarr[0, :]], visible=False)
    plt.setp([a.get_xticklabels() for a in axarr[1, :]], visible=False)
    plt.setp([a.get_yticklabels() for a in axarr[:, 1]], visible=False)
    plt.setp([a.get_yticklabels() for a in axarr[:, 2]], visible=False)
    plt.setp([a.get_yticklabels() for a in axarr[:, 3]], visible=False)
    for k in range(1,5):
        axarr[i,j].plot(1, label=labels[k-1])
    axarr[i,j].legend(loc='best')
    f.savefig("coverages.png")

def mkdir_p(path):
    try:
        os.makedirs(path)
    except OSError as exc:  # Python >2.5
        if exc.errno == errno.EEXIST and os.path.isdir(path):
            pass
        else:
            raise

workingPath = os.getcwd()
x = []
y = []
try:
    iter = inf.getTemperatures()
except ValueError:
    iter = inf.getPressures()
mkdir_p("coverages")
for f in iter:
    print(f)
    try:
        os.chdir(str(f)+"/results")
        runFolder = glob.glob("*/");
        runFolder.sort()
        os.chdir(runFolder[-1])
    except FileNotFoundError:
        pass
    os.getcwd()
    tof = 0
    try:
        tof = plotCoverages()
    except ZeroDivisionError:
        tof = 0
    except IndexError:
        pass
    x.append(f)
    y.append(tof)
    shutil.copyfile("coverages.png", "../../../coverages/coverages"+str(f)+".png")
    os.chdir(workingPath)

