package COMUNICACION;

import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author R.Cuevas
 */
public class PROTON_C_Enlace extends Thread {  
    
    //Objetos
    private final BASEDATOS.PROTON_C_ProtocoloCR300B_IO o_Proto;
    private final COMUNICACION.PROTON_C_Movimientos o_Movimientos;
    private final ArrayBlockingQueue<String> o_Buffer;
    //Variables Gobales
    private volatile Boolean x_apagar=false;
    private int x_consecutivo=0;
    private final long[] x_time=new long[3];
    private final String x_ip;

    public PROTON_C_Enlace(COMUNICACION.PROTON_C_Movimientos _movi,
                           ArrayBlockingQueue<String> _buffer,
                           BASEDATOS.PROTON_C_ProtocoloCR300B_IO _proto,
                           String _ip) {
        o_Proto=_proto;
        o_Movimientos=_movi;
        o_Buffer=_buffer;
        x_ip=_ip;
        x_time[0]=(Calendar.getInstance().getTimeInMillis())/1000;
        x_time[1]=x_time[0];
        x_time[2]=x_time[0];
    }
        
    public int[] SetColision(String _ip) { 
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Enlace] Colision de VId ".concat(o_Proto.getVId()).concat(" @ ").concat(x_ip).concat(" --> ").concat(_ip));
        int[] y_col={
            this.x_consecutivo,
            this.o_Proto.getCheck()
        };
        return y_col;
    }    
        
    public void setProceder(int[] _con, String _tramainicial) {
        try {
            x_consecutivo=_con[0];
            if (this.o_Proto.setCheck(_con[1])) x_apagar=this.Encolar(_tramainicial);
        } catch (Exception e) {
            x_apagar=true;
        }    
    }

    public boolean Pollear(String _ota) {
        if (x_apagar) return false;
        try {   
            Optional<String> o_ota = Optional.ofNullable(o_Proto.ValidaOTA(_ota));
            if (!o_ota.isPresent()) return false;
            if (!o_Proto.Enviar(o_ota.get())) return false;
            long y_envio=(Calendar.getInstance().getTimeInMillis())/1000;
            return (o_Buffer.offer("O!".concat(o_Proto.getVId())
                    .concat("!").concat(String.valueOf(y_envio))
                    .concat("!0!OUT:").concat(_ota)
                    .concat("!").concat(x_ip), 1, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Enlace] Error  de Poll ".concat(this.o_Proto.getVId()).concat(" -> ").concat(_ota));
            return false;
        }    
    }


    @Override
    public void run() {
        int y_mot=0;
        int y_nlo=0;
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[ENLACE] Conexion ".concat(" VId:").concat(this.o_Proto.getVId())
                                                                  .concat(" IP:").concat(x_ip)
                                                                  .concat(" Iniciada"));
        try {
            while ((!x_apagar)&&(y_mot==0)) {
                String y_trama=o_Proto.Recibir();
                switch (y_trama) {
                    case "#": y_mot=1; break;                                             //Stream final (se cerro conexion)
                    case "%": y_mot=4; break;                                             //Error ACK 
                    case "&": break;                                                      //Duplicado
                    case "^": y_mot=3; break;                                             //Time Out
                    case "":                                                              //trama Invalida
                        if (++y_nlo>10) y_mot=5;                                          //5 intentos trama invalida
                        break;
                    default:                                                              //trama correcta
                        if (y_nlo>0) y_nlo=0;
                        if (!o_Proto.Reconoce_HeartBeat(y_trama)) 
                            if (this.Encolar(y_trama)) y_mot=2;
                        break;
                }    
            } 
        } catch (Exception e) {
            y_mot=6;
        } finally {  
            x_apagar=true;
            x_time[2]=Calendar.getInstance().getTimeInMillis()/1000;
            o_Movimientos.setDesacoplar(o_Proto.getVId(),
                                        this,
                                        y_mot,
                                        x_consecutivo,
                                        x_time,
                                        this.o_Proto.getDuplicados(),
                                        this.o_Proto.getSubida(),
                                        this.o_Proto.getBajada());
        }    
    }
    //mot-Motivo 
    //0.- No iniciado 
    //1.- Desconexion Remota
    //2.- Buffer lleno
    //3.- Socket Off
    //4.- Ack no se pudo enviar
    //5.- acumulo 10 Tramas invalidas
    //6.- Fallo Enlace o trama invalida
    
    
    private boolean Encolar(String _trama) throws Exception {                       
        x_time[1]=(Calendar.getInstance().getTimeInMillis())/1000;
        try {
            int y_lops=0;
            do {
                if (o_Buffer.offer("T!".concat(this.o_Proto.getVId()) 
                            .concat("!").concat(String.valueOf(x_time[2]))
                            .concat("!").concat(String.valueOf(x_consecutivo)) 
                            .concat("!").concat(_trama)
                            .concat("!").concat(x_ip), 1, TimeUnit.SECONDS)) break;
                else ++y_lops;
            } while (y_lops<10);    
            if (y_lops>=10) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[ENLACES]- Serie:".concat(this.o_Proto.getVId()).concat(" Buffer Lleno"));
                return true;
            }    
            ++x_consecutivo;
        } catch(InterruptedException e) {
            throw new Exception("Error de Estructura BUFFER"); 
        }
        return false;
    }  
 

} 