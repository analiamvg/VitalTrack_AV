package vitaltrack.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;

public abstract class Persona implements Serializable{
   
    private static final long serialVersionUID = 1L;
 
    //Atributos
    private String id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNac;
    private String CedNum;
    
    //Constructor
    public Persona(String id, String nombre, String apellido, LocalDate fechaNac, String CedNum) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNac = fechaNac;
        this.CedNum = CedNum;
    }
    
    //Metodo
    public abstract String getRol();
    
    //Get
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }
    
    public String getNombreCompleto(){
        return nombre + " " + apellido;
    }

    public LocalDate getFechaNac() {
        return fechaNac;
    }

    public String getCedNum() {
        return CedNum;
    }
    
    //Set
    public void setId(String id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setFechaNac(LocalDate fechaNac) {
        this.fechaNac = fechaNac;
    }

    public void setCedNum(String CedNum) {
        this.CedNum = CedNum;
    }
    
    public int getEdad(){
        return Period.between(fechaNac, LocalDate.now()).getYears();
    }
    @Override
    public String toString() {
        return "[" + getRol() + "] " + getNombreCompleto()
             + " | Numero de cedula: " + CedNum
             + " | Edad: " + getEdad() + " años"
             + " | ID: " + id;
    }
}
