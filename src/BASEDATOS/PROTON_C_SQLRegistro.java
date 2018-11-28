package BASEDATOS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author R.Cuevas
 */
public class PROTON_C_SQLRegistro implements AutoCloseable {

    private final PROTON_C_ProtocoloCR300B_PROTO o_Protocolo;
    private final List<StringBuilder[]> o_DOC = new ArrayList<>();
    private final Calendar x_Hoy=Calendar.getInstance();
    private COMUNICACION.PROTON_C_Replicador o_Replica=null;
    private boolean x_rep=false;
    private final String[] x_meses={"ENE","FEB","MAR","ABR","MAY","JUN","JUL","AGO","SEP","OCT","NOV","DIC"};

    public PROTON_C_SQLRegistro(ARCHIVOS.PROTON_C_ArchivosMAP _firmas,
                                List<String> _tipos) {
        o_Protocolo=new BASEDATOS.PROTON_C_ProtocoloCR300B_PROTO(_tipos);
    }
      
    public PROTON_C_SQLRegistro(INTERFACE.PROTON_W_Ventana _vent, 
                                ARCHIVOS.PROTON_C_ArchivosMAP _firmas,
                                List<String> _tipos,
                                List<String> _elec) {
        try {
            o_Replica=new COMUNICACION.PROTON_C_Replicador(_elec, _vent);
            x_rep=true;
//System.out.println("replica: " + x_rep);
        } catch (Exception e) {
           x_rep=false; 
        }    
        o_Protocolo=new BASEDATOS.PROTON_C_ProtocoloCR300B_PROTO(_tipos);
    }
    
    
    public String Interpreta_Trama(String _t,
                                   String _vid,
                                   String _hur,
                                   String _ncon,
                                   String _trama,
                                   String _ip) {
        StringBuilder[] y_qry=o_Protocolo.Registro(_t,                          //INTERPRETE
                                                   _trama,
                                                   _vid,
                                                   _hur,
                                                   _ncon,
                                                   _ip);  
        this.o_DOC.add(y_qry);
//System.out.println("Envia0" + y_qry[0]);
//System.out.println("Envia0" + y_qry[1]);      
        if (x_rep) {                                                            //Replica a Electron
            if (y_qry[0].lastIndexOf("[Latitude]")>=0) x_rep=o_Replica.Replicar(y_qry[0].toString().concat("@").concat(y_qry[1].toString()));
            if (!x_rep) o_Replica.close();
        } 
        return ("[".concat(x_meses[x_Hoy.get(Calendar.MONTH)])
                   .concat(String.valueOf(x_Hoy.get(Calendar.DAY_OF_MONTH)))
                   .concat("]"));
    }
    
    public boolean getPendientes() {
        return !this.o_DOC.isEmpty();
    }
    
    public List<StringBuilder[]> getRegistros() {
        return this.o_DOC;
    }
    
    public void Depura() {
        this.o_DOC.clear();
    }
    
    //- Desde Respando a Disco
    
    public void Pone_DOC(List<String> _reg) {
        try {
            if (!_reg.isEmpty()) {
                _reg.stream().map((y_r) -> y_r.split("Ç")).map((y_pp) -> {
                    StringBuilder[] y_pr=new StringBuilder[2];
                    y_pr[0]=new StringBuilder(y_pp[0]);
                    y_pr[1]=new StringBuilder(y_pp[1]);
                    return y_pr;
                }).forEach((y_pr) -> {
                    o_DOC.add(y_pr);
                });
            }    
        } catch (Exception e) {
            //INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[SQLExporta] Error Insercion x_DOC de la recuperacion TXT - " + e.getMessage());
        }    
    }
    
    @Override
    public void close() {
       if (o_Replica!=null) o_Replica.close();
    }    
}