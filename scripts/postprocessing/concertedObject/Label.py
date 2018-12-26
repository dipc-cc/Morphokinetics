class Label:

    def __init__(self):
        code = [ "[0,0]",
                 "[1,0]",
                 "[2,0]",
                 "[2,1]",
                 "[2,2]",
                 "[3,0]",
                 "[3,1]",
                 "[3,2]",
                 "[4,0]",
                 "[4,1]",
                 "[4,2]",
                 "[5,0]"]
        self.types = []
        for i in range(0,12):
            self.types.append(code[i]+r"")
        # detachs
        self.types.append(r"D_{1}'")
        self.types.append(r"D_{2,0}'")
        self.types.append(r"D_{2,1}'")
        self.types.append(r"D_{3,0}'")

    def getLabels(self, units="\epsilon"):
        labelAlfa = []
        for i in range(0,12):
            for j in range(0,16):
                labelAlfa.append(self.__getLabel(i,j,units))
        for i in range(0,9):
            labelAlfa.append(r"$"+units+r"^R_{I"+str(i)+"}$")
        for i in range(0,4):
            labelAlfa.append(r"$"+units+r"^R_{C"+str(i+1)+"}$")
        labelAlfa.append(r"Adsorption")
        return labelAlfa

    def __getLabel(self, i, j, units):
        label = r"$"+units+r"^R_{D" + self.types[i] + r"\rightarrow " + self.types[j] + r"}$"
        return label
