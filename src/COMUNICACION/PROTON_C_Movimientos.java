package COMUNICACION;

import java.net.Socket;
import java.util.Optional;

/**
 *
 * @author R.Cuevas
 */

public class PROTON_C_Movimientos {
    
    private final COMUNICACION.PROTON_C_Moviles o_Moviles;
    private final INTERFACE.PROTON_W_Ventana o_Vent;
    private final BITACORA.PROTON_C_Estadisticas o_Estadistica;
    private volatile boolean x_acoplar=false;
    
    public PROTON_C_Movimientos(INTERFACE.PROTON_W_Ventana _vent,
                                int _maxiota,
                                BITACORA.PROTON_C_Estadisticas _distic) {
        o_Vent=_vent;           
        o_Moviles=new COMUNICACION.PROTON_C_Moviles(_maxiota);
        o_Estadistica=_distic;
    }

//-------------Rutina para Importar---------------------------------------------
    
    public Optional<String[]> AltaVID(String _vid, String _ota) {
        if (_vid!=null) {
            if (!_vid.isEmpty()) {
                synchronized(this) {
                    if (_ota==null) return o_Moviles.setRegistra(_vid);
                    if (_ota.isEmpty()) return o_Moviles.setRegistra(_vid);
                    return o_Moviles.setRegistra(_vid, _ota);
                }    
            }
        }    
        return Optional.empty();
    }
     
    public synchronized void Depurar() {
        if (o_Vent.Altas(o_Moviles.setDepura())) o_Vent.MovilesConetados(o_Moviles.getTotalAcopladas());
    }
    
 //---------------------- rutinas para escuchador-------------------------------

    public void SetPermisoAcoplar(boolean _onf) {
        x_acoplar=_onf;
    }
    
    public int getTotalConectadas(){
        return o_Moviles.getTotalAcopladas();
    }
     
    public boolean getHayAcoplados() throws Exception {
        try {
            Thread.sleep(1000);
            return o_Moviles.getTotalAcopladas()>0;
        } catch (InterruptedException e) {
            throw new Exception(e.getMessage());
        }
    }  
   
    public void setApagaTodo() {
        this.x_acoplar=false;
        synchronized(this) {
            o_Moviles.setApagarTodo();
        }    
    }
    
    public synchronized boolean setAcoplar(String _vid,
                                           Socket _soc,
                                           String _ip,
                                           String _trama,
                                           COMUNICACION.PROTON_C_Enlace _enlace) { 
        if (!this.x_acoplar) return false;
        String y_msg;
        int y_mot=o_Moviles.setAcoplarEnlace(_vid, _soc, _ip, _trama, _enlace);
        switch (y_mot) {
            case -2: y_msg=">VID: ".concat(_vid).concat(" Error de Colision"); break;
            case -1: y_msg=">VID: ".concat(_vid).concat(" No autorizado"); break; 
            case  0: y_msg=">VID: ".concat(_vid).concat(" Conectada"); break;
            default: y_msg=">Colision: ".concat(_vid).concat(o_Moviles.getVerCambioIP(_vid)); break;           
        }   
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Movimientos] ".concat(_ip).concat(" - ").concat(y_msg));
        o_Vent.setVista(y_msg);  
        if (y_mot>=0) {
            o_Vent.MovilesConetados(o_Moviles.getTotalAcopladas());
            _enlace.start();
            return true;
        }
        return false;
    }  
    
    public synchronized void setDesacoplar(String _vid,
                                           COMUNICACION.PROTON_C_Enlace _enlace,
                                           int _mot,
                                           int _cons,
                                           long[] _timei,
                                           int _dup,
                                           long[] _up,
                                           long[] _do) {
        int y_tac=o_Moviles.setDesacoplar(_vid, _enlace, _dup, _up, _do);
        o_Vent.MovilesConetados(y_tac);
        o_Vent.Dups(_dup);
        switch (_mot) {
            case 0: o_Vent.setAviso("<".concat(_vid).concat(" [Thread No Iniciado]")); break;
            case 1: o_Vent.setAviso("<".concat(_vid).concat(" [Desconecion Remota]")); break;
            case 2: o_Vent.setAviso("<".concat(_vid).concat(" [Buffer Principal Lleno]")); break;
            case 3: o_Vent.setAviso("<".concat(_vid).concat(" [Time Out Conexion]")); break;   
            case 4: o_Vent.setAviso("<".concat(_vid).concat(" [Error de Acknowledge]")); break;  
            case 5: o_Vent.setAviso("<".concat(_vid).concat(" [5 Tramas invalidas]")); break;      
            default:o_Vent.setAviso("<".concat(_vid).concat(" [Fallo de Enlace o Flujo Irreconocible]")); break;    
        }  
        o_Estadistica.setEstadistica(_vid, _timei, o_Moviles.getTotal(), o_Moviles.getTotalVid(_vid));
        if (o_Estadistica.getMensaje()) o_Vent.setVista(o_Estadistica.getDescripcion());
    } 
    //Motivo 
    //0.- No iniciado 
    //1.- Desconexion Remota
    //2.- Buffer lleno
    //3.- Socket Off
    //4.- Ack no se pudo enviar
    //5.- 10 Tramas invalidas
    //6.- Fallo Enlace o trama invalida
    
   
}



