
package vitaltrack.monitor;

import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.TipoAlerta;
import vitaltrack.utilidades.GeneradorId;

public class MonitorAvanzado extends MonitorSignosVitales{
    
    // Umbrales Frecuencia Cardiaca
    private static final double FC_MIN  = 60.0;
    private static final double FC_MAX  = 100.0;
    private static final double FC_CRIT = 130.0;
 
    // Umbrales SpO2
    private static final double SPO2_MIN     = 95.0;
    private static final double SPO2_HIPOXIA = 90.0;
 
    // Umbrales Temperatura (°C)
    private static final double TEMP_MIN      = 36.0;
    private static final double TEMP_MAX      = 37.5;
    private static final double TEMP_FIEBRE   = 38.5;
    private static final double TEMP_HIPOT    = 35.0;
 
    // Umbrales Presión Arterial sistólica (mmHg)
    private static final double PAS_MIN  = 90.0;
    private static final double PAS_MAX  = 120.0;
    private static final double PAS_CRIT = 180.0;
 
    // Umbrales Frecuencia Respiratoria (rpm)
    private static final double FR_MIN  = 12.0;
    private static final double FR_MAX  = 20.0;
    private static final double FR_CRIT = 30.0;
 
    private boolean medirPresionArterial;
    private boolean medirTemperatura;
    
    // Constructor
    public MonitorAvanzado(String idMonitor, String modelo, String fabricante,
                           boolean medirPresionArterial, boolean medirTemperatura) {
        super(idMonitor, modelo, fabricante);
        this.medirPresionArterial = medirPresionArterial;
        this.medirTemperatura     = medirTemperatura;
    }
    
    @Override
    public Medicion medir() {
        double fc   = 55  + Math.random() * 65;
        double spo2 = 88  + Math.random() * 12;
        double temp = medirTemperatura     ? 35.5 + Math.random() * 3.5 : 0.0;
        double fr   = 10  + Math.random() * 25;
 
        String pas  = medirPresionArterial
                      ? String.valueOf((int)(80  + Math.random() * 120))
                      : "N/D";
        String pad  = medirPresionArterial
                      ? String.valueOf((int)(50  + Math.random() * 60))
                      : "N/D";
 
        String idMedicion = GeneradorId.generar("MED");
        return new Medicion(idMedicion, fc, spo2, temp, fr, pas, pad);
    }
    
    //Verificar umbrales
    @Override
    public void verificarUmbrales(Medicion medicion) {
        int maxSeveridad = 0;
 
        double fc   = medicion.getFrecuenciaCardiaca();
        double spo2 = medicion.getSaturacionO2();
        double temp = medicion.getTemperatura();
        double fr   = medicion.getFrecuenciaRespiratoria();
 
        // Frecuencia Cardiaca
        if      (fc >= FC_CRIT)      maxSeveridad = Math.max(maxSeveridad, 3);
        else if (fc > FC_MAX)        maxSeveridad = Math.max(maxSeveridad, 2);
        else if (fc < FC_MIN)        maxSeveridad = Math.max(maxSeveridad, 1);
 
        // SpO2
        if      (spo2 < SPO2_HIPOXIA) maxSeveridad = Math.max(maxSeveridad, 3);
        else if (spo2 < SPO2_MIN)     maxSeveridad = Math.max(maxSeveridad, 2);
 
        // Temperatura
        if (medirTemperatura && temp > 0) {
            if      (temp >= TEMP_FIEBRE) maxSeveridad = Math.max(maxSeveridad, 2);
            else if (temp > TEMP_MAX)     maxSeveridad = Math.max(maxSeveridad, 1);
            else if (temp < TEMP_HIPOT)   maxSeveridad = Math.max(maxSeveridad, 3);
            else if (temp < TEMP_MIN)     maxSeveridad = Math.max(maxSeveridad, 1);
        }
 
        // Frecuencia Respiratoria
        if      (fr >= FR_CRIT) maxSeveridad = Math.max(maxSeveridad, 3);
        else if (fr > FR_MAX)   maxSeveridad = Math.max(maxSeveridad, 2);
        else if (fr < FR_MIN)   maxSeveridad = Math.max(maxSeveridad, 2);
 
        setNivelCriticidad(maxSeveridad);  
    }
    
    //Alerta
    @Override
    public AlertaClinica generarAlerta(String tipoAlerta, int severidad,
                                       String descripcion, String idPaciente) {
        String idAlerta = GeneradorId.generar("ALT");
        return construirAlerta(idAlerta, tipoAlerta, severidad, descripcion, idPaciente);
    }
    
    public AlertaClinica evaluarYGenerar(Medicion medicion, String idPaciente) {
        verificarUmbrales(medicion);
 
        double fc   = medicion.getFrecuenciaCardiaca();
        double spo2 = medicion.getSaturacionO2();
        double temp = medicion.getTemperatura();
        double fr   = medicion.getFrecuenciaRespiratoria();
 
        // Nivel 3 — críticos primero
        if (spo2 < SPO2_HIPOXIA)
            return generarAlerta(TipoAlerta.HIPOXIA_SEVERA, 3,
                    "SpO2 = " + String.format("%.1f", spo2) + "%", idPaciente);
        if (fc >= FC_CRIT)
            return generarAlerta(TipoAlerta.TAQUICARDIA, 3,
                    "FC = " + String.format("%.0f", fc) + " bpm", idPaciente);
        if (medirTemperatura && temp < TEMP_HIPOT)
            return generarAlerta(TipoAlerta.HIPOTERMIA, 3,
                    "Temp = " + String.format("%.1f", temp) + "°C", idPaciente);
        if (fr >= FR_CRIT)
            return generarAlerta(TipoAlerta.TAQUIPNEA, 3,
                    "FR = " + String.format("%.0f", fr) + " rpm", idPaciente);
 
        // Nivel 2 — moderados
        if (spo2 < SPO2_MIN)
            return generarAlerta(TipoAlerta.HIPOXIA_LEVE, 2,
                    "SpO2 = " + String.format("%.1f", spo2) + "%", idPaciente);
        if (fc > FC_MAX)
            return generarAlerta(TipoAlerta.TAQUICARDIA, 2,
                    "FC = " + String.format("%.0f", fc) + " bpm", idPaciente);
        if (medirTemperatura && temp >= TEMP_FIEBRE)
            return generarAlerta(TipoAlerta.FIEBRE, 2,
                    "Temp = " + String.format("%.1f", temp) + "°C", idPaciente);
        if (fr > FR_MAX || fr < FR_MIN)
            return generarAlerta(TipoAlerta.APNEA, 2,
                    "FR = " + String.format("%.0f", fr) + " rpm", idPaciente);
 
        // Nivel 1 — leves
        if (fc < FC_MIN)
            return generarAlerta(TipoAlerta.BRADICARDIA, 1,
                    "FC = " + String.format("%.0f", fc) + " bpm", idPaciente);
        if (medirTemperatura && (temp < TEMP_MIN || temp > TEMP_MAX))
            return generarAlerta(TipoAlerta.FIEBRE, 1,
                    "Temp = " + String.format("%.1f", temp) + "°C", idPaciente);
 
        return null;
    }

    public boolean isMedirPresionArterial() {
        return medirPresionArterial;
    }

    public boolean isMedirTemperatura() {
        return medirTemperatura;
    }
    
    @Override
    public String getTipoMonitor() { return "Monitor Avanzado"; }
 
    @Override
    public String toString() {
        return super.toString()
             + " | PA: " + (medirPresionArterial ? "Sí" : "No")
             + " | Temp: " + (medirTemperatura ? "Sí" : "No");
    }
}
