package BASEDATOS;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;

/**
 *
 * @author Raul
 */
public abstract class PROTON_A_SQLBase {
    
    private final int URL=0;
    private final int USER=1;
    private final int PASS=2;
    private final int BASE=3;
    protected Connection o_Conx;

    public PROTON_A_SQLBase(String[] _sql, String[] _tablas) throws Exception  {
        int y_e=0;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            o_Conx= DriverManager.getConnection("jdbc:sqlserver://".concat(_sql[URL])
                                                                   .concat(";databaseName=").concat(_sql[BASE])
                                                                   .concat(";user=").concat(_sql[USER])
                                                                   .concat(";password=").concat(_sql[PASS]).concat(";"));
            
            DatabaseMetaData o_md = o_Conx.getMetaData(); 
            for (String _tabla : _tablas) {
                try (final ResultSet o_rs = o_md.getTables(_sql[BASE], null, _tabla, null)) {
                    if (!o_rs.next()) {
                        y_e=2;
                        throw new Exception("Error Parametro .INI [".concat(_sql[BASE])
                                                                    .concat("] User:[").concat(_sql[USER])
                                                                    .concat("] Tabla:[").concat(_tabla)
                                                                    .concat("] No Existe o No tiene Permiso"));
                    }
                }catch(Exception e1) {
                    if (y_e==2) throw new Exception(e1.getMessage());
                    y_e=1;
                    throw new Exception("Error SQL Parametro .INI [".concat(_sql[BASE]).concat("] de comprobacion de tablas - ").concat(e1.getMessage()));
                }
            }    
        } catch (Exception e0) {
            if (y_e==0) throw new Exception("Error Parametro .INI SQL cadena de conexion [".concat(_sql[3]).concat("] ").concat(e0.getMessage()));
            else throw new Exception(e0.getMessage());
        }    
    }
    
}
