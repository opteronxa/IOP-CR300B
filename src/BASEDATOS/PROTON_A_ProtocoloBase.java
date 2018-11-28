package BASEDATOS;

/**
 *
 * @author R.Cuevas
**/

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;


////////// PROTOCOLO BASE ////////////////
public abstract class PROTON_A_ProtocoloBase {
  
    public Map<String, String> o_Query=new LinkedHashMap<>();
    private final SimpleDateFormat x_ffor = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat x_hfor = new SimpleDateFormat("HH:mm:ss");
    private final long x_FechaReferencia;
    
    public PROTON_A_ProtocoloBase() {
        x_FechaReferencia=((Calendar.getInstance().getTimeInMillis())/1000)-15724800;
    }
    
    public void setComandoGenerico(String _vid, String _trama, String _hur) {
        o_Query.clear();
        this.LLave(_vid, _hur);
        o_Query.put("[Event]","0");
        o_Query.putIfAbsent("[Message]", "'".concat(_trama).concat("'"));
    } 

    protected long getFechaUnix(int _aa, int _mm, int _dd, int _hh, int _mn, int _ss) throws Exception {
        long y_seg=0;
        try {
            if ((_aa>2100)||(_mm>12)||(_dd>31)) throw new Exception("Date Fuera de Rango");
            if ((_aa<2010)||(_mm<1)||(_dd<1)) throw new Exception("Date Fuera de Rango");
            if ((_hh>24)||(_mn>59)||(_ss>59)) throw new Exception("Time Fuera de Rango");
            if ((_hh<0)||(_mn<0)||(_ss<0)) throw new Exception("Time Fuera de Rango");
            for (int i=1970;i<_aa;i++) {y_seg+=(i%4)==0?31622400:31536000;}
            for (int y_i=1;y_i<_mm;y_i++) {
                switch(y_i) {
                    case  1: y_seg+=86400*31; break;
                    case  2: y_seg+=86400*28; break;
                    case  3: y_seg+=86400*31; break;
                    case  4: y_seg+=86400*30; break;
                    case  5: y_seg+=86400*31; break;
                    case  6: y_seg+=86400*30; break;
                    case  7: y_seg+=86400*31; break;
                    case  8: y_seg+=86400*31; break;  
                    case  9: y_seg+=86400*30; break;
                    case 10: y_seg+=86400*31; break;
                    case 11: y_seg+=86400*30; break;
                }    
            }
            if (((_aa%4)==0)&&(_mm>2)) y_seg+=86400;                            //dia biciesto
            y_seg+=86400*(_dd-1);
            y_seg+=3600*_hh;
            y_seg+=60*_mn;
            y_seg+=_ss;
            if (y_seg<x_FechaReferencia) throw new Exception("Date Fuera de Rango");
            return y_seg;
        } catch (Exception e) {
            throw new Exception("Error en Date_Time");
        }    
    }

    protected String getHoy(boolean _f) {
        if (_f) return "'".concat(this.x_ffor.format(new Date())).concat("'");
        else return "'".concat(this.x_hfor.format(new Date())).concat("'");
    } 

    protected void Campos_Sensores(String _s1,
                                   String _s2,
                                   String _s3,
                                   String _s4,
                                   String _s5,
                                   String _s6,
                                   String _s7,
                                   String _s8,
                                   String _s9) {
            if (this.EsFloat(_s1)) this.o_Query.put("[Sensor1]", _s1);
            if (this.EsFloat(_s2)) this.o_Query.put("[Sensor2]", _s2);
            if (this.EsFloat(_s3)) this.o_Query.put("[Sensor3]", _s3);
            if (this.EsFloat(_s4)) this.o_Query.put("[Sensor4]", _s4);
            if (this.EsFloat(_s5)) this.o_Query.put("[Sensor5]", _s5);
            if (this.EsFloat(_s6)) this.o_Query.put("[Sensor6]", _s6);
            if (this.EsFloat(_s7)) this.o_Query.put("[Sensor7]", _s7);
            if (this.EsFloat(_s8)) this.o_Query.put("[Sensor8]", _s8);
            if (this.EsFloat(_s9)) this.o_Query.put("[Sensor9]", _s9);
    }
    
    private boolean EsFloat(String _ef) {
        if (_ef==null) return false;
        try  {
            Float.valueOf(_ef);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    protected boolean EsEntero(String _ee) {
        if (_ee==null) return false;
        try  {
            Integer.valueOf(_ee);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
        
    protected void setCamposBaterias(String _bat, String _aux) {
        if (this.EsFloat(_bat)) this.o_Query.put("[Battery]", _bat);
        if (this.EsEntero(_aux)) this.o_Query.put("[Aux_Battery]", _aux);
    }
    
    protected void Evento(int _eve) {
        this.o_Query.put("[Event]", String.valueOf(_eve));
    }
    
    protected void Campos_Custom_Int(String _c1, String _c2){
        if (this.EsEntero(_c1)) this.o_Query.put("[Custom1]", _c1);
        if (this.EsEntero(_c2)) this.o_Query.put("[Custom2]", _c2);
    }
    
    protected void Campos_Custom_Small(String _c3,
                                     String _c4,
                                     String _c5,
                                     String _c6){
        if (this.EsEntero(_c3)) this.o_Query.put("[Custom3]", this.MaxSmall(_c3));
        if (this.EsEntero(_c4)) this.o_Query.put("[Custom3]", this.MaxSmall(_c4));
        if (this.EsEntero(_c5)) this.o_Query.put("[Custom3]", this.MaxSmall(_c5));
        if (this.EsEntero(_c6)) this.o_Query.put("[Custom3]", this.MaxSmall(_c6));
    }

    protected String MaxSmall(String _v) {
        try {
            int y_n=Integer.valueOf(_v);
            return String.valueOf(y_n>Short.MAX_VALUE?
                                      Short.MAX_VALUE:
                                  y_n<Short.MIN_VALUE?Short.MIN_VALUE:y_n);
        } catch (NumberFormatException e) {
            return  "0";
        }    
    }
    
    protected void Campos_Texto(String _tx1, String _tx2, String _tx3) {
        if (_tx1!=null)
            if (!_tx1.isEmpty()) this.o_Query.put("[CustomText1]", "'".concat(_tx1.length()<64?_tx1:_tx1.substring(0,64)).concat("'"));
        if (_tx2!=null)
            if (!_tx2.isEmpty()) this.o_Query.put("[CustomText2]", "'".concat(_tx2.length()<32?_tx2:_tx2.substring(0,32)).concat("'"));
        if (_tx3!=null)
            if (!_tx3.isEmpty()) this.o_Query.put("[CustomText3]", "'".concat(_tx3.length()<128?_tx3:_tx3.substring(0,128)).concat("'"));
    }
    
    protected  void setCamposLATLON(Float _lat,
                                    Float _lon,
                                    Float _alt,
                                    Integer _cou,
                                    Float _vel,
                                    Float _dop) throws Exception  {
        if (_lat!=null) {
            if (_lon!=null) {
                if ((Math.abs(_lat)>=0.0)&&(Math.abs(_lon)>0.0)) {
                    if ((Math.abs(_lat)>90.0)&&(Math.abs(_lon)>180.0)) throw new Exception("Coordenadas fuera de Rango");
                    this.o_Query.put("[Latitude]", String.valueOf(_lat));
                    this.o_Query.put("[Longitude]", String.valueOf(_lon));
                }    
            }    
        }
        if (_alt!=null) this.o_Query.put("[Altitude]", String.valueOf( _alt));
        if (_vel!=null) {
            this.o_Query.put("[Speed]", String.valueOf( _vel));
            if ((_vel>0)&&(_cou!=null)) this.o_Query.put("[Course]", String.valueOf( _cou));
        }     
        if (_dop!=null) this.o_Query.put("[HDOP]", String.valueOf( _dop));
    }
    
    protected void LLave(String _vid, String _hur) {
        Calendar y_hoy=Calendar.getInstance();
        this.o_Query.put("[Vehicle_ID]", "'".concat(_vid).concat("'"));
        this.o_Query.put("[Date_Time]", _hur); 
        this.o_Query.put("[Day]", String.valueOf(y_hoy.get(Calendar.DAY_OF_MONTH)));
        this.o_Query.put("[Month]", String.valueOf(y_hoy.get(Calendar.MONTH)+1));
        this.o_Query.put("[Year]",String.valueOf(y_hoy.get(Calendar.YEAR))); 
        this.o_Query.put("[PC_Date]", this.getHoy(true));
        this.o_Query.put("[PC_Time]", this.getHoy(false)); 
        try {
            Long y_sgs=Long.valueOf(_hur);
            int y_dss=(int)Math.floor(y_sgs/86400);
            y_sgs-=(y_dss*86400);
            this.o_Query.put("[GPSTime]", String.valueOf(y_sgs));
        } catch (NumberFormatException e){
            this.o_Query.put("[GPSTime]", "0");
        } 
    } 
    
    
    protected void setCampoComando(String _cmd) {
        this.o_Query.put("[Advisory_Event]", "'".concat(_cmd).concat("'"));
    } 
    
    protected String Hexa_Dec(String _hex) {
        try {
            return String.valueOf(Integer.decode("0x".concat(_hex)));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    protected void Mensaje(String _msg) {
        this.o_Query.put("[Message]", "'".concat(_msg).concat("'"));
    } 
    
    
    protected void Campos_Generales(String _dop,  //Accuracy
                                    String _vel,
                                    String _azi,
                                    String _alt,
                                    String _lon,
                                    String _lat,
                                    String _sat,
                                    String _cell) throws Exception {
        Float y_lat=EsFloat(_lat)?(float)Float.valueOf(_lat):null;
        Float y_lon=EsFloat(_lon)?(float)Float.valueOf(_lon):null;
        Float y_vel=EsFloat(_vel)?(float)Float.valueOf(_vel):null;
        Float y_alt=EsFloat(_alt)?(float)Float.valueOf(_alt):null;
        Float y_dop=EsFloat(_dop)?(float)Float.valueOf(_dop):null; //GPS Accuracy
        Integer y_azi=EsEntero(_azi)?Integer.valueOf(_azi):null;
        try {
            this.setCamposLATLON(y_lat, y_lon, y_alt, y_azi, y_vel, y_dop);
            int y_sat=EsEntero(_sat)?Integer.valueOf(_sat):-1;
            if ((y_sat>=0)&&(y_sat<Short.MAX_VALUE)) this.o_Query.put("[Satellites]", String.valueOf(_sat));
            if (!_cell.isEmpty()) this.o_Query.put("[Nearest_Point]", "'"
                                              .concat(_cell.length()>15?_cell.substring(0,15):_cell)
                                              .concat("'"));
        } catch (Exception e) {
            if (e.getMessage()!=null) throw new Exception(e.getMessage());
            else throw new Exception("Error en Campos Generales");
        }        
    }
    
    protected void setCampoOdometro(String _odo)  throws Exception {
        if (EsFloat(_odo)) {
            if (Float.parseFloat(_odo)>=0.0) this.o_Query.put("[Odometer]", _odo);
            else throw new Exception("Error de Odometro Negativo");
        }
    }
    		  
  
}