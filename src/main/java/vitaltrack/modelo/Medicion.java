
package vitaltrack.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Medicion implements Serializable{
    
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    private String idMedicion;
    private LocalDateTime timestamp;
 
    //Signos vitales
    private double frecuenciaCardiaca; // bpm
    private double saturacionO2; // % SpO2
    private double temperatura; // °C
    private double frecuenciaRespiratoria; // rpm
    private String presionArterialSist; // mmHg sistólica
    private String presionArterialDias; // mmHg diastólica
    
    //Constructor completo
    public Medicion(String idMedicionn, double frecuenciaCardiaca, double saturacionO2, double temperatura, double frecuenciaRespiratoria, String presionArterialSist, String presionArterialDias) {
        this.idMedicion = idMedicion;
        this.timestamp = LocalDateTime.now();
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.saturacionO2 = saturacionO2;
        this.temperatura = temperatura;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.presionArterialSist = presionArterialSist;
        this.presionArterialDias = presionArterialDias;
    }
    
    //Constructor simple
    public Medicion(String idMedicion, double frecuenciaCardiaca, double saturacionO2) {
        this.idMedicion = idMedicion;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.saturacionO2 = saturacionO2;
        
        this.timestamp = LocalDateTime.now(); // Asigna la fecha y hora exacta del momento de la medición
        this.temperatura = 0.0;
        this.frecuenciaRespiratoria = 0.0;
        this.presionArterialSist = "N/D";
        this.presionArterialDias = "N/D";
    }
    
    //Get
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public static DateTimeFormatter getFMT() {
        return FMT;
    }

    public String getIdMedicion() {
        return idMedicion;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public double getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public double getSaturacionO2() {
        return saturacionO2;
    }

    public double getTemperatura() {
        return temperatura;
    }

    public double getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public String getPresionArterialSist() {
        return presionArterialSist;
    }

    public String getPresionArterialDias() {
        return presionArterialDias;
    }
    
    public String getPresionArterial() {
        return presionArterialSist + "/" + presionArterialDias + " mmHg";
    }
    
    //Persistencia
    public String toCSV() {
        return idMedicion + ","
             + timestamp.format(FMT) + ","
             + frecuenciaCardiaca + ","
             + saturacionO2 + ","
             + temperatura + ","
             + frecuenciaRespiratoria + ","
             + presionArterialSist + ","
             + presionArterialDias;
    }
    
    public static String encabezadoCSV() {
        return "ID,Timestamp,FC(bpm),SpO2(%),Temp(°C),FR(rpm),PAS(mmHg),PAD(mmHg)";
    }
 
    @Override
    public String toString() {
        return "[" + timestamp.format(FMT) + "]"
             + " FC:" + frecuenciaCardiaca + "bpm"
             + " SpO2:" + saturacionO2 + "%"
             + " Temp:" + temperatura + "°C"
             + " FR:" + frecuenciaRespiratoria + "rpm"
             + " PA:" + getPresionArterial();
    }
}
