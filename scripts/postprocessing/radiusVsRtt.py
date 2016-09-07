#
# Compares in a graph the average island size (or its square root)
# growth with r_tt (rate to do a transition from terrace to terrace).
#
# Author: J. Alberdi-Rodriguez

import os
import numpy as np
import matplotlib.pyplot as plt
import morphokinetics as mk

yPower=0.88
xPower=1/3

#plt.title("Average island growth")
plt.grid(True)
plt.xlabel(r"$\frac{{ r_{{tt}} }} {{ F^{{ {} }} }}$".format(xPower))
plt.ylabel(r'Average radius growth rate/flux $\frac{{ \sqrt{{ \dot{{r}} }} }}{{F^{{ {} }} }} }}$'.format(yPower))

workingPath = os.getcwd()
results=[]
for i in range(-6,5):
    folder="flux3.5e"+str(i)
    flux=float("3.5e"+str(i))
    print(folder)
    try:
        os.chdir(folder)
        results.append(mk.getIslandDistribution())
    except OSError:
        print ("error changing to {}".format(folder))
        a=0 #do nothing
    os.chdir(workingPath)
    plt.loglog(np.array(mk.getAllRtt())/flux**xPower, np.array(results[-1][0])/(flux**yPower), label=folder)
    plt.legend(loc='upper left',prop={'size':6})
    plt.savefig("radiusVsRtt.png")
    
plt.close()


print("Good bye!")
          
