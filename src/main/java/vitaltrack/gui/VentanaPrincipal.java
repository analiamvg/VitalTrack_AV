package vitaltrack.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.Paciente;
import vitaltrack.modelo.Medico;
import vitaltrack.monitor.MonitorSignosVitales;

public class VentanaPrincipal extends JFrame {
    
    public static final Color COLOR_FONDO = new Color(173, 216, 230);
    public static final Color COLOR_PANEL = new Color(173, 216, 230);
    public static final Color COLOR_ACENTO = new Color(6, 182, 212);
    public static final Color COLOR_ACENTO2 = new Color(16, 185, 129);
    public static final Color COLOR_ALERTA_ROJO = new Color(239, 68, 68);
    public static final Color COLOR_ALERTA_AMBA = new Color(245, 158, 11);
    public static final Color COLOR_TEXTO = new Color(248, 250, 252);
    public static final Color COLOR_TEXTO_SUAVE = new Color(148, 163, 184);
    public static final Color COLOR_BORDE = new Color(51, 65, 85);
    
    public static final Font FUENTE_TITULO = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FUENTE_SUBTIT = new Font("SansSerif", Font.BOLD, 14);
    public static final Font FUENTE_NORMAL = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FUENTE_PEQUEÑA = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FUENTE_MONO = new Font("Monospaced", Font.PLAIN, 12);
    
    private SistemaGestion sistema;
    private JPanel panelContenido;
    private JLabel lblEstadoBar;
    private PanelDashboard panelDashboard;
 
    public VentanaPrincipal(SistemaGestion sistema) {
        this.sistema = sistema;
        inicializarVentana();
        construirUI();
        mostrarDashboard();
    }
    
    private void inicializarVentana() {
        setTitle("VitalTrack — Sistema de Gestión de Pacientes");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
 
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSistema();
            }
        });
    }
    
    private void construirUI() {
        setLayout(new BorderLayout());

        JPanel header = crearHeader();
        add(header, BorderLayout.NORTH);
 
        JPanel menuLateral = crearMenuLateral();
        add(menuLateral, BorderLayout.WEST);
 
        panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(panelContenido, BorderLayout.CENTER);
 
        JPanel statusBar = crearStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }
 
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PANEL);
        header.setPreferredSize(new Dimension(0, 55));
 
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        izq.setBackground(COLOR_PANEL);
        JLabel lblLogo = new JLabel("VitalTrack");
        lblLogo.setFont(FUENTE_TITULO);
        lblLogo.setForeground(COLOR_TEXTO);
        izq.add(lblLogo);
 
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        der.setBackground(COLOR_PANEL);
        
        JLabel lblInfo = new JLabel("Pacientes: " + sistema.getCantidadPacientes() + " | Monitores: " + sistema.getCantidadMonitores());
        lblInfo.setForeground(COLOR_TEXTO_SUAVE);
        
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarDatos();
            }
        });
 
        der.add(lblInfo);
        der.add(btnGuardar);
 
        header.add(izq, BorderLayout.WEST);
        header.add(der, BorderLayout.EAST);
        return header;
    }
 
    private JPanel crearMenuLateral() {
        JPanel menu = new JPanel();
        menu.setLayout(new GridLayout(7, 1, 5, 5)); // Ajustado a 7 filas
        menu.setBackground(COLOR_PANEL);
        menu.setPreferredSize(new Dimension(180, 0));
 
        JButton btnDash = new JButton("Resumen general");
        JButton btnPacientes = new JButton("Pacientes");
        JButton btnHistorial = new JButton("Historial");
        JButton btnMonitores = new JButton("Monitores");
        JButton btnAlertas = new JButton("Alertas");
        JButton btnNuevo = new JButton("Nuevo Paciente");
        JButton btnCiclo = new JButton("Ciclo Monitoreo");
 
        btnDash.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { mostrarDashboard(); }
        });
        btnPacientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { mostrarPacientes(); }
        });
        btnHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { mostrarHistorial(); }
        });
        btnMonitores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { mostrarMonitores(); }
        });
        btnAlertas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { mostrarAlertas(); }
        });
        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { abrirRegistroPaciente(); }
        });
        btnCiclo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { ejecutarCiclo(); }
        });
 
        menu.add(btnDash);
        menu.add(btnPacientes);
        menu.add(btnHistorial);
        menu.add(btnMonitores);
        menu.add(btnAlertas);
        menu.add(btnNuevo);
        menu.add(btnCiclo);
 
        return menu;
    }
 
    private JPanel crearStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(COLOR_FONDO);
        bar.setPreferredSize(new Dimension(0, 25));
 
        lblEstadoBar = new JLabel(" Sistema listo ");
        lblEstadoBar.setFont(FUENTE_PEQUEÑA);
        lblEstadoBar.setForeground(COLOR_ACENTO2);
        
        JLabel lblVersion = new JLabel("VitalTrack 2026 ");
        lblVersion.setFont(FUENTE_PEQUEÑA);
        lblVersion.setForeground(COLOR_TEXTO_SUAVE);
 
        bar.add(lblEstadoBar, BorderLayout.WEST);
        bar.add(lblVersion, BorderLayout.EAST);
        return bar;
    }
 
    private void mostrarPanel(JPanel panel) {
        panelContenido.removeAll();
        panelContenido.add(panel, BorderLayout.CENTER);
        panelContenido.revalidate();
        panelContenido.repaint();
    }
 
    public void mostrarDashboard() {
        if (panelDashboard == null) {
            panelDashboard = new PanelDashboard(sistema, this);
        }
        panelDashboard.actualizar();
        mostrarPanel(panelDashboard);
        setEstado("Dashboard principal cargado");
    }
 
    private void mostrarPacientes() {
        mostrarPanel(new PanelPaciente(sistema, this));
        setEstado("Visualizando módulo de pacientes");
    }
 
    private void mostrarHistorial() {
        JPanel placeholder = new JPanel(new FlowLayout());
        placeholder.setBackground(COLOR_FONDO);
        placeholder.add(new JLabel("Seleccioná un paciente de la lista para gestionar su historial"));
        mostrarPanel(placeholder);
    }
 
    private void mostrarMonitores() {
        mostrarPanel(new SimuladorMedicion(sistema, this));
        setEstado("Simulador de monitores activo");
    }
 
    private void mostrarAlertas() {
        mostrarPanel(new PanelAlertas(sistema, this));
        setEstado("Panel de trazas de alertas clínicas");
    }
 
    private void abrirRegistroPaciente() {
        DialogoRegistro dialogo = new DialogoRegistro(this, sistema);
        dialogo.setVisible(true);
        if (panelDashboard != null) {
            panelDashboard.actualizar();
        }
        setEstado("Ventana de registro desplegada");
    }
 
    private void ejecutarCiclo() {
        sistema.ejecutarCicloMonitoreo();
        if (panelDashboard != null) {
            panelDashboard.actualizar();
        }
        setEstado("Ciclo de telemetría médica ejecutado de manera global");
    }
 
    private void guardarDatos() {
        sistema.guardarDatos();
        setEstado("Datos clínicos serializados");
        JOptionPane.showMessageDialog(this, "Datos guardados en la carpeta /data", "Guardado exitoso", JOptionPane.INFORMATION_MESSAGE);
    }
 
    private void cerrarSistema() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea guardar los datos antes de cerrar?", "Salir", JOptionPane.YES_NO_CANCEL_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            sistema.guardarDatos();
            System.exit(0);
        } else if (opcion == JOptionPane.NO_OPTION) {
            System.exit(0);
        }
    }
 
    public void setEstado(String mensaje) {
        lblEstadoBar.setText(" " + mensaje);
    }
 
    public SistemaGestion getSistema() { 
        return sistema; 
    }
 
    public void asignarDiagnostico(String idPaciente) {
        Paciente paciente = sistema.buscarPaciente(idPaciente);
        if (paciente == null) return;
 
        String diagnostico = JOptionPane.showInputDialog(this, "Modificar diagnóstico:", paciente.getDiagnostico());
        if (diagnostico != null && !diagnostico.trim().isEmpty()) {
            paciente.setDiagnostico(diagnostico.trim());
            setEstado("Diagnóstico modificado para: " + paciente.getNombreCompleto());
            if (panelDashboard != null) panelDashboard.actualizar();
        }
    } 
    
    public void asignarMonitor(String idPaciente) {
        if (sistema.getMonitores().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No existen hardware o monitores en el almacén");
            return;
        }
 
        String[] opciones = new String[sistema.getMonitores().size()];
        for (int i = 0; i < sistema.getMonitores().size(); i++) {
            MonitorSignosVitales m = sistema.getMonitores().get(i);
            opciones[i] = m.getIdMonitor() + " — " + m.getTipoMonitor();
        }
 
        String seleccion = (String) JOptionPane.showInputDialog(this, "Seleccioná un dispositivo:", "Almacén de Monitores", JOptionPane.PLAIN_MESSAGE, null, opciones, opciones[0]);
        if (seleccion != null) {
            String idMonitor = seleccion.split(" — ")[0];
            boolean exito = sistema.asignarMonitor(idMonitor, idPaciente);
            setEstado(exito ? "Monitor enlazado al paciente" : "Conflicto en la asignación.");
            if (panelDashboard != null) panelDashboard.actualizar();
        }
    }    
     
    public void mostrarHistorialPaciente(String idPaciente) {
        Paciente paciente = sistema.buscarPaciente(idPaciente);
        if (paciente == null) return;
        
        mostrarPanel(new PanelHistorial(paciente, sistema, this));
        setEstado("Mostrando historial clínico de: " + paciente.getNombreCompleto());
    }
    
    public void asignarMedico(String idPaciente) {
        Paciente paciente = sistema.buscarPaciente(idPaciente);
        if (paciente == null) return;

        String[] opcionesMedicos = {
            "M-01 — Dr. Stephen Strange (Medico Cirujano)",
            "M-02 — Dra. Harleen Quinzel (Medicina Psiquiatra)",
            "M-03 — Dr. Gregory House (Medico Interno)"
        };

        String seleccion = (String) JOptionPane.showInputDialog(
            this,
            "Seleccioná un médico para " + paciente.getNombreCompleto() + ":",
            "Staff Médico",
            JOptionPane.PLAIN_MESSAGE,
            null, 
            opcionesMedicos, 
            opcionesMedicos[0]
        );

        if (seleccion != null) {
            String idMedico = seleccion.split(" — ")[0];

            Medico medico = sistema.buscarMedico(idMedico);

            if (medico == null) {
                if (idMedico.equals("M-01")) {
                    medico = new Medico("M-01", "Stephen", "Strange", java.time.LocalDate.of(1998, 5, 20), "44112233", "MAT-8845", "Medico Cirujano");
                } else if (idMedico.equals("M-02")) {
                    medico = new Medico("M-02", "Harleen", "Quinzel", java.time.LocalDate.of(1995, 8, 12), "38445566", "MAT-3312", "Medica Psiquiatra");
                } else {
                    medico = new Medico("M-03", "Gregory", "House", java.time.LocalDate.of(1990, 3, 15), "31223344", "MAT-4412", "Medico Interno");
                }
                sistema.agregarMedico(medico);
            }

            medico.asignarPaciente(paciente);
            setEstado("Médico asignado: Dr/a. " + medico.getNombreCompleto() + " ➔ " + paciente.getNombreCompleto());

            if (panelDashboard != null) {
                panelDashboard.actualizar();
            }
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando PanelDashboard");
        super.finalize();
    }
}