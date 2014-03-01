
package Kinetic_Monte_Carlo.lattice.etching.Si_etching.unit_cell;

/**
 *
 * @author Nestor
 */
public class Unit_Cell_Atom {

//sólo para la creación de la unit cell
    protected short uc_x;
    protected short uc_y;
    protected short uc_z;
    protected Unit_Cell_Atom[] neighs;
    protected short num;
    protected int tipo;
    protected double posX, posY, posZ;
    protected float posX_space, posY_space, posZ_space;
    protected static float limitX, limitY, limitZ;
    protected static float desp_x_y, desp_x_z, desp_y_z;
    protected static float offset_X, offset_Y;
    protected static int sX, sY;
    protected static float topX, topY, topXinv, topYinv;

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

    public float getPos_x(int uc_x, int uc_y, int uc_z) {

        float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;


        int desvio = (int) Math.floor(temp * topXinv);
        if (desvio != 0) {
            temp -= topX * desvio;
        }


        return temp;

    }

    public float getPos_y(int uc_x, int uc_y, int uc_z) {

        float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;

        int desvio = (int) Math.floor(temp * topYinv);
        if (desvio != 0) {
            temp -= topY * desvio;
        }
        return temp;
    }

    public float getPos_z(int uc_x, int uc_y, int uc_z) {
        return posZ_space - uc_z * limitZ;
    }

    public float getPos_x_offset(int uc_x, int uc_y, int uc_z) {

        float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;

        int desvio = (int) Math.floor(temp * topXinv);
        if (desvio != 0) {
            temp -= topX * desvio;
        }
        return temp - offset_X;
    }

    public float getPos_y_offset(int uc_x, int uc_y, int uc_z) {

        float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;

        int desvio = (int) Math.floor(temp * topYinv);
        if (desvio != 0) {
            temp -= topY * desvio;
        }
        return temp - offset_Y;
    }

    public float getPos_x_pure(int uc_x, int uc_y, int uc_z) {
        float temp = posX_space + uc_x * limitX + desp_x_y * uc_y - uc_z * desp_x_z;
        return temp;
    }

    public float getPos_y_pure(int uc_x, int uc_y, int uc_z) {
        float temp = posY_space + uc_y * limitY - desp_y_z * uc_z;
        return temp;
    }

    public float getPos_z_nobase() {
        return posZ_space;
    }

    public static void initialize_size_variables(int sX_a, int sY_a) {
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
    public Unit_Cell_Atom(double posX, double posY, double posZ, int tipo) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;


        this.tipo = tipo;
        neighs = new Unit_Cell_Atom[4];

        num = -1;
    }

    public void setUC(short uc_x, short uc_y, short uc_z) {

        this.uc_x = uc_x;
        this.uc_y = uc_y;
        this.uc_z = uc_z;
    }

    public short getUC_x() {
        return uc_x;
    }

    public short getUC_y() {
        return uc_y;
    }

    public short getUC_z() {
        return uc_z;
    }

    public int getTipo() {
        return tipo;
    }

    public void setVecino(int vecino, Unit_Cell_Atom v) {
        neighs[vecino] = v;
    }

    public Unit_Cell_Atom getVecino(int vecino) {
        return neighs[vecino];
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
