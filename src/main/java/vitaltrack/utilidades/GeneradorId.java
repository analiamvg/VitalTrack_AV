
package vitaltrack.utilidades;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneradorId {
    private static final AtomicInteger contador = new AtomicInteger(1);
    
    //Constructor privado
    private GeneradorId() {}
    
    public static String generar(String prefijo) {
        return prefijo + "-" + String.format("%04d", contador.getAndIncrement());
    }
    
    public static void reiniciar() {
        contador.set(1);
    }
}
