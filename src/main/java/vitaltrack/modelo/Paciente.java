
package vitaltrack.modelo;

import java.time.LocalDate;

public class Paciente extends Persona{
    private String nroHistoriaClinica;
    private String diagnostico;
    private String grupoSanguineo;
    private HistorialClinico historial;
    
    //Constructor 
    public Paciente(String id, String nombre, String apellido,
                    LocalDate fechaNacimiento, String dni,
                    String nroHistoriaClinica, String grupoSanguineo) {
        super(id, nombre, apellido, fechaNacimiento, dni);
        this.nroHistoriaClinica = nroHistoriaClinica;
        this.grupoSanguineo     = grupoSanguineo;
        this.diagnostico        = "Sin diagnóstico";
        // El historial se crea automáticamente: composición
        this.historial = new HistorialClinico(id + "-HIST");
    }
    
    //Metodo abstracto
    @Override
    public String getRol() {
        return "Paciente";
    }
    
    //Get
    public String getNroHistoriaClinica() {
        return nroHistoriaClinica;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public HistorialClinico getHistorial() {
        return historial;
    }
    
    //Set
    public void setNroHistoriaClinica(String nroHistoriaClinica) {
        this.nroHistoriaClinica = nroHistoriaClinica;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public void setHistorial(HistorialClinico historial) {
        this.historial = historial;
    }
    
    //Historial
    public void agregarMedicion(Medicion medicion) {
        historial.agregarMedicion(medicion);
    }
    
    @Override
    public String toString() {
        return super.toString()
             + " | Nro. historia: " + nroHistoriaClinica
             + " | Grupo: " + grupoSanguineo
             + " | Diagnóstico: " + diagnostico;
    }
}
