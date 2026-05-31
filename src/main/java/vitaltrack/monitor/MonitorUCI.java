
package vitaltrack.monitor;

import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;
import vitaltrack.modelo.TipoAlerta;
import vitaltrack.utilidades.GeneradorId;

public class MonitorUCI extends MonitorAvanzado{
    // Umbrales presión intracraneal (mmHg)
    private static final double PIC_NORMAL = 15.0;
    private static final double PIC_ALTA   = 20.0;
    private static final double PIC_CRIT   = 25.0;
 
    private boolean medirEEG;
    private double  presionIntracraneal;   
    private String  estadoEEG;    
    
    // Constructor
    public MonitorUCI(String idMonitor, String modelo, String fabricante, boolean medirEEG) {
        super(idMonitor, modelo, fabricante, true, true);
        this.medirEEG             = medirEEG;
        this.presionIntracraneal  = 10.0;
        this.estadoEEG            = "Normal";
    }
    
    @Override
    public Medicion medir() {
        // Llama al medir() del padre 
        Medicion base = super.medir();
        // Simula presión intracraneal y EEG
        this.presionIntracraneal = 8.0 + Math.random() * 22.0;
        if (medirEEG) {
            double r = Math.random();
            if      (r < 0.75) this.estadoEEG = "Normal";
            else if (r < 0.90) this.estadoEEG = "Alterado";
            else               this.estadoEEG = "Sin actividad";
        }
        return base;  // retorna la medición base; los datos UCI se leen con getters
    }
    @Override
    public void verificarUmbrales(Medicion medicion) {
        // Primero verifica los umbrales de MonitorAvanzado
        super.verificarUmbrales(medicion);
 
        // Luego agrega la evaluación de parámetros UCI
        int nivelUCI = 0;
 
        if (presionIntracraneal >= PIC_CRIT) {
            nivelUCI = 3;
        } else if (presionIntracraneal >= PIC_ALTA) {
            nivelUCI = 2;
        } else if (presionIntracraneal > PIC_NORMAL) {
            nivelUCI = 1;
        }
 
        if (medirEEG && "Sin actividad".equals(estadoEEG)) {
            nivelUCI = 3;
        } else if (medirEEG && "Alterado".equals(estadoEEG)) {
            nivelUCI = Math.max(nivelUCI, 2);
        }
 
        // Aplica el nivel más alto entre UCI y el heredado
        int nivelActual = getNivelCriticidad();
        setNivelCriticidad(Math.max(nivelActual, nivelUCI));
    }
    
    //Alerta
     @Override
    public AlertaClinica generarAlerta(String tipoAlerta, int severidad, String descripcion, String idPaciente) {
        String idAlerta = GeneradorId.generar("ALT");
        return construirAlerta(idAlerta, tipoAlerta, severidad, descripcion, idPaciente);
    }
    
    public AlertaClinica evaluarParametrosUCI(String idPaciente) {
        if (presionIntracraneal >= PIC_CRIT) {
            return generarAlerta(TipoAlerta.SHOCK_HIPOVOLEMICO, 3,
                    "Presión intracraneal crítica: "
                    + String.format("%.1f", presionIntracraneal) + " mmHg", idPaciente);
        }
        if (presionIntracraneal >= PIC_ALTA) {
            return generarAlerta(TipoAlerta.SHOCK_HIPOVOLEMICO, 2,
                    "Presión intracraneal elevada: "
                    + String.format("%.1f", presionIntracraneal) + " mmHg", idPaciente);
        }
        if (medirEEG && "Sin actividad".equals(estadoEEG)) {
            return generarAlerta(TipoAlerta.INSUF_RESPIRATORIA, 3,
                    "EEG sin actividad detectada", idPaciente);
        }
        if (medirEEG && "Alterado".equals(estadoEEG)) {
            return generarAlerta(TipoAlerta.INSUF_RESPIRATORIA, 2,
                    "EEG con actividad alterada", idPaciente);
        }
        return null;
    }
    
    //Get
    public boolean isMedirEEG() {
        return medirEEG;
    }

    public double getPresionIntracraneal() {
        return presionIntracraneal;
    }

    public String getEstadoEEG() {
        return estadoEEG;
    }
    
    @Override
    public String getTipoMonitor() { return "Monitor UCI"; }
 
    @Override
    public String toString() {
        return super.toString()
             + " | EEG: " + (medirEEG ? estadoEEG : "No")
             + " | PIC: " + String.format("%.1f", presionIntracraneal) + " mmHg";
    }
}
