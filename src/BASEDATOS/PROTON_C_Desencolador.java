package BASEDATOS;

/**
 * 
 * @author R.Cuevas
 */

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class PROTON_C_Desencolador extends Thread {
	
    //Objetos	
    private final ArrayBlockingQueue<String> o_Buffer;  
    private final INTERFACE.PROTON_W_Ventana o_Vent;
    private final COMUNICACION.PROTON_C_Escuchador o_Escuchador;
    private final CountDownLatch o_Sincroniza;  
    private final ARCHIVOS.PROTON_S_ArchivoINI o_INI;
    private final BASEDATOS.PROTON_C_SQLRegistro o_Reg;
    //Variables
    //public static volatile int CE=0, CS=0;
    //public static String  x_Trama=new String();
    private volatile boolean x_Estatus=false;
    public static int FLUSH=1;
    public static int BATCH=20;
    public static boolean MVI=false;
    public static String VID="*";
          
    public PROTON_C_Desencolador(COMUNICACION.PROTON_C_Escuchador _escuchador,
                                 ArrayBlockingQueue<String> _buffer,
		                 INTERFACE.PROTON_W_Ventana _vent,
                                 CountDownLatch _sincroniza,
                                 ARCHIVOS.PROTON_C_ArchivosMAP _firmas,
		                 ARCHIVOS.PROTON_S_ArchivoINI _ini) {
        o_Buffer = _buffer;
        o_Escuchador=_escuchador;
        o_Vent=_vent;
        o_Sincroniza=_sincroniza;
        o_INI=_ini;
        if (o_INI.getElectrones().isEmpty()) {
            _vent.setVista(">Replicador a ELECTRON No Solicitado...");
            o_Reg=new BASEDATOS.PROTON_C_SQLRegistro(_firmas, _ini.getTipos()); 
        } else o_Reg=new BASEDATOS.PROTON_C_SQLRegistro(_vent, _firmas, _ini.getTipos(), o_INI.getElectrones()); 
    }
     
    @Override
    public void run() {
        int y_eSQL=0;
        boolean y_off=false;
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("Hebra Desencolador On");
        do { 
            try (BASEDATOS.PROTON_C_SQLExporta o_SQLBaseE = new PROTON_C_SQLExporta(o_Vent, o_INI)) {
                y_eSQL=0;
                o_Vent.setVista(">Desencolador OK...");
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][A] Conexion a SQL Export OK");
                this.getBackup(o_Reg); 
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][B] Recupero Buffer");
                o_Vent.Encoladas(o_Buffer.size());
                o_Escuchador.Rechazar(false);
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] [C] Envia Mandato de Aceptacion a Escuchador");
                if (o_Sincroniza.getCount()>0) o_Sincroniza.countDown();
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] [D] Sincroniza con Escuchador (despierta antes de 10 seg)");
                x_Estatus=true;
                int y_pool=0;
                do {      
                    y_pool=(++y_pool)%BATCH; 
                    if (this.Desencola()) break;
                    if (y_pool==0) {
                        if (o_Reg.getPendientes()) {
                            int[] y_isr=o_SQLBaseE.Registra_Vehiclestate(o_Reg.getRegistros());
                            if (y_isr[0]==1) {
                                for (String y_m:o_SQLBaseE.getMensajes()) {
                                    o_Vent.setAviso(y_m);
                                }    
                            }
                            o_Vent.InseSQL(y_isr[1], y_isr[2],y_isr[3]);
                            o_Reg.Depura();
                            if (o_Vent.Encoladas(o_Buffer.size())&&MVI) MVI=false;
                        }    
                    }    
                } while (true);
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] [E] Salio de Loop Desencolador");
                y_off=true;
            } catch (Exception e1) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][F] Rompio Loop por Error en SQL");
                o_Escuchador.Rechazar(true);
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][G] Envia Mandato de Rechazo a Escuchador");
                while (!o_Buffer.isEmpty()) {
                    y_off=this.Desencola();
                    if (y_off) break;
                }
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][H] Extrajo Buffer para Resguardo");
                this.setBackup(o_Reg);
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][I] Resguardo Buffer");
                o_Reg.Depura();
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][J] Depuro Buffer");
                if (!y_off) {
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][K] Intento de Reconexion SQL");
                    ++y_eSQL;            
                    if (y_eSQL>1000) y_eSQL=1;
                    y_off=e1.getMessage()==null?this.setErrorSQL(y_eSQL, "SQL"):this.setErrorSQL(y_eSQL, e1.getMessage());
                }
            }    
            o_Vent.setVista("<SQL Exportar OFF!");
        } while (!y_off);
        o_Reg.close();
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][L] Apagando Desencolador");
        o_Vent.setVista("<Desencolador OFF!");
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] Desencolador OFF!");
        x_Estatus=false;
    }  
      
    public boolean getEstatus() {
       return this.x_Estatus;
    }          
    
 //***PRIVADAS****
    
    private boolean Desencola() {
        try {
            String  x_Trama=(FLUSH>0)?o_Buffer.poll(FLUSH, TimeUnit.SECONDS):o_Buffer.poll();
            if (x_Trama==null) return false;
            if (x_Trama.equals("OFF!")) return true; 
            String[] y_item=x_Trama.split("!");
            if (y_item.length<6) return false;
            if (y_item[0].equals("T")) o_Vent.recibidas();
            String y_FyH=o_Reg.Interpreta_Trama(y_item[0],
                                                y_item[1],
                                                y_item[2],
                                                y_item[3],
                                                y_item[4],
                                                y_item[5]);
            if (MVI) {
                if (VID.equals("*")) o_Vent.setVista(y_FyH.concat(": ").concat(y_item[4]));
                else if (VID.equals(y_item[1])) o_Vent.setVista(y_FyH.concat(": ").concat(y_item[4]));
            } 
            return false;
        } catch (InterruptedException e){
            if (e.getMessage()!=null) INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][X] Error de Buffer (Queue) - ".concat(e.getMessage()));
            else INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador][X] Error de Buffer (Queue)");
            o_Vent.setVista("<ERROR De Estructura de Buffer (CERRAR por S.O.)");
            return false;           
        }   
    }
    
    private boolean setErrorSQL (int _isql, String _err) {  
        o_Vent.Cambia_Estatus("SQL");
        x_Estatus=false;
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] Error SQL consecutivo [".concat(String.valueOf(_isql))
                                                                                      .concat("] de Conexion SQLBaseE - ")
                                                                                      .concat(_err));
        int y_ts=_isql>10?60:10;
        o_Vent.setAviso("ERROR SQL en Rutina EXPORTA");
        o_Vent.setVista("<".concat(_err));
        o_Vent.setVista(">Reintentanto Conexion SQL Exporta en " + y_ts + " Seg... " );
        try {
            for (int i=0;i<y_ts;i++) {
                String y_bf=o_Buffer.poll(1, TimeUnit.SECONDS);
                if (y_bf!=null) {
                    if (y_bf.equals("OFF!")) return true;
                }
            }
        } catch (InterruptedException e) {
            o_Vent.setVista("<ERROR De Estructura de Buffer (CERRAR por S.O.)");
            return true;
        }   
        return false;
    }    
       
    private void getBackup(BASEDATOS.PROTON_C_SQLRegistro _reg) {
        try {
            _reg.Pone_DOC(new BITACORA.PROTON_C_Backup().getRegistroTXT()); 
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] Recupero el Buffer del TXT");
        } catch (Exception e) {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] Error de recarga de BlockSQL " + e.getMessage());
            o_Vent.setVista("<Error en Recuperacion del Archivo BlockSQL, (Posible perdida de tramas)");
        } 
        o_Vent.setVista(">Recuperacion de Buffer OK...");
    }
        
    private void setBackup(BASEDATOS.PROTON_C_SQLRegistro _registro){
        try{
            int y_tot=new BITACORA.PROTON_C_Backup().setRegistroTXT(_registro.getRegistros());
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] Resguardando Buffer en TXT - Total tramas encoladas:".concat(String.valueOf(y_tot)));
            o_Vent.setVista(">Respaldando RegistroSQL: " + y_tot);
        } catch (Exception e) {
            o_Vent.setVista("<Error Respaldando el BlockSQL.dat (Perdida inminente)");
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Desencolador] Error En Escritura del RegistroSQL - " + e.getMessage());
        }  
    }    
      

}