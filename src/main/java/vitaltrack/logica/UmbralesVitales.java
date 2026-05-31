
package vitaltrack.logica;

public class UmbralesVitales {

    private UmbralesVitales() {}
 
    //Frecuencia cardíaca (bpm)
    public static final double FC_MIN = 60.0;
    public static final double FC_MAX = 100.0;
    public static final double FC_CRITICA = 130.0;
 
    //Saturación de oxígeno (%)
    public static final double SPO2_NORMAL = 95.0;
    public static final double SPO2_HIPOXIA = 90.0;
 
    //Temperatura (°C)
    public static final double TEMP_MIN = 36.0;
    public static final double TEMP_MAX = 37.5;
    public static final double TEMP_FIEBRE = 38.5;
    public static final double TEMP_HIPOT = 35.0;
 
    //Presión arterial sistólica (mmHg)
    public static final double PAS_MIN = 90.0;
    public static final double PAS_MAX = 120.0;
    public static final double PAS_CRITICA = 180.0;
 
    //Presión arterial diastólica (mmHg)
    public static final double PAD_MIN = 60.0;
    public static final double PAD_MAX = 80.0;
 
    // Frecuencia respiratoria (rpm)
    public static final double FR_MIN = 12.0;
    public static final double FR_MAX = 20.0;
    public static final double FR_CRITICA = 30.0;
 
    // Presión intracraneal (mmHg)
    public static final double PIC_NORMAL = 15.0;
    public static final double PIC_ALTA = 20.0;
    public static final double PIC_CRITICA = 25.0;
 
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando UmbralesVitales");
        super.finalize();
    }    
}
