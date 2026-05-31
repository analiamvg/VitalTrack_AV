
package vitaltrack.monitor;

import vitaltrack.modelo.AlertaClinica;
import vitaltrack.modelo.Medicion;

public interface Alertable {
    
    void verificarUmbrales(Medicion medicion);
    
    AlertaClinica generarAlerta(String tipoAlerta, int severidad,
                                String descripcion, String idPaciente);
    
    int getNivelCriticidad();
    
    boolean esCritico();
}
