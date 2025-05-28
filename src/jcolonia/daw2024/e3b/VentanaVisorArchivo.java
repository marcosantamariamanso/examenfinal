package jcolonia.daw2024.e3b;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import javax.swing.JButton;

/**
 * Ventana para confirmar guardado y salida.
 */
public class VentanaVisorArchivo  {
	/**
	 * evita advertencias de serialización.
	 */
	private static final long serialVersionUID = -5413605927632237845L;
	/**
	 * 	Botón para evitar advertencias de serialización.
	 */
	private JButton btnGuardar;
	/**
	 * Botón para guardar los cambios y salir de la aplicación.
	 */
	private JButton btnSalir;
	/**
	 * Botón para cancelar la acción de guardar y salir.
	 */
	private JButton btnCancelar;

	/**
	 * Inicializa la ventana de diálogo para guardar y salir.
	 */
	public VentanaVisorArchivo() {
		super();
		configurarVentana();
		inicializarComponentes();
		organizarLayout();
	}

	private void organizarLayout() {
		
		
	}

	private void inicializarComponentes() {
		// TODO Esbozo de método generado automáticamente
		
	}

	// Métodos para configurar la ventana, inicializar componentes y organizar el layout
	private void configurarVentana() {
	}

	String fuente = "jdbc:sqlite:ejemplo001.db";
	Connection conexión = DriverManager.getConnection(fuente);
	Statement sentenciaSQL = (Statement) conexión.createStatement();
	// Conexión fallida: ¿conexión == null? ¿try/catch?
	{
	// …obrar en consecuencia
	}
	sentenciaSQL.setQueryTimeout(5);
	sentenciaSQL.executeUpdate("DROP TABLE IF EXISTS Personal");
	sentenciaSQL.executeUpdate(
	"CREATE TABLE Personal (Id INTEGER PRIMARY KEY, Nombre TEXT NOT NULL)");}
}

    
	