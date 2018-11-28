package BASEDATOS;

import java.util.List;

/**
 *
 * @author R.Cuevas
 */
public interface PROTON_I_SQLTablaS {
    
    public int Insert_Vehiclestate_Semanal(List<StringBuilder[]> _DOC, int _tab) throws Exception;
    public void close();
    public List<String> getMensaje();
    
}
