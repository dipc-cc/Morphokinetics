package kineticMonteCarlo.unitCell;

/**
 *
 * @author Nestor
 */
public class UnitCellSiAtom {

  private UnitCellSiAtom[] neighbours;
  private short num;
  private final double posX;
  private final double posY;
  private final double posZ;
  private float posX_space;
  private float posY_space;
  private float posZ_space;
  private float limitX;
  private float limitY;
  private float limitZ;
  private float desp_x_y;
  private float desp_x_z;
  private float desp_y_z;
    
  /**
   * Creates a new instance of Atomo.
   * 
   * @param posX
   * @param posY
   * @param posZ
   */
  public UnitCellSiAtom(double posX, double posY, double posZ) {
    this.posX = posX;
    this.posY = posY;
    this.posZ = posZ;

    neighbours = new UnitCellSiAtom[4];

    num = -1;
  }
  
  public float getLimitX() {
    return limitX;
  }

  public float getLimitY() {
    return limitY;
  }

  public float getLimitZ() {
    return limitZ;
  }

  /**
   * Inicializa las variables de posicionamiento.
   * 
   * @param limitX_a
   * @param limitY_a
   * @param limitZ_a
   * @param firstOne whether is the first time calling to this method
   */
  public void isOrth_u_cell(double limitX_a, double limitY_a, double limitZ_a, boolean firstOne) {
    if (firstOne) {
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
