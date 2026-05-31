
package vitaltrack.monitor;

import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.TipoAlerta;
import vitaltrack.utilidades.GeneradorId;

public class MonitorBasico extends MonitorSignosVitales{
    
// Umbrales de Frecuencia Cardiaca (bpm)
    private static final double FC_MIN = 60.0;
    private static final double FC_MAX = 100.0;
    private static final double FC_CRIT = 130.0;   // taquicardia severa
 
    // Umbrales de SpO2 (%)
    private static final double SPO2_MIN = 95.0;
    private static final double SPO2_HIPOXIA = 90.0;  // hipoxia moderada
 
    private int intervaloMedicionSeg;   
    
    //Constructor
    public MonitorBasico(String idMonitor, String modelo, String fabricante,
                         int intervaloMedicionSeg) {
        super(idMonitor, modelo, fabricante);
        this.intervaloMedicionSeg = intervaloMedicionSeg;
    }
    
    @Override
    public Medicion medir() {
        double fc   = 55 + Math.random() * 65;   // rango 55–120 bpm
        double spo2 = 88 + Math.random() * 12;   // rango 88–100 %
 
        String idMedicion = GeneradorId.generar("MED");
        return new Medicion(idMedicion, fc, spo2);
    }
    
    //Verificacion de umbral
    @Override
    public void verificarUmbrales(Medicion medicion) {
        int maxSeveridad = 0;
 
        double fc = medicion.getFrecuenciaCardiaca();
        if (fc >= FC_CRIT) {
            maxSeveridad = Math.max(maxSeveridad, 3);
        } else if (fc > FC_MAX) {
            maxSeveridad = Math.max(maxSeveridad, 2);
        } else if (fc < FC_MIN) {
            maxSeveridad = Math.max(maxSeveridad, 1);
        }
 
        double spo2 = medicion.getSaturacionO2();
        if (spo2 < SPO2_HIPOXIA) {
            maxSeveridad = Math.max(maxSeveridad, 3);
        } else if (spo2 < SPO2_MIN) {
            maxSeveridad = Math.max(maxSeveridad, 2);
        }
 
        setNivelCriticidad(maxSeveridad);
    }
    
    @Override
    public AlertaClinica generarAlerta(String tipoAlerta, int severidad,
                                       String descripcion, String idPaciente) {
        String idAlerta = GeneradorId.generar("ALT");
        return construirAlerta(idAlerta, tipoAlerta, severidad, descripcion, idPaciente);
    }
    
    //Analiza ultima medicion y genera alerta
    public AlertaClinica evaluarYGenerar(Medicion medicion, String idPaciente) {
        verificarUmbrales(medicion);
 
        double fc   = medicion.getFrecuenciaCardiaca();
        double spo2 = medicion.getSaturacionO2();
 
        // Prioridad: SpO2 crítico > FC crítico > otros
        if (spo2 < SPO2_HIPOXIA) {
            return generarAlerta(TipoAlerta.HIPOXIA_SEVERA, 3,
                    "SpO2 = " + String.format("%.1f", spo2) + "% — hipoxia severa", idPaciente);
        }
        if (fc >= FC_CRIT) {
            return generarAlerta(TipoAlerta.TAQUICARDIA, 3,
                    "FC = " + String.format("%.0f", fc) + " bpm — taquicardia crítica", idPaciente);
        }
        if (spo2 < SPO2_MIN) {
            return generarAlerta(TipoAlerta.HIPOXIA_LEVE, 2,
                    "SpO2 = " + String.format("%.1f", spo2) + "% — hipoxia leve", idPaciente);
        }
        if (fc > FC_MAX) {
            return generarAlerta(TipoAlerta.TAQUICARDIA, 2,
                    "FC = " + String.format("%.0f", fc) + " bpm — taquicardia moderada", idPaciente);
        }
        if (fc < FC_MIN) {
            return generarAlerta(TipoAlerta.BRADICARDIA, 1,
                    "FC = " + String.format("%.0f", fc) + " bpm — bradicardia leve", idPaciente);
        }
 
        return null;  // sin alerta
    }
    
    //Get
    public int getIntervaloMedicionSeg() {
        return intervaloMedicionSeg;
    }
    
    @Override
    public String getTipoMonitor() { return "Monitor Básico"; }
 
    @Override
    public String toString() {
        return super.toString()
             + " | Intervalo: " + intervaloMedicionSeg + "s";
    }
 
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando MonitorBasico -> " + getIdMonitor());
        super.finalize();
    }
}
