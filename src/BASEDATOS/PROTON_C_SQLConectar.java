package BASEDATOS;

/**
 * 
 * @author R.Cuevas
 */

import java.sql.BatchUpdateException;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;

public class PROTON_C_SQLConectar extends PROTON_A_SQLBase implements AutoCloseable, PROTON_I_SQLTablaN, PROTON_I_SQLTablaS  {
    
    private final List<String> o_Mensaje=new ArrayList<>();
    private final String x_server;
    private final String[] x_tabla;
    
    public PROTON_C_SQLConectar (String[] _sql, String _ser) throws Exception{
        super(_sql, new String[]{"VehicleState"});
        x_tabla=null;
        x_server=_ser;
    }
     
    public PROTON_C_SQLConectar (String[] _sql, String[] _tab, String _ser) throws Exception{
        super(_sql, _tab);
        x_tabla=_tab;
        x_server=_ser;
    }
   
    @Override
    public void close() {
        try {
            if (o_Conx!=null) 
                if (!o_Conx.isClosed()) o_Conx.close();
        } catch(SQLException e) {
            //INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Error de Desconectar SQL Server Base1 " + e.getMessage());
        }      
        o_Conx=null;
    }
    

    @Override
    public int Insert_Vehiclestate(List<StringBuilder[]> _DOC) throws Exception { 
        int y_tot=0;
        boolean y_msg=false;
        while (_DOC.size()>0) {
            try (Statement o_sm = o_Conx.createStatement()) {
                for (StringBuilder[] y_i:_DOC) {
                    o_sm.addBatch("INSERT INTO Vehiclestate ("
                                  .concat(y_i[0].toString())
                                  .concat(",[Servidor]")
                                  .concat(",[timestamp]")
                                  .concat(") VALUES (")
                                  .concat(y_i[1].toString())
                                  .concat(", ").concat(x_server)
                                  .concat(", getdate())"));
                }    
                y_tot=o_sm.executeBatch().length;
                break;
            } catch (SQLTimeoutException e1) {
                throw new Exception(e1.getMessage());
            } catch (BatchUpdateException e2) {
                if (!y_msg) {
                    o_Mensaje.clear();
                    y_msg=true;
                }
                if (e2.getMessage().contains("Cannot insert duplicate key")) o_Mensaje.add("Trama Duplicada en (Vehiclestate) -".concat(e2.getMessage()));
                else if (e2.getMessage().contains("truncated")) o_Mensaje.add("Trucamiento de campo en (Vehiclestate) -".concat(e2.getMessage()));
                else if (e2.getMessage().contains("Invalid column")) o_Mensaje.add("Tipo de datos Invalido en Campo de(Vehiclestate) -".concat(e2.getMessage()));
                else o_Mensaje.add("Error en (Vehiclestate) -".concat(e2.getMessage()));
                this.Descartadas(_DOC, e2);
            } catch(SQLException e3) {
                throw new Exception(e3.getMessage()); 
            }
        }    
        return y_msg?y_tot*-1:y_tot;
    }
    
    @Override      
    public int Insert_Vehiclestate_Semanal(List<StringBuilder[]> _DOC, int _tab) throws Exception { 
        int y_tot=0;
        boolean y_msg=false;
        while (_DOC.size()>0) {
            try (Statement o_sm = o_Conx.createStatement()) {
                for (StringBuilder[] y_i:_DOC) {
                    String y_qry="INSERT INTO ";
                    y_qry=y_qry.concat(x_tabla[_tab]).concat(" (").concat(y_i[0].toString());
                    int y_a=y_qry.lastIndexOf(",");  //update number - para eliminar
                    y_qry=y_qry.substring(0, y_a);
                    y_qry=y_qry.concat(",[Servidor]")
                               .concat(",[timestamp]")
                               .concat(") VALUES (")
                               .concat(y_i[1].toString());
                    int y_b=y_qry.lastIndexOf(",");     //update number - para eliminar
                    y_qry=y_qry.substring(0,y_b);
                    y_qry=y_qry.concat(", ")
                               .concat(x_server)
                               .concat(", getdate())");
                    o_sm.addBatch(y_qry);
                }    
                y_tot=o_sm.executeBatch().length;
                break;
            } catch (SQLTimeoutException e1) {
                throw new Exception(e1.getMessage());
            } catch (BatchUpdateException e2) {
                if (!y_msg) {
                    o_Mensaje.clear();
                    y_msg=true;
                }
                if (e2.getMessage().contains("Cannot insert duplicate key")) o_Mensaje.add("Trama Duplicada en (Vehiclestate) -".concat(e2.getMessage()));
                else if (e2.getMessage().contains("truncated")) o_Mensaje.add("Trucamiento de campo en (Vehiclestate) -".concat(e2.getMessage()));
                else if (e2.getMessage().contains("Invalid column")) o_Mensaje.add("Tipo de datos Invalido en Campo de(Vehiclestate) -".concat(e2.getMessage()));
                else o_Mensaje.add("Error en (Vehiclestate) -".concat(e2.getMessage()));
                this.Descartadas(_DOC, e2);
            } catch(SQLException e3) {
                throw new Exception(e3.getMessage()); 
            }
        }    
        return y_msg?y_tot*-1:y_tot;
    }
    
    @Override
    public List<String> getMensaje() {
        return o_Mensaje;
    }
    
    private void Descartadas(List<StringBuilder[]> _DOC, BatchUpdateException _exep) {
        try {
            int[] y_err=_exep.getUpdateCounts();    
            int y_j=0;
            for (StringBuilder[] y_i:_DOC) { 
                if (y_err[y_j]==Statement.EXECUTE_FAILED) {  
                    _DOC.remove(y_i);
                    o_Mensaje.add("Trama Error: ".concat(y_i[0].toString())
                                                 .concat(" Ç ")
                                                 .concat(y_i[1].toString()));
                    break;
                }
                ++y_j;
            }    
        } catch (Exception e) {
            _DOC.clear();
        }
    }   
    
}


