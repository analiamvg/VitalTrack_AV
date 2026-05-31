package vitaltrack.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.modelo.Paciente;
import vitaltrack.utilidades.GeneradorId;

public class DialogoRegistro extends JDialog {

    private SistemaGestion sistema;

    private JTextField txtNombre;
    private JTextField txtApellido;
    private JTextField txtDni;
    private JTextField txtFechaNac;
    private JTextField txtNroHistoria;
    private JComboBox<String> cmbGrupoSanguineo;
    private JLabel lblError;

    public DialogoRegistro(JFrame padre, SistemaGestion sistema) {
        super(padre, "Registrar nuevo paciente", true);
        this.sistema = sistema;
        
        setSize(440, 480);
        setLocationRelativeTo(padre);
        setResizable(false);
        
        //Configurar la ventana principal
        setLayout(new BorderLayout(10, 10));

        //Titulos
        JPanel header = new JPanel();
        header.setBackground(VentanaPrincipal.COLOR_FONDO);
        JLabel titulo = new JLabel("Nuevo paciente");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        header.add(titulo);
        add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBackground(VentanaPrincipal.COLOR_PANEL);
        
        //Inicializar
        txtNombre = new JTextField();
        txtApellido = new JTextField();
        txtDni = new JTextField();
        txtFechaNac = new JTextField();
        txtFechaNac.setToolTipText("Formato: dd/MM/yyyy");
        txtNroHistoria = new JTextField();
        
        String[] grupos = {"A+","A-","B+","B-","AB+","AB-","O+","O-"};
        cmbGrupoSanguineo = new JComboBox<>(grupos);

        //Agregar los elementos
        form.add(new JLabel("Nombre *"));
        form.add(txtNombre);
        
        form.add(new JLabel("Apellido *"));
        form.add(txtApellido);
        
        form.add(new JLabel("Num Cedula *"));
        form.add(txtDni);
        
        form.add(new JLabel("Fecha nac. * (dd/MM/yyyy)"));
        form.add(txtFechaNac);
        
        form.add(new JLabel("Nro. historia *"));
        form.add(txtNroHistoria);
        
        form.add(new JLabel("Grupo sanguíneo"));
        form.add(cmbGrupoSanguineo);

        //Error 
        lblError = new JLabel("");
        lblError.setForeground(Color.RED);
        form.add(new JLabel("")); 
        form.add(lblError);

        add(form, BorderLayout.CENTER);

        //Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setBackground(VentanaPrincipal.COLOR_PANEL);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JButton btnRegistrar = new JButton("Registrar paciente");
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrar();
            }
        });

        botones.add(btnCancelar);
        botones.add(btnRegistrar);
        add(botones, BorderLayout.SOUTH);
    }

    private void registrar() {
        String nombre    = txtNombre.getText().trim();
        String apellido  = txtApellido.getText().trim();
        String dni       = txtDni.getText().trim();
        String fechaStr  = txtFechaNac.getText().trim();
        String nroHist   = txtNroHistoria.getText().trim();
        String grupo     = (String) cmbGrupoSanguineo.getSelectedItem();

        //Validaciones
        if (nombre.isEmpty()) {
            lblError.setText("El nombre es obligatorio.");
            return;
        }
        if (apellido.isEmpty()) {
            lblError.setText("El apellido es obligatorio.");
            return;
        }
        if (!dni.matches("\\d{6,10}")) {
            lblError.setText("NumCed inválido (6 a 10 dígitos numéricos).");
            return;
        }
        //Validación de fecha por formato
        if (!fechaStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
            lblError.setText("Fecha inválida. Use el formato dd/MM/yyyy.");
            return;
        }
        if (nroHist.isEmpty()) {
            lblError.setText("El número de historia clínica es obligatorio.");
            return;
        }

        try {
            //Conversión de la fecha de texto a LocalDate
            String[] partesFecha = fechaStr.split("/");
            int dia = Integer.parseInt(partesFecha[0]);
            int mes = Integer.parseInt(partesFecha[1]);
            int anio = Integer.parseInt(partesFecha[2]);
            LocalDate fechaNac = LocalDate.of(anio, mes, dia);

            String idPaciente = GeneradorId.generar("PAC");

            //Crear el objeto Paciente y añadirlo al sistema
            Paciente nuevo = new Paciente(idPaciente, nombre, apellido, fechaNac, dni, nroHist, grupo);
            sistema.registrarPaciente(nuevo);

            JOptionPane.showMessageDialog(this,
                    "Paciente registrado correctamente.\nID asignado: " + idPaciente,
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);

            dispose();
            
        } catch (Exception ex) {
            lblError.setText("Error al procesar la fecha del paciente.");
        }
    }
}