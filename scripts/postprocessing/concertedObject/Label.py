class Label:

    def __init__(self):
        code = ["0",
                 "1",
                 "2,0",
                 "2,1",
                 "2,2",
                 "3,0",
                 "3,1",
                 "3,2",
                 "4,0",
                 "4,1",
                 "4,2",
                 "5"]
        self.types = []
        for i in range(0,12):
            self.types.append(r"D_{"+code[i]+r"}")
        # detachs
        self.types.append(r"D_{1}'")
        self.types.append(r"D_{2,0}'")
        self.types.append(r"D_{2,1}'")
        self.types.append(r"D_{3,0}'")

    def getLabels(self):
        labelAlfa = []
        for i in range(0,12):
            for j in range(0,16):
                labelAlfa.append(self.__getLabel(i,j))
        for i in range(0,9):
            labelAlfa.append(r"$I_{"+str(i)+"}$")
        for i in range(0,4):
            labelAlfa.append(r"$M_{"+str(i+1)+"}$")
        return labelAlfa

    def __getLabel(self, i, j):
        label = r"$" + self.types[i] + r"\rightarrow " + self.types[j] + r"$"
        return label
