package kineticMonteCarlo.unitCell;

import java.util.ArrayList;

/**
 *
 * @author Nestor
 */
public class SiUnitCell {

  private static final double LATTICE_DIM = 5.430710f;
  private static final double SPACE_CELL = LATTICE_DIM / 4.0f;
  private static final int DEPTH = 25;
  private double[][] g;
  private short[] neighs;
  private byte[] nBlock;
  private double limitX;
  private double limitY;
  private double limitZ;
  private double limitXnt;
  private double limitYnt;
  private double limitZnt;
  private UnitCellSiAtom[][][] red0;
  private UnitCellSiAtom[][][] red1;
  private UnitCellSiAtom[][][] red2;
  private UnitCellSiAtom[][][] red3;
  private int iInit;
  private int iSize;
  private int jInit;
  private int jSize;
  private int kInit;
  private int kSize;
  private UnitCellSiAtom[] cellsPointer;

  public SiUnitCell() {
    g = new double[3][3];
  }

  public UnitCellSiAtom[] getCellsP() {
    return cellsPointer;
  }

  public double getLimitX() {
    return limitX;
  }

  public double getLimitY() {
    return limitY;
  }

  public double getLimitZ() {
    return limitZ;
  }

  private void setRotacion(double[] m) {
    double M, N;

    M = Math.sqrt((double) m[0] * (double) m[0] + (double) m[1] * (double) m[1]
            + (double) m[2] * (double) m[2]);
    N = Math.sqrt((double) m[3] * (double) m[3] + (double) m[4] * (double) m[4]
            + (double) m[5] * (double) m[5]);

    g[0][0] = (double) m[3] / (double) N;
    g[1][0] = (double) m[4] / (double) N;
    g[2][0] = (double) m[5] / (double) N;

    g[0][1] = (double) (((double) m[1] * (double) m[5] - (double) m[2] * (double) m[4]) / (M * N));
    g[1][1] = (double) (((double) m[2] * (double) m[3] - (double) m[0] * (double) m[5]) / (M * N));
    g[2][1] = (double) (((double) m[0] * (double) m[4] - (double) m[1] * (double) m[3]) / (M * N));

    g[0][2] = (double) m[0] / (double) M;
    g[1][2] = (double) m[1] / (double) M;
    g[2][2] = (double) m[2] / (double) M;

  }

  private double getCos3D(double v1_X, double v1_Y, double v1_Z, double v2_X, double v2_Y, double v2_Z) {
    double prod = v1_X * v2_X + v1_Y * v2_Y + v1_Z * v2_Z;
    double mag = Math.sqrt((v1_X * v1_X + v1_Y * v1_Y + v1_Z * v1_Z) * (v2_X * v2_X + v2_Y * v2_Y + v2_Z * v2_Z));
    double temp = prod / mag;
    
    if (temp > 1.0) {
      temp = 1.0;
    }

    return temp;
  }

  private double getSin3D(double v1_X, double v1_Y, double v1_Z, double v2_X, double v2_Y, double v2_Z) {
    double prod = v1_X * v2_X + v1_Y * v2_Y + v1_Z * v2_Z;
    double mag = Math.sqrt((v1_X * v1_X + v1_Y * v1_Y + v1_Z * v1_Z) * (v2_X * v2_X + v2_Y * v2_Y + v2_Z * v2_Z));
    double temp = prod / mag;

    if (temp > 1.0) {
      temp = 1.0;
    }

    return Math.sqrt(1 - (temp * temp));

  }

  private double getAreaXY(double v1_X, double v1_Y, double v1_Z, double v2_X, double v2_Y, double v2_Z) {
    double mag1 = Math.sqrt(v1_X * v1_X + v1_Y * v1_Y + v1_Z * v1_Z);
    double mag2 = Math.sqrt(v2_X * v2_X + v2_Y * v2_Y + v2_Z * v2_Z);
    return mag1 * mag2;
  }

  private double getAreaXYPower(double v1_X, double v1_Y, double v1_Z, double v2_X, double v2_Y, double v2_Z) {
    double mag1 = (v1_X * v1_X + v1_Y * v1_Y + v1_Z * v1_Z);
    double mag2 = (v2_X * v2_X + v2_Y * v2_Y + v2_Z * v2_Z);
    return mag1 * mag2;
  }

  private double truncate(double valor, int decimales) {
    int desp = 1;
    for (int i = 0; i < decimales; i++) {
      desp = desp * 10;
    }
    long temp = Math.round(valor * desp);
    return (((double) temp) / desp);
  }

  public int createUnitCell(int millerX, int millerY, int millerZ) {
    limitX = 0;
    limitY = 0;
    limitZ = 0;

    ArrayList<Double> z_xs = new ArrayList();
    ArrayList<Double> z_ys = new ArrayList();
    ArrayList<Double> z_zs = new ArrayList();

    ArrayList<Double> xs = new ArrayList();
    ArrayList<Double> ys = new ArrayList();
    ArrayList<Double> zs = new ArrayList();

    ArrayList<Integer> v1 = new ArrayList();
    ArrayList<Integer> v2 = new ArrayList();
    ArrayList<Double> va = new ArrayList();

    int limitI1, limitI2;

    if (millerZ >= 0) {
      limitI1 = 0;
      limitI2 = DEPTH;
    } else {
      limitI1 = DEPTH;
      limitI2 = 1;
    }

    for (int i = -limitI1; i < limitI2; i++) { //Eje Z
      double posZ = LATTICE_DIM * i;
      for (int j = -DEPTH; j < DEPTH; j++) { //Y SC B
        for (int k = -DEPTH; k < DEPTH; k++) { //X SC B
          //posici�n X e Y en el SC C

          double posX = 0;
          double posY = 0;
          posX = k * LATTICE_DIM / 2;
          posY = k * LATTICE_DIM / 2;         //para X(B)
          posX = posX + j * LATTICE_DIM / 2;
          posY = posY - j * LATTICE_DIM / 2;    //para Y(B)

          //ya tenemos las posiciones en el SC C del atomo, comprobamos si es parte del plano, y si lo es lo a�adimos a la lista    
          if (Math.abs(posX * millerX + posY * millerY + posZ * millerZ) < 0.000001) {
            xs.add(posX);
            ys.add(posY);
            zs.add(posZ);
          }
          double ang = getCos3D(millerX, millerY, millerZ, posX, posY, posZ);
          if (ang > 0.999999999) {
            z_xs.add(posX);
            z_ys.add(posY);
            z_zs.add(posZ);
          }
        }
      }
    }

    //celula tipo 2 (altura 2)
    for (int i = -limitI1; i < limitI2; i++) { //Eje Z
      double posZ = LATTICE_DIM * i + (SPACE_CELL * 2);
      for (int j = -DEPTH; j < DEPTH; j++) { //Y SC B
        for (int k = -DEPTH; k < DEPTH; k++) { //X SC B
          //posicion X e Y en el SC C

          double posX = 0;
          double posY = 0;
          posX = (k + 0.5) * LATTICE_DIM / 2;
          posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
          posX = posX + (j + 0.5) * LATTICE_DIM / 2;
          posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)

          //ya tenemos las posiciones en el SC C del atomo, comprobamos si es parte del plano, y si lo es lo a�adimos a la lista    
          if (Math.abs(posX * millerX + posY * millerY + posZ * millerZ) < 0.000001) {
            xs.add(posX);
            ys.add(posY);
            zs.add(posZ);
          }
          double ang = getCos3D(millerX, millerY, millerZ, posX, posY, posZ);
          if (ang > 0.999999999) {
            z_xs.add(posX);
            z_ys.add(posY);
            z_zs.add(posZ);
          }

        }
      }
    }

    //-------------------------------------------------------------------------------------------
    // Obtenemos las posiciones de las tres c�lulas que minimizan la celula unidad
    // Se almacena en las variables PX, PY y PZ
    //-------------------------------------------------------------------------------------------
    double PZ_x, PZ_y, PZ_z, P1_x, P1_y, P1_z, P2_x, P2_y, P2_z;

    try {
      //buscamos los angulos que minimizan

      double sizeMin = Math.sqrt(Math.pow(z_xs.get(0), 2) + Math.pow(z_ys.get(0), 2) + Math.pow(z_zs.get(0), 2));
      int location = 0;
      for (int i = 1; i < z_xs.size(); i++) {
        double temp = Math.sqrt(Math.pow(z_xs.get(i), 2) + Math.pow(z_ys.get(i), 2) + Math.pow(z_zs.get(i), 2));
        if (sizeMin > temp) {
          sizeMin = temp;
          location = i;
        }
      }

      int tam = xs.size();

      Double[] xs_A = new Double[tam];
      xs.toArray(xs_A);
      Double[] ys_A = new Double[tam];
      ys.toArray(ys_A);
      Double[] zs_A = new Double[tam];
      zs.toArray(zs_A);

      //--------------------------------------------------
      for (int i = 0; i < tam; i++) {

        for (int j = i + 1; j < tam; j++) {
          if (Math.abs(getCos3D(xs_A[i], ys_A[i], zs_A[i], xs_A[j], ys_A[j], zs_A[j])) < 0.0000001) {
            v1.add(i);
            v2.add(j);
            va.add(getAreaXY(xs.get(i), ys.get(i), zs.get(i), xs.get(j), ys.get(j), zs.get(j)));
          }
        }
      }

      double min = va.get(0);
      int minpos = 0;
      for (int i = 1; i < va.size(); i++) {
        if (va.get(i) < min) {
          minpos = i;
          min = va.get(i);
        }
      }

      PZ_x = z_xs.get(location);
      PZ_y = z_ys.get(location);
      PZ_z = z_zs.get(location);
      P1_x = xs.get(v1.get(minpos));
      P1_y = ys.get(v1.get(minpos));
      P1_z = zs.get(v1.get(minpos));
      P2_x = xs.get(v2.get(minpos));
      P2_y = ys.get(v2.get(minpos));
      P2_z = zs.get(v2.get(minpos));

    } catch (Exception e) {
      return -2;
    }

//----------------------------------------------------------------------------------------
// Obtenemos la longitud en cada eje y sacamos la matriz de rotacion
//----------------------------------------------------------------------------------------
    limitZ = truncate(Math.sqrt(PZ_x * PZ_x + PZ_y * PZ_y + PZ_z * PZ_z), 5);
    limitZnt = Math.sqrt(PZ_x * PZ_x + PZ_y * PZ_y + PZ_z * PZ_z);

    double[] rot = new double[6];
    rot[0] = PZ_x;
    rot[1] = PZ_y;
    rot[2] = PZ_z;

    double Cx = P1_y * P2_z - P1_z * P2_y;
    double Cy = P1_z * P2_x - P1_x * P2_z;
    double Cz = P1_x * P2_y - P1_y * P2_x;

    if (getCos3D(Cx, Cy, Cz, PZ_x, PZ_y, PZ_z) > 0) {
      rot[3] = P1_x;
      rot[4] = P1_y;
      rot[5] = P1_z;
      limitX = truncate(Math.sqrt(P1_x * P1_x + P1_y * P1_y + P1_z * P1_z), 5);
      limitXnt = Math.sqrt(P1_x * P1_x + P1_y * P1_y + P1_z * P1_z);
      limitY = truncate(Math.sqrt(P2_x * P2_x + P2_y * P2_y + P2_z * P2_z), 5);
      limitYnt = Math.sqrt(P2_x * P2_x + P2_y * P2_y + P2_z * P2_z);
    } else {
      rot[3] = P2_x;
      rot[4] = P2_y;
      rot[5] = P2_z;
      limitY = truncate(Math.sqrt(P1_x * P1_x + P1_y * P1_y + P1_z * P1_z), 5);
      limitYnt = Math.sqrt(P1_x * P1_x + P1_y * P1_y + P1_z * P1_z);
      limitX = truncate(Math.sqrt(P2_x * P2_x + P2_y * P2_y + P2_z * P2_z), 5);
      limitXnt = Math.sqrt(P2_x * P2_x + P2_y * P2_y + P2_z * P2_z);
    }

//fijamos el angulo de rotaci�n con respecto SC C
    setRotacion(rot);

    //System.out.println(g[1][0]+" "+g[1][1]+" "+g[1][2]);
    //si sabemos los limites de X, Y y Z, a partir de ellos sabemos los limites en X, Y y Z pero del sistema de coordenadas de la matriz
    double I_i, I_s, J_i, J_s, K_i, K_s;
    I_i = I_s = J_i = J_s = K_i = K_s = 0;

    double tempI = P1_z / LATTICE_DIM;
    double tempK = (P1_x + P1_y) / (LATTICE_DIM);
    double tempJ = (P1_x - P1_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }

    tempI = P2_z / LATTICE_DIM;
    tempK = (P2_x + P2_y) / (LATTICE_DIM);
    tempJ = (P2_x - P2_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }

    tempI = PZ_z / LATTICE_DIM;
    tempK = (PZ_x + PZ_y) / (LATTICE_DIM);
    tempJ = (PZ_x - PZ_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }
//-------------------------------------------------------
    tempI = (PZ_z + P1_z) / LATTICE_DIM;
    tempK = (PZ_x + PZ_y + P1_x + P1_y) / (LATTICE_DIM);
    tempJ = (PZ_x - PZ_y + P1_x - P1_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }

    tempI = (PZ_z + P2_z) / LATTICE_DIM;
    tempK = (PZ_x + PZ_y + P2_x + P2_y) / (LATTICE_DIM);
    tempJ = (PZ_x - PZ_y + P2_x - P2_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }

    tempI = (P1_z + P2_z) / LATTICE_DIM;
    tempK = (P1_x + P1_y + P2_x + P2_y) / (LATTICE_DIM);
    tempJ = (P1_x - P1_y + P2_x - P2_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }
//----------------------------------------------------

    tempI = (P1_z + P2_z + PZ_z) / LATTICE_DIM;
    tempK = (P1_x + P1_y + P2_x + P2_y + PZ_x + PZ_y) / (LATTICE_DIM);
    tempJ = (P1_x - P1_y + P2_x - P2_y + PZ_x - PZ_y) / (LATTICE_DIM);
    if (I_i > tempI) {
      I_i = tempI;
    }
    if (I_s < tempI) {
      I_s = tempI;
    }
    if (J_i > tempJ) {
      J_i = tempJ;
    }
    if (J_s < tempJ) {
      J_s = tempJ;
    }
    if (K_i > tempK) {
      K_i = tempK;
    }
    if (K_s < tempK) {
      K_s = tempK;
    }
//----------------------------------------------------
    iInit = (int) Math.round(I_i) - 2;
    iSize = (int) Math.round(I_s) + 2;
    jInit = (int) Math.round(J_i) - 2;
    jSize = (int) Math.round(J_s) + 2;
    kInit = (int) Math.round(K_i) - 2;
    kSize = (int) Math.round(K_s) + 2;

    red0 = new UnitCellSiAtom[iSize - iInit][jSize - jInit][kSize - kInit];
    red1 = new UnitCellSiAtom[iSize - iInit][jSize - jInit][kSize - kInit];
    red2 = new UnitCellSiAtom[iSize - iInit][jSize - jInit][kSize - kInit];
    red3 = new UnitCellSiAtom[iSize - iInit][jSize - jInit][kSize - kInit];

    //-------------------------------------------------------------------------
    // Creamos los atomos
    //-------------------------------------------------------------------------
    int cuantos = createAtoms();
    //-----------------------------------------------------------------------
    // Por ultimo, interconectamos las celulas
    //-----------------------------------------------------------------------
    if (cuantos > 2048) {
      return -2;
    }

    interconect();

    //----------------------------------------
    // Asignamos la celula origen y numeramos
    //----------------------------------------
    neighs = new short[4 * cuantos];
    cellsPointer = new UnitCellSiAtom[cuantos];
    nBlock = new byte[4 * cuantos];

//--------------------------------------------------------------------------
// Ahora debemos crear las matrices de neigh y n_block
// Para ello es necesario recorrer otra vez el bloque   
//--------------------------------------------------------------------------   
    setArraysNeigh();

//---------------------------------------------------------------------------
//   Y se acab� :), no veas lo contento que estoy de escribir esto
//---------------------------------------------------------------------------   
    red0 = null;
    red1 = null;
    red2 = null;
    red3 = null;

    for (int i = 0; i < cellsPointer.length; i++) {
      cellsPointer[i].isOrth_u_cell(limitX, limitY, limitZ, i == 0);
    }

    return (cuantos);
  }

  public int size() {
    if (neighs == null) {
      return 0;
    } else {
      return neighs.length / 4;
    }
  }

  private int createAtoms() {
    int cont = 0;
    //celula tipo 0 (abajo del todo)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B
          //posici�n X e Y en el SC C
          double posX = 0;
          double posY = 0;
          posX = k * LATTICE_DIM / 2;
          posY = k * LATTICE_DIM / 2; //para X(B)
          posX = posX + j * LATTICE_DIM / 2;
          posY = posY - j * LATTICE_DIM / 2; //para Y(B)
          double posZ = LATTICE_DIM * i;
          //ahora hay que pasar al nuevo sistema de coordenadas
          double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
          double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
          double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

          if (Xr >= 0 && Yr >= 0 && Zr >= 0 && Xr < limitX && Yr < limitY && Zr < limitZ) {
            red0[i - iInit][j - jInit][k - kInit] = new UnitCellSiAtom(Xr, Yr, Zr);
            //System.out.println(">"+posX+" "+posY+" "+posZ+"|new|"+Xr+" "+Yr+" "+Zr);
            red0[i - iInit][j - jInit][k - kInit].setNum((short) cont);
            cont++;
          } //else System.out.println(Math.abs(Xr-limitX)+Math.abs(Zr-limitZ)+Math.abs(Yr-limitY) );
        }
      }
    }

    //celula tipo 1 (altura 1)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B
          //posici�n X e Y en el SC C
          double posX = 0;
          double posY = 0;
          posX = k * LATTICE_DIM / 2;
          posY = k * LATTICE_DIM / 2; //para X(B)
          posX = posX + (j + 0.5) * LATTICE_DIM / 2;
          posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
          double posZ = LATTICE_DIM * i + SPACE_CELL;

          //ahora hay que pasar al nuevo sistema de coordenadas
          double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
          double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
          double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);
          //if (i==1 && j==2 && k==4) {System.out.println(Xr);}
          if (Xr >= 0 && Yr >= 0 && Zr >= 0 && Xr < limitX && Yr < limitY && Zr < limitZ) {
            red1[i - iInit][j - jInit][k - kInit] = new UnitCellSiAtom(Xr, Yr, Zr);

            red1[i - iInit][j - jInit][k - kInit].setNum((short) cont);
            cont++;
          }//else System.out.println(Math.abs(Xr-limitX)+Math.abs(Zr-limitZ)+Math.abs(Yr-limitY) );
        }
      }
    }

    //celula tipo 2 (altura 2)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B
          //posici�n X e Y en el SC C
          double posX = 0;
          double posY = 0;
          posX = (k + 0.5) * LATTICE_DIM / 2;
          posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
          posX = posX + (j + 0.5) * LATTICE_DIM / 2;
          posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
          double posZ = LATTICE_DIM * i + (SPACE_CELL * 2);

          //ahora hay que pasar al nuevo sistema de coordenadas
          double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
          double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
          double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

          //if (i==1 && j==2 && k==4) {System.out.println(Xr);}
          if (Xr >= 0 && Yr >= 0 && Zr >= 0 && Xr < limitX && Yr < limitY && Zr < limitZ) {
            red2[i - iInit][j - jInit][k - kInit] = new UnitCellSiAtom(Xr, Yr, Zr);

            red2[i - iInit][j - jInit][k - kInit].setNum((short) cont);
            cont++;
          }//else System.out.println(Math.abs(Xr-limitX)+Math.abs(Zr-limitZ)+Math.abs(Yr-limitY) );
        }
      }
    }

    //celula tipo 3 (altura 3)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B
          //posici�n X e Y en el SC C
          double posX = 0;
          double posY = 0;
          posX = (k + 0.5) * LATTICE_DIM / 2;
          posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
          posX = posX + j * LATTICE_DIM / 2;
          posY = posY - j * LATTICE_DIM / 2; //para Y(B)
          double posZ = LATTICE_DIM * i + (SPACE_CELL * 3);
          //ahora hay que pasar al nuevo sistema de coordenadas
          double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
          double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
          double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);
          //if (i==1 && j==2 && k==4) {System.out.println(Xr);}
          if (Xr >= 0 && Yr >= 0 && Zr >= 0 && Xr < limitX && Yr < limitY && Zr < limitZ) {
            red3[i - iInit][j - jInit][k - kInit] = new UnitCellSiAtom(Xr, Yr, Zr);
            red3[i - iInit][j - jInit][k - kInit].setNum((short) cont);
            cont++;
          }//else System.out.println(Math.abs(Xr-limitX)+Math.abs(Zr-limitZ)+Math.abs(Yr-limitY) );
        }
      }
    }
    return cont;
  }

  private short findAtom(double Px, double Py, double Pz) {
    //celula tipo 0 (abajo del todo)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red0[i - iInit][j - jInit][k - kInit] != null && red0[i - iInit][j - jInit][k - kInit].getX_uc() == Px && red0[i - iInit][j - jInit][k - kInit].getY_uc() == Py && red0[i - iInit][j - jInit][k - kInit].getZ_uc() == Pz) {
            return (red0[i - iInit][j - jInit][k - kInit].getNum());
          }

        }
      }
    }

    //celula tipo 1
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red1[i - iInit][j - jInit][k - kInit] != null && red1[i - iInit][j - jInit][k - kInit].getX_uc() == Px && red1[i - iInit][j - jInit][k - kInit].getY_uc() == Py && red1[i - iInit][j - jInit][k - kInit].getZ_uc() == Pz) {
            return (red1[i - iInit][j - jInit][k - kInit].getNum());
          }
        }
      }
    }

    //celula tipo 2
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red2[i - iInit][j - jInit][k - kInit] != null && red2[i - iInit][j - jInit][k - kInit].getX_uc() == Px && red2[i - iInit][j - jInit][k - kInit].getY_uc() == Py && red2[i - iInit][j - jInit][k - kInit].getZ_uc() == Pz) {
            return (red2[i - iInit][j - jInit][k - kInit].getNum());
          }
        }
      }
    }

    //celula tipo 3
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red3[i - iInit][j - jInit][k - kInit] != null && red3[i - iInit][j - jInit][k - kInit].getX_uc() == Px && red3[i - iInit][j - jInit][k - kInit].getY_uc() == Py && red3[i - iInit][j - jInit][k - kInit].getZ_uc() == Pz) {
            return (red3[i - iInit][j - jInit][k - kInit].getNum());
          }
        }
      }
    }
    return -1;
  }

  private void interconect() {
    //celula tipo 0 (abajo del todo)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (Z-1,X-1) Vec 3 (Z-1,X+1)
          if (red0[i - iInit][j - jInit][k - kInit] != null) {
            if (j > jInit) {
              red0[i - iInit][j - jInit][k - kInit].setNeighbour(0, red1[i - iInit][j - 1 - jInit][k - kInit]);
            }
            red0[i - iInit][j - jInit][k - kInit].setNeighbour(1, red1[i - iInit][j - jInit][k - kInit]);

            if (k > -kInit && i > -iInit) {
              red0[i - iInit][j - jInit][k - kInit].setNeighbour(2, red3[i - iInit - 1][j - jInit][k - kInit - 1]);
            }
            if (i > iInit) {
              red0[i - iInit][j - jInit][k - kInit].setNeighbour(3, red3[i - iInit - 1][j - jInit][k - kInit]);
            }
          }

        }
      }
    }

    //celula tipo 1 
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (X-1) Vec 3 (X+1)
          if (red1[i - iInit][j - jInit][k - kInit] != null) {
            red1[i - iInit][j - jInit][k - kInit].setNeighbour(0, red0[i - iInit][j - jInit][k - kInit]);
            if (j < jSize - 1) {
              red1[i - iInit][j - jInit][k - kInit].setNeighbour(1, red0[i - iInit][j + 1 - jInit][k - kInit]);
            }

            if (k > kInit) {
              red1[i - iInit][j - jInit][k - kInit].setNeighbour(2, red2[i - iInit][j + -jInit][k - kInit - 1]);
            }
            red1[i - iInit][j - jInit][k - kInit].setNeighbour(3, red2[i - iInit][j - jInit][k - kInit]);
          }
        }
      }
    }

    //celula tipo 2
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (X-1) Vec 3 (X+1)
          if (red2[i - iInit][j - jInit][k - kInit] != null) {
            red2[i - iInit][j - jInit][k - kInit].setNeighbour(0, red3[i - iInit][j - jInit][k - kInit]);
            if (j < jSize - 1) {
              red2[i - iInit][j - jInit][k - kInit].setNeighbour(1, red3[i - iInit][j - jInit + 1][k - kInit]);
            }

            red2[i - iInit][j - jInit][k - kInit].setNeighbour(2, red1[i - iInit][j - jInit][k - kInit]);
            if (k < kSize - 1) {
              red2[i - iInit][j - jInit][k - kInit].setNeighbour(3, red1[i - iInit][j - jInit][k - kInit + 1]);
            }
          }
        }
      }
    }

    //celula tipo 3
    for (int i = iInit; i < iSize; i++) { //Eje Z
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (X-1) Vec 3 (X+1)
          if (red3[i - iInit][j - jInit][k - kInit] != null) {
            if (j > -jInit) {
              red3[i - iInit][j - jInit][k - kInit].setNeighbour(0, red2[i - iInit][j - jInit - 1][k - kInit]);
            }
            red3[i - iInit][j - jInit][k - kInit].setNeighbour(1, red2[i - iInit][j - jInit][k - kInit]);

            if (i < iSize - 1) {
              red3[i - iInit][j - jInit][k - kInit].setNeighbour(2, red0[i - iInit + 1][j - jInit][k - kInit]);
            }
            if (i < iSize - 1 && k < kSize - 1) {
              red3[i - iInit][j - jInit][k - kInit].setNeighbour(3, red0[i - iInit + 1][j - jInit][k - kInit + 1]);
            }

          }

        }
      }
    }

  }

  private void setArraysNeigh() {
    //System.out.println("-----------");
    //celula tipo 0 (abajo del todo)
    for (int i = iInit; i < iSize; i++) { //Eje Z
      //  int cont=0;
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red0[i - iInit][j - jInit][k - kInit] != null) {
            int num = red0[i - iInit][j - jInit][k - kInit].getNum();
            cellsPointer[num] = red0[i - iInit][j - jInit][k - kInit];
            //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (Z-1,X-1) Vec 3 (Z-1,X+1)
            //para cada vecino...

            //cont++;
            if (red0[i - iInit][j - jInit][k - kInit].getNeighbour(0) != null) {
              neighs[4 * num] = red0[i - iInit][j - jInit][k - kInit].getNeighbour(0).getNum();
              nBlock[4 * num] = 0;
            } else {

              double posX = k * LATTICE_DIM / 2;
              double posY = k * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j - 0.5) * LATTICE_DIM / 2;
              posY = posY - (j - 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + SPACE_CELL;

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num] = (byte) n_bl;

              neighs[4 * num] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red0[i - iInit][j - jInit][k - kInit].getNeighbour(1) != null) {
              neighs[4 * num + 1] = red0[i - iInit][j - jInit][k - kInit].getNeighbour(1).getNum();
              nBlock[4 * num + 1] = 0;
              //System.out.println(num+" Vecino 1: "+red0[i-I_i][j-J_i][k-K_i].getVecino(1).getNum());
            } else {

              double posX = k * LATTICE_DIM / 2;
              double posY = k * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 0.5) * LATTICE_DIM / 2;
              posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + SPACE_CELL;

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }
//if (i==0 && j==0 && k==0) System.out.println(">>"+Xr+" "+Yr+" "+Zr);
              nBlock[4 * num + 1] = (byte) n_bl;
              neighs[4 * num + 1] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red0[i - iInit][j - jInit][k - kInit].getNeighbour(2) != null) {
              neighs[4 * num + 2] = red0[i - iInit][j - jInit][k - kInit].getNeighbour(2).getNum();
              nBlock[4 * num + 2] = 0;
            } else {

              double posX = (k - 0.5) * LATTICE_DIM / 2;
              double posY = (k - 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + j * LATTICE_DIM / 2;
              posY = posY - j * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * (i - 1) + (SPACE_CELL * 3);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 2] = (byte) n_bl;
              neighs[4 * num + 2] = findAtom(Xr, Yr, Zr);
            }
//-------------------------------------------------------------------------------------------------------------------------------------  

            if (red0[i - iInit][j - jInit][k - kInit].getNeighbour(3) != null) {
              neighs[4 * num + 3] = red0[i - iInit][j - jInit][k - kInit].getNeighbour(3).getNum();
              nBlock[4 * num + 3] = 0;
            } else {

              double posX = (k + 0.5) * LATTICE_DIM / 2;
              double posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + j * LATTICE_DIM / 2;
              posY = posY - j * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * (i - 1) + (SPACE_CELL * 3);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }
              nBlock[4 * num + 3] = (byte) n_bl;
              neighs[4 * num + 3] = findAtom(Xr, Yr, Zr);
            }
          }
        }
      }/*System.out.println(cont+" "+Math.ceil((float)cont/8));*/

    }

    // System.out.println();
    //celula tipo 1 
    for (int i = iInit; i < iSize; i++) { //Eje Z
      // int cont=0;
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red1[i - iInit][j - jInit][k - kInit] != null) {
            int num = red1[i - iInit][j - jInit][k - kInit].getNum();
            cellsPointer[num] = red1[i - iInit][j - jInit][k - kInit];

            //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (X-1) Vec 3 (X+1)
            //para cada vecino...
            //   cont++;
            if (red1[i - iInit][j - jInit][k - kInit].getNeighbour(0) != null) {
              neighs[4 * num] = red1[i - iInit][j - jInit][k - kInit].getNeighbour(0).getNum();
              nBlock[4 * num] = 0;
            } else {

              double posX = k * LATTICE_DIM / 2;
              double posY = k * LATTICE_DIM / 2; //para X(B)
              posX = posX + j * LATTICE_DIM / 2;
              posY = posY - j * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i;

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);
              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num] = (byte) n_bl;
              neighs[4 * num] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red1[i - iInit][j - jInit][k - kInit].getNeighbour(1) != null) {
              neighs[4 * num + 1] = red1[i - iInit][j - jInit][k - kInit].getNeighbour(1).getNum();
              nBlock[4 * num + 1] = 0;
            } else {

              double posX = k * LATTICE_DIM / 2;
              double posY = k * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 1) * LATTICE_DIM / 2;
              posY = posY - (j + 1) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i;

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 1] = (byte) n_bl;
              neighs[4 * num + 1] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red1[i - iInit][j - jInit][k - kInit].getNeighbour(2) != null) {
              neighs[4 * num + 2] = red1[i - iInit][j - jInit][k - kInit].getNeighbour(2).getNum();
              nBlock[4 * num + 2] = 0;
            } else {

              double posX = (k - 0.5) * LATTICE_DIM / 2;
              double posY = (k - 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 0.5) * LATTICE_DIM / 2;
              posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + (SPACE_CELL * 2);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 2] = (byte) n_bl;
              neighs[4 * num + 2] = findAtom(Xr, Yr, Zr);
            }
//-------------------------------------------------------------------------------------------------------------------------------------  

            if (red1[i - iInit][j - jInit][k - kInit].getNeighbour(3) != null) {
              neighs[4 * num + 3] = red1[i - iInit][j - jInit][k - kInit].getNeighbour(3).getNum();
              nBlock[4 * num + 3] = 0;
            } else {

              double posX = (k + 0.5) * LATTICE_DIM / 2;
              double posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 0.5) * LATTICE_DIM / 2;
              posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + (SPACE_CELL * 2);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }
              nBlock[4 * num + 3] = (byte) n_bl;
              neighs[4 * num + 3] = findAtom(Xr, Yr, Zr);
            }
          }
        }
      }/*System.out.println(cont+" "+Math.ceil((float)cont/8));*/

    }

    //celula tipo 2 
    for (int i = iInit; i < iSize; i++) { //Eje Z
      // int cont=0;
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red2[i - iInit][j - jInit][k - kInit] != null) {
            int num = red2[i - iInit][j - jInit][k - kInit].getNum();
            cellsPointer[num] = red2[i - iInit][j - jInit][k - kInit];
            //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (X-1) Vec 3 (X+1)
            //para cada vecino...
            //  cont++;
            if (red2[i - iInit][j - jInit][k - kInit].getNeighbour(0) != null) {
              neighs[4 * num] = red2[i - iInit][j - jInit][k - kInit].getNeighbour(0).getNum();
              nBlock[4 * num] = 0;
            } else {

              double posX = (k + 0.5) * LATTICE_DIM / 2;
              double posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + j * LATTICE_DIM / 2;
              posY = posY - j * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + (SPACE_CELL * 3);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);
              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num] = (byte) n_bl;
              neighs[4 * num] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red2[i - iInit][j - jInit][k - kInit].getNeighbour(1) != null) {
              neighs[4 * num + 1] = red2[i - iInit][j - jInit][k - kInit].getNeighbour(1).getNum();
              nBlock[4 * num + 1] = 0;
            } else {

              double posX = (k + 0.5) * LATTICE_DIM / 2;
              double posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 1) * LATTICE_DIM / 2;
              posY = posY - (j + 1) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + (SPACE_CELL * 3);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 1] = (byte) n_bl;
              neighs[4 * num + 1] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red2[i - iInit][j - jInit][k - kInit].getNeighbour(2) != null) {
              neighs[4 * num + 2] = red2[i - iInit][j - jInit][k - kInit].getNeighbour(2).getNum();
              nBlock[4 * num + 2] = 0;
            } else {

              double posX = k * LATTICE_DIM / 2;
              double posY = k * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 0.5) * LATTICE_DIM / 2;
              posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + SPACE_CELL;

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 2] = (byte) n_bl;
              neighs[4 * num + 2] = findAtom(Xr, Yr, Zr);
            }
//-------------------------------------------------------------------------------------------------------------------------------------  

            if (red2[i - iInit][j - jInit][k - kInit].getNeighbour(3) != null) {
              neighs[4 * num + 3] = red2[i - iInit][j - jInit][k - kInit].getNeighbour(3).getNum();
              nBlock[4 * num + 3] = 0;
            } else {

              double posX = (k + 1) * LATTICE_DIM / 2;
              double posY = (k + 1) * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 0.5) * LATTICE_DIM / 2;
              posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + SPACE_CELL;
              //if (red2[i-I_i][j-J_i][k-K_i].getNum()==11) System.out.println(">"+posX+" "+posY+" "+posZ);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }
              nBlock[4 * num + 3] = (byte) n_bl;
              neighs[4 * num + 3] = findAtom(Xr, Yr, Zr);

            }
          }
        }
      }
    }

    //System.out.println();
    //celula tipo 3 
    for (int i = iInit; i < iSize; i++) { //Eje Z
      int cont = 0;
      for (int j = jInit; j < jSize; j++) { //Y SC B
        for (int k = kInit; k < kSize; k++) { //X SC B

          if (red3[i - iInit][j - jInit][k - kInit] != null) {
            int num = red3[i - iInit][j - jInit][k - kInit].getNum();
            cellsPointer[num] = red3[i - iInit][j - jInit][k - kInit];
            //vecino 0, (Y-1) Vec 1 (Y+1) Vec 2 (X-1) Vec 3 (X+1)
            //para cada vecino...
            cont++;
            if (red3[i - iInit][j - jInit][k - kInit].getNeighbour(0) != null) {
              neighs[4 * num] = red3[i - iInit][j - jInit][k - kInit].getNeighbour(0).getNum();
              nBlock[4 * num] = 0;
            } else {

              double posX = (k + 0.5) * LATTICE_DIM / 2;
              double posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j - 0.5) * LATTICE_DIM / 2;
              posY = posY - (j - 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + (SPACE_CELL * 2);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);
              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num] = (byte) n_bl;
              neighs[4 * num] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red3[i - iInit][j - jInit][k - kInit].getNeighbour(1) != null) {
              neighs[4 * num + 1] = red3[i - iInit][j - jInit][k - kInit].getNeighbour(1).getNum();
              nBlock[4 * num + 1] = 0;
            } else {

              double posX = (k + 0.5) * LATTICE_DIM / 2;
              double posY = (k + 0.5) * LATTICE_DIM / 2; //para X(B)
              posX = posX + (j + 0.5) * LATTICE_DIM / 2;
              posY = posY - (j + 0.5) * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * i + (SPACE_CELL * 2);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 1] = (byte) n_bl;
              neighs[4 * num + 1] = findAtom(Xr, Yr, Zr);
            }

//-------------------------------------------------------------------------------------------------------------------------------------  
            if (red3[i - iInit][j - jInit][k - kInit].getNeighbour(2) != null) {
              neighs[4 * num + 2] = red3[i - iInit][j - jInit][k - kInit].getNeighbour(2).getNum();
              nBlock[4 * num + 2] = 0;
            } else {

              double posX = k * LATTICE_DIM / 2;
              double posY = k * LATTICE_DIM / 2; //para X(B)
              posX = posX + j * LATTICE_DIM / 2;
              posY = posY - j * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * (i + 1);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }

              nBlock[4 * num + 2] = (byte) n_bl;
              neighs[4 * num + 2] = findAtom(Xr, Yr, Zr);

            }
//-------------------------------------------------------------------------------------------------------------------------------------  

            if (red3[i - iInit][j - jInit][k - kInit].getNeighbour(3) != null) {
              neighs[4 * num + 3] = red3[i - iInit][j - jInit][k - kInit].getNeighbour(3).getNum();
              nBlock[4 * num + 3] = 0;
            } else {

              double posX = (k + 1) * LATTICE_DIM / 2;
              double posY = (k + 1) * LATTICE_DIM / 2; //para X(B)
              posX = posX + j * LATTICE_DIM / 2;
              posY = posY - j * LATTICE_DIM / 2; //para Y(B)
              double posZ = LATTICE_DIM * (i + 1);

              double Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]), 5);
              double Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]), 5);
              double Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]), 5);

              int n_bl = 0;
              if (Xr < 0) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) + limitXnt, 5);
                n_bl = n_bl + 3 * 4;
              }
              if (Xr >= limitX) {
                Xr = truncate((posX * g[0][0] + posY * g[1][0] + posZ * g[2][0]) - limitXnt, 5);
                n_bl = n_bl + 1 * 4;
              }
              if (Yr < 0) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) + limitYnt, 5);
                n_bl = n_bl + 3;
              }
              if (Yr >= limitY) {
                Yr = truncate((posX * g[0][1] + posY * g[1][1] + posZ * g[2][1]) - limitYnt, 5);
                n_bl = n_bl + 1;
              }
              if (Zr < 0) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) + limitZnt, 5);
                n_bl = n_bl + 1 * 16;
              }
              if (Zr >= limitZ) {
                Zr = truncate((posX * g[0][2] + posY * g[1][2] + posZ * g[2][2]) - limitZnt, 5);
                n_bl = n_bl + 3 * 16;
              }
              nBlock[4 * num + 3] = (byte) n_bl;
              neighs[4 * num + 3] = findAtom(Xr, Yr, Zr);
            }
          }
        }
      }
    }

  }

  public short[] getNeighs() {
    return neighs;
  }

  public byte[] getNBlock() {
    return nBlock;
  }
}
