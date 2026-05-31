package vitaltrack.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import vitaltrack.logica.AnalizadorTendencias;
import vitaltrack.logica.DiagnosticoAsistido;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.Paciente;
import vitaltrack.utilidades.FormateadorFecha;

public class PanelHistorial extends JPanel {

    private Paciente paciente;
    private SistemaGestion sistema;
    private VentanaPrincipal ventana;
    private AnalizadorTendencias analizador;

    private DefaultTableModel modeloMediciones;
    private DefaultTableModel modeloAlertas;
    private JLabel lblTendFC;
    private JLabel lblTendSpo2;
    private JLabel lblAdvertencias;
    private JLabel lblDiagnostico;

    public PanelHistorial(Paciente paciente, SistemaGestion sistema, VentanaPrincipal ventana) {
        this.paciente = paciente;
        this.sistema = sistema;
        this.ventana = ventana;
        this.analizador = new AnalizadorTendencias(paciente.getHistorial());
        
        setLayout(new BorderLayout(10, 10));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        //Encabezado
        JPanel encabezado = new JPanel(new BorderLayout(10, 5));
        encabezado.setBackground(VentanaPrincipal.COLOR_FONDO);

        JButton btnVolver = new JButton("← Volver");
        btnVolver.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ventana.mostrarDashboard();
            }
        });

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(VentanaPrincipal.COLOR_FONDO);
        
        JLabel titulo = new JLabel("Historial clínico — " + paciente.getNombreCompleto());
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel info = new JLabel("Historia: " + paciente.getNroHistoriaClinica()
                + "  |  Edad: " + paciente.getEdad() + " años"
                + "  |  Grupo Sanguíneo: " + paciente.getGrupoSanguineo()
                + "  |  Diagnóstico: " + paciente.getDiagnostico());

        panelTextos.add(titulo);
        panelTextos.add(info);

        encabezado.add(btnVolver, BorderLayout.WEST);
        encabezado.add(panelTextos, BorderLayout.CENTER);
        add(encabezado, BorderLayout.NORTH);

        //Parte central
        JPanel centro = new JPanel(new BorderLayout(10, 10));
        centro.setBackground(VentanaPrincipal.COLOR_FONDO);

        //Mediciones
        JPanel panelMediciones = new JPanel(new BorderLayout(5, 5));
        panelMediciones.add(new JLabel("Mediciones registradas", SwingConstants.LEFT), BorderLayout.NORTH);

        String[] colsMediciones = {"Fecha y hora", "FC (bpm)", "SpO2 (%)", "Temp (°C)", "FR (rpm)", "PA", "Estado FC", "Estado SpO2"};
        modeloMediciones = new DefaultTableModel(colsMediciones, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaMediciones = new JTable(modeloMediciones);
        panelMediciones.add(new JScrollPane(tablaMediciones), BorderLayout.CENTER);
        centro.add(panelMediciones, BorderLayout.CENTER);

        //Panel Lateral
        JPanel panelLateral = new JPanel(new GridLayout(6, 1, 5, 5));
        panelLateral.setPreferredSize(new Dimension(250, 0));

        panelLateral.add(new JLabel(" Tendencias:", SwingConstants.LEFT));
        lblTendFC = new JLabel("FC: --");
        lblTendSpo2 = new JLabel("SpO2: --");
        panelLateral.add(lblTendFC);
        panelLateral.add(lblTendSpo2);

        lblAdvertencias = new JLabel("<html>Advertencias:<br>Ninguna</html>");
        lblAdvertencias.setForeground(Color.ORANGE);
        lblDiagnostico = new JLabel("<html>Diagnóstico Asistido:<br>--</html>");
        
        panelLateral.add(lblAdvertencias);
        panelLateral.add(lblDiagnostico);
        centro.add(panelLateral, BorderLayout.EAST);

        add(centro, BorderLayout.CENTER);

        //Alertas
        JPanel panelAlertas = new JPanel(new BorderLayout(5, 5));
        panelAlertas.setPreferredSize(new Dimension(0, 150));
        panelAlertas.add(new JLabel("Alertas de este paciente", SwingConstants.LEFT), BorderLayout.NORTH);

        String[] colsAlertas = {"Nivel", "Tipo", "Descripción", "Fecha y hora", "Atendida"};
        modeloAlertas = new DefaultTableModel(colsAlertas, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
        };
        final JTable tablaAlertas = new JTable(modeloAlertas);
        panelAlertas.add(new JScrollPane(tablaAlertas), BorderLayout.CENTER);

        JButton btnAtender = new JButton("Marcar como atendida");
        btnAtender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int fila = tablaAlertas.getSelectedRow();
                if (fila >= 0 && fila < paciente.getHistorial().getAlertas().size()) {
                    paciente.getHistorial().getAlertas().get(fila).setAtendida(true);
                    cargarDatos(); // Recargar tablas y estado
                }
            }
        });
        panelAlertas.add(btnAtender, BorderLayout.EAST);

        add(panelAlertas, BorderLayout.SOUTH);
    }
    
    //Carga y procesamiento de datos
    public void cargarDatos() {
        cargarMediciones();
        cargarAlertas();
        actualizarTendencias();
    }

    private void cargarMediciones() {
        modeloMediciones.setRowCount(0);
        List<Medicion> lista = paciente.getHistorial().getMediciones();
        
        //Se cargan en orden inverso
        for (int i = lista.size() - 1; i >= 0; i--) {
            Medicion m = lista.get(i);
            
            Object[] fila = new Object[8];
            fila[0] = FormateadorFecha.formatearFechaHora(m.getTimestamp());
            fila[1] = String.format("%.0f", m.getFrecuenciaCardiaca());
            fila[2] = String.format("%.1f", m.getSaturacionO2());
            fila[3] = m.getTemperatura() > 0 ? String.format("%.1f", m.getTemperatura()) : "N/D";
            fila[4] = m.getFrecuenciaRespiratoria() > 0 ? String.format("%.0f", m.getFrecuenciaRespiratoria()) : "N/D";
            fila[5] = m.getPresionArterial();
            fila[6] = DiagnosticoAsistido.evaluarFC(m.getFrecuenciaCardiaca());
            fila[7] = DiagnosticoAsistido.evaluarSpo2(m.getSaturacionO2());

            modeloMediciones.addRow(fila);
        }
    }

    private void cargarAlertas() {
        modeloAlertas.setRowCount(0);
        List<AlertaClinica> alertas = paciente.getHistorial().getAlertas();
        
        for (int i = 0; i < alertas.size(); i++) {
            AlertaClinica a = alertas.get(i);
            
            Object[] fila = new Object[5];
            fila[0] = a.getNivelTexto();
            fila[1] = a.getTipoAlerta();
            fila[2] = a.getDescripcion();
            fila[3] = FormateadorFecha.formatearFechaHora(a.getTimestamp());
            fila[4] = a.isAtendida() ? "Sí ✔" : "No";

            modeloAlertas.addRow(fila);
        }
    }

    private void actualizarTendencias() {
        //Extraer tendencias
        String tendFC = analizador.tendencia("FC");
        String tendSpo2 = analizador.tendencia("SPO2");

        lblTendFC.setText("FC: " + obtenerTextoDireccion(tendFC));
        lblTendSpo2.setText("SpO2: " + obtenerTextoDireccion(tendSpo2));

        //Actualizar área de advertencias
        String adv = analizador.todasLasAdvertencias();
        if (adv.isEmpty()) {
            lblAdvertencias.setText("<html>Advertencias:<br>Sin advertencias activas</html>");
        } else {
            lblAdvertencias.setText("<html>Advertencias:<br>" + adv.replace("\n", "<br>") + "</html>");
        }

        //Evaluar la última medición para el diagnóstico asistido
        Medicion ultima = paciente.getHistorial().getUltimaMedicion();
        if (ultima != null) {
            String diag = DiagnosticoAsistido.evaluar(ultima);
            lblDiagnostico.setText("<html>Diagnóstico Asistido:<br>" + diag + "</html>");
        } else {
            lblDiagnostico.setText("<html>Diagnóstico Asistido:<br>Sin mediciones</html>");
        }
    }

    private String obtenerTextoDireccion(String tendencia) {
        if (tendencia.equals("SUBE")) {
            return "↑ Subiendo";
        } else if (tendencia.equals("BAJA")) {
            return "↓ Bajando";
        } else if (tendencia.equals("ESTABLE")) {
            return "→ Estable";
        } else {
            return "— Sin datos";
        }
    }
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando PanelDashboard");
        super.finalize();
    }
}