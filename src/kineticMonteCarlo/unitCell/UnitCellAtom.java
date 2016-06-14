package kineticMonteCarlo.unitCell;

/**
 *
 * @author Nestor
 */
public class UnitCellAtom {

  //sólo para la creación de la unit cell
  private short ucX;
  private short ucY;
  private short ucZ;
  private UnitCellAtom[] neighbours;
  private short num;
  private int type;
  private double posX, posY, posZ;
  private float posX_space, posY_space, posZ_space;
  private static float limitX, limitY, limitZ;
  private static float desp_x_y, desp_x_z, desp_y_z;
  private static float offset_X, offset_Y;
  private static int sX, sY;
  private static float topX, topY, topXinv, topYinv;

  public static float getLimitX() {
    return limitX;
  }

  public static float getLimitY() {
    return limitY;
  }

  public static float getLimitZ() {
    return limitZ;
  }

  public static float getOffsetX() {
    return offset_X;
  }

  public static float getOffsetY() {
    return offset_Y;
  }

  public static float getDesp_x_y() {
    return desp_x_y;
  }

  public static float getDesp_x_z() {
    return desp_x_z;
  }

  public static float getDesp_y_z() {
    return desp_y_z;
  }

  public static float getTopX() {
    return topX;
  }

  public static float getTopY() {
    return topY;
  }

  protected double truncate(double valor, int decimales) {
    int desp = 1;
    for (int i = 0; i < decimales; i++) {
      desp = desp * 10;
    }
    long temp = Math.round(valor * desp);
    return (((double) temp) / desp);
  }

  //Inicializa las variables de posicionamiento
  public void isOrth_u_cell(double limitX_a, double limitY_a, double limitZ_a, boolean firstone) {

    if (firstone) {
      desp_x_y = 0;
      desp_x_z = 0;
      desp_y_z = 0;
      limitX = (float) limitX_a;
      limitY = (float) limitY_a;
      limitZ = (float) limitZ_a;
    }

    posX_space = (float) posX;
    posY_space = (float) posY;
    posZ_space = (float) posZ;

  }

  public float getPosX(int uc_x, int uc_y, int uc_z) {

    float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;

    int desvio = (int) Math.floor(temp * topXinv);
    if (desvio != 0) {
      temp -= topX * desvio;
    }

    return temp;

  }

  public float getPosY(int uc_x, int uc_y, int uc_z) {

    float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;

    int desvio = (int) Math.floor(temp * topYinv);
    if (desvio != 0) {
      temp -= topY * desvio;
    }
    return temp;
  }

  public float getPosZ(int uc_x, int uc_y, int uc_z) {
    return posZ_space - uc_z * limitZ;
  }

  public float getPosXOffset(int uc_x, int uc_y, int uc_z) {

    float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;

    int desvio = (int) Math.floor(temp * topXinv);
    if (desvio != 0) {
      temp -= topX * desvio;
    }
    return temp - offset_X;
  }

  public float getPosYOffset(int uc_x, int uc_y, int uc_z) {

    float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;

    int desvio = (int) Math.floor(temp * topYinv);
    if (desvio != 0) {
      temp -= topY * desvio;
    }
    return temp - offset_Y;
  }

  public float getPosXPure(int uc_x, int uc_y, int uc_z) {
    float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;
    return temp;
  }

  public float getPosYPure(int uc_x, int uc_y, int uc_z) {
    float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;
    return temp;
  }

  public float getPosZNobase() {
    return posZ_space;
  }

  public static void initializeSizeVariables(int sX_a, int sY_a) {
    sX = sX_a;
    sY = sY_a;
    offset_X = (limitX * sX) / 2.0f;
    offset_Y = (limitY * sY) / 2.0f;
    topX = (limitX * sX);
    topY = (limitY * sY);
    topXinv = 1.0f / topX;
    topYinv = 1.0f / topY;

  }

  public int getsX() {
    return sX;
  }

  public int getsY() {
    return sY;
  }

  /**
   * Creates a new instance of Atomo
   */
  public UnitCellAtom(double posX, double posY, double posZ, int tipo) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;

    this.type = tipo;
    neighbours = new UnitCellAtom[4];

    num = -1;
  }

  public void setUC(short uc_x, short uc_y, short uc_z) {

    this.ucX = uc_x;
    this.ucY = uc_y;
    this.ucZ = uc_z;
  }

  public short getUC_x() {
    return ucX;
  }

  public short getUC_y() {
    return ucY;
  }

  public short getUC_z() {
    return ucZ;
  }

  public int getTipo() {
    return type;
  }

  public void setVecino(int vecino, UnitCellAtom v) {
    neighbours[vecino] = v;
  }

  public UnitCellAtom getVecino(int vecino) {
    return neighbours[vecino];
  }

  public short getNum() {
    return num;
  }

  public void setNum(short num) {
    this.num = num;
  }

  public double getX_uc() {
    return posX;
  }

  public double getY_uc() {
    return posY;
  }

  public double getZ_uc() {
    return posZ;
  }
}
