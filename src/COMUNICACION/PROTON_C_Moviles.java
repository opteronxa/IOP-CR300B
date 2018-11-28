package COMUNICACION;

import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author R.Cuevas
 */
public class PROTON_C_Moviles {
    
    private final Map<String, Antena> o_Registros=new ConcurrentHashMap<>();
    private volatile int x_Acoplados=0;
    private long x_totas=0;
    private long x_ttramas=0;
    private long x_tKbytedo;
    private long x_tKbyteup=0;
    private long x_tdup=0;
    private final int x_maxiota;
        

    public PROTON_C_Moviles(int _max) {
        x_maxiota=_max;
    }
        
    public Optional<String[]> setRegistra(String _vid) {
        Antena y_an=o_Registros.putIfAbsent(_vid, new Antena());
        if (y_an!=null) y_an.setMarcar(false);
        return Optional.empty();
    }   
    
    public Optional<String[]> setRegistra(String _vid, String _ota) {
        Antena y_an=o_Registros.putIfAbsent(_vid, new Antena());
        if (y_an!=null) y_an.setMarcar(false);
        else y_an=o_Registros.get(_vid);
        return this.EnviaOTA(y_an, _vid, _ota);
    } 
    
    private Optional<String[]> EnviaOTA(Antena _ant, String _vid, String _ota) {  
        short y_eota=_ant.EnviaOTA(_ota);
        switch (y_eota) {
            case-2:
                return Optional.of(new String[]{
                    "ER*".concat(String.valueOf(Calendar.getInstance().getTimeInMillis()/1000)),
                    "<OTA a ".concat(_vid).concat(": [ERROR] ").concat(_ota)    
                });
            case -1:
                return Optional.of(new String[]{
                    (_ota.length()>24)?"N/E-->".concat(_ota.substring(0,24)):"N/E-->".concat(_ota),
                    "<OTA a ".concat(_vid).concat(": [N/E] Excedio Numero de Intentos")
                });
            case 0:
                return Optional.empty();
            case 1:
                return Optional.of(new String[]{
                    "OK*".concat(String.valueOf(Calendar.getInstance().getTimeInMillis()/1000)),
                    "<OTA a ".concat(_vid).concat(": [OK] ").concat(_ota)
                });
            default:
                return Optional.of(new String[]{
                    "Pendiente...",
                    "<OTA a ".concat(_vid).concat(": [Desacoplado] intento-")
                             .concat(String.valueOf(y_eota)).concat(" ").concat(_ota)
                });        
        }
    }   
    
    public int setDepura() {
        o_Registros.entrySet().forEach((y_ent) -> {
            Antena y_an=y_ent.getValue();
            if (y_an.getMarcar()) y_an.setQuitar();
            else y_ent.getValue().setMarcar(true);
            if (y_an.getQuitar()) {
                if (y_ent.getValue().getEstaActivo()) y_ent.getValue().setApagaEnlace();
                else o_Registros.remove(y_ent.getKey());
            } 
        });
        return o_Registros.size();
    }
        
    public void setApagarTodo() {
        o_Registros.entrySet().forEach(y_ent-> y_ent.getValue().setApagaEnlace());
    }
       
        
    public int getTotalAcopladas() {
        return this.x_Acoplados;
    }
       
    public int setAcoplarEnlace(String _vid,
                                Socket _soc,
                                String _ip,
                                String _trama,
                                COMUNICACION.PROTON_C_Enlace _enlace) {     //(-2)Error colision (-1)sin alta (0)Nuevo (>1)Colision
        if (!o_Registros.containsKey(_vid)) return -1;    
        int[] y_csec=o_Registros.get(_vid).setEnlaceAcoplar(_soc, _enlace, _ip);
        if (y_csec[0]>=0) {
            _enlace.setProceder(y_csec, _trama);
            ++x_Acoplados;
        } 
        return y_csec[0];
    }  
        
    public String getVerCambioIP(String _vid) {
        if (o_Registros.containsKey(_vid)) o_Registros.get(_vid).getIPs();
        return null;
    }
        
    public int setDesacoplar(String _vid,
                             COMUNICACION.PROTON_C_Enlace _enlace,
                             int _dup,
                             long[] _up,
                             long[] _do) {
        this.x_tdup+=_dup;
        this.x_tKbyteup+=_up[0];
        this.x_tKbytedo+=_do[0];
        this.x_totas+=_up[1];
        this.x_ttramas+=_do[1];
//System.out.println("Total: " + _dup + " * " + _up[0] + " - " + _do[0] + " * " + _up[1] + " - " + _do[1]);
        if (o_Registros.containsKey(_vid)) o_Registros.get(_vid).setEnlaceDesacoplar(_enlace, _dup, _up, _do);
        --x_Acoplados;
        return x_Acoplados;
    } 
        
    public long[] getTotal(){
        long[] y_total={x_tdup,
                        x_tKbyteup,
                        x_tKbytedo,
                        x_totas,
                        x_ttramas};
        return y_total;        
    }
    
    public long[] getTotalVid(String _vid){
        return o_Registros.get(_vid).getTotalVid();
    }
    

    
/******************CLASE INTERNA ******************************************/    
    
    private class Antena {
        
        private volatile COMUNICACION.PROTON_C_Enlace o_Enlace=null;
        private volatile boolean x_marcar=false;
        private volatile boolean x_quitar=false;
        private final String x_pen="Pendiente...";
        private Socket o_Socket=null;
        private String x_ipa="";
        private String x_ipn="";
        private String x_ota=null;
        private short x_niota=0;
        private long x_otasup=0;
        private long x_otas=0;
        private long x_tramas=0;
        private long x_Kbyteup=0;
        private long x_Kbytedo=0;
        private long x_dup=0;
        
        private boolean getEstaActivo() {
            return o_Enlace!=null;
        } 
        
        private int[] setEnlaceAcoplar(Socket _soc,
                                       COMUNICACION.PROTON_C_Enlace _enlace,
                                       String _ip) {
            int[] y_ava={0,0};
            if (!x_quitar) {
                if (o_Enlace!=null) {                                           //colision 
                    y_ava=o_Enlace.SetColision(_ip);                            //[consecutivo,Checksum]     
                    if (!setApagaEnlace()) {                                    //error apagando la colision
                        y_ava[0]=-2;
                        return y_ava;
                    } else x_ipa=x_ipn;
                }    
                x_ipn=_ip;
                o_Enlace=_enlace;
                o_Socket=_soc;
            } else y_ava[0]=-1;    
            return y_ava;
        }
                
        private void setEnlaceDesacoplar(COMUNICACION.PROTON_C_Enlace _enlace,
                                         int _dup,
                                         long[] _up,
                                         long[] _do) {
            this.x_dup+=_dup;
            this.x_Kbyteup+=_up[0];
            this.x_Kbytedo+=_do[0];
            this.x_otas+=_up[1];
            this.x_tramas+=_do[1];
            if (!o_Enlace.equals(_enlace)) return;
            setApagaEnlace();
            o_Enlace=null;
        } 
        
        public long[] getTotalVid(){
            long[] y_total={x_dup,
                            x_Kbyteup,
                            x_Kbytedo,
                            x_otas,
                            x_tramas};
            return y_total;        
        }
                
        private Boolean setApagaEnlace() {
            if (o_Enlace==null) return true;
            if (o_Socket==null) return true;
            if (!o_Socket.isClosed()) {
                try {
                    o_Socket.close();
                } catch (IOException e) {
                    return false;
                }    
            }    
            o_Socket=null;
            return true;
        } 
        
        private String getIPs() {
            return x_ipa.concat(" -> ").concat(x_ipn);
        }
        
    //----envio otas-----------  
        private short EnviaOTA(String _ota) {                                   //return -2, -1, 0, 1, 2
            if (!_ota.equals(x_pen)) {
                x_ota=_ota;
                x_niota=0;
            }   
            if (o_Enlace!=null) {
                if (x_niota>x_maxiota) return -1;                               //(-1) maximo intento ota
                else {
                    if (o_Enlace.Pollear(x_ota)) {                              //(1) Envio con exito
                        ++this.x_otasup;
                        return 1;   
                    } else return -2;                                           //(-2)Error envio
                }    
            } else {    
                ++x_niota;
                return (x_niota>1)?(short)2:(short)0;                           //(0)pendiente (2)primer intento pendiente
            }                                                                   
        }    
         
        private void setMarcar(boolean _mar) {
            x_marcar=_mar;
            if ((!_mar)&&x_quitar) x_quitar=false;
        }
           
        private boolean getMarcar() {
            return x_marcar; 
        }
        
        private void setQuitar() {
            x_quitar=true; 
        }
        
        private boolean getQuitar() {
            return x_quitar; 
        }
    }
    
   
}



