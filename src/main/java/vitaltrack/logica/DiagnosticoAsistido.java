
package vitaltrack.logica;

import vitaltrack.modelo.Medicion;

public class DiagnosticoAsistido {
    private DiagnosticoAsistido() {}
    
    //Analiza patron de riesgo
    public static String evaluar(Medicion m) {
        if (m == null) return "";
 
        double fc   = m.getFrecuenciaCardiaca();
        double spo2 = m.getSaturacionO2();
        double temp = m.getTemperatura();
        double fr   = m.getFrecuenciaRespiratoria();
        
        //1: FC alta + SpO2 baja + FR alta, posible insuficiencia respiratoria
        if (fc > UmbralesVitales.FC_MAX
                && spo2 < UmbralesVitales.SPO2_NORMAL
                && fr  > UmbralesVitales.FR_MAX) {
            return "Posible insuficiencia respiratoria: FC elevada, SpO2 baja y FR aumentada.";
        }
        
        //2: FC baja + PA sistólica baja, posible shock hipovolémico
        if (fc < UmbralesVitales.FC_MIN
                && spo2 < UmbralesVitales.SPO2_NORMAL) {
            return "Posible hipoperfusión: FC baja con SpO2 reducida.";
        }
 
        //3: Fiebre alta + FC alta + FR alta, posible sepsis
        if (temp > 0
                && temp >= UmbralesVitales.TEMP_FIEBRE
                && fc  >  UmbralesVitales.FC_MAX
                && fr  >  UmbralesVitales.FR_MAX) {
            return "Posible cuadro séptico: fiebre alta con FC y FR elevadas.";
        }
        
        //4: Hipotermia + FC baja, posible hipotermia sistémica
        if (temp > 0
                && temp < UmbralesVitales.TEMP_HIPOT
                && fc  < UmbralesVitales.FC_MIN) {
            return "⚕ Posible hipotermia sistémica: temperatura y FC por debajo del rango normal.";
        }
 
        //5: SpO2 muy baja sola, hipoxia aislada
        if (spo2 < UmbralesVitales.SPO2_HIPOXIA) {
            return "⚕ Hipoxia detectada: SpO2 por debajo del umbral crítico.";
        }
 
        return "";
    }
    
    //Resumen
    public static String evaluarFC(double fc) {
        if (fc >= UmbralesVitales.FC_CRITICA) return "Taquicardia crítica";
        if (fc >  UmbralesVitales.FC_MAX) return "Taquicardia";
        if (fc <  UmbralesVitales.FC_MIN) return "Bradicardia";
        return "Normal";
    }
 
    public static String evaluarSpo2(double spo2) {
        if (spo2 < UmbralesVitales.SPO2_HIPOXIA) return "Hipoxia severa";
        if (spo2 < UmbralesVitales.SPO2_NORMAL) return "Hipoxia leve";
        return "Normal";
    }
    
    public static String evaluarTemperatura(double temp) {
        if (temp <= 0) return "N/D";
        if (temp >= UmbralesVitales.TEMP_FIEBRE) return "Fiebre";
        if (temp >  UmbralesVitales.TEMP_MAX) return "Subfebril";
        if (temp <  UmbralesVitales.TEMP_HIPOT) return "Hipotermia";
        if (temp <  UmbralesVitales.TEMP_MIN) return "Hipotermia leve";
        return "Normal";
    }
}
