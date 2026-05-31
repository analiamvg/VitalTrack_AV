
package vitaltrack.persistencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import vitaltrack.modelo.Medico;
import vitaltrack.modelo.Paciente;

public class SerializadorPacientes {
    private String rutaPacientes;
    private String rutaMedicos;
 
    public SerializadorPacientes(String carpeta) {
        GestorArchivos.crearCarpetaSiNoExiste(carpeta);
        this.rutaPacientes = carpeta + "pacientes.dat";
        this.rutaMedicos   = carpeta + "medicos.dat";
    }
    
    //Guardar
    public boolean guardar(List<Paciente> pacientes, List<Medico> medicos) {
        boolean okP = serializarObjeto(rutaPacientes, pacientes);
        boolean okM = serializarObjeto(rutaMedicos,   medicos);
        return okP && okM;
    }
 
    private boolean serializarObjeto(String ruta, Object objeto) {
        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(objeto);
            return true;
        } catch (IOException e) {
            System.err.println("Error al serializar: " + ruta + " — " + e.getMessage());
            return false;
        }
    }
    
    //Cargar
    public List<Object[]> cargar() {
        List<Object[]> resultado = new ArrayList<>();
 
        List<Paciente> pacientes = deserializarPacientes();
        List<Medico>   medicos   = deserializarMedicos();
 
        resultado.add(new Object[]{ pacientes, medicos });
        return resultado;
    }
    
    @SuppressWarnings("unchecked")
    private List<Paciente> deserializarPacientes() {
        if (!GestorArchivos.existe(rutaPacientes)) return new ArrayList<>();
        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(rutaPacientes))) {
            return (List<Paciente>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar pacientes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
 
    @SuppressWarnings("unchecked")
    private List<Medico> deserializarMedicos() {
        if (!GestorArchivos.existe(rutaMedicos)) return new ArrayList<>();
        try (ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(rutaMedicos))) {
            return (List<Medico>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar médicos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
 
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando SerializadorPacientes");
        super.finalize();
    }
}
