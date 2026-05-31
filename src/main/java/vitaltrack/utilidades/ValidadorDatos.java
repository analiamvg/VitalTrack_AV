
package vitaltrack.utilidades;

public class ValidadorDatos {
    private ValidadorDatos() {}
 
    //Que el texto no sea null ni vacío
    public static boolean noEsVacio(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
 
    //Que el texto tenga el min de caracteres
    public static boolean longitudMinima(String texto, int minLen) {
        return noEsVacio(texto) && texto.trim().length() >= minLen;
    }
 
    //Que el DNI tenga entre 6 y 10 dígitos
    public static boolean esDniValido(String dni) {
        return noEsVacio(dni) && dni.trim().matches("\\d{6,10}");
    }
 
    //Que el texto sea un número entero positivo
    public static boolean esEnteroPositivo(String texto) {
        try {
            return Integer.parseInt(texto.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    //Que el texto represente un número decimal en el rango
    public static boolean esDecimalEnRango(String texto, double min, double max) {
        try {
            double valor = Double.parseDouble(texto.trim());
            return valor >= min && valor <= max;
        } catch (NumberFormatException e) {
            return false;
        }
    }
 
    //Que el formato de fecha sea dd/MM/yyyy
    public static boolean esFechaValida(String texto) {
        return FormateadorFecha.parsearFecha(texto) != null;
    }
    
    //Formato de la presion arterial
    public static boolean esPresionArterialValida(String texto) {
        if (!noEsVacio(texto)) return false;
        String[] partes = texto.trim().split("/");
        if (partes.length != 2) return false;
        return esEnteroPositivo(partes[0]) && esEnteroPositivo(partes[1]);
    }
    
    public static String mensajeSiVacio(String texto, String nombreCampo) {
        return noEsVacio(texto) ? "" : nombreCampo + " no puede estar vacío.";
    }
    
    @Override
    protected void finalize() throws Throwable {
        System.out.println("Destructor: liberando ValidadorDatos");
        super.finalize();
    }
}
