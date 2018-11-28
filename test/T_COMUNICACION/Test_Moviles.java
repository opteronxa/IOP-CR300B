package T_COMUNICACION;

import INTERFACE.PROTON_W_Ventana;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Raul
 */
public class Test_Moviles {
    
    static INTERFACE.PROTON_W_Ventana o_Vent;
    
    public Test_Moviles() {
        
    }
    
    @BeforeClass
    public static void setUpClass() {
        try {
            o_Vent=new PROTON_W_Ventana("CANiQ", "1.6.7.0", ARCHIVOS.PROTON_S_ArchivoINI.getInstancia());
        } catch (Exception e) {
            System.out.println("Windows Error");
        }    
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void prueba1() {
        try {
            COMUNICACION.PROTON_C_Movimientos o_Moviles=new COMUNICACION.PROTON_C_Movimientos(o_Vent, 5, new BITACORA.PROTON_C_Estadisticas(false));
            o_Moviles.AltaVID("990000", null);
            o_Moviles.AltaVID("990001", "#GETPOS");
        } catch (Exception e) {
            
        }
    }
}
