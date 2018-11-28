package T_COMUNICACION;

import INTERFACE.PROTON_W_Ventana;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 * @author Raul
 */
public class Test_Escuchador {
    
    public static BITACORA.PROTON_S_Log o_Log;
    static INTERFACE.PROTON_W_Ventana o_Vent;
    static ARCHIVOS.PROTON_S_ArchivoINI o_INI;
    
    public Test_Escuchador() {
        try {
            o_Log=BITACORA.PROTON_S_Log.getInstancia();     
            o_INI=ARCHIVOS.PROTON_S_ArchivoINI.getInstancia(); 
            o_Vent=new PROTON_W_Ventana("CANiQ", "1.6.7.0", o_INI);
        } catch (Exception e) {
            System.out.println("Windows Error " + e.getMessage());
        }    
        
    }
    
    @BeforeClass
    public static void setUpClass() {
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
        ArrayBlockingQueue<String> o_Buffer=new ArrayBlockingQueue<>(5000, true);
        COMUNICACION.PROTON_C_Movimientos o_Movimientos=new COMUNICACION.PROTON_C_Movimientos(o_Vent, 5, new BITACORA.PROTON_C_Estadisticas(o_INI.getEstadisticas()));
        o_Movimientos.AltaVID("2214411", null);
        CountDownLatch  o_Sincroniza=new CountDownLatch(1);
        try {
            COMUNICACION.PROTON_C_Escuchador o_Escuchador=new COMUNICACION.PROTON_C_Escuchador(o_Vent,   
                                                                                               o_Buffer,
                                                                                               o_Sincroniza,
                                                                                               o_Movimientos,
                                                                                               o_INI); 
            o_Escuchador.start();
            o_Escuchador.Rechazar(false);
            o_Sincroniza.countDown();
            do {
                System.out.println(o_Buffer.poll(1, TimeUnit.SECONDS));
            } while(true);
        } catch (InterruptedException e) {
            System.out.println("Error" + e.getMessage());
        }    
        
    }
    
}
