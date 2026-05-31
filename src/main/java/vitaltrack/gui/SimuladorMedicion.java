package vitaltrack.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.Paciente;
import vitaltrack.modelo.Medico;
import vitaltrack.monitor.MonitorAvanzado;
import vitaltrack.monitor.MonitorBasico;
import vitaltrack.monitor.MonitorSignosVitales;
import vitaltrack.monitor.MonitorUCI;
import vitaltrack.utilidades.GeneradorId;
import vitaltrack.utilidades.ValidadorDatos;

public class SimuladorMedicion extends JPanel {
    
    private SistemaGestion sistema;
    private VentanaPrincipal ventana;

    private JComboBox<String> cmbPaciente;
    private JTextField txtFC, txtSpo2, txtTemp, txtFR, txtPAS, txtPAD;
    private JTextArea txtResultado;
    private JLabel lblMonitorAsignado;

    public SimuladorMedicion(SistemaGestion sistema, VentanaPrincipal ventana) {
        this.sistema = sistema;
        this.ventana = ventana;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        
        construirUI();
    }

    private void construirUI() {
        //Encabezado
        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setBackground(VentanaPrincipal.COLOR_FONDO);
        
        JLabel titulo = new JLabel("Simulador de Mediciones");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel subtitulo = new JLabel("Ingresá valores manualmente o generá una medición automática con el monitor del paciente");
        
        encabezado.add(titulo);
        encabezado.add(subtitulo);
        add(encabezado, BorderLayout.NORTH);

        //Principal
        JPanel centro = new JPanel(new GridLayout(1, 2, 15, 0));
        centro.setBackground(VentanaPrincipal.COLOR_FONDO);

        //Formulario de ingreso y controles
        JPanel panelFormulario = new JPanel(new BorderLayout(10, 10));
        
        //Selector de paciente
        JPanel panelFilaSuperior = new JPanel(new GridLayout(3, 1, 5, 5));
        panelFilaSuperior.add(new JLabel("1. Seleccioná un paciente:"));
        
        cmbPaciente = new JComboBox<>();
        cmbPaciente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarMonitorInfo();
            }
        });
        panelFilaSuperior.add(cmbPaciente);
        
        lblMonitorAsignado = new JLabel("Monitor: —");
        panelFilaSuperior.add(lblMonitorAsignado);
        panelFormulario.add(panelFilaSuperior, BorderLayout.NORTH);

        JPanel panelCampos = new JPanel(new GridLayout(6, 2, 5, 5));
        txtFC = new JTextField();
        txtSpo2 = new JTextField();
        txtTemp = new JTextField();
        txtFR = new JTextField();
        txtPAS = new JTextField();
        txtPAD = new JTextField();

        panelCampos.add(new JLabel("FC (bpm):")); panelCampos.add(txtFC);
        panelCampos.add(new JLabel("SpO2 (%):")); panelCampos.add(txtSpo2);
        panelCampos.add(new JLabel("Temperatura (°C):")); panelCampos.add(txtTemp);
        panelCampos.add(new JLabel("Frec. resp. (rpm):")); panelCampos.add(txtFR);
        panelCampos.add(new JLabel("PA sistólica (mmHg):")); panelCampos.add(txtPAS);
        panelCampos.add(new JLabel("PA diastólica (mmHg):")); panelCampos.add(txtPAD);
        panelFormulario.add(panelCampos, BorderLayout.CENTER);

        //Botones de acciones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JButton btnGenerar = new JButton("Generar automático");
        JButton btnManual = new JButton("Registrar manual");
        JButton btnLimpiar = new JButton("Limpiar campos");

        btnGenerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarAutomatico();
            }
        });
        btnManual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarManual();
            }
        });
        btnLimpiar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });

        panelBotones.add(btnGenerar);
        panelBotones.add(btnManual);
        panelBotones.add(btnLimpiar);
        panelFormulario.add(panelBotones, BorderLayout.SOUTH);
        
        centro.add(panelFormulario);

        //Resultados
        JPanel panelResultado = new JPanel(new BorderLayout(5, 5));
        panelResultado.add(new JLabel("Resultado de la medición:"), BorderLayout.NORTH);
        
        txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        txtResultado.setLineWrap(true);
        txtResultado.setWrapStyleWord(true);
        txtResultado.setText("Aquí aparecerá el reporte clínico después de operar.");
        
        panelResultado.add(new JScrollPane(txtResultado), BorderLayout.CENTER);
        centro.add(panelResultado);

        add(centro, BorderLayout.CENTER);
        
        //Carga inicial de datos de pacientes
        cargarPacientes();
    }

    //Simulacion y procesamiento
    private void generarAutomatico() {
        Paciente paciente = getPacienteSeleccionado();
        if (paciente == null) return;

        MonitorSignosVitales monitor = getMonitorDePaciente(paciente.getId());
        if (monitor == null) {
            mostrarError("Este paciente no tiene un monitor asignado.\nAsignale uno desde el panel de Pacientes.");
            return;
        }

        Medicion medicion = monitor.medir();
        paciente.agregarMedicion(medicion);

        AlertaClinica alerta = evaluarMonitor(monitor, medicion, paciente.getId());
        if (alerta != null) {
            paciente.getHistorial().agregarAlerta(alerta);
        }

        mostrarResultado(medicion, alerta, monitor.getTipoMonitor(), true);
        rellenarCampos(medicion);
        ventana.setEstado("Medición automática registrada para " + paciente.getNombreCompleto());
    }

    private void registrarManual() {
        Paciente paciente = getPacienteSeleccionado();
        if (paciente == null) return;

        if (!ValidadorDatos.esDecimalEnRango(txtFC.getText(), 0, 300)) {
            mostrarError("FC inválida. Ingresá un valor entre 0 y 300."); return;
        }
        if (!ValidadorDatos.esDecimalEnRango(txtSpo2.getText(), 0, 100)) {
            mostrarError("SpO2 inválida. Ingresá un valor entre 0 y 100."); return;
        }

        double fc = Double.parseDouble(txtFC.getText().trim());
        double spo2 = Double.parseDouble(txtSpo2.getText().trim());
        double temp = parsearOpcional(txtTemp.getText());
        double fr = parsearOpcional(txtFR.getText());
        String pas = txtPAS.getText().trim().isEmpty() ? "N/D" : txtPAS.getText().trim();
        String pad = txtPAD.getText().trim().isEmpty() ? "N/D" : txtPAD.getText().trim();

        String idMed = GeneradorId.generar("MED");
        Medicion medicion = new Medicion(idMed, fc, spo2, temp, fr, pas, pad);
        paciente.agregarMedicion(medicion);

        MonitorSignosVitales monitor = getMonitorDePaciente(paciente.getId());
        AlertaClinica alerta = null;
        if (monitor != null) {
            alerta = evaluarMonitor(monitor, medicion, paciente.getId());
            if (alerta != null) {
                paciente.getHistorial().agregarAlerta(alerta);
            }
        }

        mostrarResultado(medicion, alerta, monitor != null ? monitor.getTipoMonitor() : "Ingreso manual", false);
        ventana.setEstado("Medición manual registrada para " + paciente.getNombreCompleto());
    }

    private AlertaClinica evaluarMonitor(MonitorSignosVitales monitor, Medicion medicion, String idPaciente) {
        if (monitor instanceof MonitorUCI) {
            MonitorUCI uci = (MonitorUCI) monitor;
            AlertaClinica a = uci.evaluarYGenerar(medicion, idPaciente);
            return a != null ? a : uci.evaluarParametrosUCI(idPaciente);
        } else if (monitor instanceof MonitorAvanzado) {
            return ((MonitorAvanzado) monitor).evaluarYGenerar(medicion, idPaciente);
        } else if (monitor instanceof MonitorBasico) {
            return ((MonitorBasico) monitor).evaluarYGenerar(medicion, idPaciente);
        }
        return null;
    }

    private void mostrarResultado(Medicion m, AlertaClinica alerta, String tipoMonitor, boolean automatico) {
        StringBuilder sb = new StringBuilder();
        sb.append("===================================\n");
        sb.append("      ESCANEO MÉDICO    \n"); // 🌟 Título personalizado
        sb.append("===================================\n\n");
        sb.append("Origen: ").append(automatico ? "Automático (Sensor)" : "Manual").append("\n");
        sb.append("Monitor: ").append(tipoMonitor).append("\n");
        sb.append("ID Medición: ").append(m.getIdMedicion()).append("\n\n");
        
        sb.append("SIGNOS VITALES:\n");
        sb.append("  FC: ").append(String.format("%.0f bpm\n", m.getFrecuenciaCardiaca()));
        sb.append("  SpO2: ").append(String.format("%.1f %%\n", m.getSaturacionO2()));
        
        if (m.getTemperatura() > 0) sb.append("  Temp: ").append(String.format("%.1f °C\n", m.getTemperatura()));
        if (m.getFrecuenciaRespiratoria() > 0) sb.append("  FR: ").append(String.format("%.0f rpm\n", m.getFrecuenciaRespiratoria()));
        if (!"N/D".equals(m.getPresionArterialSist())) sb.append("  PA: ").append(m.getPresionArterial()).append("\n");

        sb.append("\n");

        String diagnosticoRobot = vitaltrack.logica.DiagnosticoAsistido.evaluar(m);
        
        sb.append("ANÁLISIS DE SINTOMATOLOGÍA:\n");
        if (!diagnosticoRobot.isEmpty()) {
            sb.append("  ").append(diagnosticoRobot).append("\n\n");
        } else {
            sb.append("  No se detectan patrones de riesgo clínico inmediato.\n\n");
        }

        if (alerta != null) {
            sb.append("ALERTA DETECTADA:\n");
            sb.append("  Nivel: ").append(alerta.getNivelTexto()).append("\n");
            sb.append("  Tipo: ").append(alerta.getTipoAlerta()).append("\n");
            sb.append("  Detalle: ").append(alerta.getDescripcion()).append("\n");
        } else {
            sb.append("Valores estables bajo rangos normales.\n");
        }
        
        txtResultado.setText(sb.toString());
    }

    private void rellenarCampos(Medicion m) {
        txtFC.setText(String.format("%.0f", m.getFrecuenciaCardiaca()));
        txtSpo2.setText(String.format("%.1f", m.getSaturacionO2()));
        txtTemp.setText(m.getTemperatura() > 0 ? String.format("%.1f", m.getTemperatura()) : "");
        txtFR.setText(m.getFrecuenciaRespiratoria() > 0 ? String.format("%.0f", m.getFrecuenciaRespiratoria()) : "");
        txtPAS.setText(!"N/D".equals(m.getPresionArterialSist()) ? m.getPresionArterialSist() : "");
        txtPAD.setText(!"N/D".equals(m.getPresionArterialDias()) ? m.getPresionArterialDias() : "");
    }

    private void limpiarCampos() {
        txtFC.setText(""); txtSpo2.setText(""); txtTemp.setText("");
        txtFR.setText("");  txtPAS.setText("");  txtPAD.setText("");
        txtResultado.setText("Campos listos para una nueva medición.");
    }

    private void mostrarError(String msg) {
        txtResultado.setText("ERROR CLÍNICO\n\n" + msg);
    }

    public void cargarPacientes() {
        cmbPaciente.removeAllItems();
        if (sistema.getPacientes().isEmpty()) {
            cmbPaciente.addItem("— Sin pacientes registrados —");
            return;
        }
        for (int i = 0; i < sistema.getPacientes().size(); i++) {
            Paciente p = sistema.getPacientes().get(i);
            cmbPaciente.addItem(p.getId() + " — " + p.getNombreCompleto());
        }
        actualizarMonitorInfo();
    }

    private void actualizarMonitorInfo() {
        Paciente p = getPacienteSeleccionado();
        if (p == null) { lblMonitorAsignado.setText("Monitor: —"); return; }
        MonitorSignosVitales m = getMonitorDePaciente(p.getId());
        if (m != null) {
            lblMonitorAsignado.setText("Monitor asignado: " + m.getTipoMonitor() + " (" + m.getIdMonitor() + ")");
        } else {
            lblMonitorAsignado.setText("Sin monitor clínico asignado");
        }
    }

    private Paciente getPacienteSeleccionado() {
        if (cmbPaciente.getSelectedItem() == null) return null;
        String sel = cmbPaciente.getSelectedItem().toString();
        if (sel.startsWith("—")) {
            JOptionPane.showMessageDialog(this, "No hay pacientes válidos seleccionados.", "Atención", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        String id = sel.split(" — ")[0];
        return sistema.buscarPaciente(id);
    }

    private MonitorSignosVitales getMonitorDePaciente(String idPaciente) {
        for (int i = 0; i < sistema.getMonitores().size(); i++) {
            MonitorSignosVitales m = sistema.getMonitores().get(i);
            if (idPaciente.equals(m.getIdPacienteAsignado())) return m;
        }
        return null;
    }

    private double parsearOpcional(String texto) {
        try { return Double.parseDouble(texto.trim()); } 
        catch (Exception e) { return 0.0; }
    }
    
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando PanelDashboard");
        super.finalize();
    }
}