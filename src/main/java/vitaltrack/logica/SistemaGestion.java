package vitaltrack.logica;

import java.util.ArrayList;
import java.util.List;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.Paciente;
import vitaltrack.modelo.Medico;
import vitaltrack.monitor.MonitorAvanzado;
import vitaltrack.monitor.MonitorBasico;
import vitaltrack.monitor.MonitorSignosVitales;
import vitaltrack.monitor.MonitorUCI;
import vitaltrack.persistencia.LogAlertas;
import vitaltrack.persistencia.SerializadorPacientes;

public class SistemaGestion {
    private List<Paciente> pacientes;
    private List<Medico> medicos;
    private List<MonitorSignosVitales> monitores;
 
    private SerializadorPacientes serializador;
    private LogAlertas logAlertas;
 
    private static final String RUTA_DATOS = "data/";
    
    // Constructor
    public SistemaGestion() {
        this.pacientes    = new ArrayList<>();
        this.medicos      = new ArrayList<>();
        this.monitores    = new ArrayList<>();
        this.serializador = new SerializadorPacientes(RUTA_DATOS);
        this.logAlertas   = new LogAlertas(RUTA_DATOS + "alertas.log");
    }
    
    // Gestion de pacientes
    public void registrarPaciente(Paciente paciente) {
        pacientes.add(paciente);
    }
 
    public void eliminarPaciente(String idPaciente) {
        pacientes.removeIf(p -> p.getId().equals(idPaciente));
    }
 
    public Paciente buscarPaciente(String idPaciente) {
        for (Paciente p : pacientes) {
            if (p.getId().equals(idPaciente)) return p;
        }
        return null;
    }
 
    public List<Paciente> getPacientes() {
        return pacientes; 
    }
    
    // Gestion de medicos (Unificada para evitar duplicados y conectar con la GUI)
    public void agregarMedico(Medico medico) {
        if (!medicos.contains(medico)) {
            medicos.add(medico);
        }
    }
 
    public Medico buscarMedico(String idMedico) {
        for (Medico m : medicos) {
            if (m.getId().equals(idMedico)) return m;
        }
        return null;
    }
 
    public List<Medico> getMedicos() {
        return medicos; 
    }
    
    // Gestion de monitores
    public void registrarMonitor(MonitorSignosVitales monitor) {
        monitores.add(monitor);
    }
 
    public MonitorSignosVitales buscarMonitor(String idMonitor) {
        for (MonitorSignosVitales m : monitores) {
            if (m.getIdMonitor().equals(idMonitor)) return m;
        }
        return null;
    }
 
    public List<MonitorSignosVitales> getMonitores() {
        return monitores; 
    }
    
    // Asignacion
    public boolean asignarMonitor(String idMonitor, String idPaciente) {
        MonitorSignosVitales monitor = buscarMonitor(idMonitor);
        Paciente paciente = buscarPaciente(idPaciente);
        if (monitor == null || paciente == null) {
            return false;
        }
        monitor.setIdPacienteAsignado(idPaciente);
        return true;
    }
    
    // Ciclo de monitoreo
    public void ejecutarCicloMonitoreo() {
        for (MonitorSignosVitales monitor : monitores) {
            if (!monitor.isActivo()) continue;
 
            String idPaciente = monitor.getIdPacienteAsignado();
            if (idPaciente == null) continue;
 
            Paciente paciente = buscarPaciente(idPaciente);
            if (paciente == null) continue;
 
            // Medir
            Medicion medicion = monitor.medir();
 
            // Guardar en historial
            paciente.agregarMedicion(medicion);
 
            // Evaluar y generar alerta
            AlertaClinica alerta = null;
 
            if (monitor instanceof MonitorUCI) {
                MonitorUCI uci = (MonitorUCI) monitor;
                alerta = uci.evaluarYGenerar(medicion, idPaciente);
                if (alerta == null) {
                    alerta = uci.evaluarParametrosUCI(idPaciente);
                }
            } else if (monitor instanceof MonitorAvanzado) {
                alerta = ((MonitorAvanzado) monitor).evaluarYGenerar(medicion, idPaciente);
            } else if (monitor instanceof MonitorBasico) {
                alerta = ((MonitorBasico) monitor).evaluarYGenerar(medicion, idPaciente);
            }
 
            // Si hay alerta, registrar
            if (alerta != null) {
                paciente.getHistorial().agregarAlerta(alerta);
                logAlertas.registrar(alerta);
            }
 
            // Diagnóstico asistido, informativo
            String sugerencia = DiagnosticoAsistido.evaluar(medicion);
            if (!sugerencia.isEmpty()) {
                System.out.println("[DiagAsist] Paciente " + idPaciente + ": " + sugerencia);
            }
        }
    }
    
    // Persistencia
    public void guardarDatos() {
        serializador.guardar(pacientes, medicos);
    }
 
    @SuppressWarnings("unchecked")
    public void cargarDatos() {
        List<Object[]> cargado = serializador.cargar();
        if (cargado != null && !cargado.isEmpty()) {
            Object[] datos = cargado.get(0);
            if (datos[0] instanceof List) pacientes = (List<Paciente>) datos[0];
            if (datos[1] instanceof List) medicos   = (List<Medico>)   datos[1];
        }
    }
    
    // Resumenes numéricos
    public int getCantidadPacientes()  { return pacientes.size(); }
    public int getCantidadMedicos()    { return medicos.size(); }
    public int getCantidadMonitores()  { return monitores.size(); }

    public List<Paciente> getPacientesCriticos() {
        List<Paciente> criticos = new ArrayList<>();
        for (Paciente p : pacientes) {
            if (p.getHistorial() != null && !p.getHistorial().getAlertasPendientes().isEmpty()) {
                for (AlertaClinica a : p.getHistorial().getAlertasPendientes()) {
                    if (a.esCritica()) { 
                        criticos.add(p); 
                        break; 
                    }
                }
            }
        }
        return criticos;
    }
 
    @Override
    public String toString() {
        return "SistemaGestion | Pacientes: " + pacientes.size()
             + " | Médicos: " + medicos.size()
             + " | Monitores: " + monitores.size();
    }
}