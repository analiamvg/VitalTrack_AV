
package vitaltrack.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Medico extends Persona{
    
    private String matricula;
    private String especialidad;
    private List<Paciente> pacientesACargo;
    
    //Constructor
    public Medico(String id, String nombre, String apellido,
                  LocalDate fechaNacimiento, String dni,
                  String matricula, String especialidad) {
        super(id, nombre, apellido, fechaNacimiento, dni);
        this.matricula        = matricula;
        this.especialidad     = especialidad;
        this.pacientesACargo  = new ArrayList<>();
    }
    
    //Metodo abstracto
    @Override
    public String getRol() {
        return "Médico";
    }
    
    //Gestion de pacientes
    public void asignarPaciente(Paciente paciente) {
        if (!pacientesACargo.contains(paciente)) {
            pacientesACargo.add(paciente);
        }
    }
 
    public void removerPaciente(Paciente paciente) {
        pacientesACargo.remove(paciente);
    }
 
    public boolean tienePaciente(Paciente paciente) {
        return pacientesACargo.contains(paciente);
    }
    
    //Get
    public String getMatricula() {
        return matricula;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public List getPacientesACargo() {
        return pacientesACargo;
    }
    
    public int getCantidadPacientes(){
        return pacientesACargo.size();
    }
    
    //Set
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public void setPacientesACargo(List pacientesACargo) {
        this.pacientesACargo = pacientesACargo;
    }
    @Override
    public String toString() {
        return super.toString()
             + " | Matrícula: " + matricula
             + " | Especialidad: " + especialidad
             + " | Pacientes: " + pacientesACargo.size();
    }
 
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando Medico -> " + getNombreCompleto());
        super.finalize();
    }
}

