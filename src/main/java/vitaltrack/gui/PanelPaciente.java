package vitaltrack.gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.Paciente;
import vitaltrack.monitor.MonitorSignosVitales;

public class PanelPaciente extends JPanel {

    private SistemaGestion sistema;
    private VentanaPrincipal ventana;
    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public PanelPaciente(SistemaGestion sistema, VentanaPrincipal ventana) {
        this.sistema = sistema;
        this.ventana = ventana;
        
        setLayout(new BorderLayout(10, 10));
        setBackground(VentanaPrincipal.COLOR_FONDO);
        
        construirUI();
        cargarDatos();
    }

    private void construirUI() {
        //Encabezado
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel panelTextos = new JPanel(new GridLayout(2, 1));
        panelTextos.setBackground(VentanaPrincipal.COLOR_FONDO);
        
        JLabel titulo = new JLabel("Pacientes registrados");
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel subtitulo = new JLabel("Seleccioná un paciente para ver opciones");
        
        panelTextos.add(titulo);
        panelTextos.add(subtitulo);

        JButton btnNuevo = new JButton("Nuevo paciente");
        btnNuevo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame ventanaPadre = (JFrame) SwingUtilities.getWindowAncestor(PanelPaciente.this);
                DialogoRegistro dialogo = new DialogoRegistro(ventanaPadre, sistema);
                dialogo.setVisible(true);
                cargarDatos();
            }
        });

        encabezado.add(panelTextos, BorderLayout.WEST);
        encabezado.add(btnNuevo, BorderLayout.EAST);
        add(encabezado, BorderLayout.NORTH);

        //Central
        String[] columnas = {"ID", "Nombre completo", "Num Cedula", "Edad", "Grupo", "Historia", "Monitor asignado", "Diagnóstico"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override 
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        add(scroll, BorderLayout.CENTER);

        //Acciones
        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.setBackground(VentanaPrincipal.COLOR_FONDO);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton btnHistorial = new JButton("Ver historial");
        JButton btnDiagnostico = new JButton("Asignar diagnóstico");
        JButton btnMonitor = new JButton("Asignar monitor");
        JButton btnEliminar = new JButton("Eliminar");

        btnHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = getIdSeleccionado();
                if (id != null) {
                    ventana.mostrarHistorialPaciente(id);
                }
            }
        });

        btnDiagnostico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = getIdSeleccionado();
                if (id != null) { 
                    ventana.asignarDiagnostico(id); 
                    cargarDatos(); 
                }
            }
        });

        btnMonitor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = getIdSeleccionado();
                if (id != null) { 
                    ventana.asignarMonitor(id); 
                    cargarDatos(); 
                }
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarSeleccionado();
            }
        });

        acciones.add(btnHistorial);
        acciones.add(btnDiagnostico);
        acciones.add(btnMonitor);
        acciones.add(btnEliminar);
        
        panelInferior.add(acciones, BorderLayout.CENTER);

        JLabel hint = new JLabel(" Seleccioná una fila y usá los botones, o haz doble clic para ver historial");
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        panelInferior.add(hint, BorderLayout.SOUTH);

        add(panelInferior, BorderLayout.SOUTH);

        // Doble clic
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String id = getIdSeleccionado();
                    if (id != null) {
                        String nombre = sistema.buscarPaciente(id).getNombreCompleto();
                        String[] opciones = {"Ver historial", "Asignar diagnóstico",
                                             "Asignar monitor", "Asignar médico", "Cancelar"};

                        int opcion = JOptionPane.showOptionDialog(
                            ventana, "¿Qué deseas hacer?", nombre,
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, opciones, opciones[0]
                        );

                        if (opcion == 0) ventana.mostrarHistorialPaciente(id);
                        if (opcion == 1) ventana.asignarDiagnostico(id);
                        if (opcion == 2) ventana.asignarMonitor(id);
                        if (opcion == 3) ventana.asignarMedico(id);
                    }
                }
            }
        });
    }

    public void cargarDatos() {
        modeloTabla.setRowCount(0);
        
        for (int i = 0; i < sistema.getPacientes().size(); i++) {
            Paciente p = sistema.getPacientes().get(i);
            
            // Buscar si tiene un monitor asignado
            String monitor = "Sin monitor";
            for (int j = 0; j < sistema.getMonitores().size(); j++) {
                MonitorSignosVitales m = sistema.getMonitores().get(j);
                if (p.getId().equals(m.getIdPacienteAsignado())) {
                    monitor = m.getTipoMonitor().replace("Monitor ", "") + " (" + m.getIdMonitor() + ")";
                    break;
                }
            }

            Object[] fila = new Object[8];
            fila[0] = p.getId();
            fila[1] = p.getNombreCompleto();
            fila[2] = p.getCedNum();
            fila[3] = p.getEdad() + " años";
            fila[4] = p.getGrupoSanguineo();
            fila[5] = p.getNroHistoriaClinica();
            fila[6] = monitor;
            fila[7] = p.getDiagnostico();
            
            modeloTabla.addRow(fila);
        }
    }

    private String getIdSeleccionado() {
    int filaVisual = tabla.getSelectedRow();
    if (filaVisual < 0) {
        JOptionPane.showMessageDialog(this,
            "Seleccioná un paciente de la tabla primero.",
            "Sin selección", JOptionPane.WARNING_MESSAGE);
        return null;
    }
    
    int filaModelo = tabla.convertRowIndexToModel(filaVisual);
    
    return modeloTabla.getValueAt(filaModelo, 0).toString();
}

    private void eliminarSeleccionado() {
        String id = getIdSeleccionado();
        if (id == null) {
            return;
        }

        Paciente p = sistema.buscarPaciente(id);
        int conf = JOptionPane.showConfirmDialog(this,
            "¿Eliminar a " + p.getNombreCompleto() + " del sistema?\nSe perderá su historial clínico.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (conf == JOptionPane.YES_OPTION) {
            sistema.eliminarPaciente(id);
            cargarDatos();
            ventana.setEstado("Paciente eliminado: " + p.getNombreCompleto());
        }
    }
}