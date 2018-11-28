/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package T_BASEDATOS;

import INTERFACE.PROTON_W_Ventana;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
public class Test_Desencolador {
    
    public static BITACORA.PROTON_S_Log o_Log;
    static INTERFACE.PROTON_W_Ventana o_Vent;
    static ARCHIVOS.PROTON_C_ArchivosMAP o_Firmas;
    static List<String> x_tipos=new ArrayList<>();
    static ARCHIVOS.PROTON_S_ArchivoINI o_INI;
    
    public Test_Desencolador() {
        try {
            o_Log=BITACORA.PROTON_S_Log.getInstancia();     
            o_INI=ARCHIVOS.PROTON_S_ArchivoINI.getInstancia(); 
            o_Vent=new PROTON_W_Ventana("CANiQ", "1.6.7.0", o_INI);
            o_Firmas=new ARCHIVOS.PROTON_C_ArchivosMAP(o_Vent);
            x_tipos.add("0");
            x_tipos.add("9");
            x_tipos.add("11");
            
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
    public void prueba2() {
        ArrayBlockingQueue<String> o_Buffer=new ArrayBlockingQueue<>(5000, true);
        COMUNICACION.PROTON_C_Movimientos o_Control=new COMUNICACION.PROTON_C_Movimientos(o_Vent, 5, new BITACORA.PROTON_C_Estadisticas(o_INI.getEstadisticas()));
        CountDownLatch  o_Sincroniza=new CountDownLatch(1);
        try {
            COMUNICACION.PROTON_C_Escuchador o_Escuchador=new COMUNICACION.PROTON_C_Escuchador(o_Vent,   
                                                                                               o_Buffer,
                                                                                               o_Sincroniza,
                                                                                               o_Control,
                                                                                               o_INI); 
            BASEDATOS.PROTON_C_Desencolador o_Desencolador=new BASEDATOS.PROTON_C_Desencolador(o_Escuchador,
                                                                                               o_Buffer,
                                                                                               o_Vent,
                                                                                               o_Sincroniza,
                                                                                               o_Firmas,
                                                                                               o_INI);
            o_Desencolador.start();  
            o_Desencolador.join(2000);
            String x_trama="4D4347500B0BCA210008160200D2000000000008060000012416090006130000000000000000000000000000000000000000070700002606111609121905001E00010000029A001E00CD0C292215864004000000009A4004000000009F4004000000009B4004000000009C40040000000089400400000000844004000000009540040000000080400400000000814004000000008C4004000000008D400400000000904004000000008F4004000000008E400400000000834004000000008240040000000085400400000000884004000000008A4004000000008B40040000000024";
            String x_vid="2214411";
            String x_ip="197.0.0.1";
            int x_cons=1;
            long x_time=(Calendar.getInstance().getTimeInMillis())/1000;
            o_Buffer.offer("T!".concat(x_vid) 
                               .concat("!").concat(String.valueOf(x_time))
                               .concat("!").concat(String.valueOf(x_cons)) 
                               .concat("!").concat(x_trama)
                               .concat("!").concat(x_ip), 1, TimeUnit.SECONDS);
            o_Desencolador.join();
        } catch (InterruptedException e) {
            System.out.println("Error" + e.getMessage());
        }    
    }
}
