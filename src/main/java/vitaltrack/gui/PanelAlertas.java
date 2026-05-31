package vitaltrack.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Paciente;
import vitaltrack.utilidades.FormateadorFecha;

public class PanelAlertas extends JPanel {

    private SistemaGestion sistema;
    private VentanaPrincipal ventana;

    private DefaultTableModel modeloTabla;
    private JTable tabla;
    private JComboBox<String> cmbFiltro;
    private JLabel lblResumen;

    private java.util.List<AlertaClinica> alertasVisibles = new java.util.ArrayList<>();
    private java.util.List<Paciente> pacientesVisibles = new java.util.ArrayList<>();

    public PanelAlertas(SistemaGestion sistema, VentanaPrincipal ventana) {
        this.sistema = sistema;
        this.ventana = ventana;
        
        setLayout(new BorderLayout(5, 5));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        
        construirUI();
        cargarDatos("Todas");
    }

    private void construirUI() {
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(VentanaPrincipal.COLOR_FONDO);

        //Textos del titulo
        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(VentanaPrincipal.COLOR_FONDO);
        
        JLabel titulo = new JLabel("Panel de Alertas");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        lblResumen = new JLabel("Cargando...");
        
        panelTextos.add(titulo);
        panelTextos.add(lblResumen);

        //Filtros
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filtroPanel.setBackground(VentanaPrincipal.COLOR_FONDO);

        JLabel lblFiltro = new JLabel("Filtrar por nivel: ");
        
        String[] niveles = {"Todas", "CRITICA", "MODERADA", "LEVE"};
        cmbFiltro = new JComboBox<>(niveles);
        cmbFiltro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatos((String) cmbFiltro.getSelectedItem());
            }
        });

        JButton btnRefresh = new JButton("Actualizar");
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarDatos((String) cmbFiltro.getSelectedItem());
            }
        });

        filtroPanel.add(lblFiltro);
        filtroPanel.add(cmbFiltro);
        filtroPanel.add(btnRefresh);

        panelNorte.add(panelTextos, BorderLayout.WEST);
        panelNorte.add(filtroPanel, BorderLayout.EAST);
        add(panelNorte, BorderLayout.NORTH);

        //Tabla
        String[] cols = {"Nivel", "Tipo de alerta", "Paciente", "Descripción", "Fecha y hora", "Atendida"};
        
        //Tabla estándar
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { 
                return false; 
            }
        };
        
        tabla = new JTable(modeloTabla);
        
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        //Acciones
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setBackground(VentanaPrincipal.COLOR_PANEL);

        JButton btnAtender = new JButton("Marcar como atendida");
        btnAtender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marcarAtendida();
            }
        });

        JButton btnAtenderTodas = new JButton("Atender todas las visibles");
        btnAtenderTodas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                marcarTodasAtendidas();
            }
        });

        JButton btnVerPaciente = new JButton("Ver historial del paciente");
        btnVerPaciente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verHistorialPaciente();
            }
        });

        barra.add(btnAtender);
        barra.add(btnAtenderTodas);
        barra.add(btnVerPaciente);

        JLabel hint = new JLabel(" Selecciona una fila para usar las acciones");
        barra.add(hint);

        add(barra, BorderLayout.SOUTH);
    }

    public void cargarDatos(String filtro) {
        modeloTabla.setRowCount(0);
        alertasVisibles.clear();
        pacientesVisibles.clear();

        int totalCrit = 0;
        int totalMod = 0;
        int totalLeve = 0;
        int totalPend = 0;

        //Recorrer las listas de pacientes
        for (int i = 0; i < sistema.getPacientes().size(); i++) {
            Paciente p = sistema.getPacientes().get(i);
            
            for (int j = 0; j < p.getHistorial().getAlertas().size(); j++) {
                AlertaClinica a = p.getHistorial().getAlertas().get(j);

                //Contar totales según gravedad
                if (a.getNivelTexto().equals("CRITICA")) {
                    totalCrit++;
                } else if (a.getNivelTexto().equals("MODERADA")) {
                    totalMod++;
                } else {
                    totalLeve++;
                }
                
                if (!a.isAtendida()) {
                    totalPend++;
                }

                //Evaluar si coincide
                boolean mostrar = filtro.equals("Todas") || a.getNivelTexto().equals(filtro);
                
                if (mostrar) {
                    alertasVisibles.add(a);
                    pacientesVisibles.add(p);

                    //Insertar fila en la tabla
                    Object[] fila = new Object[6];
                    fila[0] = a.getNivelTexto();
                    fila[1] = a.getTipoAlerta();
                    fila[2] = p.getNombreCompleto();
                    fila[3] = a.getDescripcion();
                    fila[4] = FormateadorFecha.formatearFechaHora(a.getTimestamp());
                    fila[5] = a.isAtendida() ? "Sí ✔" : "No";
                    
                    modeloTabla.addRow(fila);
                }
            }
        }

        //Actualizar etiqueta del resumen de texto superior
        lblResumen.setText("Total: " + (totalCrit + totalMod + totalLeve)
                + "   |   Críticas: " + totalCrit
                + "   |   Moderadas: " + totalMod
                + "   |   Leves: " + totalLeve
                + "   |   Pendientes: " + totalPend);
    }

    private void marcarAtendida() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná una alerta primero.", "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        alertasVisibles.get(fila).setAtendida(true);
        cargarDatos((String) cmbFiltro.getSelectedItem());
        ventana.setEstado("Alerta marcada como atendida");
    }

    private void marcarTodasAtendidas() {
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Marcar todas las alertas visibles como atendidas?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
                
        if (conf == JOptionPane.YES_OPTION) {
            for (int i = 0; i < alertasVisibles.size(); i++) {
                alertasVisibles.get(i).setAtendida(true);
            }
            cargarDatos((String) cmbFiltro.getSelectedItem());
            ventana.setEstado("Todas las alertas marcadas como atendidas");
        }
    }

    private void verHistorialPaciente() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccioná una alerta primero.", "Sin selección",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Paciente p = pacientesVisibles.get(fila);
        ventana.mostrarHistorialPaciente(p.getId());
    }
}