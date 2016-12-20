# It process output file of morphokinetics
# "dataEvery1percentAndNucleation", it removes islands histograms and
# deletes the first line. Thus, it is possible to read it with the
# function getFractalDimension of morphokineticsLow.py. More precisely
# with numpy: matrix=np.loadtxt(fname="matrix.txt", delimiter="\t")

# fluxes
for i in $(ls -d */);
do
    echo $i;
    cd $i;
    # temperatures
    for j in $(ls -d */);
    do
	echo $j;
	if [ -d $j/results/ ]
	then
	    cd $j/results/;
	    ls;
	    grep -v histo dataEvery1percentAndNucleation.txt > matrix.txt;
	    sed -i -e "s/\[/\#\[/" matrix.txt; # comment second line
	    sed -i '3d' matrix.txt # remove 3rd line
	    sed -i -e "s/\ /\t/g" matrix.txt # replace spaces with tabs
	    sed -i -e "s/\t$/\n/" matrix.txt # remove last tab
	    cd ../..;
	fi
    done;
    cd ..;
done

