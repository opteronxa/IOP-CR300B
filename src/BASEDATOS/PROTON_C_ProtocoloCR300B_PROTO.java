package BASEDATOS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * 
 * @author R.Cuevas
 */
    
public final class PROTON_C_ProtocoloCR300B_PROTO extends PROTON_A_ProtocoloBase {
 
    public static boolean x_TPMS;
    private final List<String> x_Hexa=new ArrayList<>();
    private List<Integer> o_Tipos=new ArrayList<>();
    private int x_CMD=-1;
    public static int C1=0, C2=0, C3=0;
    public static String XTRM=new String();
    private boolean x_ctp=false;
    
    public PROTON_C_ProtocoloCR300B_PROTO() {   }

    public PROTON_C_ProtocoloCR300B_PROTO(List<String> _tipos) {
        if (!_tipos.isEmpty()) {
            for (String y_t:_tipos) {
                try {
                    o_Tipos.add(Integer.valueOf(y_t));
                } catch (NumberFormatException e) {
                    break;
                }    
            }
            x_ctp=true;
        }    
    }
   
    public StringBuilder[] Registro(String _t,
                                    String _trama,
                                    String _vid,
                                    String _hur,
                                    String _ncon,
                                    String _ip) {
        this.o_Query.clear();
        C1=(++C1)%1000000;
        XTRM=_trama;
        try {
            if (_t.equals("T")) {
                int y_evt=getClasificaEVT(Reconoce_Typo(_trama));
                switch (y_evt) {
                    case 0:
                        this.setComandoGenerico(_vid, _trama, _hur);
                        break;
                    case 30:
                        this.Tipo_3(_vid, _hur);
                        break;
                    case 70:
                        this.setCampoComando("Tipo-7");
                        this.Evento(70);
                        this.LLave(_vid, _hur);
                        this.Mensaje(_trama);
                        break;
                    case 80:
                        this.setCampoComando("Tipo-8");
                        this.Evento(80);
                        this.LLave(_vid, _hur);
                        this.Mensaje(_trama);
                        break;     
                    case 90:
                        this.setCampoComando("Tipo-9");
                        this.Tipo_9(_vid, _trama, _hur);
                        break;
                    case 110:
                        this.setCampoComando("Tipo-11");
                        this.Tipo_11(_vid, _hur);
                        break;     
                    default:
                        this.setCampoComando("Tipo-0");
                        this.Tipo_0(_vid, y_evt);
                        if ((x_ctp)&&(o_Tipos.contains(this.x_CMD))) this.o_Query.put("[Information]", "'".concat(_trama).concat("'"));
                }    
            } else this.Tipo_OTA(_vid, _trama, _hur);
        } catch (Exception e) {
            this.setComandoGenerico(_vid, _trama, _hur);
            this.Campos_Texto(null, null, e.getMessage());
        }   
        C2=C1;
        StringBuilder[] y_qury=new StringBuilder[2];
        y_qury[0]=new StringBuilder("");
        y_qury[1]=new StringBuilder("");
        for (Entry y_ent:o_Query.entrySet()) {
            y_qury[0].append(y_ent.getKey()).append(",");
            y_qury[1].append(y_ent.getValue()).append(",");       
        }
        try {
            y_qury[1].append("'").append(_ip.split(":")[0].substring(1)).append("'").append(",");
            y_qury[0].append("[XML],");
        } catch (Exception e) {
            y_qury[1].append("'NoIP',");
            y_qury[0].append("[XML],");
        }
        C3=C1;
        y_qury[0].append("[Update_Number]");
        y_qury[1].append(_ncon);
        return y_qury;
    }

//------------rutinas privadas-----//
       
    private int Reconoce_Typo(String _trama) throws Exception {
        try {
            char[] y_cha=_trama.toCharArray();
            this.x_Hexa.clear();
            this.x_Hexa.add(String.valueOf(y_cha.length/2));
            int y_i=0;
            while (y_i<y_cha.length) {
               this.x_Hexa.add(String.valueOf(y_cha, y_i, 2));
               y_i+=2;
            } 
            int y_tipo=T1(5);
            this.o_Query.put("[Advisory_Event]","'TIPO-".concat(String.valueOf(y_tipo)).concat("'"));
            return y_tipo; 
        } catch (Exception e) {
            throw new Exception("Error de Estructura determinando Type-#");
        }
    }
    
    private int getClasificaEVT(int _tipo) throws Exception { 
        switch (_tipo) {
            case 0:   
                try {
                    int y_evn=(T1(19)*10)+1000;
                    if ((T1(10)&1)==1) this.o_Query.put("[Error_Code]", "1");
                    else this.o_Query.put("[Error_Code]", "0");    
                    return y_evn;
                } catch (Exception e) {
                    throw new Exception("Error Razon de Transmision");
                }    
            case 3:  return 30;
            case 7:  return 70;   
            case 8:  return 80; 
            case 9:  return 90;    
            case 11: return 110;    
            default: throw new Exception("Error Tipo Indeterminado");
        }    
    }
        
    private void Tipo_OTA(String _vid, String _trama, String _hur) throws Exception {
        try {
            this.LLave(_vid, _hur);
            this.setCampoComando("OTA");
            if (_trama.startsWith("OUT:#")) {
                switch (_trama.toUpperCase()) {
                    case "OUT:#GETPOS":
                        this.Campos_Texto("[Enviado: Solicitud de Ubicacion]", null, _trama);
                        this.Evento(7);
                        break; 
                    case "OUT:#RESET":
                        this.Campos_Texto("[Enviado: Reset]", null, _trama);
                        this.Evento(5);
                        break;
                    case "OUT:#OP1ON":
                        this.Campos_Texto("[Enviado: Activar Salida 1]", null, _trama);
                        this.Evento(11);
                        break;
                    case "OUT:#OP1OFF":
                        this.Campos_Texto("[Enviado: Desactivar Salida 1]", null, _trama);
                        this.Evento(10);
                        break;    
                    case "OUT:#OP2ON":
                        this.Campos_Texto("[Enviado: Activar Salida 2]", null, _trama);
                        this.Evento(21);
                        break;    
                    case "OUT:#OP2OFF":
                        this.Campos_Texto("[Enviado: Desactivar Salida 2]", null, _trama);
                        this.Evento(20);
                        break;
                    case "OUT:#OP3ON":
                        this.Campos_Texto("[Enviado: Activar Salida 3]", null, _trama);
                        this.Evento(31);
                        break;
                    case "OUT:#OP3OFF":
                        this.Campos_Texto("[Enviado: Desactivar Salida 3]", null, _trama);
                        this.Evento(30);
                        break;    
                    case "OUT:#OP4ON":
                        this.Campos_Texto("[Enviado: Activar Salida 4]", null, _trama);
                        this.Evento(41);
                        break;    
                    case "OUT:#OP4OFF":
                        this.Campos_Texto("Enviado: Desactivar Salida 4]", null, _trama);
                        this.Evento(40);
                        break;   
                    default:
                        if (_trama.startsWith("OUT:#TR")) {
                            this.Campos_Texto("[Enviado: Cambio de Frecuencia]", null, _trama);
                            this.Evento(6);
                        } else throw new Exception("Error En comando OTA"); 
                        break;
                }
            } else {
                String y_ota=_trama.substring(4);
                String y_com=y_ota.substring(28,30);
                switch (y_com) {
                    case "00":
                        this.Campos_Texto("[Enviado: Solicitud de Ubicacion]", null, y_ota);
                        this.Evento(7);
                        break;
                    case "02":
                        this.Campos_Texto("[Enviado: Reset]", null, y_ota);
                        this.Evento(5);
                        break;
                    case "05":
                        this.Campos_Texto("[Enviado: Cambio de Frecuencia]", null, y_ota);
                        this.Evento(6);
                        break;   
                    case "03":
                        switch (y_ota.substring(32,34)) {
                            case "18":
                                this.Campos_Texto("[Enviado: Activar Salida 1]", null, y_ota);
                                this.Evento(11);
                                break;
                            case "08":
                                this.Campos_Texto("[Enviado: Desactivar Salida 1]", null, y_ota);
                                this.Evento(10);
                                break;    
                            case "15":
                                this.Campos_Texto("[Enviado: Activar Salida 2]", null, y_ota);
                                this.Evento(21);
                                break;    
                            case "05":
                                this.Campos_Texto("[Enviado: Desactivar Salida 2]", null, y_ota);
                                this.Evento(20);
                                break;
                            case "14":
                                this.Campos_Texto("[Enviado: Activar Salida 4]", null, y_ota);
                                this.Evento(41);
                                break;    
                            case "04":
                                this.Campos_Texto("[Enviado: Desactivar Salida 4]", null, y_ota);
                                this.Evento(40);
                                break;          
                        }
                        break;
                    case "07":
                        switch (y_ota.substring(32,34)) {    
                            case "00":
                                this.Campos_Texto("[Enviado: Activar Salida 3]", null, y_ota);
                                this.Evento(31);
                                break;
                            case "01":
                                this.Campos_Texto("[Enviado: Desactivar Salida 3]", null, y_ota);
                                this.Evento(30);
                                break;  
                        }
                        break;
                    default:    
                        this.Campos_Texto("[Enviado: OTA]", null, y_ota);
                        this.Evento(1);
                        break;
                }
            }    
        } catch (Exception e) {
             throw new Exception("Error estructura OTA");
        }                 
    }
    

//**** TRAMA TIPO 0***************//      
    private void Tipo_0(String _vid, int _evt) throws Exception {
        try {
            switch (_evt) {
                case 3060:         //reason 206*10+1000  jammer
                    this.Evento(((T1(18)&1)==0)?_evt:_evt+2);
                    break;
                case 1310:         //reason 31*10+1000 command reply
                    if ((T1(24)&5)==0) this.Campos_Texto("Salida 1 Inactiva / Salida 2 Inactiva",null,null);
                    if ((T1(24)&5)==1) this.Campos_Texto("Salida 1 Activa / Salida 2 Inactiva",null,null);
                    if ((T1(24)&5)==4) this.Campos_Texto("Salida 1 Inactiva / Salida 2 Activa",null,null);
                    if ((T1(24)&5)==5) this.Campos_Texto("Salida 1 Activa / Salida 2 Activa",null,null);
                    switch (T1(12)) {
                        case  1: this.Evento(_evt+1); break;
                        case 11: this.Evento(_evt+2); break;
                        case 10: this.Evento(_evt+3); break;
                        case 21: this.Evento(_evt+4); break;
                        case 20: this.Evento(_evt+5); break;
                        case 31: this.Evento(_evt+12); break;
                        case 30: this.Evento(_evt+13); break;
                        case 41: this.Evento(_evt+14); break;
                        case 40: this.Evento(_evt+15); break;
                        case 239:this.Evento(_evt+9);  break;    
                        case 255:this.Evento(_evt+999); break;     
                        default: this.Evento(_evt);
                    }
                    break;        
                default:
                    this.Evento(_evt);
                    break;
            }
            this.Campos_Custom_Int(String.valueOf(T1(12)), String.valueOf(T1(18)));
            int y_16=T1(16);
            this.o_Query.put("[GPS]", ((y_16&128)==128)?"'ERR'":"'GPS'");
            switch (this.x_Hexa.get(20)) {
                case "00": this.o_Query.put("[Advisory_Inputs]", "'00-Motor Funcionando'"); break;
                case "01": this.o_Query.put("[Advisory_Inputs]", "'01-Motor Apagado'"); break;
                case "0E": this.o_Query.put("[Advisory_Inputs]", "'14-Modo Garage'"); break;         
                case "0F": this.o_Query.put("[Advisory_Inputs]", "'15-Latencia de Transmision'"); break;
                case "10": this.o_Query.put("[Advisory_Inputs]", "'10-Modo Arrastre'"); break;    
                default: this.o_Query.put("[Advisory_Inputs]", "'".concat(String.valueOf(T1(20))).concat("-Modo Alarma'")); break;
            }
            this.Campos_Custom_Small(String.valueOf(T1(21)),
                                     String.valueOf(T1(22)),
                                     String.valueOf(T1(23)),
                                     String.valueOf(T1(24)));
            this.setCamposBaterias(String.valueOf(T1(26)), String.valueOf(T1(27)));
            this.Campos_Sensores(String.valueOf((T1(28)*0.4314)-40),
                                 String.valueOf(T1(29)),
                                 null,
                                 null,   
                                 null,
                                 null,
                                 null,
                                 null,
                                 null);
            this.setCampoOdometro(String.valueOf(T3(30)));
            if (T1(38)>0) {
                StringBuilder y_button=new StringBuilder(String.valueOf(T1(38)));
                y_button.append(String.valueOf(T1(37)))
                        .append(String.valueOf(T1(36)))
                        .append(String.valueOf(T1(35)))
                        .append(String.valueOf(T1(34)))
                        .append(String.valueOf(T1(33)));
                this.o_Query.put("[Driver_ID]","'".concat(y_button.toString()).concat("'"));
            }    
            int y_gps=T2(39);
            int y_dm=y_gps>>11;
            int y_hh=(y_gps>>6)&31;
            int y_mm=(y_gps&63);
            this.Campos_Texto(null,
                             "Dia ".concat(String.valueOf(y_dm)).concat(" / Hora ").concat(String.valueOf(y_hh)).concat(":").concat(String.valueOf(y_mm)),
                             null);     // _trama.substring(24,138));
            this.setCamposLATLON((float)((T4(49)*0.00000001)*(180/Math.PI)),
                                 (float)((T4(45)*0.00000001)*(180/Math.PI)),
                                 (float)((T4(53)*0.01)),
                                 (int)Math.round((T2(61)*0.001)*(180/Math.PI)),
                                 (float)((T4(57)*0.00001)*3600), 
                                  this.celloDOP(T1(42)));
            this.o_Query.put("[Satellites]", String.valueOf(T1(44)));
            this.LLave(_vid, String.valueOf(this.getFechaUnix(T2(68),
                                                              T1(67),
                                                              T1(66),
                                                              T1(65),
                                                              T1(64),
                                                              T1(63))));
        } catch (NumberFormatException e0) {
            throw new Exception("Error de Estructura Type-0");
        }    
    }    

//**** TRAMA TIPO 3***************//      
    private void Tipo_3(String _vid, String _hur) throws Exception {
        try {
            this.setCampoComando("Tipo-3");
            this.LLave(_vid, _hur);
            this.Evento(30);
            this.Campos_Custom_Int(String.valueOf(T1(12)), String.valueOf(T1(13)));
            StringBuilder y_block=new StringBuilder("'");
            y_block.append(this.x_Hexa.get(14))
                   .append(":");
            for (int i=15;i<30;i++) {
                y_block.append(this.x_Hexa.get(i))
                       .append(",");
            }
            y_block.append(this.x_Hexa.get(30)).append("'");
            this.o_Query.put("Messsage",y_block.toString());
        } catch (Exception e) {
            throw new Exception("Error de Estructura Type-3");
        }  
    }    
    
///**** TRAMA TIPO 9***************//  
    private void Tipo_9(String _vid, String _trama, String _hur) throws Exception {
        try {
            int y_evt=92;
            long y_hur=Long.valueOf(_hur);
            int y_lon=this.T1(14)+14;
            boolean y_coo=false;
            for (int y_i=15;y_i<y_lon;y_i++) {
                int y_var=this.T1(y_i);
                ++y_i;
                int y_lng=this.T1(y_i);
                switch (y_var) {
                    case 4:
                        if (!y_coo) y_hur=this.tp9_M4(y_i);
                        y_coo=true;
                        break;
                    case 13: //(0D)
                        if (!y_coo) y_hur=this.tp9_M13(y_i);
                        y_coo=true;
                        break;    
                    case 22: //(16)
                        y_evt=this.tp9_M22(y_i);
                        break;
                    default:    
                        this.Mensaje(_trama);   
                        break;
                }
                y_i+=y_lng;
            }
            this.LLave(_vid, String.valueOf(y_hur));
            this.Evento(y_evt);
        } catch (Exception e) {
            throw new Exception("Error de Estructura Type-9");
        }
    }   
    
    //-----Tipo-9 >> Modulo 4 >> Time Location Stamp data
    private Long tp9_M4(int _p) throws Exception {
        try {
            this.o_Query.put("[GPS]", (this.T1(_p+1)&64)>>6==1?"'GPS Error'":"'GPS'");
            this.o_Query.put("[Satellites]", String.valueOf(T1(_p+4)));
            this.setCamposLATLON((float)((T4(_p+9)*0.00000001)*(180/Math.PI)),
                               (float)((T4(_p+5)*0.00000001)*(180/Math.PI)),
                               (float)(T3(_p+13)*0.01),
                               (int)Math.round((T2(_p+18)*0.001)*(180/Math.PI)),
                               (float)((T2(_p+16)*0.00001)*3600),
                               this.celloDOP(T1(_p+2)));
            return this.getFechaUnix(T1(_p+25)+2000,
                                     T1(_p+24),
                                     T1(_p+23),
                                     T1(_p+22),
                                     T1(_p+21),
                                     T1(_p+20));
        } catch (Exception e2) {
            throw new Exception("Modulo 4");
        }
    }
    
    //-----Tipo-9 >> Modulo 13(D hex) >> Compressed Vector Change Report
    private long tp9_M13(int _p) throws Exception {
        try {
            this.setCamposLATLON((float)((T4(_p+6)*0.00000001)*(180/Math.PI)),
                                 (float)((T4(_p+2)*0.00000001)*(180/Math.PI)),
                                 (float)0.0,
                                 (int)Math.round((T1(_p+14)*360)/255),
                                 (float)((T1(_p+15)*0.00001)*3600),
                                 (float)1.0);
            int y_tim=this.T1(_p+16);
            int y_ss=(y_tim&63);
            int y_mn=y_tim>>>6;
            int y_hh=this.T1(_p+17);
            y_tim=(y_hh&15)<<2;
            y_mn=y_tim|y_mn;
            y_hh=y_hh>>>4;
            int y_dd=this.T1(_p+18)&63;
            y_tim=(y_dd&1)<<4;
            y_hh=y_tim|y_hh;
            y_dd=y_dd>>>1;
            String y_hoy=this.getHoy(true);
            int y_aa=Integer.valueOf(y_hoy.substring(1,5));
            int y_mm=Integer.valueOf(y_hoy.substring(6,8));
            return this.getFechaUnix(y_aa,
                                     y_mm,
                                     y_dd,
                                     y_hh,
                                     y_mn,
                                     y_ss);
        } catch (NumberFormatException e) {
            throw new Exception("Modulo 13");
        }
    }
    
    //-----Tipo-9 >> Modulo 22(16 hex) >> Fleet End of trip report
    private int tp9_M22(int _p) throws Exception {
        try {
            int y_evt=91;
            int y_val=this.T4(_p+3);
            if (y_val>0) y_evt=90;
            this.setCampoOdometro(String.valueOf(y_val));
            float y_econ=((float)this.T4(_p+7))/10;
            if (y_econ>0) y_evt=90;
            this.o_Query.put("[Fuel_Economy]", String.valueOf(y_econ));
            y_val=this.T1(_p+13);
            if (y_val>0) y_evt=90;
            this.o_Query.put("[Fuel_Level]", String.valueOf(y_val));
            y_val=this.T2(_p+14);
            if (y_val>0) y_evt=90;
            this.o_Query.put("[ECU_Action_Machine]", String.valueOf(y_val));
            //y_val=this.T2(_p+11);                                             Eliminado por QA 
            //if (y_val>0) y_evt=90;                                            Eliminado por QA
            //this.o_Query.put("[Bus_Field1]",String.valueOf(y_val));           Eliminado por QA
            return y_evt;
        } catch (Exception e) {
            throw new Exception("Modulo 4");
        }
    }            
   
//**** TRAMA TIPO 11*************//    
    private void Tipo_11(String _vid, String _hur) throws Exception {
        try {
            int y_evt=112;                                                       //Evento Default
            int y_ltr=this.T2(14)+15;                                            //Longitud
            long y_hur=Long.valueOf(_hur);
            int y_loop=0;
            for(int y_i=20;y_i<y_ltr;y_i++) {                                   //20-Inicia el primer modulo
                ++y_loop;
                if (y_loop>=100) throw new Exception("Error de LOOP Estructura Type-11");   //seguro contr loop;
                switch (this.T1(y_i)) {                                         //Id del Modulo
                    case 1:                                                     //DTC Event
                        y_evt=120;                                              //Tama con modulo 1 DTC_Event
                        this.tp11_M1(y_i);
                        break;
                    case 6:                                                     //GPS Location Stamp
                        this.tp11_M6(y_i);
                        break;
                    case 7:                                                     //GPS Time Stamp
                        y_hur=this.tp11_M7(y_i, _hur);
                        break;
                    case 8:                                                     //FW ID
                        this.Campos_Texto("Fw:".concat(x_Hexa.get(y_i+4))
                                               .concat(x_Hexa.get(y_i+5))
                                               .concat(x_Hexa.get(y_i+6))
                                               .concat(x_Hexa.get(y_i+7)),null,null);
                        break;
                }
                y_i+=this.T2(y_i+1)+2;                                          //brinca al siguiente modulo... o al final
            }
            this.LLave(_vid, String.valueOf(y_hur));
            this.Evento(y_evt);
        } catch (Exception e) {
            throw new Exception("Error de Estructura Type-11 ".concat(e.getMessage()));
        }
    }   
    
    //-----Tipo-11 >> Modulo 1 >> DTC Event
    private void tp11_M1(int _p) throws Exception {
        try {
            int y_pp=_p+1;
            int y_lon=this.T2(y_pp);
            y_pp+=2;
            y_lon+=y_pp;
            int y_mode3=this.T1(y_pp);
            y_pp+=2;
            StringBuilder y_dtc=new StringBuilder("");
            boolean y_m3=true;
            boolean y_m7=true;
            for (int y_i=0;y_i<y_mode3;y_i++) {
                if (y_m3) {
                    y_dtc.append(" *MODE3:");
                    y_m3=false;
                }
                y_dtc.append("[").append(this.tp11_M1_code(y_pp)).append("]");
                y_pp+=2;
                if (y_i+1<y_mode3) y_dtc.append(",");
            }   
            int y_loop=1;
            while (y_pp<y_lon) {    
                if (y_m7) {
                    y_dtc.append(" *MODE7:");
                    y_m7=false;
                }
                y_dtc.append("[").append(this.tp11_M1_code(y_pp)).append("]");
                y_pp+=2;
                if (y_pp<y_lon) y_dtc.append(",");
                ++y_loop;
                if (y_loop>=100) throw new Exception("Error de LOOP Estructura T11-M1");   //seguro contra loop;
            }
            this.Mensaje(y_dtc.toString());
        } catch (NumberFormatException e) {
             throw new Exception("Modulo-1");
        }     
    }
    
    private String tp11_M1_code(int _p){
        String y_dtc;
        int y_dec=this.T1(_p);
        switch (y_dec>>6) {
            case 0:  y_dtc="P-"; break;
            case 1:  y_dtc="C-"; break;
            case 2:  y_dtc="B-"; break;
            case 3:  y_dtc="U-"; break;
            default: y_dtc="X-";
        }
        y_dec=y_dec&63;
        y_dec=y_dec<<8;
        y_dec=y_dec|this.T1(_p+1);
        String y_dtd=Integer.toHexString(y_dec);
        switch (y_dtd.length()) {
            case 1: y_dtd="000".concat(y_dtd); break;
            case 2: y_dtd="00".concat(y_dtd); break;
            case 3: y_dtd="0".concat(y_dtd); break;
        }
        return y_dtc.concat(y_dtd).toUpperCase();
    }
    
    //-----Tipo-11 >> Modulo 6 >> GPS Location Stamp Module
    private void tp11_M6(int _p) throws Exception {
        try {
            this.o_Query.put("[Satellites]", String.valueOf(this.T1(_p+6)));
            this.setCamposLATLON((float)((T4(_p+11)*0.00000001)*(180/Math.PI)),
                                 (float)((T4(_p+7)*0.00000001)*(180/Math.PI)),
                                 (float)(T3(_p+15)*0.01),
                                 (int)Math.round((T2(_p+20)*0.001)*(180/Math.PI)),
                                 (float)T1(_p+19),
                                  this.celloDOP(T1(_p+3)));
        } catch (Exception e) {
            throw new Exception("Modulo 6");
        }   
    }
    
    //-----Tipo-11 >> Modulo 7 >> GPS Time Stamp
    private long tp11_M7(int _p, String _hur) throws Exception  {
        try {
            if (this.T1(_p+3)==0) return Long.valueOf(_hur);
            return this.getFechaUnix(2000+T1(_p+9),
                                          T1(_p+8),
                                          T1(_p+7),
                                          T1(_p+6),
                                          T1(_p+5),
                                          T1(_p+4));
        } catch (NumberFormatException e) {
            throw new Exception("Modulo 7");
        }      
    }
    
    
    /************************************************************/
   
    private float celloDOP(int _dop) {
        if (_dop<2) return (float)0.0;
        else if (_dop<6) return (float)1.0;
        return (float)_dop;
    }        
    
    private int T1(int _p) {
        return Integer.decode("0x".concat(x_Hexa.get(_p)));
    } 
    
    private int T2(int _p) {
        return Integer.decode("0x".concat(x_Hexa.get(_p+1)).concat(x_Hexa.get(_p)));
    }        
    
    private int T3(int _p) {
        return Integer.decode("0x".concat(x_Hexa.get(_p+2)).concat(x_Hexa.get(_p+1)).concat(x_Hexa.get(_p)));
    }   
    
    private int T4(int _p) {
        long y_tm=Long.decode("0x".concat(x_Hexa.get(_p+3)).concat(x_Hexa.get(_p+2)).concat(x_Hexa.get(_p+1)).concat(x_Hexa.get(_p)));
        return ((int)y_tm);
    }   


}
