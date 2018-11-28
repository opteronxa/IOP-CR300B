package BASEDATOS;

/**
 * 
 * @author R.Cuevas
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Optional;


public final class PROTON_C_SQLExporta implements AutoCloseable {
 //Objetos	 
    private final Optional<PROTON_I_SQLTablaN> o_SQL1;
    private final Optional<PROTON_I_SQLTablaS> o_SQL2;
    private final Optional<PROTON_I_SQLTablaS> o_SQL3;
    private final SQLRutinaN o_SQLN;
    private final SQLRutinaS o_SQLS;
    private boolean x_msg=false;    
    private final List<String> o_Mensaje=new ArrayList<>();
    //**
    
    public PROTON_C_SQLExporta (INTERFACE.PROTON_W_Ventana _vent,
    		                ARCHIVOS.PROTON_S_ArchivoINI _ini) throws Exception {
        if (_ini.getSQL_ON(1)) {
            try {    
                o_SQL1=Optional.of(new PROTON_C_SQLConectar(_ini.getSQL("SQL1_DB"),
                                                            _ini.getServer())); 
                _vent.setVista(">Exportar a SQL Server ".concat(_ini.getSQL("SQL1_DB")[3]).concat(" OK... "));
            } catch (Exception e) {
                //INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Error SQL1_DB - ".concat(e.getMessage()));
                _vent.setVista(">SQL Server Conexion a SQL1_DB Fallo...");
                throw new Exception(e.getMessage());
            } 
        } else {
            o_SQL1=Optional.empty();
            _vent.setVista(">Exportar a SQL1 No Solicitado... " );
        }
        if (_ini.getSQL_ON(2)) {
            try {    
                o_SQL2=Optional.of(new PROTON_C_SQLConectar(_ini.getSQL("SQL2_DB"),
                                                            new String[]{"VehicleState0",
                                                                         "VehicleState1",
                                                                         "VehicleState2",
                                                                         "VehicleState3",
                                                                         "VehicleState4",
                                                                         "VehicleState5",
                                                                         "VehicleState6"},
                                                                         _ini.getServer())); 
                _vent.setVista(">Exportar a SQL Server ".concat(_ini.getSQL("SQL2_DB")[3]).concat(" OK... "));
            } catch (Exception e) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Error SQL2_DB - ".concat(e.getMessage()));
                _vent.setVista(">SQL Server Conexion a SQL2_DB Fallo...");
                throw new Exception(e.getMessage());
            } 
        } else {
            o_SQL2=Optional.empty();
            _vent.setVista(">Exportar a SQL2 No Solicitado... " );
        }
        if (_ini.getSQL_ON(3)) {
            try {    
                o_SQL3=Optional.of(new PROTON_C_SQLConectar(_ini.getSQL("SQL3_DB"),
                                                            new String[]{"VehicleState0",
                                                                         "VehicleState1",
                                                                         "VehicleState2",
                                                                         "VehicleState3",
                                                                         "VehicleState4",
                                                                         "VehicleState5",
                                                                         "VehicleState6"},
                                                                         _ini.getServer())); 
                _vent.setVista(">Exportar a SQL Server ".concat(_ini.getSQL("SQL3_DB")[3]).concat(" OK... "));
            } catch (Exception e) {
                //INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Error SQL3_DB - ".concat(e.getMessage()));
                _vent.setVista(">SQL Server Conexion a SQL3_DB Fallo...");
                throw new Exception(e.getMessage());
            } 
        } else {
            o_SQL3=Optional.empty();
            _vent.setVista(">Exportar a SQL3 No Solicitado... " );
        }
        o_SQLN=((_sql, _doc) ->{
            try {
                int y_ist=_sql.Insert_Vehiclestate(_doc);
                if (y_ist<=0) {
                    for (String y_m:_sql.getMensaje()) {
                        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Vehiclestate - ".concat(y_m));
                        o_Mensaje.add(">Error VehicleState: ".concat(y_m));
                        x_msg=true;
                    }     
                    return y_ist*-1;
                } else return y_ist;
            } catch (Exception e) {
                throw new Exception(e.getMessage()); 
            }   
        });  
        o_SQLS=((_sql, _doc, _tab) ->{
            try {
                int y_ist=_sql.Insert_Vehiclestate_Semanal(_doc, _tab);
                if (y_ist<=0) {
                    for (String y_m:_sql.getMensaje()) {
                        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Vehiclestate".concat(String.valueOf(_tab)).concat("- ").concat(y_m));
                        o_Mensaje.add(">Error VehicleState".concat(String.valueOf(_tab)).concat(" - ").concat(y_m));
                        x_msg=true;
                    }     
                    return y_ist*-1;
                } else return y_ist;
            } catch (Exception e) {
                throw new Exception(e.getMessage()); 
            }   
        }); 
    }

    @Override
    public void close() {
        //INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Rutina de Desconectar SQL Server");
        o_SQL1.ifPresent(sql->sql.close());
        o_SQL2.ifPresent(sql->sql.close());
        o_SQL3.ifPresent(sql->sql.close());
    }
            
    public int[] Registra_Vehiclestate(List<StringBuilder[]> _DOC) throws Exception {
        int[] y_rni={0,0,0,0};
        try {
            if (o_SQL1.isPresent()) y_rni[1]=o_SQLN.Rutina(o_SQL1.get(), _DOC);                    //VehicleState
            if (o_SQL2.isPresent()) y_rni[2]=o_SQLS.Rutina(o_SQL2.get(), _DOC, this.DiaSemana());  //VehicleState_semanal
            if (o_SQL3.isPresent()) y_rni[3]=o_SQLS.Rutina(o_SQL3.get(), _DOC, this.DiaSemana());  //VehicleState_semanal
            if (x_msg) y_rni[0]=1;
            return y_rni;
        } catch(SQLException e) {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] SQL Error de conexion - ".concat(e.getMessage()));
            throw new Exception(e.getMessage()); 
        }
    }
    
    public String[] getMensajes() {
        String[] y_msg=o_Mensaje.toArray(new String[o_Mensaje.size()]);
        o_Mensaje.clear();
        x_msg=false;
        return y_msg;
    }

    /*******PRIVADAS *****/

                
    private int DiaSemana() {
        Calendar y_fecha = Calendar.getInstance(); 
        return y_fecha.get(Calendar.DAY_OF_WEEK)-1;		
    }
    
    //Interfaces Funcionales
   
    @FunctionalInterface
    private interface SQLRutinaN {
        public int Rutina(PROTON_I_SQLTablaN _sql, List<StringBuilder[]> _doc) throws Exception;
    }

    @FunctionalInterface
    private interface SQLRutinaS {
        public int Rutina(PROTON_I_SQLTablaS _sql, List<StringBuilder[]> _doc, int _tab) throws Exception;
    }
}


