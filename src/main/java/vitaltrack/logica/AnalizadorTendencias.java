
package vitaltrack.logica;

import java.util.List;
import vitaltrack.modelo.HistorialClinico;
import vitaltrack.modelo.Medicion;

public class AnalizadorTendencias {
    private static final int VENTANA_DEFAULT = 5;  // últimas 5 mediciones
 
    private HistorialClinico historial;
 
    public AnalizadorTendencias(HistorialClinico historial) {
        this.historial = historial;
    }
    
    //Tendencia general
    public String tendencia(String signo, int ventana) {
        List<Medicion> mediciones = historial.getMediciones();
        if (mediciones.size() < 2) return "INSUFICIENTE";
 
        int desde = Math.max(0, mediciones.size() - ventana);
        List<Medicion> muestra = mediciones.subList(desde, mediciones.size());
 
        boolean sube = true;
        boolean baja = true;
 
        for (int i = 1; i < muestra.size(); i++) {
            double ant = valor(muestra.get(i - 1), signo);
            double act = valor(muestra.get(i),     signo);
            if (act <= ant) sube = false;
            if (act >= ant) baja = false;
        }
 
        if (sube) return "SUBE";
        if (baja) return "BAJA";
        return "ESTABLE";
    }
 
    public String tendencia(String signo) {
        return tendencia(signo, VENTANA_DEFAULT);
    }
    
    //Advertencias
    public String advertenciaSpo2() {
        Medicion ultima = historial.getUltimaMedicion();
        if (ultima == null) return "";
 
        String tendSpo2 = tendencia("SPO2");
        double spo2     = ultima.getSaturacionO2();
 
        if ("BAJA".equals(tendSpo2) && spo2 < UmbralesVitales.SPO2_NORMAL + 3) {
            return "SpO2 en descenso sostenido (" + String.format("%.1f", spo2)
                    + "%) — riesgo de hipoxia";
        }
        return "";
    }
    
    public String advertenciaFC() {
        Medicion ultima = historial.getUltimaMedicion();
        if (ultima == null) return "";
 
        String tendFC = tendencia("FC");
        double fc     = ultima.getFrecuenciaCardiaca();
 
        if ("SUBE".equals(tendFC) && fc > UmbralesVitales.FC_MAX - 10) {
            return "FC en ascenso sostenido (" + String.format("%.0f", fc)
                    + " bpm) — riesgo de taquicardia";
        }
        return "";
    }
    
    public String todasLasAdvertencias() {
        StringBuilder sb = new StringBuilder();
        String wSpo2 = advertenciaSpo2();
        String wFC   = advertenciaFC();
        if (!wSpo2.isEmpty()) sb.append(wSpo2).append("\n");
        if (!wFC.isEmpty())   sb.append(wFC).append("\n");
        return sb.toString().trim();
    }
    
    //Resumen
    public double promedioFC(int n) {
        return historial.promedioFrecuenciaCardiaca(n);
    }
 
    public double promedioSpo2(int n) {
        return historial.promedioSaturacionO2(n);
    }
    
    private double valor(Medicion m, String signo) {
        switch (signo) {
            case "FC":   return m.getFrecuenciaCardiaca();
            case "SPO2": return m.getSaturacionO2();
            case "TEMP": return m.getTemperatura();
            case "FR":   return m.getFrecuenciaRespiratoria();
            default:     return 0.0;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando AnalizadorTendencias");
        super.finalize();
    }
}
