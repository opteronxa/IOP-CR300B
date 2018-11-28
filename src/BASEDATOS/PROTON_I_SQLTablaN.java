package BASEDATOS;

import java.util.List;

/**
 *
 * @author R.Cuevas
 */
public interface PROTON_I_SQLTablaN {
    
    public int Insert_Vehiclestate(List<StringBuilder[]> _DOC) throws Exception;  
    public void close();
    public List<String> getMensaje();
    
}
