
package vitaltrack.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    private GestorArchivos() {}
    
    //Crear la carpeta, retorna true si ya existe
    public static boolean crearCarpetaSiNoExiste(String ruta) {
        File carpeta = new File(ruta);
        if (!carpeta.exists()) {
            return carpeta.mkdirs();
        }
        return true;
    }
    
    //Escribir una lista en el archivo
    public static boolean escribir(String rutaArchivo, List<String> lineas, boolean append) {
        crearCarpetaSiNoExiste(new File(rutaArchivo).getParent());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, append))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir archivo: " + rutaArchivo + " — " + e.getMessage());
            return false;
        }
    }
    
    //Escribir una linea
    public static boolean escribirLinea(String rutaArchivo, String linea, boolean append) {
        List<String> lista = new ArrayList<>();
        lista.add(linea);
        return escribir(rutaArchivo, lista, append);
    }
    
    //Leer las lineas del archivo
    public static List<String> leerLineas(String rutaArchivo) {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) return lineas;
 
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + rutaArchivo + " — " + e.getMessage());
        }
        return lineas;
    }
    
    public static boolean existe(String rutaArchivo) {
        return new File(rutaArchivo).exists();
    }
}
