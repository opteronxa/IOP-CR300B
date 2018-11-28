package COMUNICACION;

/**
 * 
 * @author R.Cuevas
 */
    
import java.io.Closeable;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;



public final class PROTON_C_Escuchador extends Thread { //Clase
    //objetos
    private final INTERFACE.PROTON_W_Ventana o_Vent;
    private final ArrayBlockingQueue<String> o_Buffer;
    private final SynchronousQueue<String> o_Apagar=new SynchronousQueue<>();
    private final COMUNICACION.PROTON_C_Movimientos o_Movimientos;
    private Closeable o_CerrarSerSocket=null;
    private volatile boolean x_Aceptar=false;
    private volatile boolean x_Estatus=false;
    private volatile boolean x_Apagar=false;
    private volatile boolean x_apagando=false;
    private final int x_pto;
    private final String x_wel;
    private final Integer[] x_ttl;
    private final int x_maximo;
    private final boolean x_ack;
    private final boolean x_cks;
    
    public  PROTON_C_Escuchador(INTERFACE.PROTON_W_Ventana _vent,   
                              ArrayBlockingQueue<String> _buffer,
                              CountDownLatch _sincroniza,
                              COMUNICACION.PROTON_C_Movimientos _movimientos,
  	  	              ARCHIVOS.PROTON_S_ArchivoINI _ini) {
        o_Buffer =_buffer;
    	o_Vent =_vent;
        o_Movimientos=_movimientos;
        x_pto=_ini.getPuerto();
        x_ttl =_ini.getTTL();
        x_wel =_ini.getWelcome();
        x_maximo=_ini.getMaximo();
        x_ack=_ini.getAck();
        x_cks=_ini.getCheck();
    }
  
    public void Apagar_Escuchador(){
        this.x_apagando=true;
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador]- [OFF] Peticion de OFF Recibida"); 
        o_Vent.setAviso("Apagar Escuchador Recibida...");
        this.o_Movimientos.SetPermisoAcoplar(false);
        x_Aceptar=false;
        try {
            synchronized(this) {
                this.Cerrar_Hilos();
                x_Apagar=true;
                if (o_CerrarSerSocket!=null) o_CerrarSerSocket.close();
            }    
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] Server socket OFF"); 
            this.o_Apagar.put("1");
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] Hilo OFF");
        } catch (IOException | InterruptedException e1) {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] Error en Rutina Shutdown - " + e1.getMessage());    
        }
    }   

    public boolean getEstatus() {
        return this.x_Estatus;
    }    
    
    public void Rechazar(boolean _rech){
        if (this.x_apagando) return;
        try {
            synchronized(this) {
                if (_rech) {
                    x_Aceptar=false;
                    o_Movimientos.SetPermisoAcoplar(false);
                    this.Cerrar_Hilos();
                } else {
                    o_Movimientos.SetPermisoAcoplar(true);
                    this.x_Aceptar=true;
                }
            }    
        } catch (Exception e) {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] Error en Rutina Rechazar - " + e.getMessage()); 
        }    
    }  
    
    @Override
    public void run() {
        int y_ssk=0;
        do {
            try (ServerSocket o_SerSocket=new ServerSocket(this.x_pto)) {
                y_ssk=1;
                o_CerrarSerSocket=o_SerSocket;
                o_Vent.setVista(">Puerto ".concat(String.valueOf(x_pto)).concat(" Abierto..."));
                o_Vent.setVista(">Escuchador OK..." ); 
                x_Estatus=true;
                while (!x_Apagar) {
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][A] Atendiendo Serversocket");
 /**espera**/       Socket o_Socket = o_SerSocket.accept();                    
                    String y_ip=o_Socket.getInetAddress().toString().concat(":").concat(String.valueOf(o_Socket.getPort())).substring(1);
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][B]Conexion Atendida ".concat(": ").concat(y_ip));
                    if (x_Aceptar) {
                        int y_att=o_Movimientos.getTotalConectadas();
                        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][C]Conexion Numero ".concat(": ").concat(String.valueOf(y_att+1)));
                        if (y_att<x_maximo) {
                            if ((y_att+1)>=x_maximo) {
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][G] LLego al limite de ".concat(String.valueOf(x_maximo)));
                                o_Vent.setVista("<Limite de ".concat(String.valueOf(x_maximo)).concat(" Conexiones"));
                            }
                            try {
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][D]Previo a Aduana en ".concat(": ").concat(String.valueOf(y_att+1)));
                                new Aduana(o_Socket, y_ip).start();
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][E]Siguiente Peticion");
                            } catch (Exception e2) {
                                this.CierraSock(o_Socket, y_ip);
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][H] Error en ejecucion de Aduana");
                            }    
                        } else {
                            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][I] Conexion rechazada por limite maximo ".concat(y_ip));
                            o_Vent.setVista("<Conexion ".concat(y_ip).concat(" rechazada por maximo"));
                            this.CierraSock(o_Socket, y_ip);
                        }
                    } else {
                        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][J] Conexiones en Rechazo ".concat(y_ip));
                        this.CierraSock(o_Socket, y_ip);
                    }  
                }
            } catch(Exception e1) { 
                if (!x_Apagar) {
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][K] ServerSocket en Estado de Escepcion");
                    x_Apagar=this.Error_ServerSocket(y_ssk);
                }
                y_ssk=(++y_ssk)%1000;
            }
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][L] ** Server Socket Close");
        } while (!x_Apagar);  
        try { 
            o_Buffer.put("OFF!");
            o_Vent.setVista("<Escuchador OFF!");
        } catch (InterruptedException e) {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][M] Error del Cierre de Escuchador (cerrar por S.O.)");
            x_Estatus=false;
            o_Vent.setVista("<Error del Cierre de Escuchador (cerrar por S.O.)"); 
            o_Vent.Cambia_Estatus("FAT");
        }    
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] ** Escuchador APAGADO");
        o_Apagar.poll();
        this.x_Estatus=false;
    }
    
    protected void CierraSock(Socket _sock, String _ip) {
        try {
            _sock.shutdownInput();
            _sock.close();
        } catch (IOException e3){
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] Error en cierre de socket: ".concat(_ip));
        } 
    }
        
    private  boolean Error_ServerSocket(int _ite) {  
        try {
            if (o_Apagar.poll(1, TimeUnit.SECONDS)!=null) return true;
            x_Estatus=false;
            o_Vent.Cambia_Estatus("TCP");
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador] Error de Puerto TCP ".concat(String.valueOf(x_pto)));
            o_Vent.setVista("<Reiniciando Socket TCP en 10 Seg... ");
            for (int i=0;i<10;i++) {
                if (o_Apagar.poll(1, TimeUnit.SECONDS)!=null) return true;
            } 
        } catch (InterruptedException e) {
            x_Estatus=false;
            o_Vent.setVista("<Error del Cierre de Escuchador (cerrar por S.O.)"); 
            o_Vent.Cambia_Estatus("FAT");
            return true; 
        }
        if (_ite>0) o_Vent.setVista("<Error del Socket TCP ".concat(String.valueOf(_ite)).concat("/10"));
        else  o_Vent.setVista("<PUERTO TCP ".concat(String.valueOf(x_pto)).concat(" Ocupado por otro proceso"));
        return false;
    } 
    
    private void Cerrar_Hilos() {
        o_Movimientos.setApagaTodo();
        try {
            do {
                if (!o_Movimientos.getHayAcoplados()) break;
                o_Vent.setVista("...");
            } while(true);    
            o_Vent.setVista("<Todas las Conexiones Fueron Apagadas");
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][P] Todos los Enlaces ya estan apagados");
            o_Buffer.clear();
        } catch (Exception e) {
            x_Estatus=false;
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Escuchador][Q] Error del Cierre de Hilos (cerrar por S.O.)");
            o_Vent.setVista("<Error del Cierre de Hilos (cerrar por S.O.)"); 
            o_Vent.Cambia_Estatus("FAT");
        }  
    }  
    
    
  //********Clase Interna ****************
    
    private class Aduana extends Thread {
        
        private final String x_ip;
        private final Socket x_soc;
        
        public Aduana(Socket _soc, String _ip) {
            x_soc=_soc;
            x_ip=_ip;
            o_Vent.setVista(">Conexion ".concat(_ip));
        }
            
        @Override
        public void run() {
            boolean y_csk=false;
            if (!x_apagando) {
                o_Vent.Solicitudes(true);
                try {
                    x_soc.setSoTimeout(x_ttl[0]*1000);
                    BASEDATOS.PROTON_C_ProtocoloCR300B_IO o_ProtoID=new BASEDATOS.PROTON_C_ProtocoloCR300B_IO(x_soc, x_ack, x_cks, x_wel);
                    String y_trama=o_ProtoID.Recibir();
                    if (!x_apagando) {
                        switch (y_trama) {
                            case "#":                                           //Stream final (se cerro conexion)
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Aduana] Movil se desconecto antes de Registrar ".concat(x_ip));
                                o_Vent.setVista("<Movil Desconectado antes de registrar".concat(x_ip));                                               
                                break;
                            case "%":                                           //Error ACK 
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Aduana] Movil con error de Ack ".concat(x_ip));
                                o_Vent.setVista("<Movil falla en envio ACK: ".concat(x_ip));   
                                break;
                            case "&":                                           //Duplicado
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Aduana] Movil con trama duplicada ".concat(x_ip));
                                o_Vent.setVista("<Movil Trama duplicada: ".concat(x_ip));   
                                break;
                            case "":  
                                 INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Aduana] trama Invalida ".concat(x_ip));
                                o_Vent.setVista("<Movil envio Trama Invalida: ".concat(x_ip));   
                                break;
                            case "^":
                                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Aduana] Conexion ".concat(x_ip)
                                                                                          .concat(" ITTL:")
                                                                                          .concat(x_ip)
                                                                                          .concat(String.valueOf(x_ttl[0]))
                                                                                          .concat(" Seg. Expiro"));
                                o_Vent.setAviso("ITTL ".concat(String.valueOf(x_ttl[0])).concat(" seg. Expiro: ").concat(x_ip));
                                break;
                            default:                        
                                x_soc.setSoTimeout(x_ttl[1]*1000);
                                /***********/
                                String y_vid=o_ProtoID.getVId();
                                y_csk=o_Movimientos.setAcoplar(y_vid, 
                                                               x_soc,
                                                               x_ip,
                                                               y_trama,
                                                               new COMUNICACION.PROTON_C_Enlace(o_Movimientos,
                                                                                                o_Buffer,
                                                                                                o_ProtoID,
                                                                                                x_soc.getInetAddress().toString()));
                                /**********/
                                break;
                                
                        }        
                    } 
                } catch (Exception e) {
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Aduana] Error de Socket no reconocible ".concat(x_ip));
                    o_Vent.setAviso("Error de Socket: ".concat(x_ip));
                }
                o_Vent.Solicitudes(false);
            }  
            if (!y_csk) CierraSock(x_soc, x_ip);  
        }    
    }    

}    

