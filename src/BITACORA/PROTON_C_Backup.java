package BITACORA;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author R.Cuevas
**/

public class PROTON_C_Backup {
     //Variables Gobales
    private File o_Arch = null;
        
    public PROTON_C_Backup() throws Exception { 	//Constructor
        File Directorio = new File (".");
	try {
            o_Arch=new File(Directorio.getCanonicalPath().concat(File.separator).concat("RegistroSQL.dat"));
	} catch(IOException e) {
            if (e.getMessage()!=null) throw new Exception("Direcctorio para Encolamiento".concat(e.getMessage()));
            throw new Exception("Direcctorio para Encolamiento");
        }
    }
    
    public List<String> getRegistroTXT() throws Exception {
        List<String> o_DOC=new ArrayList<>();
        try {
            if (o_Arch.exists()) {
                try (BufferedReader o_linea=new BufferedReader(new FileReader(o_Arch))) {
                    String y_trama=o_linea.readLine();
                    while (y_trama!=null) {
                        o_DOC.add(y_trama);
                        y_trama=o_linea.readLine();
                    }
                }catch (IOException e1){
                    if (e1.getMessage()!=null) throw new Exception("Lectura RegistroTXT.DAT - ".concat(e1.getMessage()));
                    throw new Exception("Lectura RegistroTXT.DAT");
                }
                new BufferedWriter(new FileWriter(o_Arch)).close();
                o_Arch.delete();
            }
        } catch(Exception e0) {
           if (e0.getMessage()!=null) throw new Exception("Apertura de RegistroSQL.DAT - ".concat(e0.getMessage()));
           throw new Exception("Apertura de RegistroSQL.DAT");
        } 
        return o_DOC;
    }
    
    public int setRegistroTXT(List<StringBuilder[]> _doc) throws Exception {
        if (_doc.size()<=0) return 0;
        int y_tot=0;
        try (PrintWriter o_Imprime = new PrintWriter(new BufferedWriter(new FileWriter(o_Arch,true)))) {
            for (StringBuilder[] y_i:_doc) {
                ++y_tot;
                o_Imprime.println(y_i[0].toString().concat("Ç").concat(y_i[1].toString()));
            }    
        } catch (IOException e1) {
            throw new Exception("Error Apertura RegistroSQL.DAT" + e1.getMessage());
        }
        return y_tot;
    }
    
}
    
    
    
