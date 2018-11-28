package BITACORA;

/**
 *
 * @author R.Cuevas.B.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

//S-SINGLETON
public class PROTON_S_Log {
    
    private static PROTON_S_Log o_LOG;
    private Archivo_LOG o_WLog=null;
    private boolean x_onof=false;

    public static PROTON_S_Log getInstancia() throws Exception {
        if (o_LOG==null) o_LOG=new BITACORA.PROTON_S_Log();
        else throw new Exception ("No puede Duplicarse Objeto LOG");
        return o_LOG;
    }

    public void On_Log(String _pro, String _ver, String _iop) throws Exception {
        try {
            this.o_WLog = new Archivo_LOG("[MAIN] ** START VERSION ".concat(_ver)
                                                                    .concat(" : ")
                                                                    .concat(_pro)
                                                                    .concat(" : ")
                                                                    .concat(_iop)
                                                                    .concat(" **"));
            x_onof=true;
        } catch (Exception e) {
            if (e.getMessage()!=null) throw new Exception (e.getMessage());
            throw new Exception ("No puede crearse archivo LOG");
        }
    }
    
    public void Off_Log(String _ol) {
        if (this.o_WLog!=null) this.o_WLog.DesactivaLogs();
        x_onof=false;
    }
    
    public synchronized void Bitacora(String _log) {
        if (!x_onof) return;
        try {
            o_WLog.Escribe_LOG(_log);
        } catch (Exception e) {
            x_onof=false;
        }    
    }

//***********************************************************/    
    
   public class Archivo_LOG {
    
        //Variables Gobales
        private final File x_dir;
        private FileWriter o_Escribe=null;
        private PrintWriter o_Imprime=null;
        private long x_tam=1;

        public Archivo_LOG(String _msg) throws Exception { 	//Constructor
            try {
                x_dir = new File(new File(".").getCanonicalPath().concat(File.separator).concat("logs"));
                if(!x_dir.exists()) x_dir.mkdirs();
                this.setArchivo();
                o_Imprime.println(_msg);      
            } catch(Exception e) {
                if (e.getMessage()!=null) throw new Exception("Creacion de archivo Log - ".concat(e.getMessage()));
                throw new Exception("ERROR de Direcctorio LOG: ".concat(e.getMessage()));
            }
        }

        protected void DesactivaLogs() {
            try {
                if (o_Imprime!=null) o_Imprime.close();
                if (o_Escribe!=null) o_Escribe.close();
            } catch (IOException e) {
                System.out.println("ERROR en Cerrando Bitacora ".concat(e.getMessage()));
            } 
            o_Escribe=null;
            o_Imprime=null;
        } 

        protected void Escribe_LOG(String _mensaje) throws Exception {
            try {
                if (this.x_tam==0) this.setArchivo();
                o_Imprime.println(_mensaje);
                o_Imprime.flush();
                x_tam=(++x_tam)%100000;
            }  catch (Exception e) {
                if (e.getMessage()!=null) {
                    System.out.println(e.getMessage());
                    throw new Exception("Escritura de Log - ".concat(e.getMessage()));
                }
                throw new Exception("Escritura de Log");
            }
        }


        private void setArchivo() throws Exception {
            try {
                x_tam=0;
                if (o_Imprime!=null) o_Imprime.close();
                if (o_Escribe!=null) o_Escribe.close();
                o_Escribe=new FileWriter(new File(x_dir.getCanonicalPath()
                                        .concat(File.separator)
                                        .concat("Bitacora_")
                                        .concat(String.valueOf(Calendar.getInstance().getTimeInMillis()))
                                        .concat(".log")),true);
                o_Imprime = new PrintWriter(new BufferedWriter(o_Escribe));
            } catch (IOException e) {
                if (e.getMessage()!=null) throw new Exception("Creacion de archivo Log - ".concat(e.getMessage()));
                throw new Exception("Creacion de archivo Log");
            }    
        }

    }

    
}