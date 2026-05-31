
package vitaltrack.utilidades;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormateadorFecha {
    private static final DateTimeFormatter FMT_FECHA     =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATETIME  =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FMT_HORA      =
            DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FMT_ARCHIVO   =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
 
    private FormateadorFecha() {}
    
    public static String formatearFecha(LocalDate fecha) {
        return fecha != null ? fecha.format(FMT_FECHA) : "N/D";
    }
 
    public static String formatearFechaHora(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_DATETIME) : "N/D";
    }
 
    public static String formatearHora(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_HORA) : "N/D";
    }
 
    /** Formato para nombres de archivos: 20260525_143022 */
    public static String formatearParaArchivo(LocalDateTime dt) {
        return dt != null ? dt.format(FMT_ARCHIVO) : "00000000_000000";
    }
    
    public static LocalDate parsearFecha(String texto) {
        try {
            return LocalDate.parse(texto.trim(), FMT_FECHA);
        } catch (Exception e) {
            return null;
        }
    }
}
