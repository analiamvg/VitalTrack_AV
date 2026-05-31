
package vitaltrack.persistencia;

import java.time.LocalDateTime;
import vitaltrack.modelo.AlertaClinica;
import vitaltrack.utilidades.FormateadorFecha;

public class LogAlertas {
    private String rutaLog;
 
    public LogAlertas(String rutaLog) {
        this.rutaLog = rutaLog;
        // Encabezado inicial solo si el archivo no existía
        if (!GestorArchivos.existe(rutaLog)) {
            GestorArchivos.escribirLinea(rutaLog,
                    "=== LOG DE ALERTAS CLÍNICAS — VitalTrack ===", false);
        }
    }
    
    //Registrar alerta
    public void registrar(AlertaClinica alerta) {
        if (alerta == null) return;
        GestorArchivos.escribirLinea(rutaLog, alerta.toLog(), true);
    }
    
    public void registrarEvento(String mensaje) {
        String linea = FormateadorFecha.formatearFechaHora(LocalDateTime.now())
                     + " | [SISTEMA] | " + mensaje;
        GestorArchivos.escribirLinea(rutaLog, linea, true);
    }
    
    //Retornar como lista de Strings
    public java.util.List<String> leerLog() {
        return GestorArchivos.leerLineas(rutaLog);
    }
 
    public String getRutaLog() { 
        return rutaLog; 
    }
}
