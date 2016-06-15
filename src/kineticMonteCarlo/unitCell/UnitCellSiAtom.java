package kineticMonteCarlo.unitCell;

/**
 *
 * @author Nestor
 */
public class UnitCellSiAtom {

  private UnitCellSiAtom[] neighbours;
  private short num;
  private double posX, posY, posZ;
  private float posX_space, posY_space, posZ_space;
  private static float limitX, limitY, limitZ;
  private static float desp_x_y, desp_x_z, desp_y_z;
  private static float offset_X, offset_Y;

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
    return temp;
  }

  public float getPosY(int uc_x, int uc_y, int uc_z) {
    float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;
    return temp;
  }

  public float getPosZ(int uc_x, int uc_y, int uc_z) {
    return posZ_space - uc_z * limitZ;
  }

  public float getPosXPure(int uc_x, int uc_y, int uc_z) {
    float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;
    return temp;
  }

  public float getPosYPure(int uc_x, int uc_y, int uc_z) {
    float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;
    return temp;
  }
    
  /**
   * Creates a new instance of Atomo
   */
  public UnitCellSiAtom(double posX, double posY, double posZ) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;

    neighbours = new UnitCellSiAtom[4];

    num = -1;
  }
  
  public void setNeighbour(int pos, UnitCellSiAtom atom) {
    neighbours[pos] = atom;
  }

  public UnitCellSiAtom getNeighbour(int pos) {
    return neighbours[pos];
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
