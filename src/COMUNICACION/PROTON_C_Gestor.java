package COMUNICACION;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author R.Cuevas
 */
 
public final class PROTON_C_Gestor extends Thread { //Clase
	
	//objetos
    private final INTERFACE.PROTON_W_Ventana o_Vent;
    //variables	
    ARCHIVOS.PROTON_S_ArchivoINI o_INI;
    private final SynchronousQueue<Integer> o_Apagar = new SynchronousQueue<>();
    
    public  PROTON_C_Gestor(INTERFACE.PROTON_W_Ventana _ventana,       //Constructor
                         ARCHIVOS.PROTON_S_ArchivoINI _ini) {
    	o_Vent = _ventana;
        o_INI=_ini; 
    }
    
   public void Apagado(){ 
       try {
            o_Vent.TF_Xomando.setEnabled(false);
            o_Vent.Cambia_Estatus("ESP");
            o_Vent.setAviso("Proceso de Apagado");
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Gestor] Solicitud OFF");
            this.o_Apagar.put(1);
            this.o_Apagar.take();
            this.o_Vent.Cambia_Estatus("OFF");
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("*******PROTON OFF***********");
            o_Vent.TF_Xomando.setEnabled(true);
       } catch (InterruptedException e) {}    
   }        

  
    @Override
    public void run() {
        o_Vent.Cambia_Estatus("ESP");
        try (BITACORA.PROTON_C_Estadisticas o_Estadistica=new BITACORA.PROTON_C_Estadisticas(o_INI.getEstadisticas())) {
            if (o_Estadistica.getMensaje()) o_Vent.setAviso(o_Estadistica.getDescripcion());
            COMUNICACION.PROTON_C_Movimientos o_Movimientos=new COMUNICACION.PROTON_C_Movimientos(o_Vent, 5, o_Estadistica);     //5-maximo intentos ota
            try (BASEDATOS.PROTON_C_SQLImportar o_SQLBaseI = new BASEDATOS.PROTON_C_SQLImportar(o_Vent,
                                                                                                o_Movimientos,
                                                                                                o_INI.Nombre_IOP(),
                                                                                                o_INI.getSQL("SQL_IMPORTAR"),
                                                                                                o_INI.getMaximo())) {
                ArrayBlockingQueue<String> o_Buffer=new ArrayBlockingQueue<>(5000, true);
                o_Vent.Actividad(true);
                CountDownLatch  o_Sincroniza=new CountDownLatch(1);
                COMUNICACION.PROTON_C_Escuchador o_Escuchador=new COMUNICACION.PROTON_C_Escuchador(o_Vent,   
                                                                                                   o_Buffer,
                                                                                                   o_Sincroniza,
                                                                                                   o_Movimientos,
                                                                                                   o_INI);  
                o_Escuchador.start();
                BASEDATOS.PROTON_C_Desencolador o_Desencolador=new BASEDATOS.PROTON_C_Desencolador(o_Escuchador,
                                                                                                   o_Buffer,
                                                                                                   o_Vent,
                                                                                                   o_Sincroniza,
                                                                                                   new ARCHIVOS.PROTON_C_ArchivosMAP(o_Vent),
                                                                                                   o_INI);
                o_Desencolador.start();  
                o_Vent.setAviso("En Linea...\r\n");
                o_Vent.TF_Xomando.setEnabled(true);
                if (o_SQLBaseI.getEstatus()) o_SQLBaseI.Actualizar();
                o_Vent.MovilesConetados(0);
                int y_frc=1;
                do {
                    if (o_SQLBaseI.getEstatus()&&o_Escuchador.getEstatus()&&o_Desencolador.getEstatus()) o_Vent.Cambia_Estatus("ON");
                    if (o_Apagar.poll(1, TimeUnit.SECONDS)!=null) break;
                    if (y_frc==0) {
                        o_Vent.Actividad(true);
                        if (o_SQLBaseI.getEstatus()) o_SQLBaseI.Actualizar();
                        else o_SQLBaseI.Reconectar();
                    } else o_Vent.Actividad(false);
                    y_frc=(++y_frc)%o_INI.getFrecuencia();
                } while(true);
                o_SQLBaseI.setEsperar(true);
                o_Vent.setVista("\r\n");
                o_Escuchador.Apagar_Escuchador();
                o_Escuchador.join();
                o_Desencolador.join();
            } catch (Exception e1) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Gestor] ".concat(e1.getMessage()));
            }    
        } finally {
            try {
                o_Apagar.put(2);
            } catch (InterruptedException e) {
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Gestor] de Estructura");
                o_Vent.setAviso("Error de estructura en GESTOR (Apagar por S.O.)");
                o_Vent.Cambia_Estatus("FAT");
            }
        }    
    }
    
}    

