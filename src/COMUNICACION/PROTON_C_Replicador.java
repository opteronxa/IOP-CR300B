package COMUNICACION;

/**
 * 
 * @author R.Cuevas
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public final class PROTON_C_Replicador implements AutoCloseable {
    
    private final List<DatagramSocket> o_SketCli=new ArrayList<>();  
   
    public PROTON_C_Replicador(List<String> _electrones, INTERFACE.PROTON_W_Ventana _vent) throws Exception {
        for (String y_ele:_electrones) {
            try {
                String[] y_url=y_ele.split(":");
                InetAddress y_dirIP1=InetAddress.getByName(y_url[0]);
                DatagramSocket o_udpsck=new DatagramSocket();
                try {
                    o_udpsck.connect(y_dirIP1, Integer.valueOf(y_url[1]));
                    o_SketCli.add(o_udpsck);       
                    _vent.setVista(">Electron: ".concat(y_ele).concat(" OK..."));
                } catch (NumberFormatException e2 ) {
                    o_SketCli.remove(o_udpsck);
                    _vent.setVista("ERROR en Electron: ".concat(y_ele));
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Replicador] Error de Conexion Electron ".concat(y_ele));
                } 
           } catch (SocketException | UnknownHostException e1) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Replicador] Error de socket UDP Electron ".concat(e1.getMessage()));
                throw new Exception("Error de socket UDP Electron ".concat(e1.getMessage()));
           }
        }
        if (o_SketCli.isEmpty()) throw new Exception("No se pudieron conectar los Electrones");
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("Replicador  Electron Ok");
    }
    
    public boolean Replicar(String _cadena) {
        for (DatagramSocket y_skt:o_SketCli) {
            try {
                y_skt.send(new DatagramPacket(_cadena.getBytes(), _cadena.length()));   
            } catch (IOException e) {
                o_SketCli.remove(y_skt);
                if (o_SketCli.isEmpty()) return false;
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Replicador] Error al Enviar Trama UDP a: ".concat(e.getMessage()));
            }
        }
        return true;
    }
    
    @Override
    public void close() {
        for (DatagramSocket y_skt:o_SketCli) {
            try {
                y_skt.close();
            } catch (Exception e) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Replicador] Error al cierre de sockets UDP - ".concat(e.getMessage()));
            }
        }
        o_SketCli.clear();
    }      
        
}
