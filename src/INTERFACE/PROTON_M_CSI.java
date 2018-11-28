package INTERFACE;

import javax.swing.JOptionPane;

/**
 * @author R.Cuevas
**/

public class PROTON_M_CSI {

    public static BITACORA.PROTON_S_Log o_Log;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String y_protocolo="CR300B";
            String y_version="1.6.7.1";
            o_Log=BITACORA.PROTON_S_Log.getInstancia();                                      //singleton;
            ARCHIVOS.PROTON_S_ArchivoINI o_INI=ARCHIVOS.PROTON_S_ArchivoINI.getInstancia();  //singleton
            INTERFACE.PROTON_W_Ventana o_Vent=new PROTON_W_Ventana(y_protocolo, y_version, o_INI); 
            o_Log.On_Log(y_protocolo, y_version, o_INI.Nombre_IOP());
            if (!o_INI.getLog()) o_Log.Off_Log("** LOG APAGADO **");
            o_Vent.EscribeAvisoINI(o_INI.MuestraINI());
            o_Vent.setAviso("      * BIENVENIDO AMIGO *\r\n");
            if (o_INI.getAUTO()) o_Vent.AutoON();
            else o_Vent.setAviso("Comando ON para Encender...");
        } catch (Exception e) {
            if (e.getMessage().startsWith("Error")) JOptionPane.showMessageDialog(null, e.getMessage(), "PROTON ERROR", JOptionPane.ERROR_MESSAGE);
            else JOptionPane.showMessageDialog(null, "!Corrija Proton.INI y Vuelva a Intentarlo!", "PROTON ERROR", JOptionPane.ERROR_MESSAGE);
            System.out.println("ERROR: de Archivo/Directorio: "  + e.getMessage() );
            System.out.println("No se puede Iniciar PROTON sin Acceso al Directorio de Programa"); 
            System.exit(0);
        }  
    }
    
}
    
