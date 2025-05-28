package jcolonia.daw2024.e3b;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Clase principal que migra datos desde un archivo de texto a una base de datos SQLite.
 * @author Marco S.
 * @version 1.0
 */
public class MigrarInventarioBD {
    /**
     * Método principal que ejecuta la migración de datos.
     * 
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Usamos try-with-resources para asegurar el cierre de la conexión
        try (AccesoBD bd = new AccesoBD()) {
            // Configuración inicial de la base de datos
            bd.crearConexion();
            bd.crearTabla();

            // Opción 1: Usar el método integrado de importación
            bd.importarDesdeArchivo("Inventario ICXX.txt");
            
            // Opción 2: Procesar manualmente línea por línea
            // procesarArchivoManual(bd, "Inventario ICXX.txt");
            
            System.out.println("Migración finalizada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error de base de datos: " + e.getMessage());
        } catch (AccesoBDException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }

    /**
     * Procesa un archivo de texto línea por línea e inserta en la base de datos.
     * 
     * @param bd objeto AccesoBD para la conexión a base de datos
     * @param nombreArchivo ruta del archivo a procesar
     * @throws IOException si hay error leyendo el archivo
     * @throws SQLException si hay error en la base de datos
     */
    private static void procesarArchivoManual(AccesoBD bd, String nombreArchivo) 
            throws IOException, SQLException {
        try (BufferedReader lector = new BufferedReader(new FileReader(nombreArchivo))) {
            String linea;
            int lineasProcesadas = 0;
            int lineasIgnoradas = 0;

            while ((linea = lector.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    try {
                        bd.insertarRegistro(linea);
                        lineasProcesadas++;
                    } catch (IllegalArgumentException e) {
                        System.err.printf("Línea ignorada (%s): %s%n", e.getMessage(), linea);
                        lineasIgnoradas++;
                    } catch (SQLException e) {
                        System.err.printf("Error en base de datos al procesar línea: %s%n", linea);
                        throw e;
                    }
                }
            }
            
            System.out.printf("Resumen: %d líneas procesadas, %d líneas ignoradas%n",
                            lineasProcesadas, lineasIgnoradas);
        }
    }
}