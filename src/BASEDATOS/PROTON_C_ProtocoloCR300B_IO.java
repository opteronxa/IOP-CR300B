package BASEDATOS;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * 
 * @author R.Cuevas
 */
    
public final class PROTON_C_ProtocoloCR300B_IO extends PROTON_A_Estadisticas {
 
    //public static boolean x_TPMS;
    private DataInputStream o_Entt=null;
    private DataOutputStream o_Sall=null;  
    private final String x_wel;
    private final boolean x_cks; 
    private final boolean x_ack;
    private boolean x_1ra=true;
    private String x_Id;
    private String x_VId;
    private int[] x_checksum={0,0};
    private int x_psum=0;
    private boolean x_owe;
    
    public PROTON_C_ProtocoloCR300B_IO(Socket _soc, boolean _ack, boolean _cks, String _wel) throws Exception {
        x_ack=_ack;
        x_cks=_cks;
        x_wel=_wel;
        try {
            x_owe=!_wel.isEmpty();
            o_Entt=new DataInputStream(_soc.getInputStream());
            o_Sall=new DataOutputStream(_soc.getOutputStream());
        } catch (IOException e) {
            throw new Exception("Error de conexion Socket iostream"); 
        }    
    }
    
    public String Recibir() throws Exception {
        StringBuilder y_trama=new StringBuilder("4D");
        int y_ttp=0;
        int y_xi=0;
        int y_ite=512;
        int y_vlon=100;
        int y_byte;
//System.out.println("##" + y_trama);         
        try {
            int y_sum=0;
            do {
                y_byte =o_Entt.read();    //**ESPERA
//System.out.println("<<i:" + y_xi + "  byte: " + y_byte);             
                if (y_byte<0) return "#";
                this.x_1024bytdo=(++this.x_1024bytdo)%1024;
                if (this.x_1024bytdo==0) ++this.x_Kbytdo;
                y_sum+=y_byte;
                if (y_ttp==0) {
                    if (y_byte==77) y_ttp=1;
                    y_xi=1;
                } else { 
                    if (y_byte==0) y_trama.append("00");
                    else {
                        if (y_byte<16) y_trama.append("0");
                        y_trama.append(Integer.toHexString(y_byte).toUpperCase());
                    }
                    switch (y_ttp) {
                        case 1:
                            if (y_trama.toString().equals("4D434750")) y_ttp=2;  //ascii-MCGP
                            break;
                        case 2:
                            switch (y_byte) {
                                case 0:  //i Type-0   Largo 70 bytes
                                    y_ite=70;
                                    y_ttp=-1;
//System.out.println("-->tipo 0 Largo:" + y_ite);
                                    break;
                                case 3:  // Type-3    Largo 31 bytes
                                    y_ite=31;
                                    y_ttp=-1;
//System.out.println("-->tipo 3 Largo:" + y_ite);                                    
                                    break;    
                                case 7:  // Type-7    Largo 70 bytes
                                    y_ite=70;
                                    y_ttp=-1;
//System.out.println("-->tipo 7 Largo:" + y_ite);                                    
                                    break;
                                case 8:  // Type-8    Largo variable
                                    y_ite=19;       //posicion 19 para longitud
                                    y_ttp=3;
//System.out.println("-->tipo 8 Largo:" + y_ite);                                    
                                    break;   
                                case 9:  // Type-9    Largo Variable
                                    y_ite=14;       //Posicion 15 pala longitud
                                    y_ttp=4;
//System.out.println("-->tipo 9 Largo:" + y_ite);                                    
                                    break;
                                case 11:  // Type-11  Largo Variable
                                    y_ite=20;       //Posicion 20 para longitud
                                    y_ttp=5;
//System.out.println("-->tipo 11 Largo:" + y_ite);
                                    break;       
                            }
                            break;
                        case 3:    //Actualiza Largo type-8
                            switch (y_xi) {
                                case 17:
                                    y_vlon=y_byte;
                                    break;
                                case 18:
                                    y_byte<<=8;
                                    y_ite=y_byte|y_vlon;    //payload
                                    y_ite+=19;
                                    y_ttp=-1;
                                    break;
                            }
                            break;
                        case 4:    //Largo  type-9
                          
                            if (y_xi==13) {
                                y_ite=y_byte;
                                y_ite+=(y_xi+2);
                                y_ttp=-1;
                            }   
//System.out.println("<-->tipo 9 x:" + y_ite);                           
                            break;    
                        case 5:   //Largo  type-11
                             
                            switch(y_xi) {
                                case 13:
                                    y_vlon=y_byte;
                                    break;
                                case 14:
                                    y_byte<<=8;
                                    y_ite=y_byte|y_vlon;    //payload
                                    y_ite+=(y_xi+2);
                                    y_ttp=-1;
                                    break;
                            }
//System.out.println("<-->tipo 11 x:" + y_ite);                             
                            break;  
                    }
                    ++y_xi;
                }
//System.out.println(">>"+y_trama);               
            } while (y_xi<y_ite);
//System.out.println("**"+y_trama);  
            if (!y_trama.substring(0,8).equals("4D434750")) return "";
            if (x_1ra) {
                x_Id=y_trama.substring(10,18);
                String[] y_hex=x_Id.split("");
                int y_id=Integer.decode("0x"
                                .concat(y_hex[6])
                                .concat(y_hex[7])
                                .concat(y_hex[4])
                                .concat(y_hex[5])
                                .concat(y_hex[2])
                                .concat(y_hex[3])
                                .concat(y_hex[0])
                                .concat(y_hex[1]));
                x_VId=String.valueOf(y_id);
                x_1ra=false;
            }    
            ++this.x_trado;
            if (!this.Enviar(this.Acknowledge(y_trama))) return "%";
            if (x_owe) {                                                        //reponde welcome
                x_owe=false;
                if (!this.Enviar(this.ValidaOTA(x_wel))) return "%";
            }
            if (x_cks) {                                                        //comprueba checksum     
                if (this.x_checksum[this.x_psum]!=y_sum)  {
                    this.x_psum=(++this.x_psum)%2;
                    if (this.x_checksum[this.x_psum]==y_sum) {
                        ++this.x_dup;
                        return "&";                                             //retorna & si checksum duplica
                    } else this.x_checksum[this.x_psum]=y_sum;             
                }    
            }   
            return y_trama.toString();
        } catch (SocketTimeoutException e1) {
            return "^";
        } catch (IOException | NumberFormatException e2) {
            if (e2.getMessage().contains("Socket Closed")) return "#";
            throw new Exception("[".concat(y_trama.toString()).concat("] ").concat(e2.getMessage()));
        } 
    }


    private String Acknowledge(StringBuilder _trama) {
        try {
//System.out.println("ack:" + _trama);
            StringBuilder y_tipoACK=new StringBuilder("");
            String y_tipo=_trama.substring(8,10);
            switch (y_tipo) {
                case "00": y_tipoACK.append(this.getAckTipo4(_trama.substring(22, 24))); break;
                case "09": y_tipoACK.append(this.getAckTipo4(_trama.substring(22, 24))); break;
                case "0B": y_tipoACK.append(this.getAckTipo11(_trama.substring(22, 24))); break;
                default:    
                    return null;
            }    
            int y_cs=0;
            int y_i=0;
            while (y_i<y_tipoACK.length()) {
                String y_b=y_tipoACK.substring(y_i,y_i+2); 
                y_cs+=Integer.decode("0x".concat(y_b));
                y_i+=2;
            }
            String y_c=Integer.toHexString(y_cs);
            if (y_c.length()>2) y_c=y_c.substring(y_c.length()-2);
            if (y_c.length()<2) y_c="0".concat(y_c);
            y_c=y_c.toUpperCase();
//System.out.println("ack: 4D434750".concat(y_tipoACK.toString().toUpperCase()).concat(y_c));            
            return "4D434750".concat(y_tipoACK.toString().toUpperCase()).concat(y_c);
        } catch (NumberFormatException e) {
            return null;       
        }    
    }
    
    private String getAckTipo4(String _tango) {
        StringBuilder y_ACK=new StringBuilder("04");                             
        y_ACK.append(this.x_Id)                                                 //ID
                 .append("00")                                                  //Numerator Field 
                 .append("00000000")                                            //Autentication
                 .append("00")                                                  //Action code
                 .append(_tango)                                                //Main Acknowledge number  
                 .append("00000000");                                           //reserved, reserved, reserved, reserved
                    //-*Fecha
        Calendar y_cal=Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        int y_au=y_cal.get(Calendar.DAY_OF_MONTH);
        int y_ff=y_au<<11;
        y_au=y_cal.get(Calendar.MONTH)+1;
        y_au=y_au<<7;
        y_ff=y_ff|y_au;
        y_au=y_cal.get(Calendar.YEAR);
        y_au-=2000;
        y_ff=y_ff|y_au;
        String y_bi=Integer.toHexString(y_ff);
        switch (y_bi.length()) {
            case 3: y_bi="0".concat(y_bi); break;
            case 2: y_bi="00".concat(y_bi); break;
            case 1: y_bi="000".concat(y_bi); break;
        }
        char[] y_ha=y_bi.toCharArray();
        y_ACK.append(y_ha[y_ha.length-2]).append(y_ha[y_ha.length-1]);
        y_ACK.append(y_ha[y_ha.length-4]).append(y_ha[y_ha.length-3]);
        //-*Hora
        y_ff=8388608;                                               //[1000 0000] [0000 0000] [0000 0000] 
        y_au=y_cal.get(Calendar.SECOND);
        y_au=y_au<<11;
        y_ff=y_ff|y_au;
        y_au=y_cal.get(Calendar.MINUTE);
        y_au<<=5;
        y_ff=y_ff|y_au;
        y_au=y_cal.get(Calendar.HOUR_OF_DAY);
        y_ff=y_ff|y_au;
        y_bi=Integer.toHexString(y_ff);  
        switch (y_bi.length()) {
            case 5: y_bi="0".concat(y_bi); break;
            case 4: y_bi="00".concat(y_bi); break;
            case 3: y_bi="000".concat(y_bi); break;
            case 2: y_bi="0000".concat(y_bi); break;
            case 1: y_bi="00000".concat(y_bi); break;
        }
        char[] y_hb=y_bi.toCharArray();
        y_ACK.append(y_hb[y_hb.length-2]).append(y_hb[y_hb.length-1])
                 .append(y_hb[y_hb.length-4]).append(y_hb[y_hb.length-3])
                 .append(y_hb[y_hb.length-6]).append(y_hb[y_hb.length-5])
                 .append("0000");                          //Reserved
        return y_ACK.toString();
    }
    
    
    private String getAckTipo11(String _tango) {
        StringBuilder y_ACK=new StringBuilder("0B");                             
        y_ACK.append(this.x_Id);                        //ID 
        y_ACK.append(_tango);                           //Command Num (anti-Tango)
        y_ACK.append("00000000");                       //Autentication 
        y_ACK.append("80");                             //packet control field (1000 0000)
        y_ACK.append("0A00");                           //Length (10)
        y_ACK.append("00000000");                       //Spare
        y_ACK.append("09");                             //Module ACK
        y_ACK.append("0300");                           //Module Length
        y_ACK.append("00");                             //0-ACK 1-NACK
        y_ACK.append("0000");                           //Spare, spare
        return y_ACK.toString();
    }
    
    public String getVId() {
        return x_VId;
    }
    
    public boolean Reconoce_HeartBeat(String _trama) {
        return false;   
    }     
    
    public int getCheck() {
        return this.x_checksum[this.x_psum];
    }
    
    public boolean setCheck(int _sum) {
        if (!this.x_cks) return true;
        if (_sum<=0) return true;
        if (this.x_checksum[1]==_sum) {
            ++this.x_dup;
            return false;
        }
        this.x_checksum[0]=_sum;
        return true;
    }
         
    public synchronized boolean Enviar(String _ota) {
//System.out.println("OTA:" + _OTA);
        if (_ota.isEmpty()) return true;
        try {
            char[] y_bys=_ota.toCharArray();
            byte[] y_ack=new byte[y_bys.length];
            if (y_bys.length<2) return false;
            int y_i=0;
            int y_j=0;
            do {
                StringBuilder y_bts=new StringBuilder("0x");
                y_bts.append(y_bys[y_i]);
                ++y_i;
                y_bts.append(y_bys[y_i]);
                ++y_i;
//System.out.println("-" + y_bts);               
                short y_sh=Short.decode(y_bts.toString());
                y_ack[y_j]=(byte)y_sh;
                ++y_j;
                this.x_1024bytup=(++this.x_1024bytup)%1024;
                if (this.x_1024bytup==0) ++this.x_Kbytup;
            } while(y_i<y_bys.length);   
            if (y_j>0) {
                o_Sall.write(y_ack,0,y_j);
                o_Sall.flush();
                ++this.x_otaup;
//System.out.println(" sal:" + y_ack);
            }
            return true;
        } catch (IOException | NumberFormatException e) {
//System.out.println("Expt:" + e.getMessage());
            return false;
        }    
    }
    
    
    //-------------Envio de comando OTA -----
    
    public String ValidaOTA(String _OTA) {
        try {
            switch (_OTA.toUpperCase()) {
                case "#GETPOS": return this.Polleo_Ubicacion();
                case "#OP1ON":  return this.Salida1(true);
                case "#OP1OFF": return this.Salida1(false);
                case "#OP2ON":  return this.Salida2(true);
                case "#OP2OFF": return this.Salida2(false);     
                default:    
                    if (_OTA.toUpperCase().startsWith("#TR")) return this.Tiempo_Reporte(_OTA);  
                    if (_OTA.toUpperCase().equals("#RESET")) return this.ResetHot();  
                    if (_OTA.toUpperCase().startsWith("4D434750")) return _OTA.toUpperCase();
                    String y_ota="00".concat(x_Id).concat(_OTA.toUpperCase());
                    y_ota=y_ota.concat(this.CheckSuma(y_ota));
                    return "4D434750".concat(y_ota);
            }        
        } catch (Exception e) {    
            return "";
        }    
    }
  
    private String Polleo_Ubicacion()  throws Exception {
        try {
            StringBuilder y_poll=new StringBuilder("00");
            y_poll.append(x_Id);
            y_poll.append("010000000000000000000000000000");
            y_poll.append(this.CheckSuma(y_poll.toString()));
            return "4D434750".concat(y_poll.toString().toUpperCase());
        } catch (Exception e) {
            throw new Exception("Error Trama Polleo: " + e.getMessage());
        }
    }
    
    private String CheckSuma(String _check) throws Exception {
        try {
            String[] y_dig=_check.split("");
            int y_cs=0;
            boolean y_pn=false;
            String y_hx="0x";
            for (String y_d:y_dig) {
                if (!y_pn) y_hx="0x".concat(y_d);
                else y_cs+=Integer.decode(y_hx.concat(y_d));
                y_pn=!y_pn;
            }
            y_hx=Integer.toHexString(y_cs);
            if (y_hx.length()==1) return "0".concat(y_hx);
            if (y_hx.length()==2) return y_hx;
            if (y_hx.length()>2)  return y_hx.substring(y_hx.length()-2);
            throw new Exception("0<=CheckSum");
        } catch (Exception e) {
            throw new Exception("CheckSum");
        }    
    }
    
    private String Salida1(boolean _on) throws Exception {
        try {
            StringBuilder y_poll=new StringBuilder("00");
            y_poll.append(x_Id);    
            if (_on) y_poll.append("0B0000000003031818000000000000");
            else y_poll.append("0A0000000003030808000000000000");
            y_poll.append(this.CheckSuma(y_poll.toString()));
            return "4D434750".concat(y_poll.toString());
        } catch (Exception e) {
            throw new Exception("Error Trama Salida 1: " + e.getMessage());
        }
    }
     
    private String Salida2(boolean _on)  throws Exception {
        try {
            StringBuilder y_poll=new StringBuilder("00");
            y_poll.append(x_Id);
            if (_on) y_poll.append("150000000003031414000000000000");
            else y_poll.append("140000000003030404000000000000");
             y_poll.append(this.CheckSuma(y_poll.toString()));
            return "4D434750".concat(y_poll.toString());
        } catch (Exception e) {
            throw new Exception("Error Trama Salida 2: " + e.getMessage());
        }
    }
    
    private String Tiempo_Reporte(String _ota)  throws Exception {
        try {
            if (_ota.length()>=4) {
                int y_seg=Integer.valueOf(_ota.substring(4));
                if ((y_seg<=10)||(y_seg>=255)) return "*Cambio fuera de rango"; 
                StringBuilder y_poll=new StringBuilder("00");
                y_poll.append(x_Id);
                y_poll.append("EE000000000505");
                if (y_seg<16) y_poll.append("0");
                y_poll.append(Integer.toHexString(y_seg));
                if (y_seg<16) y_poll.append("0");
                y_poll.append(Integer.toHexString(y_seg));
                y_poll.append("000000000000");
                y_poll.append(this.CheckSuma(y_poll.toString()));
                return "4D434750".concat(y_poll.toString().toUpperCase());
            } else return "*Cambio Frecuencia sin valor";    
        } catch (Exception e) {
            throw new Exception("Error OTA Cambio Frecuenca: " + e.getMessage());
        }
    }
        
    private String ResetHot()  throws Exception {
        try {
            StringBuilder y_poll=new StringBuilder("00");
            y_poll.append(x_Id);
            y_poll.append("FF0000000002020202000000000000");
            y_poll.append(this.CheckSuma(y_poll.toString()));
            return "4D434750".concat(y_poll.toString().toUpperCase());
        } catch (Exception e) {
            throw new Exception("Error OTA Reset: " + e.getMessage());
        }
    }

}
