package ARCHIVOS;
//Librerias
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * 
 * @author R.Cuevas
 */

//S-SINGLETON
public final class PROTON_S_ArchivoINI {
    
    private static PROTON_S_ArchivoINI o_INI;
    private final  Archivo_CONF o_AI;
    //Variables Gobales
    private String x_IOP="IOP";
    private final List<String> x_EMI=new ArrayList<>();

    public static PROTON_S_ArchivoINI getInstancia() throws Exception {
        if (o_INI==null) o_INI = new PROTON_S_ArchivoINI();
        else throw new Exception ("No puede crearse doble INI");
        return o_INI;
    }
    
    
    PROTON_S_ArchivoINI() throws Exception { 	//Constructor
        try {
            o_AI=new Archivo_CONF("Proton.ini");
            o_AI.Inicializa("IOP","IOP5242");
            o_AI.Inicializa("PUERTO","5242");
            o_AI.Inicializa("SERVIDOR","251");
            o_AI.Inicializa("BIENVENIDA","");
            o_AI.Inicializa("ACK","OFF");
            o_AI.Inicializa("TTL","600");
            o_AI.Inicializa("ITTL","5");
            o_AI.Inicializa("SQL1","OFF");
            o_AI.Inicializa("SQL1_DB","10.20.20.30:1433;mtrac;mt800alfa;masdb");
            o_AI.Inicializa("SQL2","ON");
            o_AI.Inicializa("SQL2_DB","10.20.20.30:1433;mtrac;mt800alfa;masdb");
            o_AI.Inicializa("SQL3","OFF");
            o_AI.Inicializa("SQL3_DB","10.20.20.30:1433;mtrac;mt800alfa;masdb");            
            o_AI.Inicializa("SQL_IMPORTAR","10.20.20.30:1433;mtrac;mt800alfa;masdb");
            o_AI.Inicializa("FRECUENCIA_IMPORTAR","60");
            o_AI.Inicializa("MAX","500");
            o_AI.Inicializa("BUFFER_FLUSH","5");
            o_AI.Inicializa("BUFFER_BATCH","20");
            o_AI.Inicializa("ELECTRON_01","127.0.0.1:6671");
            o_AI.Inicializa("ELECTRON_02","127.0.0.1:6672");
            o_AI.Inicializa("ELECTRON_03","127.0.0.1:6673");
            o_AI.Inicializa("ELECTRON_04","");
            o_AI.Inicializa("ELECTRON_05","");
            o_AI.Inicializa("ELECTRON_06","");
            o_AI.Inicializa("ELECTRON_07","");
            o_AI.Inicializa("ELECTRON_08","");
            o_AI.Inicializa("ELECTRON_09","");
            o_AI.Inicializa("ELECTRON_10","");
            o_AI.Inicializa("TPMS","OFF");
            o_AI.Inicializa("TIPOS","");
            o_AI.Inicializa("CHECKSUM","ON");
            o_AI.Inicializa("ESTADISTICAS","OFF");
            o_AI.Inicializa("AUTO_ON", "OFF");
            o_AI.Inicializa("LOG","OFF");
            o_AI.Leer();
            x_IOP=o_AI.GetParametro("IOP");
            if (o_AI.GetParametro("IOP").isEmpty()) x_IOP=x_IOP.concat(o_AI.GetParametro("PUERTO"));
            if (!o_AI.Valida_Puerto(o_AI.GetParametro("PUERTO"))) throw new Exception ("Error de Parametro PUERTO=".concat(o_AI.GetParametro("PUERTO")).concat(" Invalido"));
            if (o_AI.GetParametro("SQL1").toUpperCase().equals("ON")) {
                if (!o_AI.Valida_Cadena_Conexion(o_AI.GetParametro("SQL1_DB"))) throw new Exception ("Error de Parametro SQL1=".concat(o_AI.GetParametro("SQL1")).concat(" Invalido"));
                new TestSQLBase(this.getSQL("SQL1_DB"), new String[]{"Vehiclestate"}).cerrar();
            }
            if (o_AI.GetParametro("SQL2").toUpperCase().equals("ON")) {
                if (!o_AI.Valida_Cadena_Conexion(o_AI.GetParametro("SQL2_DB"))) throw new Exception ("Error de Parametro SQL2=".concat(o_AI.GetParametro("SQL2")).concat(" Invalido"));
                new TestSQLBase(this.getSQL("SQL2_DB"), new String[]{"Vehiclestate0",
                                                                     "Vehiclestate1",
                                                                     "Vehiclestate2",
                                                                     "Vehiclestate3",
                                                                     "Vehiclestate4",
                                                                     "Vehiclestate5",
                                                                     "Vehiclestate6"}).cerrar();
            }
            if (o_AI.GetParametro("SQL3").toUpperCase().equals("ON")) {
                if (!o_AI.Valida_Cadena_Conexion(o_AI.GetParametro("SQL3_DB"))) throw new Exception ("Error de Parametro SQL3=".concat(o_AI.GetParametro("SQL3")).concat(" Invalido"));
                new TestSQLBase(this.getSQL("SQL3_DB"), new String[]{"Vehiclestate0",
                                                                     "Vehiclestate1",
                                                                     "Vehiclestate2",
                                                                     "Vehiclestate3",
                                                                     "Vehiclestate4",
                                                                     "Vehiclestate5",
                                                                     "Vehiclestate6"}).cerrar();
            }
            if (!o_AI.Valida_Cadena_Conexion(o_AI.GetParametro("SQL_IMPORTAR"))) throw new Exception ("Error de Parametro SQL_IMPORTAR=".concat(o_AI.GetParametro("SQL_IMPORTAR")).concat(" Invalido"));
            new TestSQLBase(this.getSQL("SQL_IMPORTAR"), new String[]{"Default_Vehicles"}).cerrar();
            if (!o_AI.Valida_Entero(o_AI.GetParametro("FRECUENCIA_IMPORTAR"))) throw new Exception ("Error de Parametro FRECUENCIA_IMPORTAR=".concat(o_AI.GetParametro("FRECUENCIA_IMPORTAR")).concat(" Invalido"));
            if (((Integer.valueOf(o_AI.GetParametro("FRECUENCIA_IMPORTAR")))<1)||((Integer.valueOf(o_AI.GetParametro("FRECUENCIA_IMPORTAR")))>600)) throw new Exception ("Error de Parametro FRECUENCIA_IMPORTAR=" + o_AI.GetParametro("FRECUENCIA_IMPORTAR") + " Fuera de Rango");  
            if (!o_AI.Valida_Entero(o_AI.GetParametro("TTL"))) throw new Exception ("Error de Parametro TTL=".concat(o_AI.GetParametro("TTL")).concat(" Invalido"));
            if (((Integer.valueOf(o_AI.GetParametro("TTL")))<1)||((Integer.valueOf(o_AI.GetParametro("TTL")))>7200)) throw new Exception ("Error de Parametro TTL=".concat(o_AI.GetParametro("TTL")).concat("Fuera de Rango"));  
            if (!o_AI.Valida_Entero(o_AI.GetParametro("MAX"))) throw new Exception ("Error de Parametro MAX=".concat(o_AI.GetParametro("MAX")).concat(" Invalido"));
            if (((Integer.valueOf(o_AI.GetParametro("MAX")))<1)||((Integer.valueOf(o_AI.GetParametro("MAX")))>5000)) throw new Exception ("Error de Parametro MAX=".concat(o_AI.GetParametro("MAX")).concat(" Fuera de Rango"));  
            if (!o_AI.Valida_Entero(o_AI.GetParametro("BUFFER_FLUSH"))) throw new Exception ("Error de Parametro BUFFER_FLUSH=".concat(o_AI.GetParametro("BUFFER_FLUSH")).concat(" Invalido"));
            if ((Integer.valueOf(o_AI.GetParametro("BUFFER_FLUSH"))<0)||((Integer.valueOf(o_AI.GetParametro("BUFFER_FLUSH")))>600)) throw new Exception ("Error de Parametro BUFFER_FLUSH Fuera de Rango");  
            if (!o_AI.Valida_Entero(o_AI.GetParametro("BUFFER_BATCH"))) throw new Exception ("Error de Parametro BUFFER_BATCH".concat(o_AI.GetParametro("BUFFER_BATCH")).concat(" Invalido"));
            if ((Integer.valueOf(o_AI.GetParametro("BUFFER_BATCH"))<0)||((Integer.valueOf(o_AI.GetParametro("BUFFER_BATCH")))>20)) throw new Exception ("Error de Parametro BUFFER_BATCH Fuera de Rango");  
            for (int y_i=1;y_i<=10;y_i++) {
                try {
                    String[] y_url;
                    if (y_i<10) y_url=o_AI.getURL(o_AI.GetParametro("ELECTRON_0" + y_i));
                    else y_url=o_AI.getURL(o_AI.GetParametro("ELECTRON_" + y_i));
                    if (y_url!=null) x_EMI.add(y_url[0].concat(":").concat(y_url[1]));
                } catch (Exception e) {
                    break;
                }    
            } 
	} catch(IOException e1) {
            throw new Exception (e1.getMessage());
	}
    }
    
    public String Nombre_IOP() {
        return this.x_IOP;
    }
    
    public Integer getPuerto() {
        try {
            return Integer.valueOf(o_AI.GetParametro("PUERTO"));
        } catch (NumberFormatException e) {
            return 5242;
        }    
    }
        
    public Integer[] getTTL() {
        Integer[] y_ttl=new Integer[2];
        try{
            y_ttl[0]=Integer.valueOf(o_AI.GetParametro("ITTL"));
            if (y_ttl[0]<1) y_ttl[0]=5;
            if (y_ttl[0]>600) y_ttl[0]=600;
        } catch(NumberFormatException e) {
            y_ttl[0]=5;
        }     
        try{
            y_ttl[1]=Integer.valueOf(o_AI.GetParametro("TTL"));
            if (y_ttl[1]<5) y_ttl[1]=600;
            if (y_ttl[1]>3200) y_ttl[1]=3200;
        } catch(NumberFormatException e) {
            y_ttl[1]=600;
        } 
        return y_ttl;
    }
    
    public String getWelcome() {
        String y_wel=this.o_AI.GetParametro("BIENVENIDA");
        if (y_wel==null) return "";
        return y_wel;
    }
    
    public boolean getAck() {
        return this.o_AI.GetParametro("ACK").equals("ON");
    }
    
    public String getServer() {
        return this.o_AI.GetParametro("SERVIDOR");
    }
    
    public Integer getMaximo() {
        try{
            return Integer.valueOf(o_AI.GetParametro("MAX"));
        } catch(NumberFormatException e) {
            return 5000;
        }    
    } 
    
    public Integer getFlush() {
        try{
            int y_flh=Integer.valueOf(this.o_AI.GetParametro("BUFFER_FLUSH"));
            if (y_flh>300) y_flh=30;
            if (y_flh<0) y_flh=1;
            return y_flh;
        } catch(NumberFormatException e) { 
            return 1;
        }    
    } 
    
    public Integer getBacth() {
        try{
            int y_batch=Integer.valueOf(this.o_AI.GetParametro("BUFFER_BATCH"));
            if (y_batch>100) y_batch=100;
            if (y_batch<1) y_batch=1;
            return y_batch;
        } catch(NumberFormatException e) {
            return 20;
        }    
    } 
    
    public Integer getFrecuencia() {
        try{
          int y_frec=Integer.valueOf(this.o_AI.GetParametro("FRECUENCIA_IMPORTAR"));
          if (y_frec>300) y_frec=300;
          if (y_frec<10) y_frec=10;
          return y_frec;
        } catch(NumberFormatException e) { 
            return 10;
        }    
    }
    
    public boolean getCheck(){
        return this.o_AI.GetParametro("CHECKSUM").toUpperCase().equals("ON");
    }
    
    public boolean getLog(){
        return this.o_AI.GetParametro("LOG").toUpperCase().equals("ON");
    }
    
    public boolean getTPMS() {
        return this.o_AI.GetParametro("TPMS").equals("ON");
    }
    
    public List<String> getTipos() {
        List<String> o_tps=new ArrayList<>();
        String[] y_tipos=this.o_AI.GetParametro("TIPOS").split(",");
        if (y_tipos.length<=0) return null;
        for (String y_tp:y_tipos) {
            if (!y_tp.trim().isEmpty()) o_tps.add(y_tp);
        }
        return o_tps;
    }
    
    public boolean getEstadisticas() {
        return this.o_AI.GetParametro("ESTADISTICAS").toUpperCase().equals("ON");
    }
    
    public boolean getAUTO() {
        return this.o_AI.GetParametro("AUTO_ON").toUpperCase().equals("ON");
    }
    
    public boolean getSQL_ON(int _u) {
        try {
         return this.o_AI.GetParametro("SQL".concat(String.valueOf(_u))).toUpperCase().equals("ON");
        } catch (Exception e) {
            return false;
        }        
    }
    
    public String[] getSQL(String _sql) throws Exception {
        try {
            String[] y_ssq=this.o_AI.GetParametro(_sql).split(";");
            String[] y_url=this.o_AI.getURL(y_ssq[0]);
            if (y_url[1].equals("def")) y_url[1]="1433";
            String[] y_sql=new String[4];
            y_sql[0]=y_url[0].concat(":").concat(y_url[1]);   //ip
            y_sql[1]=y_ssq[1];                                //user
            y_sql[2]=y_ssq[2];                                //pass
            y_sql[3]=y_ssq[3];                                //base
            return y_sql;
        } catch (Exception e) {
            throw new Exception ("Error en cadena de conexion ".concat(_sql));
        }    
    }
    
    public  List<String> getElectrones() {
        return x_EMI;
    }
    
    public String MuestraINI () {
        String y_conf="CONFIGURACION:\r\n" + 
                      "  Proton...........: " + this.o_AI.GetParametro("IOP") + "\r\n" + 
                      "  Puerto...........: " + this.o_AI.GetParametro("PUERTO") + " TCP\r\n" + 
                      "  Servidor.........: " + this.o_AI.GetParametro("SERVIDOR") + "\r\n" + 
                      "  Trama Bienvenida.: " + this.o_AI.GetParametro("BIENVENIDA") + "\r\n" +
                      "  Acknowledge......: " + this.o_AI.GetParametro("ACK") + "\r\n" +
                      "  TTL..............: " + this.o_AI.GetParametro("TTL") + " Seg\r\n" +
                      "  ITTL.............: " + this.o_AI.GetParametro("ITTL") + " Seg\r\n" +
                      "  Exportar a 1.....: " + this.o_AI.GetParametro("SQL1") + "\r\n";
        if (this.o_AI.GetParametro("SQL1").toUpperCase().equals("ON")) {       
            y_conf+="    Conexion.......: " + this.o_AI.GetParametro("SQL1_DB") + "\r\n";
        }    
        y_conf+="  Exportar a 2.....: " + this.o_AI.GetParametro("SQL2") + "\r\n";
        if (this.o_AI.GetParametro("SQL2").toUpperCase().equals("ON")) {    
            y_conf+="    Conexion.......: " + this.o_AI.GetParametro("SQL2_DB") + "\r\n";
        }   
        y_conf+="  Exportar a 3.....: " + this.o_AI.GetParametro("SQL3") + "\r\n";
        if (this.o_AI.GetParametro("SQL3").toUpperCase().equals("ON")) {    
            y_conf+="    Conexion.......: " + this.o_AI.GetParametro("SQL3_DB") + "\r\n";
        }    
        y_conf+="  Importar\r\n" +
                "    Conexion.......: " + this.o_AI.GetParametro("SQL_IMPORTAR") + "\r\n" +
                "    Frecuencia.....: " + this.o_AI.GetParametro("FRECUENCIA_IMPORTAR") + " Seg\r\n" +
                "  Maximo...........: " + this.o_AI.GetParametro("MAX") + "\r\n" +    
                "  Buffer Espera....: " + this.o_AI.GetParametro("BUFFER_FLUSH") + " Seg\r\n" +
                "  Buffer Ciclos....: " + this.o_AI.GetParametro("BUFFER_BATCH") + " Ciclos \r\n" +
                "  Electrones\r\n";             
        for (int y_i=0;y_i<this.x_EMI.size();y_i++) {
            if (y_i+1<10) y_conf+="     [0" + (y_i+1) + "]..........: " +   this.x_EMI.get(y_i) + " UDP\r\n";
            else y_conf+="     [" + (y_i+1) + "]..........: " + this.x_EMI.get(y_i) + " UDP\r\n";
        }
        y_conf+="  TPMS.............: " + this.o_AI.GetParametro("TPMS").toUpperCase() + "\r\n" +
                "  TIPOS............: " + this.o_AI.GetParametro("TIPOS") + "\r\n" + 
                "  Checksum.........: " + (this.o_AI.GetParametro("CHECKSUM").toUpperCase().equals("ON")?"ON":"OFF") + "\r\n" +
                "  Estadisticas.....: " + this.o_AI.GetParametro("ESTADISTICAS").toUpperCase() + "\r\n" +
                "  Auto ON..........: " + (this.o_AI.GetParametro("AUTO_ON").toUpperCase().equals("ON")?"ON":"OFF") + "\r\n" + 
                "  Archivo Log......: " + (this.o_AI.GetParametro("LOG").toUpperCase().equals("ON")?"ON":"OFF") + "\r\n" +            
                " ";  
        return y_conf;
    }   
    
    
//*****************************************************************************/
    
    private class TestSQLBase extends BASEDATOS.PROTON_A_SQLBase {
    
        public TestSQLBase(String[] _sql, String[] _tab) throws Exception {
            super(_sql, _tab); 
        }
        
        public void cerrar() {
            try {
                if (this.o_Conx!=null) o_Conx.close();
                o_Conx=null;
            } catch (SQLException e) {
                e.getMessage();
            }     
        }
    }
       
//*****************************************************************************/
    
    public class Archivo_CONF {
    
        private final Map<String, String> o_Parametros = new LinkedHashMap<>();
        private String x_path;
        private String  x_ID;


        public Archivo_CONF(String _arch) throws Exception {
            try {
                this.x_path=new File(".").getCanonicalPath();
                this.x_path+=File.separator.concat(_arch);
            } catch(IOException e) {
                throw new Exception (e.getMessage());
            }
        }

        protected void Inicializa(String _clave, String _valor) {
            if (_clave==null) return;
            if (_clave.isEmpty()) return;
            o_Parametros.put(_clave, _valor==null?"":_valor);
        }       

        protected void Leer() throws Exception {
            final File y_archivo = new File(this.x_path);  
            final List<String> o_ixi=new ArrayList<>();
            boolean y_rea=false;
            if (y_archivo.exists()) {
                final List<String> o_ini=new ArrayList<>();
                try (final BufferedReader renglon = new BufferedReader(new FileReader(y_archivo))) {
                    String y_cadena = renglon.readLine().trim();
                    while (y_cadena!=null) {   
                        if (y_cadena.startsWith("#")) o_ini.add(y_cadena);
                        else if (y_cadena.contains("=")) o_ini.add(y_cadena);
                        y_cadena=renglon.readLine();
                    }
                    Stream<String> y_para=o_ini.stream();
                    y_para.forEach(y_p->{
                        if (!y_p.startsWith("#")) {
                            String[] y_pv=y_p.split("=");
                            String y_pv1="";
                            if (y_pv.length>=2) y_pv1=y_pv[1];
                            y_pv[0]=y_pv[0].toUpperCase();
                            if (o_Parametros.containsKey(y_pv[0])) o_Parametros.replace(y_pv[0], y_pv1);
                            o_ixi.add(y_pv[0].concat("=").concat(y_pv1));
                        } else o_ixi.add(y_p);
                    });
                } catch (Exception e1){
                    throw new Exception ("ERROR delectura CONF: ".concat(e1.getMessage()));
                }
             } else y_rea=true;
            for (Map.Entry<String, String> y_e:o_Parametros.entrySet()) {
                String y_pv=y_e.getKey().concat("=").concat(y_e.getValue());
                if (!o_ixi.contains(y_pv)) {
                    o_ixi.add(y_pv);
                    if (!y_rea) y_rea=true;
                }
            }  
            if (y_rea) {
                try (FileWriter escribirarchivo = new FileWriter(new File(this.x_path));
                     PrintWriter imprime = new PrintWriter(new BufferedWriter(escribirarchivo))) {
                        o_ixi.stream().forEach((y_cadena) -> {
                            imprime.println(y_cadena);
                        });	  
                } catch (Exception e0){
                    throw new Exception ("ERROR de Escritura CONF: ".concat(e0.getMessage()));
                }
            }    
        }

        protected String GetParametro(String _clave) {
            if (o_Parametros.containsKey(_clave)) return o_Parametros.get(_clave);
            return "";
        } 


        protected String GetID() {
            return GetParametro("ID").isEmpty()?x_ID:x_ID.concat(GetParametro("ID"));
        }

        protected boolean Valida_Cadena_Conexion(String _cast) {
            try {
                String[] y_cc=_cast.split(";");
                if (y_cc.length!=4) return false;
                if (this.getURL(y_cc[0])[0].equals("err")) return false;
                if (y_cc[1].isEmpty()) return false;
                if (y_cc[2].isEmpty()) return false;
                return !y_cc[3].isEmpty();
            } catch (Exception e) {
                return false;        
            }   
        }

        protected boolean Valida_Entero(String _valor) {
            try {
                Integer.valueOf(_valor);
            } catch(NumberFormatException e) {
                return false;
            }
            return true;
        }

        protected String[] getURL(String _url) {
            String[] y_oip={"err","def"};
            if (_url.length()<4) return null; 
            try {
                int y_ipi=_url.indexOf("//");
                y_ipi=y_ipi<0?0:y_ipi+2;
                int y_ipf=_url.indexOf(":");
                if ((y_ipf>0)&&(y_ipf+1)<_url.length()) y_oip[1]=_url.substring(y_ipf+1);
                if (!y_oip[1].equals("def")) {
                    if (!this.Valida_Puerto(y_oip[1])) return y_oip;
                }            
                y_ipf=y_ipf<0?_url.length()-1:y_ipf;
                y_oip[0]=_url.substring(y_ipi,y_ipf);
                String y_nip=y_oip[0].replaceAll("\\.", "-");  
                String[] y_ip=y_nip.split("-");
                if (y_ip.length==4) {
                    Integer[] y_num=new Integer[4];
                    y_num[0]=this.Valida_Entero(y_ip[0])?Integer.valueOf(y_ip[0]):-1;
                    y_num[1]=this.Valida_Entero(y_ip[1])?Integer.valueOf(y_ip[1]):-1;
                    y_num[2]=this.Valida_Entero(y_ip[2])?Integer.valueOf(y_ip[2]):-1;
                    y_num[3]=this.Valida_Entero(y_ip[3])?Integer.valueOf(y_ip[3]):-1;           
                    if ((y_num[0]<0)||(y_num[1]<0)||(y_num[2]<0)||(y_num[3]<0)) y_oip[0]="err";
                    if ((y_num[0]>255)||(y_num[1]>255)||(y_num[2]>255)||(y_num[3]>255)) y_oip[0]="err";
                }   
            } catch(NumberFormatException e) {
                y_oip[0]="err";
            }
            return y_oip;
        }

        protected boolean Valida_Puerto(String _pto) {
            try {
                int y_pto=Integer.valueOf(_pto);
                if ((y_pto<100)||(y_pto>65535)) return false;
                switch (y_pto) {
                    case 109:  return false; //Puerto Reservado para POP3
                    case 110:  return false; //Puerto Reservado para POP3
                    case 115:  return false; //Puerto Reservado para SFTP
                    case 119:  return false; //Puerto Reservado para NNTP
                    case 143:  return false; //Puerto Reservado para IMAP
                    case 161:  return false; //Puerto Reservado para SNMP
                    case 162:  return false; //Puerto Reservado para SNMP
                    case 194:  return false; //Puerto Reservado para IRC
                    case 197:  return false; //Puerto Reservado para DLS
                    case 210:  return false; //Puerto Reservado para WAIS
                    case 389:  return false; //Puerto Reservado para LDPA
                    case 443:  return false; //Puerto Reservado para HTTPS
                    case 444:  return false; //Puerto Reservado para SNNP
                    case 513:  return false; //Puerto Reservado para RLOGIN
                    case 546:  return false; //Puerto Reservado para DHCP
                    case 547:  return false; //Puerto Reservado para DHCP
                    case 1723: return false; //Puerto Reservado para PPTP
                    default: return true;
                }    
            }  catch (NumberFormatException e){
                return false;
            }
        }  
    
    }
    
}
