package ARCHIVOS;
//Librerias
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author R.Cuevas
 */

//C-Clase
public class PROTON_C_ArchivosMAP {
    
    private final Signatures o_Sig=new Signatures();
    private final INTERFACE.PROTON_W_Ventana o_Vent;
    private final Map<String,String> o_Columnas=new HashMap<>();
    
    public PROTON_C_ArchivosMAP(INTERFACE.PROTON_W_Ventana _vent) throws Exception {
        o_Vent=_vent;
        o_Columnas.put("Vehicle_ID".toLowerCase(),"T:32");
        o_Columnas.put("Alias".toLowerCase(),"T:32");
        o_Columnas.put("Day".toLowerCase(),"S:0");
        o_Columnas.put("Month".toLowerCase(),"S:0");
        o_Columnas.put("Year".toLowerCase(),"S:0");
        o_Columnas.put("GPSTime".toLowerCase(),"F:0");
        o_Columnas.put("Date_Time".toLowerCase(),"I:0");
        o_Columnas.put("PC_Date".toLowerCase(),"T:12");
        o_Columnas.put("PC_Time".toLowerCase(),"T:8");
        o_Columnas.put("Street".toLowerCase(),"T:128");
        o_Columnas.put("City".toLowerCase(),"T:96");
        o_Columnas.put("State".toLowerCase(),"T:2");
        o_Columnas.put("Zip_Code".toLowerCase(),"T:5");
        o_Columnas.put("Latitude".toLowerCase(),"F:0");
        o_Columnas.put("Longitude".toLowerCase(),"F:0");
        o_Columnas.put("Speed".toLowerCase(),"F:0");
        o_Columnas.put("Course".toLowerCase(),"S:0");
        o_Columnas.put("Altitude".toLowerCase(),"F:0");
        o_Columnas.put("Event".toLowerCase(),"I:0");
        o_Columnas.put("Advisory_Event".toLowerCase(),"T:32");
        o_Columnas.put("Distance".toLowerCase(),"F:0");
        o_Columnas.put("Satellites".toLowerCase(),"S:0");
        o_Columnas.put("GPS".toLowerCase(),"T:32");
        o_Columnas.put("State_Code".toLowerCase(),"S:0");
        o_Columnas.put("Inputs".toLowerCase(),"S:0");
        o_Columnas.put("Advisory_Inputs".toLowerCase(),"T:50");
        o_Columnas.put("Outputs".toLowerCase(),"S:0");
        o_Columnas.put("Message".toLowerCase(),"T:1024");
        o_Columnas.put("Advisories".toLowerCase(),"T:50");
        o_Columnas.put("Nearest_Point".toLowerCase(),"T:64");
        o_Columnas.put("Analog1".toLowerCase(),"S:0");
        o_Columnas.put("Analog2".toLowerCase(),"S:0");
        o_Columnas.put("Analog3".toLowerCase(),"S:0");
        o_Columnas.put("Analog4".toLowerCase(),"S:0");
        o_Columnas.put("HDOP".toLowerCase(),"F:0");
        o_Columnas.put("VDOP".toLowerCase(),"F:0");
        o_Columnas.put("GDOP".toLowerCase(),"F:0");
        o_Columnas.put("PDOP".toLowerCase(),"F:0");
        o_Columnas.put("Rate_of_Climb".toLowerCase(),"F:0");
        o_Columnas.put("Custom1".toLowerCase(),"I:0");
        o_Columnas.put("Custom2".toLowerCase(),"I:0");
        o_Columnas.put("Custom3".toLowerCase(),"S:0");
        o_Columnas.put("Custom4".toLowerCase(),"S:0");
        o_Columnas.put("Custom5".toLowerCase(),"S:0");
        o_Columnas.put("Custom6".toLowerCase(),"S:0");
        o_Columnas.put("CustomText1".toLowerCase(),"T:64");
        o_Columnas.put("CustomText2".toLowerCase(),"T:32");
        o_Columnas.put("Nearby_Streets".toLowerCase(),"T:50");
        o_Columnas.put("Available_Inputs".toLowerCase(),"S:0");
        o_Columnas.put("Available_Outputs".toLowerCase(),"S:0");
        o_Columnas.put("Driver_ID".toLowerCase(),"T:32");
        o_Columnas.put("VIN".toLowerCase(),"T:32");
        o_Columnas.put("Error_Code".toLowerCase(),"I:0");
        o_Columnas.put("XML".toLowerCase(),"T:50");
        o_Columnas.put("CustomText3".toLowerCase(),"T:128");
        o_Columnas.put("Extended_Inputs".toLowerCase(),"S:0");
        o_Columnas.put("Available_Extended_Inputs".toLowerCase(),"S:0");
        o_Columnas.put("Odometer".toLowerCase(),"F:0");
        o_Columnas.put("Fuel_Level".toLowerCase(),"F:0");
        o_Columnas.put("Battery".toLowerCase(),"F:0");
        o_Columnas.put("Oil_Level".toLowerCase(),"F:0");
        o_Columnas.put("Oil_Temperature".toLowerCase(),"F:0");
        o_Columnas.put("Oil_Pressure".toLowerCase(),"F:0");
        o_Columnas.put("Coolant_Level".toLowerCase(),"F:0");
        o_Columnas.put("Coolant_Temperature".toLowerCase(),"F:0");
        o_Columnas.put("Fuel_Economy".toLowerCase(),"F:0");
        o_Columnas.put("Average_Fuel_Economy".toLowerCase(),"F:0");
        o_Columnas.put("Vehicle_Speed".toLowerCase(),"F:0");
        o_Columnas.put("Engine_RPM".toLowerCase(),"F:0");
        o_Columnas.put("Throttle_Position".toLowerCase(),"F:0");
        o_Columnas.put("Sensor1".toLowerCase(),"F:0");
        o_Columnas.put("Sensor2".toLowerCase(),"F:0");
        o_Columnas.put("Sensor3".toLowerCase(),"F:0");
        o_Columnas.put("Sensor4".toLowerCase(),"F:0");
        o_Columnas.put("Sensor5".toLowerCase(),"F:0");
        o_Columnas.put("Sensor6".toLowerCase(),"F:0");
        o_Columnas.put("Sensor7".toLowerCase(),"F:0");
        o_Columnas.put("Sensor8".toLowerCase(),"F:0");
        o_Columnas.put("Aux_Battery".toLowerCase(),"S:0");
        o_Columnas.put("Engine_ID".toLowerCase(),"S:0");
        o_Columnas.put("Update_Number".toLowerCase(),"Y:0");
        o_Columnas.put("County".toLowerCase(),"T:128");
        o_Columnas.put("timeStamp".toLowerCase(),"D:0");
        o_Columnas.put("servidor".toLowerCase(),"T:15");
        o_Columnas.put("Sensor9".toLowerCase(),"F:0");
        o_Columnas.put("Sensor10".toLowerCase(),"F:0");
        o_Columnas.put("Engine_Hours".toLowerCase(),"F:0");
        o_Columnas.put("GeoObjects".toLowerCase(),"T:1024");
        o_Columnas.put("Bus_Type".toLowerCase(),"S:0");
        o_Columnas.put("Bus_Field1".toLowerCase(),"I:0");
        o_Columnas.put("Bus_Field2".toLowerCase(),"S:0");
        o_Columnas.put("Bus_Field3".toLowerCase(),"S:0");
        o_Columnas.put("Bus_Field4".toLowerCase(),"S:0");
        o_Columnas.put("Bus_Field5".toLowerCase(),"I:0");
        o_Columnas.put("Local_time".toLowerCase(),"D:0");
        o_Columnas.put("Accumulated".toLowerCase(),"I:0");
        o_Columnas.put("Acceleration".toLowerCase(),"F:0");
        o_Columnas.put("Fix".toLowerCase(),"T:15");
        o_Columnas.put("Old".toLowerCase(),"T:15");
        o_Columnas.put("Advisory_Type".toLowerCase(),"T:10");
        o_Columnas.put("Command_Type".toLowerCase(),"T:10");
        o_Columnas.put("Id_Type".toLowerCase(),"T:50");
        o_Columnas.put("Ignition".toLowerCase(),"B:0");
        o_Columnas.put("Input1".toLowerCase(),"B:0");
        o_Columnas.put("Input2".toLowerCase(),"B:0");
        o_Columnas.put("Input3".toLowerCase(),"B:0");
        o_Columnas.put("Output1".toLowerCase(),"B:0");
        o_Columnas.put("Output2".toLowerCase(),"B:0");
        o_Columnas.put("Power".toLowerCase(),"B:0");
        o_Columnas.put("One_Wire".toLowerCase(),"T:30");
        o_Columnas.put("Analog_Input".toLowerCase(),"F:0");
        o_Columnas.put("Cell".toLowerCase(),"T:10");
        o_Columnas.put("Wireless_Way".toLowerCase(),"T:5");
        o_Columnas.put("ECU_Code".toLowerCase(),"T:15");
        o_Columnas.put("ECU_Odometer".toLowerCase(),"I:0");
        o_Columnas.put("ECU_RPM".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Total_Consumption".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Action_Machine".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Idle_Consumption".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Velocity".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Trip".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Idle_Machine".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Fuel_Analog".toLowerCase(),"F:0");
        o_Columnas.put("ECU_Average_Consumption".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Performance".toLowerCase(),"I:0");
        o_Columnas.put("ECU_Fuel_Time".toLowerCase(),"I:0");
        o_Columnas.put("Information".toLowerCase(),"T:1024");
        o_Columnas.put("Description".toLowerCase(),"T:150");
        o_Columnas.put("Flag".toLowerCase(),"T:5");
        o_Columnas.put("VS_comdelay".toLowerCase(),"F:0");
        o_Columnas.put("VS_eventType".toLowerCase(),"F:0");
        o_Columnas.put("VS_shortDesc".toLowerCase(),"T:50");
        o_Columnas.put("VS_metric".toLowerCase(),"F:0");
        o_Columnas.put("VS_metricValue".toLowerCase(),"F:0");
        o_Columnas.put("VS_metricUnits".toLowerCase(),"T:50");
        o_Columnas.put("VS_reType".toLowerCase(),"T:50");
        o_Columnas.put("VS_bReSense".toLowerCase(),"B:0");
        o_Columnas.put("VS_reIndex".toLowerCase(),"F:0");
        o_Columnas.put("VS_georefId".toLowerCase(),"I:0");
        o_Columnas.put("VS_georefName".toLowerCase(),"T:50");
        o_Columnas.put("VS_georefEvtype".toLowerCase(),"F:0");
        o_Columnas.put("VS_georefEvent".toLowerCase(),"T:50");
        o_Columnas.put("VS_georefEvinfo".toLowerCase(),"T:50");
        o_Columnas.put("VS_vhHours".toLowerCase(),"F:0");
        o_Columnas.put("VS_vhMins".toLowerCase(),"F:0");
        o_Columnas.put("VS_ad".toLowerCase(),"F:0");
        o_Columnas.put("VS_ac".toLowerCase(),"F:0");
        o_Columnas.put("VS_source".toLowerCase(),"F:0");
        o_Columnas.put("VS_age".toLowerCase(),"F:0");
        o_Columnas.put("VS_cfRssi".toLowerCase(),"F:0");
        o_Columnas.put("VS_cfLac".toLowerCase(),"T:50");
        o_Columnas.put("VS_ip".toLowerCase(),"T:50");
        o_Columnas.put("VS_port".toLowerCase(),"I:0");
        o_Columnas.put("VS_bEcuDistanceFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuIdleFuelFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuTotalFuelFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuTripDistanceFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuFuelLevelFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuFuelLevelRealFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuFuelInconsumptionFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuHoursFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuHoursIdleFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuRpmFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_bEcuErrorFlag".toLowerCase(),"T:50");
        o_Columnas.put("VS_ecuError1".toLowerCase(),"F:0");
        o_Columnas.put("VS_ecuError2".toLowerCase(),"F:0");
        o_Columnas.put("VS_ecuError3".toLowerCase(),"F:0");
        o_Columnas.put("VS_ecuError4".toLowerCase(),"F:0");
    	try {
            File y_dir=new File(".");
            y_dir = new File(y_dir.getCanonicalPath().concat(File.separator).concat("map"));
            if (!y_dir.exists()) y_dir.mkdirs();
            String[] y_xmls=y_dir.list();
            _vent.setVista(">Abriendo Archivos INI:");
            for (String y_i:y_xmls) {
                if (y_i.toLowerCase().endsWith(".ini")) {
                    o_Vent.setAviso("  : ".concat(y_i));
                    File arc=new File(y_dir.getCanonicalPath().concat(File.separator).concat(y_i));
                    if (!arc.isDirectory()) {
                        this.Mapeos(arc, y_i);
                    }
                }    
            }
        } catch(IOException e) {
            throw new IOException (e.getMessage());
	}
    }
    
    public Optional<float[]> getLiterales(String _sig, String _var) {
        return this.o_Sig.getLiterales(_sig, _var);
    }
    
    public Optional<String[]> getAsignacion(String _sig, String _var, float _val) {
        try {
            Optional<String> y_opt=o_Sig.getCampo(_sig, _var);
            if (!y_opt.isPresent()) return Optional.empty();
            if (y_opt.get().equals("-")) return Optional.of(new String[]{"-",
                                                                         "[".concat(_var)
                                                                            .concat(":")
                                                                            .concat(String.valueOf(_val))
                                                                            .concat("]")
                                                                        });
            if (!this.o_Columnas.containsKey(y_opt.get())) return Optional.empty();
            String[] y_tipo=this.o_Columnas.get(y_opt.get()).split(":");
            switch (y_tipo[0]) {
                case "T":
                    int y_len=Integer.valueOf(y_tipo[1]);
                    String y_val=String.valueOf(_val);
                    if (y_val.length()>y_len) y_val=y_val.substring(0,y_len-1);
                    return Optional.of(new String[]{"[".concat(y_opt.get()).concat("]"),"'".concat(y_val).concat("'")});
                case "S":
                    int y_vi=Math.round(_val);
                    y_vi=y_vi>Short.MAX_VALUE?Short.MAX_VALUE:y_vi<Short.MIN_VALUE?Short.MIN_VALUE:y_vi;
                    return Optional.of(new String[]{"[".concat(y_opt.get()).concat("]"),String.valueOf(y_vi)});
                case "B":
                    return Optional.of(new String[]{"[".concat(y_opt.get()).concat("]"),Math.round(_val)==1?"1":"0"});
                case "I":
                    return Optional.of(new String[]{"[".concat(y_opt.get()).concat("]"),String.valueOf(Math.round(_val))});
                default:
                    return Optional.of(new String[]{"[".concat(y_opt.get()).concat("]"),String.valueOf(_val)});
            }        
        } catch (NumberFormatException e) {
            return Optional.empty();
        }    
    }
    
    private void Mapeos(File _arc, String _nom)  throws Exception {
        boolean y_f=true;
        String y_firma="";
        try (FileReader leerarchivo = new FileReader(_arc);
            BufferedReader renglon = new BufferedReader(leerarchivo)) {
            String y_cadena = renglon.readLine();
            this.o_Vent.setVista(">FIRMA CANiQ CARGADA: ");
            while (y_cadena!=null) {
                if (!y_cadena.startsWith("#")) {
                    if (y_cadena.contains("#")) y_cadena=y_cadena.substring(0,y_cadena.indexOf("#")-1);
                    if (!y_cadena.trim().isEmpty()) {
                        if (y_f) {
                            y_cadena=y_cadena.toUpperCase();
                            if (y_cadena.contains("FIRMA=")) {
                                y_firma=y_cadena.split("=")[1];
                                this.o_Vent.setVista("     * ".concat(y_firma).concat(" <- ").concat(_nom));
                                y_f=false;
                            } else {
                                o_Vent.setVista("<-- Error Faltante de FIRMA en " + _arc.getName());
                                break;
                            }
                        } else {
                            String[] y_var=y_cadena.split("=");
                            if (y_var.length==2) {
                                String[] y_lite=y_var[1].split(",");
                                if (y_lite.length==4) o_Sig.setFirma(y_firma,
                                                                     y_var[0],
                                                                     y_lite[0],
                                                                     y_lite[1],
                                                                     y_lite[2],
                                                                     y_lite[3]);
                            }
                        }
                    }    
                }            
                y_cadena = renglon.readLine();
            }
        } catch(Exception e) {
            throw new Exception (e.getMessage());
        }
    }    
    
    
/***Clases Internas  ****/
    
    public class Signatures {
    
        private final Map<String, String[]> o_Firmas = new HashMap<>();

        public Optional<float[]> getLiterales(String _sig, String _var) { 
            try { 
                String[] y_lite=o_Firmas.get(_sig.concat("-").concat(_var));
                if (y_lite==null) return Optional.empty(); 
                return Optional.of(new float[]{
                    Float.parseFloat(y_lite[0]),  //offset
                    Float.parseFloat(y_lite[1]),  //mult
                    Float.parseFloat(y_lite[2])   //div
                });        
            } catch (NumberFormatException e) {
                o_Vent.setVista(">Error Estructura Firma:".concat(_sig).concat(" Variable:").concat(_var));
                return Optional.empty();
            }
        }
        
        public Optional<String> getCampo(String _sig, String _var) {
            try {
                String[] y_lite=o_Firmas.get(_sig.concat("-").concat(_var));
                if (y_lite==null) return Optional.empty();
                return Optional.of(y_lite[3]);
            } catch (Exception e) {
                return Optional.empty();
            }    
        } 

        public void setFirma(String _firma, String _var, String _offset, String _mult, String _div, String _cam) {
            if (_cam.isEmpty()) return;
            if ((_cam.trim().equals("-"))||(o_Columnas.containsKey(_cam.trim().toLowerCase()))) {
                List<String> y_rese=new ArrayList<>();
                y_rese.add("vehicle_id");
                y_rese.add("alias");
                y_rese.add("day");
                y_rese.add("month");
                y_rese.add("year");
                y_rese.add("gpstime");
                y_rese.add("date_time");
                y_rese.add("pc_date");
                y_rese.add("pc_time");
                y_rese.add("street");
                y_rese.add("city");
                y_rese.add("state");
                y_rese.add("zip_code");
                y_rese.add("latitude");
                y_rese.add("longitude");
                y_rese.add("speed");
                y_rese.add("course");
                y_rese.add("altitude");
                y_rese.add("event");
                y_rese.add("advisory_event");
                y_rese.add("distance");
                y_rese.add("satellites");
                y_rese.add("gps");
                y_rese.add("state_code");
                y_rese.add("inputs");
                y_rese.add("advisory_inputs");
                y_rese.add("outputs");
                y_rese.add("hdop");
                y_rese.add("custom1");
                y_rese.add("battery");
                y_rese.add("odometer");
                y_rese.add("aux_battery");
                y_rese.add("update_number");
                y_rese.add("county");
                y_rese.add("xml");
                y_rese.add("timestamp");
                y_rese.add("servidor");
                if (y_rese.contains(_cam.toLowerCase())) {
                    o_Vent.setVista("<-- Error en Firma ".concat(_firma)
                                                             .concat(": \r\n")
                                                             .concat("      El campo [")
                                                             .concat(_cam)
                                                             .concat("] Asignado a la variable [")
                                                             .concat(_var)
                                                             .concat("]\r\n")
                                                             .concat("      Es de uso Reservado\r\n "));
                    INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Archivos MAP] ERROR de Firma ".concat(_firma)
                                                                                                  .concat(" - El campo [").concat(_cam)
                                                                                                  .concat("] Asignado a la variable [").concat(_var)
                                                                                                  .concat(" Es de uso Reservado\r\n "));
                } else o_Firmas.putIfAbsent(_firma.concat("-").concat(_var), new String[]{_offset,_mult,_div,_cam});
            } else {
                o_Vent.setVista("<-- Error en Firma ".concat(_firma)
                                                         .concat(": \r\n")
                                                         .concat("      El campo [")
                                                         .concat(_cam)
                                                         .concat("] Asignado a la variable [")
                                                         .concat(_var)
                                                         .concat("]\r\n")
                                                         .concat("      No Esta definido en la tabla SQL\r\n "));
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Archivos MAP] ERROR de Firma ".concat(_firma)
                                                                                              .concat(" - El campo [").concat(_cam)
                                                                                              .concat("] Asignado a la variable [").concat(_var)
                                                                                              .concat(" No Esta definido en la tabla SQL\r\n "));   
            }
        }    
           
        /*
        public void imprime() {
            o_Firmas.keySet().stream().forEach((_i) -> {
                o_Vent.setVista("-->".concat(_i).concat(" : ").concat(o_Firmas.get(_i)[0]).concat(",")
                                                              .concat(o_Firmas.get(_i)[1]).concat(",")
                                                              .concat(o_Firmas.get(_i)[2]).concat(" --> ")
                                                              .concat(o_Firmas.get(_i)[3]));
            });
        }
        */
        
    }
    
    
}
