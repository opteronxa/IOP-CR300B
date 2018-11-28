package BITACORA;
//Librerias
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author R.Cuevas.B.
 */

//C-Clase
public class PROTON_C_Estadisticas implements AutoCloseable {
    
    private Archivo_Estadisticas o_WEst=null;
    private boolean x_msg=true;
    private String x_desc="";
    private final int duplicado=0;
    private final int kbup=1;
    private final int kbdo=2;
    private final int otas=3;
    private final int tramas=4;
    private final int inicio=0;
    private final int ultimo=1;
    private final int termino=2;
    private boolean x_onof=false;       

    public PROTON_C_Estadisticas(boolean _onof) {
        x_onof=_onof;
        if (_onof) {
            try {
                this.o_WEst = new Archivo_Estadisticas();
            } catch (Exception e) {
                if (e.getMessage()!=null) this.Error(e.getMessage());
            }
        }    
    }
    
    public void setEstadistica(String _vid, long[] _time, long[] _total, long[] _totalvid) {
        if (!x_onof) return;
        try {
            StringBuilder y_linea=new StringBuilder(String.valueOf(Calendar.getInstance().getTimeInMillis()/1000));
            y_linea.append(",'").append(_vid).append("\',")
                   .append(_time[inicio]).append(",").append(_time[ultimo]).append(",").append(_time[termino]).append(",")
                   .append(_totalvid[duplicado]).append(",")
                   .append(_totalvid[kbup]).append(",").append(_totalvid[otas]).append(",")
                   .append(_totalvid[kbdo]).append(",").append(_totalvid[tramas]).append(",")
                   .append(_total[duplicado]).append(",")
                   .append(_total[kbup]).append(",").append(_total[otas]).append(",")
                   .append(_total[kbdo]).append(",").append(_total[tramas]);
            o_WEst.Escribe_Estadistica(y_linea.toString());
        } catch (Exception e0) {
            this.Error(e0.getMessage());
        }    
    }
    
    public boolean getMensaje() {
        return x_msg;
    }
    
    public String getDescripcion() {
        x_msg=false;
        return x_desc;
    }
    
    @Override
    public void close() {
        try {
            if (o_WEst!=null) o_WEst.cierra();
        } catch (Exception e) {
            this.Error(e.getMessage());
        }    
    }
    
    private void Error(String _err) {
        x_onof=false;
        x_msg=true;
        x_desc="[Estadistica] Error: ".concat(_err);
    }
//***********************************************************/    
    
//Clase Interna
    
   public final class Archivo_Estadisticas {
    
        //Variables Gobales
        private final File x_dir;
        private FileWriter o_Escribe=null;
        private PrintWriter o_Imprime=null;
        private final int x_maximo=10000;
        private long x_tam=1;
        private int x_ncon=0;
        
        public Archivo_Estadisticas() throws Exception { 	                //Constructor
            try {
                x_dir = new File(new File(".").getCanonicalPath().concat(File.separator).concat("estadisticas"));
                if(!x_dir.exists()) x_dir.mkdirs();
                o_Escribe=new FileWriter(this.setNombreArchivo(),true);
                o_Imprime=new PrintWriter(new BufferedWriter(o_Escribe));
                o_Imprime.println("Unix_Time,Vid,Time_Inicio,Time_Ultimo,Time_Termino,Tramas_Duplicadas,KBytes_SEND,Otas,KBytes_RECV,Tramas," +
                                  "Total_Duplicadas,Total_KBytes_SEND,Total_Otas,Total_KBytes_RECV,Total_Tramas");
            } catch(IOException e) {
                if (e.getMessage()==null) throw new Exception("path");
                if (e.getMessage().contains("Creacion")) throw new Exception(e.getMessage());
                throw new Exception("path - ".concat(e.getMessage()));
            }
        }

        protected void Escribe_Estadistica(String _esta) throws Exception {
            try {
                if (this.x_tam==0) {
                    this.cierra();
                    o_Escribe=new FileWriter(this.setNombreArchivo(),true);
                    o_Imprime=new PrintWriter(new BufferedWriter(o_Escribe));
                    o_Imprime.println("Unix_Time,Vid,Time_Inicio,Time_Ultimo,Time_Termino,Tramas_Duplicadas,KBytes_SEND,Otas,KBytes_RECV,Tramas," +
                                      "Total_Duplicadas,Total_KBytes_SEND,Total_Otas,Total_KBytes_RECV,Total_Tramas");
                }    
                o_Imprime.println(_esta);
                o_Imprime.flush();
                x_tam=(++x_tam)%x_maximo;
            }  catch (Exception e) {
                if (e.getMessage()==null) throw new Exception("Escritura Archivo Estadistica");
                if (e.getMessage().contains("Creacion")) throw new Exception(e.getMessage());
                if (e.getMessage().contains("Cierre")) throw new Exception(e.getMessage());
                throw new Exception("Escritura Archivo Estadistica - ".concat(e.getMessage()));
            }
        }

        private File setNombreArchivo() throws Exception {
            try {
                ++x_ncon;
                String y_ncon=String.valueOf(x_ncon);
                if (x_ncon<10) y_ncon="0000".concat(y_ncon);
                else if (x_ncon<100) y_ncon="000".concat(y_ncon);
                else if (x_ncon<1000) y_ncon="00".concat(y_ncon);
                else if (x_ncon<10000) y_ncon="0".concat(y_ncon);
                File o_Arch =new File(x_dir.getCanonicalPath()
                                           .concat(File.separator)
                                           .concat("Estadistica_")
                                           .concat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
                                           .concat("_")
                                           .concat(y_ncon)
                                           .concat(".txt"));
                if (o_Arch.exists()) o_Arch=this.setNombreArchivo();
                return o_Arch;
            } catch (Exception e) {
                if (e.getMessage()==null) throw new Exception("Creacion de Archivo Estadistica");
                throw new Exception("Creacion de Archivo Estadistica - ".concat(e.getMessage()));
            }    
        }
        
        protected void cierra() throws Exception {
            try {
                if (o_Imprime!=null) o_Imprime.close();
                if (o_Escribe!=null) o_Escribe.close();
            } catch (IOException e) {
                if (e.getMessage()==null) throw new Exception("Cierre de archivo");
                throw new Exception("Cierre de archivo - ".concat(e.getMessage()));
            }    
            o_Imprime=null;
            o_Escribe=null;
        }

    }

    
}