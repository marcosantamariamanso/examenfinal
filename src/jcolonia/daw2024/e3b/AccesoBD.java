package jcolonia.daw2024.e3b;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Utilidades de acceso a una base de datos SQLite para gestión de
 * {@link InventarioAula puestos de un aula de informática}.
 * 
 * @version 09.08 (20250525001)
 * @author <a href="dmartin.jcolonia@gmail.com">David H. Martín</a>
 */
public class AccesoBD implements AutoCloseable {
	/** Sentencia SQL para crear la tabla «Puestos» –vacía– si no existe. */
	private static final String SQL_CREAR_TABLA = "CREATE TABLE IF NOT EXISTS Puestos (código_puesto TEXT NOT NULL, ordenador TEXT NOT NULL, nombre TEXT NOT NULL, apellidos TEXT NOT NULL)";

	/** Prototipo de sentenciaSQL preparada para insertar puestos. */
	private static final String SQL_INSERTAR_CONTACTO = "INSERT INTO Puestos VALUES (?, ?, ?, ?)";

	/** Sentencia SQL para obtener un volcado completo de los resultados. */
	private static final String SQL_LISTADO_AULA = "SELECT * FROM Puestos WHERE código_puesto LIKE ?";

	/** Sentencia SQL para vaciar los resultados. */
	private static final String SQL_VACIAR_TABLA = "DELETE FROM Puestos";

	/** Sentencia SQL para compactar espacio en el archivo de la base de datos. */
	private static final String SQL_COMPACTAR_ESPACIO = "VACUUM";

	/**
	 * Nombre predeterminado del archivo de configuración del acceso a la base de
	 * datos.
	 */
	public static final String ARCHIVO_CONFIG_PREDETERMINADO = "config.xml";

	/** Nombre predeterminado del archivo de la base de datos. */
	public static final String ARCHIVO_BD_PREDETERMINADO = "inventarioPuestosIC.db";

	/** Nombre del archivo de datos para impotación/exportación. */
	public static final String ARCHIVO_DATOS_PREDETERMINADO = "Inventario ICXX.txt";

	/** Configuración del acceso a la base de datos. */
	private Properties configuración;

	/** Conexión a la base de datos. */
	private Connection conexión;

	/** Sentencia general SQL. */
	private Statement sentenciaGeneralSQL;

	/**
	 * Sentencia preparada SQL, para inserciones en la base de datos.
	 * 
	 * @see #SQL_INSERTAR_CONTACTO
	 */
	private PreparedStatement preInserciónSQL;
	/**
	 * Sentencia preparada SQL, para consultas a la base de datos.
	 * 
	 * @see #SQL_LISTADO_AULA
	 */
	private PreparedStatement preConsultaSQL;

	/**
	 * Carga la configuración desde el archivo de configuración predeterminado.
	 * 
	 * @see #ARCHIVO_CONFIG_PREDETERMINADO
	 */
	public AccesoBD() {
		this(ARCHIVO_CONFIG_PREDETERMINADO, ARCHIVO_BD_PREDETERMINADO);
	}

	/**
	 * Carga la configuración desde un archivo de configuración. En caso de error
	 * genera uno nuevo.
	 * 
	 * @param archivoConfiguración la ruta y nombre del archivo
	 * @param archivoBD            la ruta y nombre del archivo de base de datos a
	 *                             emplear en caso de tener que crear una nueva
	 *                             configuración. En caso de ser nulo o vacío se
	 *                             empleará el {@link #ARCHIVO_BD_PREDETERMINADO
	 *                             nombre predeterminado}.
	 */
	public AccesoBD(String archivoConfiguración, String archivoBD) {
		try {
			configuración = cargarConfiguración(archivoConfiguración);
		} catch (AccesoBDException e) {
			if (archivoBD == null || archivoBD.isEmpty()) {
				archivoBD = ARCHIVO_BD_PREDETERMINADO;
			}
			System.err.printf("Error cargando configuración de «%s»: %s%n", archivoConfiguración, e.getMessage());
			configuración = crearConfiguración(archivoConfiguración, archivoBD);
		}
	}

	/**
	 * Lee un archivo de configuración.
	 * 
	 * @param archivoConfiguración la ruta del archivo
	 * @return la configuración leída
	 * @throws AccesoBDException si no existe el archivo o se produce alguna
	 *                           incidencia durante la lectura
	 */
	public static Properties cargarConfiguración(String archivoConfiguración) throws AccesoBDException {
		Path rutaConfig;
		rutaConfig = Path.of(archivoConfiguración);

		if (!existeArchivo(rutaConfig)) {
			String mensaje;
			mensaje = String.format("No existe el archivo «%s»", rutaConfig.getFileName());
			throw new AccesoBDException(mensaje);
		}

		Properties configuración = new Properties();
		try (FileInputStream in = new FileInputStream(rutaConfig.toFile())) {
			configuración.loadFromXML(in);
		} catch (IOException e) {
			throw new AccesoBDException("Error al cargar configuración", e);
		}

		return configuración;
	}

	/**
	 * Crea un archivo de configuración con los datos de acceso a la base de datos
	 * en formato SQLite. El único aspecto relevante que contiene es el nombre del
	 * archivo.
	 * 
	 * @param archivoConfiguración el nombre, ruta del archivo de configuración
	 * @param archivoBD            el nombre, ruta del archivo de la base de datos
	 * @return la configuración creada
	 */
	public static Properties crearConfiguración(String archivoConfiguración, String archivoBD) {
		Path rutaConfig;
		rutaConfig = Path.of(archivoConfiguración);

		Properties configuración = new Properties();
		configuración.setProperty("jdbc.url", "jdbc:sqlite:" + archivoBD);
		configuración.setProperty("jdbc.user", "");
		configuración.setProperty("jdbc.password", "");
		configuración.setProperty("jdbc.codificación", "UTF-8");

		try (FileOutputStream out = new FileOutputStream(rutaConfig.toFile())) {
			configuración.storeToXML(out, "Configuración BD", "UTF-8");
			System.err.printf("Creado nuevo archivo de configuración «%s» para «%s»%n", archivoConfiguración,
					archivoBD);
		} catch (IOException e) {
			System.err.printf("Error al guardar configuración en «%s»: %s%n", archivoConfiguración,
					e.getLocalizedMessage());
		}
		return configuración;
	}

	/**
	 * Comprueba la existencia de un archivo.
	 * 
	 * @param ruta el nombre, ruta del archivo a comprobar
	 * @return si existe o no
	 * @throws AccesoBDException si el archivo existe pero no se puede leer
	 */
	public static boolean existeArchivo(Path ruta) throws AccesoBDException {
		boolean existe;

		existe = Files.exists(ruta);

		if (existe && !Files.isReadable(ruta)) {
			String mensaje;
			mensaje = String.format("No se puede leer el archivo «%s»", ruta.getFileName());
			throw new AccesoBDException(mensaje);
		}
		return existe;
	}

	/**
	 * Abre la conexión a la base de datos si no ha sido abierta previamente. Crea
	 * también una sentencia SQL genérica –disponible para ejecutar consultas no
	 * preparadas– y la tabla principal en caso de no existir.
	 * 
	 * @return la conexión existente o creada
	 * @throws AccesoBDException si no se completa o se produce alguna incidencia
	 *                           durante la conexión
	 */
	public Connection abrirConexión() throws AccesoBDException {
		if (conexión == null) {
			String jdbcURL = configuración.getProperty("jdbc.url");
			String jdbcUser = configuración.getProperty("jdbc.user");
			String jdbcPassword = configuración.getProperty("jdbc.password");

			try {
				conexión = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPassword);

				if (conexión == null) { // Conexión fallida
					String mensaje = String.format("%s — Conexión fallida 😕%n", jdbcURL);
					throw new AccesoBDException(mensaje);
				}

				sentenciaGeneralSQL = conexión.createStatement();
				sentenciaGeneralSQL.setQueryTimeout(5);
				sentenciaGeneralSQL.execute(SQL_CREAR_TABLA);
			} catch (SQLException e) {
				String mensaje = String.format("%s — Conexión fallida: %s", jdbcURL, e.getLocalizedMessage());
				throw new AccesoBDException(mensaje, e);
			}
		}
		return conexión;
	}

	/**
	 * Lee el contenido de la base de datos y crea un inventario con los puestos de
	 * un aula.
	 * 
	 * @param prefijo el prefijo del aula
	 * @return el aula recién creada
	 * @throws AccesoBDException si se produce alguna incidencia
	 */
	public InventarioAula leer(String prefijo) throws AccesoBDException {
		ResultSet resultado;
		InventarioAula aula;

		String códigoPuesto, ordenador, nombre, apellidos;

		try {
			if (preConsultaSQL == null) {
				preConsultaSQL = conexión.prepareStatement(SQL_LISTADO_AULA);
				preConsultaSQL.setQueryTimeout(5);
			}

			aula = InventarioAula.of(prefijo);
			preConsultaSQL.setString(1, prefijo + "%"); // Comodín SQL en «LIKE»

			resultado = preConsultaSQL.executeQuery();
			while (resultado.next()) {
				códigoPuesto = resultado.getString("código_puesto");

				ordenador = resultado.getString("ordenador");
				nombre = resultado.getString("nombre");
				apellidos = resultado.getString("apellidos");
				aula.añadir(códigoPuesto, new PuestoUsuario(ordenador, nombre, apellidos));
			}
		} catch (SQLException | InventarioException ex) {
			String mensaje = String.format("Error al cargar aula «%s»: %s", prefijo, ex.getLocalizedMessage());
			throw new AccesoBDException(mensaje, ex);
		}

		return aula;
	}

	/**
	 * Inserta un resultado en la base de datos. En caso de no existir la sentencia
	 * preparada se crea -permitiendo así que se pueda compartir en caso de realizar
	 * varias inserciones consecutivas.
	 * 
	 * @param código el código del puesto
	 * @param puesto los datos del puesto
	 * 
	 * @return el número de filas afectadas –cero o una…–
	 * @throws AccesoBDException si se produce alguna incidencia
	 */
	public int insertar(String código, PuestoUsuario puesto) throws AccesoBDException {
		int númFilas = 0;
		try {
			if (preInserciónSQL == null) {
				preInserciónSQL = conexión.prepareStatement(SQL_INSERTAR_CONTACTO);
				preInserciónSQL.setQueryTimeout(5);
			}

			preInserciónSQL.setString(1, código);
			preInserciónSQL.setString(2, puesto.ordenador());
			preInserciónSQL.setString(3, puesto.nombre());
			preInserciónSQL.setString(4, puesto.apellidos());
			númFilas = preInserciónSQL.executeUpdate();
		} catch (SQLException ex) {
			String mensaje = String.format("Error al insertar contacto: %s", ex.getLocalizedMessage());
			throw new AccesoBDException(mensaje, ex);
		}
		return númFilas;
	}

	/**
	 * Inserta los puestos de un aula en la base de datos.
	 * 
	 * @param aula el aula con los puestos a grabar
	 * @return el número de filas afectadas, debería coincidir con el tamaño de la
	 *         colección original
	 * @throws AccesoBDException si se produce alguna incidencia
	 */
	public int escribir(InventarioAula aula) throws AccesoBDException {
		Set<String> lista;
		int númFilas = 0;

		if (aula == null) {
			throw new AccesoBDException("Lista nula");
		}

		lista = aula.generarListaCódigos();
		for (String código : lista) {
			númFilas += insertar(código, aula.get(código));
		}

		return númFilas;
	}

	/**
	 * Deja cerrada la conexión y descarta las sentencias SQL inicializadas.
	 * 
	 * @throws AccesoBDException si se produce alguna incidencia
	 */
	@Override
	public void close() throws AccesoBDException {
		if (conexión != null) {
			try (Connection conexiónCerrada = conexión) {
			} catch (SQLException e) {
				String mensaje = String.format("Error en cierre de conexión: %s", e.getLocalizedMessage());
				throw new AccesoBDException(mensaje, e);
			}

			// Cierre totalmente completo, descartamos ya referencias con mayor seguridad
			sentenciaGeneralSQL = null;
			preInserciónSQL = null;
			preConsultaSQL = null;
			conexión = null;
		}
	}

	/**
	 * Importa equipos almacenados en un archivo de texto reemplazando el contenido
	 * actual del programa. Emplea un formato propio –de estilo CSV con separador
	 * «#» u otro– producido por una exportación previa. El formato concreto de cada
	 * línea así como la lógica de importación/exportación de los puestos depende
	 * del inventario. En caso de producirse algún error de acceso o por el propio
	 * formato del archivo, se envía el mensaje a la salida de error estándar y el
	 * programa continúa sin perder el contenido anterior.
	 * 
	 * @param rutaArchivo la ubicación del archivo de texto original
	 * 
	 * @throws AccesoBDException si se produce alguna incidencia al acceder a la
	 *                           base de datos
	 */
	public void generarBD(String rutaArchivo) throws AccesoBDException {
		AccesoArchivo archivo;
		InventarioAula nuevaLista;
		List<String> contenido;
		int númElementos;
		String mensaje;

		try {
			archivo = new AccesoArchivo(rutaArchivo);
			contenido = archivo.leer();
			númElementos = contenido.size() - 1; // 1 línea de cabecera, el prefijo

			if (númElementos < 1) {
				VistaGeneral.mostrarAviso("No hay ningún elemento que importar");
			} else {
				nuevaLista = InventarioAula.of(contenido);
				númElementos = nuevaLista.getNúmElementos();

				if (númElementos > 0) {
					mensaje = String.format("%d equipos migrados", númElementos);
					VistaGeneral.mostrarTexto(mensaje);

					abrirConexión();
					// vaciarBD();
					escribir(nuevaLista);
				} else {
					mensaje = String.format("%d equipos migrados, transferencia fallida", númElementos);
					VistaGeneral.mostrarAviso(mensaje);
				}
			}
		} catch (InventarioException ex) {
			mensaje = String.format("Error de importación: %s", ex.getLocalizedMessage());
			VistaGeneral.mostrarAviso(mensaje);
		}
	}

	/**
	 * Genera un archivo de base de datos de ejemplo y el archivo de configuración
	 * correspondiente.
	 * 
	 * @param argumentos opciones de ejecución –no usado–
	 */
	public static void main(String[] argumentos) {
		try (AccesoBD acceso = new AccesoBD()) {
			// Cierre implícito con close() –try_with_resources–
			acceso.generarBD(ARCHIVO_DATOS_PREDETERMINADO);
		} catch (AccesoBDException e) {
			System.err.println(e.getLocalizedMessage());
		}
	}
}
