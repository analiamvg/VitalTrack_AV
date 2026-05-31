package vitaltrack.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import vitaltrack.logica.AnalizadorTendencias;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.Paciente;
import vitaltrack.monitor.MonitorSignosVitales;

public class PanelDashboard extends JPanel {

    private SistemaGestion   sistema;
    private VentanaPrincipal ventana;

    private JLabel lblCantPacientes;
    private JLabel lblCantMonitores;
    private JLabel lblCantAlertas;
    private JLabel lblCantCriticos;

    private DefaultTableModel modeloTablaPacientes;
    private DefaultTableModel modeloTablaAlertas;

    public PanelDashboard(SistemaGestion sistema, VentanaPrincipal ventana) {
        this.sistema  = sistema;
        this.ventana  = ventana;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        
        construirUI();
    }

    private void construirUI() {
        //Encabezado
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(VentanaPrincipal.COLOR_FONDO);
        
        JLabel titulo = new JLabel("Pagina Principal");
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        JLabel subtitulo = new JLabel("Resumen general del sistema en tiempo real");
        
        panelTextos.add(titulo);
        panelTextos.add(subtitulo);

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizar();
            }
        });

        encabezado.add(panelTextos, BorderLayout.WEST);
        encabezado.add(btnRefresh, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        //Principal
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel filaTarjetas = new JPanel(new GridLayout(1, 4, 10, 10));
        filaTarjetas.setBackground(VentanaPrincipal.COLOR_FONDO);

        lblCantPacientes = new JLabel("0", SwingConstants.CENTER);
        lblCantPacientes.setFont(new Font("Arial", Font.BOLD, 24));
        JPanel tarjeta1 = new JPanel(new BorderLayout());
        tarjeta1.add(new JLabel(" Pacientes", SwingConstants.CENTER), BorderLayout.NORTH);
        tarjeta1.add(lblCantPacientes, BorderLayout.CENTER);

        lblCantMonitores = new JLabel("0", SwingConstants.CENTER);
        lblCantMonitores.setFont(new Font("Arial", Font.BOLD, 24));
        JPanel tarjeta2 = new JPanel(new BorderLayout());
        tarjeta2.add(new JLabel(" Monitores", SwingConstants.CENTER), BorderLayout.NORTH);
        tarjeta2.add(lblCantMonitores, BorderLayout.CENTER);

        lblCantAlertas = new JLabel("0", SwingConstants.CENTER);
        lblCantAlertas.setFont(new Font("Arial", Font.BOLD, 24));
        JPanel tarjeta3 = new JPanel(new BorderLayout());
        tarjeta3.add(new JLabel(" Alertas pendientes", SwingConstants.CENTER), BorderLayout.NORTH);
        tarjeta3.add(lblCantAlertas, BorderLayout.CENTER);

        lblCantCriticos = new JLabel("0", SwingConstants.CENTER);
        lblCantCriticos.setFont(new Font("Arial", Font.BOLD, 24));
        lblCantCriticos.setForeground(Color.RED);
        JPanel tarjeta4 = new JPanel(new BorderLayout());
        tarjeta4.add(new JLabel(" Críticos", SwingConstants.CENTER), BorderLayout.NORTH);
        tarjeta4.add(lblCantCriticos, BorderLayout.CENTER);

        filaTarjetas.add(tarjeta1);
        filaTarjetas.add(tarjeta2);
        filaTarjetas.add(tarjeta3);
        filaTarjetas.add(tarjeta4);
        centro.add(filaTarjetas, BorderLayout.NORTH);

        JPanel tablas = new JPanel(new GridLayout(1, 2, 10, 0));
        tablas.setBackground(VentanaPrincipal.COLOR_FONDO);

        //Tabla Pacientes
        JPanel panelPacientes = new JPanel(new BorderLayout(5, 5));
        panelPacientes.add(new JLabel("Pacientes registrados", SwingConstants.LEFT), BorderLayout.NORTH);
        
        String[] colsPacientes = {"Estado", "Nombre", "Historia", "Última medición", "Monitor"};
        modeloTablaPacientes = new DefaultTableModel(colsPacientes, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
        };
        final JTable tablaPacientes = new JTable(modeloTablaPacientes);
        
        //Doble clic
        tablaPacientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaPacientes.getSelectedRow();
                    if (fila >= 0) {
                        String idPaciente = obtenerIdPacienteFila(fila);
                        String nombre = sistema.getPacientes().get(fila).getNombreCompleto();
                        String[] opciones = {"Ver historial", "Asignar diagnóstico", "Asignar monitor","Asignar médico", "Cancelar"};
                        
                        int opcion = JOptionPane.showOptionDialog(
                            ventana, "¿Qué deseas hacer?", nombre,
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, opciones, opciones[0]
                        );
                        
                        if (opcion == 0) ventana.mostrarHistorialPaciente(idPaciente);
                        if (opcion == 1) ventana.asignarDiagnostico(idPaciente);
                        if (opcion == 2) ventana.asignarMonitor(idPaciente);
                        if (opcion == 3) ventana.asignarMedico(idPaciente);
                    }
                }
            }
        });
        panelPacientes.add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);
        panelPacientes.add(new JLabel("Doble clic para ver historial"), BorderLayout.SOUTH);
        tablas.add(panelPacientes);

        //Tabla Alertas
        JPanel panelAlertas = new JPanel(new BorderLayout(5, 5));
        panelAlertas.add(new JLabel("Alertas recientes", SwingConstants.LEFT), BorderLayout.NORTH);

        String[] colsAlertas = {"Nivel", "Tipo", "Paciente", "Descripción"};
        modeloTablaAlertas = new DefaultTableModel(colsAlertas, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaAlertas = new JTable(modeloTablaAlertas);
        panelAlertas.add(new JScrollPane(tablaAlertas), BorderLayout.CENTER);
        panelAlertas.add(new JLabel("Mostrando las últimas 20 alertas"), BorderLayout.SOUTH);
        tablas.add(panelAlertas);

        centro.add(tablas, BorderLayout.CENTER);
        add(centro, BorderLayout.CENTER);
    }

    public void actualizar() {
        actualizarTarjetas();
        actualizarTablaPacientes();
        actualizarTablaAlertas();
    }

    private void actualizarTarjetas() {
        lblCantPacientes.setText(String.valueOf(sistema.getCantidadPacientes()));
        lblCantMonitores.setText(String.valueOf(sistema.getCantidadMonitores()));

        int totalPendientes = 0;
        int totalCriticos   = 0;
        
        for (int i = 0; i < sistema.getPacientes().size(); i++) {
            Paciente p = sistema.getPacientes().get(i);
            totalPendientes += p.getHistorial().getAlertasPendientes().size();
            
            for (int j = 0; j < p.getHistorial().getAlertasPendientes().size(); j++) {
                AlertaClinica a = p.getHistorial().getAlertasPendientes().get(j);
                if (a.esCritica()) { 
                    totalCriticos++; 
                    break; 
                }
            }
        }
        lblCantAlertas.setText(String.valueOf(totalPendientes));
        lblCantCriticos.setText(String.valueOf(totalCriticos));
    }

    private void actualizarTablaPacientes() {
        modeloTablaPacientes.setRowCount(0);
        
        for (int i = 0; i < sistema.getPacientes().size(); i++) {
            Paciente p = sistema.getPacientes().get(i);
            
            List<AlertaClinica> pendientes = p.getHistorial().getAlertasPendientes();
            String estado = "NORMAL";
            for (int j = 0; j < pendientes.size(); j++) {
                AlertaClinica a = pendientes.get(j);
                if (a.esCritica()) { 
                    estado = "CRÍTICO"; 
                    break; 
                }
                if (a.getSeveridad() >= 2) {
                    estado = "ALERTA";
                }
            }

            //Datos de medición
            Medicion ultima = p.getHistorial().getUltimaMedicion();
            String ultimaMed = "Sin mediciones";
            if (ultima != null) {
                ultimaMed = "FC:" + String.format("%.0f", ultima.getFrecuenciaCardiaca())
                          + " SpO2:" + String.format("%.0f", ultima.getSaturacionO2()) + "%";
            }

            //Localizar el monitor asignado
            String idMonitor = "Sin monitor";
            for (int k = 0; k < sistema.getMonitores().size(); k++) {
                MonitorSignosVitales m = sistema.getMonitores().get(k);
                if (p.getId().equals(m.getIdPacienteAsignado())) {
                    idMonitor = m.getTipoMonitor().replace("Monitor ", "");
                    break;
                }
            }

            Object[] fila = new Object[5];
            fila[0] = estado;
            fila[1] = p.getNombreCompleto();
            fila[2] = p.getNroHistoriaClinica();
            fila[3] = ultimaMed;
            fila[4] = idMonitor;
            
            modeloTablaPacientes.addRow(fila);
        }
    }

    private void actualizarTablaAlertas() {
    modeloTablaAlertas.setRowCount(0);
    
    //Listas las alertas 
    java.util.ArrayList<AlertaClinica> listaTemporalAlertas = new java.util.ArrayList<>();
    java.util.ArrayList<String> listaTemporalNombres = new java.util.ArrayList<>();
    
    //Recorremos todos los pacientes
    for (int i = 0; i < sistema.getPacientes().size(); i++) {
        Paciente p = sistema.getPacientes().get(i);
        
        if (p.getHistorial() != null && p.getHistorial().getAlertasPendientes() != null) {
            for (int j = 0; j < p.getHistorial().getAlertasPendientes().size(); j++) {
                AlertaClinica a = p.getHistorial().getAlertasPendientes().get(j);
                
                listaTemporalAlertas.add(a);
                listaTemporalNombres.add(p.getNombreCompleto());
            }
        }
    }
    
    //Mostramos desde la ultima hacia atrás
    int count = 0;
    for (int k = listaTemporalAlertas.size() - 1; k >= 0; k--) {
        if (count >= 20) {
            break;
        }
        
        AlertaClinica a = listaTemporalAlertas.get(k);
        String nombrePaciente = listaTemporalNombres.get(k);
        
        Object[] fila = new Object[4];
        fila[0] = a.getNivelTexto();
        fila[1] = a.getTipoAlerta();
        fila[2] = nombrePaciente;
        fila[3] = a.getDescripcion();
        
        modeloTablaAlertas.addRow(fila);
        count++;
    }
}
    
    private String obtenerIdPacienteFila(int fila) {
        if (fila < 0 || fila >= sistema.getPacientes().size()) {
            return "";
        }
        return sistema.getPacientes().get(fila).getId();
    }
    
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando PanelDashboard");
        super.finalize();
    }
}