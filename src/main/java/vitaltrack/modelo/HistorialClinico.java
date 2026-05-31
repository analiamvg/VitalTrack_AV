
package vitaltrack.modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistorialClinico implements Serializable{
    private static final long serialVersionUID = 1L;
 
    private String idHistorial;
    private List<Medicion>     mediciones;
    private List<AlertaClinica> alertas;
    
    //Constructor
    public HistorialClinico(String idHistorial) {
        this.idHistorial = idHistorial;
        this.mediciones = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }
    
    //Mediciones
    public void agregarMedicion(Medicion m) {
        mediciones.add(m);
    }
 
    public void agregarAlerta(AlertaClinica a) {
        alertas.add(a);
    }
    
    //Get
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getIdHistorial() {
        return idHistorial;
    }

    public List<Medicion> getMediciones() {
        return mediciones;
    }

    public List<AlertaClinica> getAlertas() {
        return alertas;
    }
    
    public int getCantidadMediciones(){
        return mediciones.size();
    }
    
    public int getCantidadAlertas(){
        return alertas.size();
    }
    
    //Ultima medicion
    public Medicion getUltimaMedicion() {
        if (mediciones.isEmpty()) return null;
        return mediciones.get(mediciones.size() - 1);
    }
    
    //Promedio de mediciones
    public double promedioFrecuenciaCardiaca(int n) {
        return promedioSigno(n, "FC");
    }
 
    public double promedioSaturacionO2(int n) {
        return promedioSigno(n, "SPO2");
    }
 
    public double promedioTemperatura(int n) {
        return promedioSigno(n, "TEMP");
    }
 
    private double promedioSigno(int n, String signo) {
        if (mediciones.isEmpty()) return 0.0;
        int desde = Math.max(0, mediciones.size() - n);
        List<Medicion> ultimas = mediciones.subList(desde, mediciones.size());
        double suma = 0.0;
        for (Medicion m : ultimas) {
            switch (signo) {
                case "FC":    suma += m.getFrecuenciaCardiaca(); break;
                case "SPO2":  suma += m.getSaturacionO2(); break;
                case "TEMP":  suma += m.getTemperatura(); break;
                case "FR":    suma += m.getFrecuenciaRespiratoria(); break;
            }
        }
        return suma / ultimas.size();
    }
    
    //Tendencias
    public String tendenciaFrecuenciaCardiaca(int n) {
        return tendenciaSigno(n, "FC");
    }
 
    public String tendenciaSaturacionO2(int n) {
        return tendenciaSigno(n, "SPO2");
    }
 
    private String tendenciaSigno(int n, String signo) {
        if (mediciones.size() < 2) return "INSUFICIENTE";
        int desde = Math.max(0, mediciones.size() - n);
        List<Medicion> ultimas = mediciones.subList(desde, mediciones.size());
 
        boolean sube = true;
        boolean baja = true;
 
        for (int i = 1; i < ultimas.size(); i++) {
            double anterior = valorSigno(ultimas.get(i - 1), signo);
            double actual   = valorSigno(ultimas.get(i),     signo);
            if (actual <= anterior) sube = false;
            if (actual >= anterior) baja = false;
        }
 
        if (sube) return "SUBE";
        if (baja) return "BAJA";
        return "ESTABLE";
    }
        private double valorSigno(Medicion m, String signo) {
        switch (signo) {
            case "FC":   return m.getFrecuenciaCardiaca();
            case "SPO2": return m.getSaturacionO2();
            case "TEMP": return m.getTemperatura();
            case "FR":   return m.getFrecuenciaRespiratoria();
            default:     return 0.0;
        }
    }
    
    //Alertas
    public List<AlertaClinica> getAlertasPendientes() {
        List<AlertaClinica> pendientes = new ArrayList<>();
        for (AlertaClinica a : alertas) {
            if (!a.isAtendida()) pendientes.add(a);
        }
        return pendientes;
    }
    
    @Override
    public String toString() {
        return "Historial [" + idHistorial + "]"
             + " | Mediciones: " + mediciones.size()
             + " | Alertas: " + alertas.size()
             + " | Pendientes: " + getAlertasPendientes().size();
    }
}
