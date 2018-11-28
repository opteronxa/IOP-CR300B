package INTERFACE;

/**
 * 
 * @author R.Cuevas
 */

//import BASEDATOS.PROTON_C_Desencolador;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;


//Clase 
public final class PROTON_W_Ventana {
	
    //Objetos
    private COMUNICACION.PROTON_C_Gestor o_Gestor=null;
    private final Control_Servicio o_Control;
    private final Frame o_Vista = new Frame();
    private final JProgressBar PB_Buff=new JProgressBar();
    public final TextField TF_Xomando=new TextField();
    private final Label L_Estatus=new Label("OFF");
    private final JTextField TF_Dups=new JTextField();
    private final JTextField TF_Otas=new JTextField();
    private final JTextField TF_Solicitud=new JTextField();
    private final JTextField TF_Unidades=new JTextField();
    private final JTextField TF_Altas=new JTextField();
    private final JTextField TF_Ins1=new JTextField();
    private final JTextField TF_Ins2=new JTextField();
    private final JTextField TF_Ins3=new JTextField();
    private final JTextField TF_Recv=new JTextField();
    private final JTextField TF_Activo=new JTextField();
    private final JTextField TF_Actual=new JTextField();
    public TextArea TA_INI=new TextArea(); 
    public TextArea TA_Tramas=new TextArea(); 
    public TextArea TA_Mensajes=new TextArea(); 
    private final Lock o_Candado=new ReentrantLock();
    //Variables Globales
    private final StringBuilder x_utext=new StringBuilder();
    private final StringBuilder x_umens=new StringBuilder();
    private int x_lineasM=0;
    private int x_lineasT=0;
    private final AtomicInteger x_otas = new AtomicInteger(0);
    private int x_soli=0;
    private int x_alta=0;
    private int x_reci=0;
    private int x_ins1=0;
    private int x_ins2=0;
    private int x_ins3=0;
    private short x_ves=0;
    private short x_cic=0;

public PROTON_W_Ventana(String _proto,
                      String _ver,
                      ARCHIVOS.PROTON_S_ArchivoINI _ini) throws Exception {
        o_Control=new Control_Servicio(_ini, this);
        Toolkit y_tk=Toolkit.getDefaultToolkit();
        o_Vista.setBounds(150,80,910,650);
        try {
            o_Vista.setIconImage(y_tk.getImage((new File(".").getCanonicalPath()).concat(File.separator).concat("csi.png")));
        } catch (IOException e) {
            throw new IOException ("Error en Directorio del .jar");
        }    
        //fuentes
        Font fuente0=new Font("Monospeced", Font.CENTER_BASELINE,22);
        Font fuente1=new Font("Monospaced", Font.BOLD, 16);
        Font fuente2=new Font("Monospaced", Font.TRUETYPE_FONT, 12);
        Font fuente3=new Font("Monospaced", Font.BOLD, 15);
        //paneles       
        Panel base0 = new Panel(null);
        Label L_ST=new Label(" P R O T O N - G.1");
        L_ST.setFont(new Font("Monospaced", Font.BOLD+Font.PLAIN, 20));
        L_ST.setBounds(330, 1, 275, 20);
        base0.add(L_ST);
        Panel base1 = new Panel(null);
        base1.setBounds(1,21,790,50);
        JPanel base1a = new JPanel(null);
        base1a.setBounds(792,21,101,51);
        Panel base2 = new Panel(null);
        base2.setBounds(1,68,892,460);
        Panel base3 = new Panel(null);
        base3.setBounds(1,530,892,80);
        //base1
        JLabel L_Proto = new JLabel("     ".concat(_proto));
        L_Proto.setBounds(8,0,155,45);
        L_Proto.setBackground(Color.GRAY);
        L_Proto.setForeground(new Color(82,94,243));  
        L_Proto.setFont(fuente0);
        L_Proto.setBorder(BorderFactory.createLineBorder(Color.black));
        L_Proto.setBorder(BorderFactory.createTitledBorder( BorderFactory.createEtchedBorder(),"Protocolo"));
        Label L_Xom=new Label("Comando:");
        L_Xom.setFont(fuente1);
        L_Xom.setBounds(203,17,80,20);
        TF_Xomando.addActionListener(o_Control);
        TF_Xomando.setForeground(Color.blue);
        TF_Xomando.setFont(fuente3);
        TF_Xomando.setBounds(290,15,170,20);
        TF_Xomando.setCaretPosition(2);
        TF_Xomando.setFocusable(true);
        Label L_ver=new Label("CSI.PROTON ".concat(_ver));
        L_ver.setFont(new Font("Serif", Font.ITALIC, 14));
        L_ver.setBounds(615,25,150,35);
        L_ver.setForeground(Color.LIGHT_GRAY);
        base1.add(L_Proto);
        base1.add(L_Xom);
        base1.add(TF_Xomando);
        base1.add(L_ver);
        base1.add(L_Estatus);
        base1.setBackground(Color.gray);
        //base 1a
        L_Estatus.setBounds(8,10,85,30);
        L_Estatus.setFont(new Font("Monospaced", Font.PLAIN, 12)); 
        L_Estatus.setBackground(Color.gray);
        L_Estatus.setAlignment(Label.CENTER);
        base1a.setBackground(Color.lightGray);
        base1a.add(L_Estatus);
        base1a.setBorder(BorderFactory.createEtchedBorder());
        //base2
        TA_INI.setFont(fuente2);
        TA_INI.setBounds(1,5,280,200);
        TA_INI.setBackground(Color.BLACK);
        TA_INI.setForeground(Color.ORANGE);
        TA_INI.setEditable(false);
        TA_Mensajes.setFont(fuente2);
        TA_Mensajes.setBounds(1,210,280,250);
        TA_Mensajes.setBackground(Color.LIGHT_GRAY);
        TA_Mensajes.setEditable(false);
        TA_Tramas.setBounds(282,5,610,455);
        TA_Tramas.setEditable(false);
        TA_Tramas.setFont(fuente2);
        base2.add(TA_INI);          
        base2.add(TA_Mensajes);
        base2.add(TA_Tramas);
        //base3
        JPanel base3a = new JPanel(null);
        base3a.setBounds(25, 2, 227, 76);
        base3a.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()," Moviles "));
        base3a.setBackground(new Color(0,196,136));
        Label L_Sol=new Label("Peticiones:");
        L_Sol.setBounds(5,15,80,20);
        L_Sol.setFont(fuente2);
        Label L_Unid=new Label("Activos...:");
        L_Unid.setBounds(5,33,80,20);
        L_Unid.setFont(fuente2);
        Label L_Diag=new Label("Inactivos.:");
        L_Diag.setBounds(5,52,80,20);
        L_Diag.setFont(fuente2);
        TF_Solicitud.setBounds(87,15, 40, 18);
        TF_Solicitud.setBackground(Color.pink);
        TF_Solicitud.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Solicitud.setHorizontalAlignment(JTextField.RIGHT);
        TF_Solicitud.setEditable(false);
        TF_Unidades.setText("0");
        TF_Unidades.setBounds(87,33, 40, 18);
        TF_Unidades.setBackground(Color.pink);
        TF_Unidades.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Unidades.setHorizontalAlignment(JTextField.RIGHT);
        TF_Unidades.setEditable(false);
        TF_Unidades.setCaretColor(Color.red);
        TF_Altas.setBounds(87,51, 40, 18);
        TF_Altas.setBackground(Color.pink);
        TF_Altas.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Altas.setHorizontalAlignment(JTextField.RIGHT);
        TF_Altas.setEditable(false);
        
        TF_Activo.setBounds(170, 15, 40, 30);
        TF_Activo.setBackground(new Color(0,196,136));
        TF_Activo.setBorder(BorderFactory.createEmptyBorder());
        TF_Activo.setFont(new Font("Monospaced", Font.BOLD, 30));
        TF_Activo.setForeground(new Color(1,130,90));
        TF_Activo.setText("|");
        TF_Activo.setEditable(false);
        
        TF_Actual.setBounds(140, 45, 75, 20);
        TF_Actual.setBackground(new Color(0,196,136));
        TF_Actual.setBorder(BorderFactory.createEmptyBorder());
        TF_Actual.setFont(new Font("Monospaced", Font.ITALIC, 10));
        TF_Actual.setForeground(new Color(1,130,90));
        TF_Actual.setEditable(false);
        TF_Actual.setVisible(false);
        TF_Actual.setText("Actualizando");
        
        base3a.add(TF_Activo);
        base3a.add(TF_Actual);
        
        base3a.add(L_Sol);
        base3a.add(TF_Solicitud);
        base3a.add(L_Unid);
        base3a.add(TF_Unidades);
        base3a.add(L_Diag);
        base3a.add(TF_Altas);
        base3.add(base3a);
        JPanel base3b = new JPanel(null);
        base3b.setBounds(310, 2, 165, 76);
        base3b.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()," Tramas "));
        base3b.setBackground(new Color(0,196,136));
        Label L_Recv=new Label("Recibidas.:");
        L_Recv.setBounds(5,15,80,20);
        L_Recv.setFont(fuente2);
        TF_Recv.setText("0");
        TF_Recv.setBounds(85,15,70, 18);
        TF_Recv.setBackground(Color.pink);
        TF_Recv.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Recv.setHorizontalAlignment(JTextField.RIGHT);
        TF_Recv.setEditable(false);
        base3b.add(TF_Recv);
        Label L_Dups=new Label("Duplicadas:");
        L_Dups.setBounds(5,33,80,20);
        L_Dups.setFont(fuente2);
        TF_Dups.setText("0");
        TF_Dups.setBounds(85,33, 70, 18);
        TF_Dups.setBackground(Color.pink);
        TF_Dups.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Dups.setHorizontalAlignment(JTextField.RIGHT);
        TF_Dups.setEditable(false);
        Label L_Otas=new Label("Otas......:");
        L_Otas.setBounds(5,52,80,20);
        L_Otas.setFont(fuente2);
        TF_Otas.setText("0");
        TF_Otas.setBounds(85,51,70,18);
        TF_Otas.setBackground(Color.pink);
        TF_Otas.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Otas.setHorizontalAlignment(JTextField.RIGHT);
        TF_Otas.setEditable(false);
        base3b.add(L_Recv);
        base3b.add(L_Dups);
        base3b.add(L_Otas);
        base3b.add(TF_Dups);
        base3b.add(TF_Otas);
        base3.add(base3b);
        JPanel base3c = new JPanel(null);
        base3c.setBounds(480, 15, 165, 45);
        base3c.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()," Buffer "));
        base3c.setBackground(new Color(0,136,136));
        PB_Buff.setValue(0);
        PB_Buff.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        PB_Buff.setStringPainted(true);
        PB_Buff.setBounds(5, 15, 150, 20);
        PB_Buff.setForeground(Color.black);
        base3c.add(PB_Buff);
        base3.add(base3c);
        JPanel base3d = new JPanel(null);
        base3d.setBounds(650, 2, 220, 76);
        base3d.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()," Insertados "));
        base3d.setBackground(new Color(0,196,136));
        Label L_Sql1=new Label("1) VehicleState...:");
        L_Sql1.setBounds(5,15,134,17);
        L_Sql1.setFont(fuente2);
        TF_Ins1.setBounds(143,15,70, 18);
        TF_Ins1.setBackground(Color.pink);
        TF_Ins1.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Ins1.setHorizontalAlignment(JTextField.RIGHT);
        TF_Ins1.setEditable(false);
        base3d.add(L_Sql1);
        base3d.add(TF_Ins1);
        Label L_Sql2=new Label("2) VehicleState[x]:");
        L_Sql2.setBounds(5,33,134,17);
        L_Sql2.setFont(fuente2);
        TF_Ins2.setBounds(143,33,70, 18);
        TF_Ins2.setBackground(Color.pink);
        TF_Ins2.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Ins2.setHorizontalAlignment(JTextField.RIGHT);
        TF_Ins2.setEditable(false);
        base3d.add(L_Sql2);
        base3d.add(TF_Ins2);
        Label L_Sql3=new Label("3) Vehiclestate[x]:");
        L_Sql3.setBounds(5,51,134,17);
        L_Sql3.setFont(fuente2);
        TF_Ins3.setBounds(143,51,70, 18);
        TF_Ins3.setBackground(Color.pink);
        TF_Ins3.setBorder(BorderFactory.createLineBorder(Color.black));
        TF_Ins3.setHorizontalAlignment(JTextField.RIGHT);
        TF_Ins3.setEditable(false);
        base3d.add(L_Sql3);
        base3d.add(TF_Ins3);
        base3.add(base3d);
        base3.setBackground(new Color(0,136,136));
        base0.add(base1);
        base0.add(base1a);
        base0.add(base2);
        base0.add(base3);
        o_Vista.add(base0);
        o_Vista.setResizable(false);
        o_Vista.setVisible(true);  
        o_Vista.addWindowListener(new Conclusion());
        o_Vista.setTitle(_ini.Nombre_IOP().concat(" - ").concat(_ver));
        this.Cambia_Estatus("OFF");
    }

    public void AutoON() {
        INTERFACE.PROTON_M_CSI.o_Log.Bitacora("[Ventana] AUTO Encendido");
        o_Control.ON_Servicio();
    }
    
    public void Cambia_Estatus(String _estatus) {
        if (x_ves==6) return;
        switch (_estatus)  {
            case "ON":
                if (x_ves==1) break;
                L_Estatus.setForeground(Color.BLACK);
                L_Estatus.setText("ON");
                L_Estatus.setBackground(Color.GREEN);
                x_ves=1;
                break;
            case "ERR":
                if (x_ves==2) break;
                L_Estatus.setForeground(Color.BLACK);
                L_Estatus.setText("ERR SQL"); 
                L_Estatus.setBackground(Color.RED);
                x_ves=2;
                break;
            case "ESP":
                if (x_ves==3) break;
                L_Estatus.setForeground(Color.WHITE);
                L_Estatus.setText("ESPERE"); 
                L_Estatus.setBackground(Color.GRAY);
                x_ves=3;
                break;  
            case "TCP":
                if (x_ves==4) break;
                L_Estatus.setForeground(Color.BLACK);
                L_Estatus.setText("ERR TCP"); 
                L_Estatus.setBackground(Color.RED);
                x_ves=4;
                break; 
            case "SQL":
                if (x_ves==5) break;
                L_Estatus.setForeground(Color.BLACK);
                L_Estatus.setText("ERR SQL"); 
                L_Estatus.setBackground(Color.RED);
                x_ves=5;
                break;     
            case "FAT":
                L_Estatus.setForeground(Color.WHITE);
                L_Estatus.setText("FORZAR S.O."); 
                L_Estatus.setBackground(Color.RED);
                x_ves=6;
                break;                  
            default:
                if (x_ves==0) break;
                L_Estatus.setForeground(Color.black);
                L_Estatus.setText("OFF"); 
                L_Estatus.setBackground(Color.GRAY);
                x_ves=0;
                break;
        }
        this.TF_Xomando.requestFocus();
     }

    public void EscribeAvisoINI (String _aviso) {
        this.TA_INI.insert(_aviso,0);
        this.TA_INI.setCaretPosition(0);
    }

    public void setAviso (String _mensaje) {
        this.o_Candado.lock();
        try {
            ++x_lineasM;
            if ((x_lineasM>=95)&&(x_lineasM)<100) this.x_umens.append(_mensaje).append("\r\n");
            if (x_lineasM==100) TA_Mensajes.setText(this.x_umens.toString());
            this.TA_Mensajes.append(_mensaje.concat("\r\n"));
            x_lineasM=x_lineasM%100;
        } catch (Exception e) {
            TA_Mensajes.setText("Error Despliege Mensaje");
        } finally {this.o_Candado.unlock();}    
    }

    public void setVista (String _texto) {
        this.o_Candado.lock();
        try {
            ++x_lineasT;
            if ((x_lineasT>=295)&&(x_lineasT<300)) this.x_utext.append(_texto).append("\r\n");
            if (x_lineasT==300) TA_Tramas.setText(this.x_utext.toString());
            this.TA_Tramas.append(_texto.concat("\r\n"));
            x_lineasT=x_lineasT%300;
        } catch (Exception e) {
            TA_Tramas.setText("Error Despliege Texto");
        } finally {this.o_Candado.unlock();}    
      }  
    
    public boolean Altas(int _altas) {
        if (x_alta==_altas) return false;
        x_alta=_altas;
        return true;
    }

    public void Otas() {
        x_otas.incrementAndGet();
        this.TF_Otas.setText(String.valueOf(x_otas.get()));
    }
    
    public void Dups(int _dup) {
        this.TF_Dups.setText(String.valueOf(_dup));
    }

    public void MovilesConetados(int _con) {
        this.TF_Unidades.setText(String.valueOf(_con));
        this.TF_Altas.setText(String.valueOf(x_alta-_con));
    }
      
    public synchronized void Solicitudes(boolean _sol) {
        if (_sol) ++x_soli;
        else --x_soli;
        this.TF_Solicitud.setText(x_soli>0?String.valueOf(x_soli):"");
    }
        
    public void Actividad(boolean _ac) {
        ++x_cic;
        if (x_cic>=8) x_cic=0;        
        switch (x_cic) {
            case 1:  this.TF_Activo.setText("/"); break;
            case 2:  this.TF_Activo.setText("-"); break;
            case 3:  this.TF_Activo.setText("\\"); break;
            case 4:  this.TF_Activo.setText("|"); break;
            case 5:  this.TF_Activo.setText("/"); break;
            case 6:  this.TF_Activo.setText("-"); break;
            case 7:  this.TF_Activo.setText("\\"); break;
            default: this.TF_Activo.setText("|"); break;
        }
        this.TF_Actual.setVisible(_ac);
    }
    
    public void recibidas() {
        x_reci=(++this.x_reci)%1000000000;
        this.TF_Recv.setText(String.valueOf(x_reci));
    }    
        
    public void InseSQL(int _isn1, int _isn2,int _isn3) {
        if (_isn1>0) {
            this.x_ins1+=_isn1;
            this.x_ins1=this.x_ins1%1000000000;
            this.TF_Ins1.setText(String.valueOf(x_ins1));
        }
        if (_isn2>0) {
            this.x_ins2+=_isn2;
            this.x_ins2=this.x_ins2%1000000000;
            this.TF_Ins2.setText(String.valueOf(x_ins2));
        }    
        if (_isn3>0) {
            this.x_ins3+=_isn3;
            this.x_ins3=this.x_ins3%1000000000;
            this.TF_Ins3.setText(String.valueOf(x_ins3));
        }
    }   
    
    public void OffSQL1(boolean _on) {
        if (!_on) this.TF_Ins1.setText("OFF");
        else this.TF_Ins1.setText("0");
         this.x_ins1=0;
    }  
    
    public void OffSQL2(boolean _on) {
        if (!_on) this.TF_Ins2.setText("OFF");
        else this.TF_Ins2.setText("0");
        this.x_ins2=0;
    }  
        
    public void OffSQL3(boolean _on) {
        if (!_on) this.TF_Ins3.setText("OFF");
        else this.TF_Ins3.setText("0");
        this.x_ins3=0;
    }  
    
    public boolean Encoladas(int _colasize) {
        int y_perc=Math.round(_colasize*100/5000);
        PB_Buff.setValue(y_perc);
        return y_perc>60;
    }
        
    public synchronized void Error_Bitacora() {
        this.o_Vista.setBackground(Color.red);
        this.setVista(">>>ERROR DE BITACORA<<<");
    } 
    /*--*/
    
    
    /****Sub Class 1*********************************************************/

    private class Control_Servicio implements ActionListener {    

        private final ARCHIVOS.PROTON_S_ArchivoINI o_INI;
        private final INTERFACE.PROTON_W_Ventana o_Vent;
        
        public Control_Servicio(ARCHIVOS.PROTON_S_ArchivoINI _ini, INTERFACE.PROTON_W_Ventana _vent) {
            o_INI=_ini;
            o_Vent=_vent;
        }
                       	   	   	
   	@Override 
        public void actionPerformed(ActionEvent evt) {
            String y_com=TF_Xomando.getText();
            if (y_com.isEmpty()) return;
            setAviso(">>".concat(y_com));                   
            String[] y_par=y_com.concat("-!").split("-");    
            y_par[0]=y_par[0].toUpperCase();

            switch (y_par[0]) {
                case "ON": ON_Servicio(); break; 
                case "OFF": OFF_Servicio(); break; 
                case "BYE": this.BYE_Salir(); break; 
                case "EXIT": this.BYE_Salir(); break; 
                case "SALIR": this.BYE_Salir(); break; 
                case "VID": VID_Servicio(y_par[1]); break;
                case "*": VID_Servicio("TODOS"); break;
                default:
                    setAviso("¡COMANDO NO RECONOCIDO!");
                    setAviso("Intentelo de Nuevo\r\n");
                    break;    
            }
        }
	  
	private void ON_Servicio() {
            INTERFACE.PROTON_M_CSI.o_Log.Bitacora("PROTON ON");
            if (o_Gestor==null) {
                TF_Xomando.setEnabled(false);
                OffSQL1(o_INI.getSQL_ON(1));
                OffSQL2(o_INI.getSQL_ON(2));
                OffSQL3(o_INI.getSQL_ON(3));
                TF_Otas.setText("0");
                TF_Recv.setText("0");
                TF_Dups.setText("0");
                x_reci=0;
                x_otas.set(0);
                BASEDATOS.PROTON_C_ProtocoloCR300B_PROTO.x_TPMS=o_INI.getTPMS();
                try {
                    BASEDATOS.PROTON_C_Desencolador.FLUSH=o_INI.getFlush();
                    BASEDATOS.PROTON_C_Desencolador.BATCH=o_INI.getBacth();
                    o_Gestor = new COMUNICACION.PROTON_C_Gestor(o_Vent, o_INI);  
                    o_Gestor.start();
                    setAviso("Encendiendo");
                    setAviso("Espere un momento... ");
                } catch (Exception e) {
                    setVista(">ERROR de START: ".concat(e.getMessage()));
                }
            } else setAviso("La IOP ya esta Escuchando\r\n");  
            TF_Xomando.setText(null);
	}
	  
	private void OFF_Servicio() {
	    if (o_Gestor==null) setAviso("La IOP ya esta Apagada\r\n");
            else {            
                INTERFACE.PROTON_M_CSI.o_Log.Bitacora("<<OFF>> procesando solicitud de Apagado");
                try {
                    TF_Xomando.setEnabled(false);
                    o_Gestor.Apagado();
  	            o_Gestor.join();
                    o_Vent.setAviso("\r\n------------------------------");
                    o_Vent.setAviso("  ** RESUMEN **");
                    o_Vent.setAviso("------------------------------");
                    o_Vent.setAviso(" Recibidas......: ".concat(TF_Recv.getText()));
                    o_Vent.setAviso(" Insertadas en 1: ".concat(TF_Ins1.getText()));
                    o_Vent.setAviso(" Insertadas en 2: ".concat(TF_Ins2.getText()));
                    o_Vent.setAviso(" Insertadas en 3: ".concat(TF_Ins3.getText()));
                    o_Vent.setAviso(" Duplicadas.....: ".concat(TF_Dups.getText()));
                    o_Vent.setAviso(" Otas...........: ".concat(TF_Otas.getText()));
                    o_Vent.setAviso("------------------------------");
                    o_Vent.setAviso(" IOP 100% Apagada");
                    o_Vent.setAviso("------------------------------\r\n\r\n");
                    setVista("\r\n**GRACIAS por utilizar este Software (Luar-RCB)**\r\n");
                    TF_Altas.setText("");
                    TF_Otas.setText("");
                    TF_Recv.setText("");
                    TF_Dups.setText("");
                    TF_Ins1.setText("");
                    TF_Ins2.setText("");
                    TF_Ins3.setText("");
                    TF_Xomando.setEnabled(true);
    	        } catch(InterruptedException e) {
                    setVista(">ERROR de OFF: ".concat(e.getMessage()));
                }
            }
            TF_Xomando.setText(null);
            o_Gestor=null;
	}
	 
        private void VID_Servicio(String _vid) {
	    if (o_Gestor==null) {
                setAviso("COMANDO solo puede");
                setAviso("ser aplicado en ON\r\n");
            } else  {    
                if (_vid.isEmpty()) BASEDATOS.PROTON_C_Desencolador.MVI=false;
                else {
                    BASEDATOS.PROTON_C_Desencolador.VID=(_vid.toUpperCase().equals("TODOS"))?"*":_vid;
                    BASEDATOS.PROTON_C_Desencolador.MVI=true;
                }
            }
            TF_Xomando.setText(null);
	}
                 
        private void BYE_Salir() {
            TF_Xomando.setText(null);
            if (o_Gestor!=null) {
                if (o_Gestor.isAlive()) {
                    setAviso("*No se puede");
                    setAviso("Salir de la IOP");
                    setAviso("sin apagar primero");
                    setAviso("** Escriba OFF **");
                    setAviso("... ");
                    return;
                } else o_Gestor=null;
            }
            try {
                setVista("<<Saliendo Ahora>>");
                setAviso("\r\n    ¡HASTA LA VISTA, AMIGO!");
                Thread.sleep(2000);
                INTERFACE.PROTON_M_CSI.o_Log.Off_Log(">>>PROTON EXIT<<<");
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(null, "!Error de cierre de PROTON!", "PROTON ERROR", JOptionPane.ERROR_MESSAGE);
                System.out.println("ERROR: de proceso: "  + e.getMessage() );
                System.out.println("No se puedo ecerrar la aplicacion. Termine por S.O."); 
            } 
            System.exit(0);
	}
 
    }
    
/****sub class 2*********************************************************/
    private class Conclusion extends WindowAdapter {
        
        @Override public void windowClosing( WindowEvent evt ) {
            if (o_Gestor!=null) {
                setAviso("*No se puede");
                setAviso("salir de la IOP");
                setAviso("estando en ON");
                setAviso("** Apague con OFF **");
                setAviso("... ");
            } else {   
                try {
                    setVista("<<Saliendo Ahora>>");
                    setAviso("\r\n    ¡HASTA LA VISTA, AMIGO!");
                    Thread.sleep(2000);
                    INTERFACE.PROTON_M_CSI.o_Log.Off_Log(">>>PROTON EXIT<<<");
                } catch (InterruptedException e) {
                    JOptionPane.showMessageDialog(null, "!Error de cierre de PROTON!", "PROTON ERROR", JOptionPane.ERROR_MESSAGE);
                    System.out.println("ERROR: de proceso: "  + e.getMessage() );
                    System.out.println("No se puedo ecerrar la aplicacion. Termine por S.O.");
                } 
                System.exit(0);
            }
        }
    }
   
    
}