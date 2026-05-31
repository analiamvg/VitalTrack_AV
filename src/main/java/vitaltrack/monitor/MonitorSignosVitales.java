
package vitaltrack.monitor;

import java.io.Serializable;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;

public abstract class MonitorSignosVitales implements Alertable, Serializable{
    
    private static final long serialVersionUID = 1L;
    
    private String idMonitor;
    private String modelo;
    private String fabricante;
    private boolean activo;
    private int nivelCriticidad;
    private String idPacienteAsignado;
    
    //Constructor
    public MonitorSignosVitales(String idMonitor, String modelo, String fabricante) {
        this.idMonitor = idMonitor;
        this.modelo = modelo;
        this.fabricante = fabricante;
        this.activo = true;
        this.nivelCriticidad = 0;
        this.idPacienteAsignado = null;
    }
    
    public abstract Medicion medir();
    
    //Implementa Alertable
    @Override
    public int getNivelCriticidad() {
        return nivelCriticidad;
    }
    @Override
    public boolean esCritico() {
        return nivelCriticidad == 3;
    }
    
    protected void setNivelCriticidad(int nivel) {
        this.nivelCriticidad = Math.max(0, Math.min(3, nivel));
    }
    
    protected AlertaClinica construirAlerta(String idAlerta, String tipoAlerta, int severidad, String descripcion, String idPaciente) {
        return new AlertaClinica(idAlerta, tipoAlerta, severidad, descripcion, idPaciente, this.idMonitor);
    }
    
    //Get
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIdMonitor() {
        return idMonitor;
    }

    public String getModelo() {
        return modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public boolean isActivo() {
        return activo;
    }

    public String getIdPacienteAsignado() {
        return idPacienteAsignado;
    }
    
    //Set
    public void setIdMonitor(String idMonitor) {
        this.idMonitor = idMonitor;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public void setIdPacienteAsignado(String idPacienteAsignado) {
        this.idPacienteAsignado = idPacienteAsignado;
    }
    
    //Para GUI
    public abstract String getTipoMonitor();
 
    @Override
    public String toString() {
        return "[" + getTipoMonitor() + "] "
             + modelo + " (" + fabricante + ")"
             + " | ID: " + idMonitor
             + " | Estado: " + (activo ? "Activo" : "Inactivo")
             + " | Criticidad: " + nivelCriticidad
             + " | Paciente: " + (idPacienteAsignado != null ? idPacienteAsignado : "Sin asignar");
    }
}
