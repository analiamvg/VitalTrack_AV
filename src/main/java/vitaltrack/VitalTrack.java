
package vitaltrack;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import vitaltrack.gui.VentanaPrincipal;
import vitaltrack.logica.SistemaGestion;
import vitaltrack.monitor.MonitorAvanzado;
import vitaltrack.monitor.MonitorBasico;
import vitaltrack.monitor.MonitorUCI;
import vitaltrack.utilidades.GeneradorId;
import java.time.LocalDate;
import vitaltrack.modelo.Medico;

public class VitalTrack {

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el LookAndFeel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {

            //Crea el sistema de gestión
            SistemaGestion sistema = new SistemaGestion();

            //Carga datos guardados
            sistema.cargarDatos();

            //Si no hay monitores, crea
            if (sistema.getCantidadMonitores() == 0) {
                crearMonitoresDemo(sistema);
            }

            //Registrar inicio
            System.out.println("VitalTrack iniciado — " + java.time.LocalDateTime.now());

            //Lanzar la ventana principal
            VentanaPrincipal ventana = new VentanaPrincipal(sistema);
            ventana.setVisible(true);
        });
    }

    private static void crearMonitoresDemo(SistemaGestion sistema) {
        MonitorBasico    mb  = new MonitorBasico(
                GeneradorId.generar("MON"), "PulseGuard X1", "MedTech", 30);
        MonitorAvanzado  ma  = new MonitorAvanzado(
                GeneradorId.generar("MON"), "VitalScan Pro", "BioMed", true, true);
        MonitorUCI       mu  = new MonitorUCI(
                GeneradorId.generar("MON"), "CriticalView UCI", "MedCore", true);

        sistema.registrarMonitor(mb);
        sistema.registrarMonitor(ma);
        sistema.registrarMonitor(mu);

        System.out.println("Monitores de demo creados: Básico, Avanzado, UCI");
    }
}