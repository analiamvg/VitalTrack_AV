
package vitaltrack.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertaClinica implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
 
    private String idAlerta;
    private String tipoAlerta;       
    private int severidad;           
    private String descripcion;
    private LocalDateTime timestamp;
    private String idPaciente;
    private String idMonitor;
    private boolean atendida;
    
    //Constructor
    public AlertaClinica(String idAlerta, String tipoAlerta,
                         int severidad, String descripcion,
                         String idPaciente, String idMonitor) {
        this.idAlerta   = idAlerta;
        this.tipoAlerta = tipoAlerta;
        this.severidad  = Math.max(1, Math.min(3, severidad)); // clamp 1-3
        this.descripcion = descripcion;
        this.timestamp  = LocalDateTime.now();
        this.idPaciente = idPaciente;
        this.idMonitor  = idMonitor;
        this.atendida   = false;
    }
    
    //Get

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static DateTimeFormatter getFMT() {
        return FMT;
    }

    public String getIdAlerta() {
        return idAlerta;
    }

    public String getTipoAlerta() {
        return tipoAlerta;
    }

    public int getSeveridad() {
        return severidad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getIdPaciente() {
        return idPaciente;
    }

    public String getIdMonitor() {
        return idMonitor;
    }

    public boolean isAtendida() {
        return atendida;
    }
    
     //Set
    public void setIdAlerta(String idAlerta) {
        this.idAlerta = idAlerta;
    }

    public void setTipoAlerta(String tipoAlerta) {
        this.tipoAlerta = tipoAlerta;
    }

    public void setSeveridad(int severidad) {
        this.severidad = severidad;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setIdPaciente(String idPaciente) {
        this.idPaciente = idPaciente;
    }

    public void setIdMonitor(String idMonitor) {
        this.idMonitor = idMonitor;
    }

    public void setAtendida(boolean atendida) {
        this.atendida = atendida;
    }
    
    public boolean esCritica() {
        return severidad == 3;
    }
 
    public String getNivelTexto() {
        switch (severidad) {
            case 1: return "LEVE";
            case 2: return "MODERADA";
            case 3: return "CRITICA";
            default: return "DESCONOCIDA";
        }
    }
    
    public String toLog() {
        return timestamp.format(FMT)
             + " | [" + getNivelTexto() + "]"
             + " | " + tipoAlerta
             + " | Paciente: " + idPaciente
             + " | Monitor: " + idMonitor
             + " | " + descripcion
             + " | Atendida: " + (atendida ? "Sí" : "No");
    }
 
    @Override
    public String toString() {
        return "[" + getNivelTexto() + "] " + tipoAlerta
             + " — " + descripcion
             + " (" + timestamp.format(FMT) + ")"
             + (atendida ? " ✓" : "");
    }
}
