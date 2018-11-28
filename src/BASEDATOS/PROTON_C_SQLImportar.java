package BASEDATOS;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.util.Optional;

/**
 * 
 * @author R.Cuevas
 */

public class PROTON_C_SQLImportar implements AutoCloseable {
      
    private final INTERFACE.PROTON_W_Ventana o_Vent;
    private final COMUNICACION.PROTON_C_Movimientos o_Movimientos;
    private final String[] x_SQL;
    private final String x_iop;
    private volatile boolean x_esperar=false;
    private final String x_Query;
    private boolean x_estatus=false;
    private Importa o_IMP=null;
            
    public PROTON_C_SQLImportar(INTERFACE.PROTON_W_Ventana _vent,
                                COMUNICACION.PROTON_C_Movimientos _movimientos,
                                String _iop,
                                String[] _SQL,
                                int _max) throws Exception {
        o_Vent=_vent;      
        o_Movimientos=_movimientos;
        x_iop=_iop;
        x_SQL=_SQL;
        x_Query = "SELECT TOP(".concat(String.valueOf(_max)).concat(") [ID], [OTA] FROM Default_Vehicles WHERE [IOLIST]='").concat(x_iop).concat("'"); 
        o_IMP=new Importa();
        o_IMP.Leer_Default_Vehicles();
        o_Vent.setVista(">Importar desde SQL Server ".concat(_SQL[3]).concat(" OK... "));
        x_estatus=true;
    }

    public void Reconectar() {
        try {
            if (o_IMP==null) o_IMP=new Importa();
            x_estatus=true;
            o_Vent.setVista(">Reconexion SQL Importar desde ".concat(x_SQL[3]).concat(" OK... "));
        } catch (Exception e) {
            if (o_IMP!=null) o_IMP.Desconecta();
            o_Vent.setVista("Fallo de Reconexion, Reiniciando SQL Importar en en 10 Seg..."); 
            o_Vent.Cambia_Estatus("SQL");
            o_IMP=null;
            x_estatus=false;
        }    
    }
    
    public void setEsperar(boolean _esp) {
        this.x_esperar=_esp;
    }
    
    public boolean getEstatus() {
        return x_estatus;
    }
    
    public void Actualizar() { 
        if (x_esperar) return;
        try {
            o_IMP.Leer_Default_Vehicles();
            o_Movimientos.Depurar();
        } catch (Exception e) {
            o_Vent.setVista(e.getMessage());
            o_Vent.setVista("Reiniciando SQL Importar en en 10 Seg..."); 
            o_Vent.Cambia_Estatus("SQL");
            o_IMP.Desconecta();
            o_IMP=null;
            x_estatus=false;
        }    
    }
    
    @Override
    public void close() throws Exception {
        o_IMP.Desconecta();
        o_IMP=null;  
        x_estatus=false;
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLImportar] Cerro el tread Importador");
        o_Vent.setVista("<SQL Importar OFF!");
    }
    
//********ClASE Interna****
    class Importa extends PROTON_A_SQLBase {   
        
        public Importa() throws Exception {
            super(x_SQL, new String[]{"Default_Vehicles"});
        }
       
        private void Desconecta() {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLImportar] Rutina de Desconectar SQL Server");
            try {
                if (o_Conx!=null) 
                    if (!o_Conx.isClosed()) o_Conx.close();
            } catch(SQLException e) { 
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLImportar] Error el Close SQL ".concat(e.getMessage()) );
                o_Vent.setVista("Error en Close SQL Importar");
            }    
            o_Conx=null;
        }

        private void Leer_Default_Vehicles() throws Exception {
            try (ResultSet o_doc = o_Conx.prepareStatement(x_Query).executeQuery()) {
                while (o_doc.next()) {
                    Optional<String[]> o_upd=o_Movimientos.AltaVID(o_doc.getString("ID"),o_doc.getString("OTA"));
                    if (o_upd.isPresent()) {
                        this.Actualiza_Default_Vehicles(o_doc.getString("ID"), o_upd.get()[0]);
                        o_Vent.setAviso(o_upd.get()[1]);
                    }
                } 
            } catch(SQLTimeoutException e2) {
                if (e2.getMessage()!=null) throw new Exception("Error SQL TimeOut en Select - ".concat(e2.getMessage()));
                throw new Exception("Error SQL TimeOut en Select");
            } catch(SQLException e1) {
                if (e1.getMessage()!=null) throw new Exception("Error en rutina lectura Import SQL - ".concat(e1.getMessage()));
               throw new Exception("Error en rutina lectura Import SQL");
            } 
        }    

        private void Actualiza_Default_Vehicles(String _id, String _eota) throws Exception {
            StringBuilder y_upd=new StringBuilder("UPDATE Default_Vehicles set ");
            if (_eota.equals("Pendiente...")) y_upd.append("[OTA]='Pendiente...', [EOTA]=''");
            else y_upd.append("[OTA]='', [EOTA]='").append(_eota).append("'");
            y_upd.append(" Where [ID]='").append(_id).append("'").append(" and [IOList]='").append(x_iop).append("'");
            try (Statement y_sm = o_Conx.createStatement()) {  
                y_sm.execute(y_upd.toString());
            } catch(SQLTimeoutException e2) {
                if (e2.getMessage()!=null) throw new Exception("SQL TimeOut en Update del Import - ".concat(e2.getMessage()));
                throw new Exception("SQL TimeOut en Update del Import");
            } catch(SQLException e1) {
                if (e1.getMessage()!=null) throw new Exception("SQL Statement en Update Import - ".concat(e1.getMessage()));
                throw new Exception("SQL Statement en Update del Import");
            }    
        }
        
            
    }    
    
}