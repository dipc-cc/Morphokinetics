#grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=0 '{if ($1<prev) {n++}prev=$1;} {print > "data"n".txt"} END{print n}'
# sed -i '1d' data1.txt

### THIS SHOULD BE A METHOD
# PARAMETERS: number of columns
#             L1
#             L2
import numpy as np
import matplotlib.pyplot as plt
import glob
import re
import os

def thetaFunc(t):
    F = 5e6
    return 1 - np.exp(-F*t)

# split files
os.system("grep -v histo dataEvery1percentAndNucleation.txt | grep -v Ae | awk -v n=0 '{if ($1<prev) {n++}prev=$1;} {print > \"data\"n\".txt\"} END{print n}'")
os.system("sed -i '1d' data1.txt")

# get r_tt
fileName = glob.glob("../output*")[0]
f = open(fileName)
hit = False
for line in f:
    if hit:
        r_tt = float(re.split(' ', line)[0])
        break
    if re.match("These", line):
        hit = True
print(r_tt)
allData = [] # np.array()

filesN = glob.glob("data[1-9]*.txt")
for i in range(1,len(filesN)+1):
    fileName = "data"+str(i)+".txt"
    print(fileName)
    allData.append(np.loadtxt(fname=fileName, delimiter="\t"))

L1 = 400
L2 = 462

cove = np.mean([i[:,0]  for i in allData], axis=0)
time = np.mean([i[:,1]  for i in allData], axis=0)
isld = np.mean([i[:,3]  for i in allData], axis=0)
depo = np.mean([i[:,4]  for i in allData], axis=0)
prob = np.mean([i[:,5]  for i in allData], axis=0)
even = np.mean([i[:,7]  for i in allData], axis=0)
hops = np.mean([i[:,15] for i in allData], axis=0)
diff = np.mean([i[:,12] for i in allData], axis=0)
neg0 = np.mean([i[:,16] for i in allData], axis=0)
neg1 = np.mean([i[:,17] for i in allData], axis=0)
neg2 = np.mean([i[:,18] for i in allData], axis=0)
neg3 = np.mean([i[:,19] for i in allData], axis=0)
neg4 = np.mean([i[:,20] for i in allData], axis=0) + \
       np.mean([i[:,21] for i in allData], axis=0) + \
       np.mean([i[:,22] for i in allData], axis=0)
hops = even
parti = np.mean([i[:,0]*L1*L2/100 for i in allData], axis=0)

# Plot 2
plt.clf()
#plt.figure(num=None, figsize=(8,6))
plt.loglog(time, diff/L1/L2, "s", color="black", label=r"$R^2 (\times \frac{1}{N_1 N_2})$", markerfacecolor="None")
plt.loglog(time, hops/L1/L2, "*", markeredgecolor="gray", label=r"$N_h l^2 (\times \frac{1}{N_1 N_2})$", markerfacecolor="None")
plt.loglog(time, diff/hops, ".", color="darkblue", label=r"$\frac{R^2}{N_h l^2}$", markerfacecolor="None")
plt.loglog(time, diff/time/L1/L2/r_tt, "o", markeredgecolor="red", label=r"$\frac{R^2}{t} (\times \frac{1}{N_1 N_2 r_{tt}})$", markerfacecolor="None")
plt.loglog(time, hops/time/L1/L2/r_tt, "x", markeredgecolor="lightpink", label=r"$\frac{N_h l^2}{t} (\times \frac{1}{N_1 N_2 r_{tt}})$", markerfacecolor="None")
plt.loglog(time, diff/(parti*time*r_tt), ">", markeredgecolor="lightblue", label=r"$\frac{R^2}{Nt} (\times \frac{1}{r_{tt}})$", markerfacecolor="None")
plt.loglog(time, hops/(parti*time*r_tt), "+", markeredgecolor="lightskyblue", label=r"$\frac{N_h l^2}{Nt} (\times \frac{1}{r_{tt}})$", markerfacecolor="None")
#coverages
plt.loglog(time, neg0/L1/L2, label=r"$\theta_0$")
plt.loglog(time, neg1/L1/L2, label=r"$\theta_1$")
plt.loglog(time, neg2/L1/L2, label=r"$\theta_2$")
plt.loglog(time, neg3/L1/L2, label=r"$\theta_3$")
plt.loglog(time, neg4/L1/L2, label=r"$\theta_{4+}$")
plt.loglog(time, isld/L1/L2, label="number of islands")

plt.loglog(time, thetaFunc(time), label=r"$1-e^{-Ft}$")
plt.loglog(time, cove, ".", color="orange", label=r"$\theta$", markerfacecolor="None")
plt.subplots_adjust(left=0.12, bottom=0.1, right=0.7, top=0.9, wspace=0.2, hspace=0.2)
plt.legend(numpoints=1, prop={'size':12}, bbox_to_anchor=(1.05, 1), loc=2, borderaxespad=0.)
plt.grid()
plt.savefig("rembrisPlot.png")

