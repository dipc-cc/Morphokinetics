import numpy as np
from scipy.optimize import curve_fit
import operator

def power(x, a, b):
    """ a*x^b function """
    return a*x**b


def exp(x, a, b):
    """ ae^bx function """
    return a*np.exp(x*b)


def linear(x, a, b):
    return a+x*b


def theta(t, F):
    return 1 - np.exp(-F*t)


def thetaAverage(t, F):
    return 1-((1-np.exp(-F*t))/(F*t))


def fractalD(x):
    """Fractal dimension for x (= rtt/flux)"""
    minD = 1.66
    maxRatio = 5e8
    minRatio = 3e7
    y = []
    for i in range(len(x)):
        if (x[i] <= minRatio):
            y.append(minD)
        else:
            if (x[i] > maxRatio):
                y.append(2.0)
            else:
                #y.append(minD+(2-minD)/np.log(5e8/3e7)*np.log(x[i]))
                y.append(minD+(2-minD)/(maxRatio-minRatio)*(x[i]-minRatio))
    return np.array(y)


def fractDTemperature(temp):
    maxD = 2.00
    minD = 1.75
    maxTemp = 62
    minTemp = 55
    y = []
    for i in range(len(temp)):
        if (temp[i] <= minTemp):
            y.append(maxD)
        else:
            if (temp[i] > maxTemp):
                y.append(minD)
            else:
                y.append(maxD-(maxD-minD)/(maxTemp-minTemp)*(temp[i]-minTemp))#    minD+(2-minD)/(maxTemp-minTemp)*(temp[i]-minTemp))
    return y
    

def fractalDimension(x):
    """returns fractal dimension, based on a fit of a study of single
flake simulations. """
    a=0.27
    b=1.73
    c=19
    sigma=2
    x0=np.exp(c)
    d=b+a*(x**sigma)/(x**sigma+x0**sigma)
    return np.array(d)


def readFractalD(flux):
    return np.array(mk.readFractalD(flux)).astype(float)


def shapeFactor(x):
    """returns shape factor, based on a fit of a study of single flake
simulations. """
    a=5.5
    b=3.5
    c=18
    sigma=1
    x0=np.exp(c)
    f=b+a*(x**sigma)/(x**sigma+x0**sigma)
    max = 7.3
    f=[max-(i-max)  if i > max else i for i in f]
    return np.array(f)


def err(x, a, b, c, sigma):
    return a*scipy.special.erf(sigma*(x-c))+b


def ourErr(x, a, b, c, sigma):
    return a*(np.exp(sigma*(x-c))/(1+np.exp(sigma*(x-c))))+b


def fit(x, y, initI, finishI):
    indexes = np.array(range(initI,finishI))
    x1 = x[indexes]
    y1 = y[indexes]
    popt = curve_fit(exp, x1, y1, p0=[1e10,-0.10])
    a = popt[0][0]
    b = popt[0][1]
    return list([a,b])


def linearFit(x, y, initI, finishI):
    """ linear fit of an exponential function """
    indexes = np.array(range(initI,finishI))
    x1 = x[indexes]
    y1 = np.log(y[indexes])
    try:
        popt = curve_fit(linear, x1, y1)#, p0=[1e10,-0.10])
        a = popt[0][0]
        b = popt[0][1]
    except ValueError:
        a = 0
        b = 0
    return list([a,b])

def timeAverage(v, t):
    vAvg = np.array(v)
    partialSum = v[0]*t[0]
    vAvg[0] = partialSum / t[0]
    for i in range(1,len(v)):
        partialSum = partialSum + v[i]*(t[i] - t[i-1])
        vAvg[i] = partialSum / t[i]
    return vAvg


def timeDerivative(x, t):
    return np.gradient(x)/np.gradient(t)


def getOnlyAscending(vector):
    max_index, max_value = max(enumerate(vector), key=operator.itemgetter(1))
    vector[max_index:] = max_value
    return vector
