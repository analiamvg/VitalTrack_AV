# VitalTrack — Sistema de Gestión de Pacientes con Monitoreo de Signos Vitales
**Proyecto Final — Programación Orientada a Objetos (2026)**
*Facultad de Ingeniería — UNA*

## 1. Descripción del problema
En los hospitales y clínicas, controlar a los pacientes todo el tiempo es de vital importancia para detectar rapidamente si alguno se pone grave. El problema es que cuando hay muchos pacientes y distintos tipos de monitores, anotar los signos vitales a mano toma mucho tiempo y es fácil equivocarse.

Para solucionar esto, tenemos **VitalTrack**. Es un sistema para gestionar pacientes en el que se les puede asignar monitores médicos a cada uno. El programa registra las mediciones de los signos vitales, avisa si hay algo fuera del rango normal mediante alertas de diferente gravedad, guarda el historial clínico para ver si el paciente mejora o empeora, y hasta da sugerencias de diagnóstico según la combinación de los datos recibidos. Está pensado para que lo use el personal de salud en salas comunes o en terapia intensiva.

## 2. Conceptos de POO Aplicados
Para cumplir con los requisitos del proyecto, se aplicaron los siguientes pilares de la programación orientada a objetos:

* **Abstracción y Jerarquías:** Tenemos la clase abstracta `Persona` y de ella heredan `Paciente` y `Medico`. También la interfaz `Alertable` para obligar a los monitores a manejar alarmas, y una clase abstracta `MonitorSignosVitales` de donde salen los tipos de monitores.
* **Herencia:** Los monitores tienen tres niveles de complejidad. `MonitorBasico` y `MonitorAvanzado` heredan de la clase base, y `MonitorUCI` hereda de `MonitorAvanzado` para aprovechar su código.
* **Polimorfismo:** Modifiqué el comportamiento de los métodos usando `@Override`. Por ejemplo, cada monitor implementa a su manera los métodos `medir()` y `verificarUmbrales()`, y cada rol (`Paciente` o `Medico`) devuelve su texto correspondiente en el método `getRol()`.
* **Encapsulamiento:** Puse todos los atributos de las clases como `private` para proteger los datos. El acceso y la modificación de las variables se hacen únicamente a través de los métodos `get` y `set`.
* **Relaciones:** * *Composición:* Un `Paciente` tiene un `HistorialClinico` (si el paciente se borra, su historial también).
    * *Agregación:* Un `Paciente` tiene asignado un `MonitorSignosVitales`, pero el monitor sigue existiendo aunque no tenga paciente.
    * *Asociación:* El `Medico` tiene una lista de pacientes asignados a su cargo.

## 3. Diagrama UML
Aquí abajo está el diagrama de clases que armé para diseñar el sistema:



## 4. Capturas de la Interfaz Gráfica (Swing)
Así se ve la aplicación en ejecución:

* **Pantalla Principal (Dashboard):** *(Colocar captura en docs/screenshots/dashboard.png)*
* **Registro de Pacientes y Médicos:** *(Colocar captura en docs/screenshots/pacientes.png)*
* **Simulador de Signos Vitales y Alertas:** *(Colocar captura en docs/screenshots/simulador.png)*

## 5. Estructura del Código fuente
Organicé el código en paquetes dentro de NetBeans para que sea fácil de mantener:
* `vitaltrack.modelo`: Clases de los objetos del sistema (`Persona`, `Paciente`, `Medico`, `Medicion`, etc.).
* `vitaltrack.monitor`: La interfaz de alertas y los distintos tipos de monitores médicos.
* `vitaltrack.logica`: Las reglas del negocio (los umbrales de salud, detección de enfermedades y el sistema principal).
* `vitaltrack.persistencia`: Clases para guardar y leer los datos en archivos.
* `vitaltrack.gui`: Todas las pantallas y paneles hechos con Swing.
* `vitaltrack.utilidades`: Helpers para formatear fechas, validar datos y generar IDs.
* `App.java`: Es el archivo principal (Main) que arranca todo el programa.

## 6. Tecnologías utilizadas
* **Java 17:** Lenguaje base del proyecto.
* **Swing:** Para el diseño de las ventanas y los botones.
* **Archivos (.dat y .txt):** Usé serialización de objetos para guardar los datos de pacientes/médicos sin perderlos al cerrar el programa, y archivos de texto para el historial de alertas (log).
* **NetBeans y Maven:** IDE y gestor del proyecto.
* **GitHub:** Para el control de versiones.

## 7. Cómo hacer funcionar el proyecto
1.  Clonar este repositorio en tu computadora usando la terminal:
    ```bash
    git clone [https://github.com/analiamvg/VitalTrack.git](https://github.com/analiamvg/VitalTrack.git)
    ```
2.  Abrir Apache NetBeans.
3.  Ir a **File -> Open Project**, buscar la carpeta descargada y abrirla (NetBeans va a reconocer que es un proyecto Maven).
4.  Hacer clic derecho sobre el proyecto y elegir **Clean and Build** para compilar todo.
5.  Buscar el archivo `App.java`, hacer clic derecho y darle a **Run File** (o presionar `Shift + F6`).

*Nota: Al arrancar por primera vez, el programa va a crear solo una carpeta llamada `data/` en la raíz para guardar los archivos de persistencia.*

## Alumna
* **Analia MVG**
* GitHub: [@analiamvg](https://github.com/analiamvg)
* Carrera: Ingeniería Electrónica / Mecatrónica
* Materia: Programación Orientada a Objetos — 2026
