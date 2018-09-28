#!/usr/bin/env python3

import Concerted
import sys


c = Concerted.Concerted()
c.init(sys.argv)
c.read()
c.compute()
c.plotTotalRate()
c.plotMultiplicities()
c.plotResume()
