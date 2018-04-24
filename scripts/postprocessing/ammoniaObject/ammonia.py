#!/usr/bin/env python3

import Ammonia
import sys

a = Ammonia.AmmoniaCatalysis()
a.init(sys.argv)
a.read()
a.compute()
a.plotTotalRate()
a.plotMultiplicities()
a.plotResume()
