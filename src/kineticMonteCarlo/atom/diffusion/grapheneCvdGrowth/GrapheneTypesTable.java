
package kineticMonteCarlo.atom.diffusion.grapheneCvdGrowth;


public class GrapheneTypesTable {
//oooooooooooooooooooooooooooo
/*  

 [1º neighbors amount][2º neighbors amount][ 3º neighbors amount]=atom type;
  
//Types: 0 terrace, 2 corner 2 zigzag edge,3 armchair edge, 4 zigzag__with_extra, 5 sick, 6 kinks, 7 bulk
//[1º neighbors amount][2º neighbors amount][ 3º neighbors amount]: local atom configuration
*/
//oooooooooooooooooooooooooooo
 
 private final byte[][][] table;
  
 
 public byte getType(int neigh1st,int neigh2nd, int neigh3rd) {
     
     return table[neigh1st][neigh2nd][neigh3rd];}
 
public GrapheneTypesTable(){
    
table=new byte[4][7][4];

table[0][0][0]=0;
table[0][0][1]=0;
table[0][0][2]=0;
table[0][0][3]=0;

table[0][1][0]=0;
table[0][1][1]=0;
table[0][1][2]=0;
table[0][1][3]=0;

table[0][2][0]=0;
table[0][2][1]=0;
table[0][2][2]=0;
table[0][2][3]=0;

table[0][3][0]=0;
table[0][3][1]=0;
table[0][3][2]=0;
table[0][3][3]=0;


table[0][4][0]=0;
table[0][4][1]=0;
table[0][4][2]=0;
table[0][4][3]=0;

table[0][5][0]=0;
table[0][5][1]=0;
table[0][5][2]=0;
table[0][5][3]=0;


table[0][6][0]=0;
table[0][6][1]=0;
table[0][6][2]=0;
table[0][6][3]=0;


table[1][0][0]=1;
table[1][0][1]=1;
table[1][0][2]=1;
table[1][0][3]=1;

table[1][1][0]=1;
table[1][1][1]=1;
table[1][1][2]=1;
table[1][1][3]=1;

table[1][2][0]=1;
table[1][2][1]=1;
table[1][2][2]=2;  // zigzag adatom
table[1][2][3]=5;

table[1][3][0]=5;
table[1][3][1]=3;  // armchair adatom
table[1][3][2]=4;
table[1][3][3]=5;


table[1][4][0]=5;
table[1][4][1]=5;
table[1][4][2]=5;
table[1][4][3]=5;

table[1][5][0]=5;
table[1][5][1]=5;
table[1][5][2]=5;
table[1][5][3]=5;


table[1][6][0]=5;
table[1][6][1]=5;
table[1][6][2]=5;
table[1][6][3]=5;

table[2][0][0]=6;
table[2][0][1]=6;
table[2][0][2]=6;
table[2][0][3]=6;

table[2][1][0]=6;
table[2][1][1]=6;
table[2][1][2]=6;
table[2][1][3]=6;

table[2][2][0]=6;
table[2][2][1]=6;
table[2][2][2]=6;
table[2][2][3]=6;

table[2][3][0]=6;
table[2][3][1]=6;
table[2][3][2]=6;
table[2][3][3]=6;


table[2][4][0]=6;
table[2][4][1]=6;
table[2][4][2]=6;
table[2][4][3]=6;

table[2][5][0]=6;
table[2][5][1]=6;
table[2][5][2]=6;
table[2][5][3]=6;


table[2][6][0]=6;
table[2][6][1]=6;
table[2][6][2]=6;
table[2][6][3]=6;

table[3][0][0]=7;
table[3][0][1]=7;
table[3][0][2]=7;
table[3][0][3]=7;

table[3][1][0]=7;
table[3][1][1]=7;
table[3][1][2]=7;
table[3][1][3]=7;

table[3][2][0]=7;
table[3][2][1]=7;
table[3][2][2]=7;
table[3][2][3]=7;

table[3][3][0]=7;
table[3][3][1]=7;
table[3][3][2]=7;
table[3][3][3]=7;


table[3][4][0]=7;
table[3][4][1]=7;
table[3][4][2]=7;
table[3][4][3]=7;

table[3][5][0]=7;
table[3][5][1]=7;
table[3][5][2]=7;
table[3][5][3]=7;


table[3][6][0]=7;
table[3][6][1]=7;
table[3][6][2]=7;
table[3][6][3]=7;
}



    
    
}
