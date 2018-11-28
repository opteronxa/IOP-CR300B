package BASEDATOS;


/**
 *
 * @author R.Cuevas
 */
public abstract class PROTON_A_Estadisticas {
    
    protected long x_otaup=0;
    protected long x_trado=0;
    protected long x_Kbytup=0;
    protected long x_Kbytdo=0;
    protected long x_1024bytup=0;
    protected long x_1024bytdo=0;
    protected int x_dup=0;
    
    
    public int getDuplicados() {
        return x_dup;
    }    
    
    public long[] getSubida() {
        return new long[]{x_Kbytup, x_otaup};
    }
    
    public long[] getBajada() {
        return new long[] {x_Kbytdo, x_trado};   
    }
    
    
    
}
