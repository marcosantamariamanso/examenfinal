package jcolonia.daw2024.e3b;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Relación de equipos informáticos de un aula o despacho. Implementación basada
 * en colecciones de tipo mapa/diccionario.
 * 
 * @see Map
 * @see HashMap
 * 
 * @author <a href= "mailto:dmartin.jcolonia@gmail.com">David H. Martín</a>
 * @version 09.08 (20250525000)
 */
public class InventarioAula {
	/** Separador usado al exportar/importar en archivos de texto */
	private static final String SEPARADOR_CSV = "##";
	/** Formato de línea empleado para listados de texto. */
	private static final String FORMATO_LISTADO_TEXTO = "%s – %s (%s %s)";
	/** Formato de línea pseudo CSV empleado para importación/exportación. */
	private static final String FORMATO_EXPORTACIÓN_CSV; // "%s##%s##%s##%s)";

	/**
	 * Prefijo común obligatorio en todos los códigos del aula. El prefijo debe
	 * contener exclusivamente letras y números, y será insensible a
	 * mayúsculas/minúsculas.
	 */
	private String prefijoAula;

	/**
	 * Colección de puestos indexada por código. El código debe incluir el prefijo
	 * común obligatorio –que servirá para identificar físicamente el aula y su
	 * ubicación– y un número/valor –que identificará la posición del equipo en el
	 * aula.<div>Ejemplo:
	 * 
	 * <pre>
	 * AX12
	 * </pre>
	 * 
	 * </div>
	 */
	private Map<String, PuestoUsuario> listaPuestos;

	static {
		FORMATO_EXPORTACIÓN_CSV = String.format("%%s%1$s%%s%1$s%%s%1$s%%s", SEPARADOR_CSV);
	}

	/**
	 * Inicializa el prefijo de aula y crea la colección, vacía.
	 * 
	 * @param prefijo el texto correspondiente al prefijo obligatorio
	 */
	private InventarioAula(String prefijo) {
		this.prefijoAula = prefijo;
		listaPuestos = new HashMap<>();
	}

	/**
	 * Crea un aula vacía con un prefijo determinado. El prefijo se almacena en
	 * mayúsculas, quitando los espacios iniciales y finales.
	 * 
	 * @param prefijo el texto correspondiente al prefijo obligatorio
	 * @return la nueva aula creada
	 * @throws InventarioException si la longitud efectiva del prefijo queda fuera
	 *                             del rango [2, 4]
	 */
	public static InventarioAula of(String prefijo) throws InventarioException {
		InventarioAula aulaGenerada;

		if (prefijo == null) {
			throw new InventarioException("Prefijo requerido");
		}

		prefijo = prefijo.trim();
		if (prefijo.length() < 2 || prefijo.length() > 4) {
			throw new InventarioException("Longitud de prefijo debe estar entre [2, 4]");
		}

		prefijo = prefijo.toUpperCase(Locale.of("es", "ES"));

		aulaGenerada = new InventarioAula(prefijo);
		return aulaGenerada;
	}

	/**
	 * Crea un aula con los datos recogidos de un archivo de exportación.
	 * 
	 * @see #generarListadoCSV()
	 * @param listaImportada volcado de las líneas del archivo
	 * 
	 * @return la nueva aula creada
	 * @throws InventarioException si se produce algún error fatal en la importación
	 */
	public static InventarioAula of(List<String> listaImportada) throws InventarioException {
		InventarioAula aulaImportada;
		String prefijo, despiece[];

		prefijo = listaImportada.get(0);
		if (prefijo == null) {
			throw new InventarioException("Prefijo requerido en la primera línea");
		}

		prefijo = prefijo.trim();
		if (prefijo.length() < 2 || prefijo.length() > 4) {
			throw new InventarioException("Longitud de prefijo debe estar entre [2, 4]");
		}

		prefijo = prefijo.toUpperCase(Locale.of("es", "ES"));

		aulaImportada = new InventarioAula(prefijo);

		for (int i = 1; i < listaImportada.size(); i++) { // Descartando línea 0 del prefijo
			try {
				despiece = listaImportada.get(i).split(SEPARADOR_CSV);
				if (despiece.length == 4) {
					aulaImportada.añadir(despiece[0], new PuestoUsuario(despiece[1], despiece[2], despiece[3]));
				}
			} catch (InventarioException e) {
				// Línea corrupta: saltar y seguir…
			}
		}

		return aulaImportada;
	}

	/**
	 * Informa si la relación está vacía.
	 * 
	 * @return sí o no está vacía
	 */
	public boolean estáVacío() {
		return listaPuestos.isEmpty();
	}

	/**
	 * Informa del número de elementos almacenados.
	 * 
	 * @return el valor correspondiente
	 */
	public int getNúmElementos() {
		return listaPuestos.size();
	}

	/**
	 * Facilita los datos de un puesto determinado
	 * 
	 * @param códigoPuesto el código del puesto a consultar
	 * @return los datos correspondientes o <code>null</code> si no se encuentra el
	 *         código
	 */
	public PuestoUsuario get(String códigoPuesto) {
		return listaPuestos.get(códigoPuesto);
	}

	/**
	 * Incorpora un nuevo puesto a la relación. Normaliza los datos almacenados:
	 * retira los espacios al inicio y fin y transforma en mayúsculas el código del
	 * puesto.
	 * 
	 * @param códigoPuesto el código del nuevo puesto
	 * @param datosPuesto  el resto de datos del nuevo puesto
	 * @throws InventarioException si alguno de los datos facilitados hubiera
	 *                             quedado vacío, si el código del puesto no es
	 *                             válido o si ya estaba presente.
	 */
	public void añadir(String códigoPuesto, PuestoUsuario datosPuesto) throws InventarioException {
		String nombre, apellidos, ordenador;
		String mensaje;

		códigoPuesto = normalizarTexto(códigoPuesto, "Código de puesto requerido");
		códigoPuesto = códigoPuesto.toUpperCase(Locale.of("es", "ES"));

		if (!códigoPuesto.startsWith(prefijoAula)) {
			mensaje = String.format("Código de puesto «%s» no corresponde a la relación «%s»", códigoPuesto,
					prefijoAula);
			throw new InventarioException(mensaje);
		}

		if (listaPuestos.containsKey(códigoPuesto)) {
			mensaje = String.format("Código de puesto «%s» duplicado", códigoPuesto);
			throw new InventarioException(mensaje);
		}

		verificarNulo(datosPuesto, "Datos de puesto requeridos");
		nombre = normalizarTexto(datosPuesto.nombre(), "Nombre del usuario habitual requerido");
		apellidos = normalizarTexto(datosPuesto.apellidos(), "Apellidos del usuario habitual requeridos");
		ordenador = normalizarTexto(datosPuesto.ordenador(), "Identificación del equipo informático requerida");
		listaPuestos.put(códigoPuesto, new PuestoUsuario(ordenador, nombre, apellidos));
	}

	/**
	 * Genera una lista con todos los códigos de puestos presentes.
	 * 
	 * @return una colección inmutable con todas las líneas de texto
	 */
	public Set<String> generarListaCódigos() {
		Set<String> listaCódigos;
		listaCódigos = listaPuestos.keySet();

		return Collections.unmodifiableSet(listaCódigos);
	}

	/**
	 * Genera un listado de textos con una línea por cada puesto presente en la
	 * relación.
	 * 
	 * @param formatoPrintf formato de salida tipo «printf» para cada elemento
	 * @return una colección con todas las líneas de texto
	 */
	private List<String> generarListado(String formatoPrintf) {
		ArrayList<String> listadoTexto;
		Set<String> listaCódigos;
		PuestoUsuario datosPuesto;
		String línea;

		listadoTexto = new ArrayList<>();

		listaCódigos = listaPuestos.keySet();
		for (String códigoPuesto : listaCódigos) {
			datosPuesto = listaPuestos.get(códigoPuesto);

			línea = String.format(formatoPrintf, códigoPuesto, datosPuesto.ordenador(), datosPuesto.nombre(),
					datosPuesto.apellidos());
			listadoTexto.add(línea);
		}

		return listadoTexto;
	}

	/**
	 * Genera un listado de textos con una línea por cada puesto presente en la
	 * relación en formato para visualizar por el usuario.
	 * 
	 * @see #generarListado(String)
	 * 
	 * @return una colección inmutable con todas las líneas de texto
	 */
	public List<String> generarListadoTexto() {
		List<String> listaPublicable;

		listaPublicable = generarListado(FORMATO_LISTADO_TEXTO);

		return Collections.unmodifiableList(listaPublicable);
	}

	/**
	 * Genera un listado de textos con una línea por cada puesto presente en la
	 * relación en formato para exportar a un archivo de estilo CSV. La primera
	 * línea contiene solo el prefijo.
	 * 
	 * @see #generarListado(String)
	 * @return una colección inmutable con todas las líneas de texto
	 */
	public List<String> generarListadoCSV() {
		List<String> listaExportable;

		listaExportable = generarListado(FORMATO_EXPORTACIÓN_CSV);
		listaExportable.addFirst(prefijoAula);

		return Collections.unmodifiableList(listaExportable);
	}

	/**
	 * Genera una descripción de la relación incluyendo el prefijo obligatorio común
	 * y el número de elementos.<div>Ejemplo:
	 * 
	 * <pre>
	 *   IC* – 12 puestos
	 * </pre>
	 * 
	 * </div>
	 * 
	 * @return el texto correspondiente
	 */
	@Override
	public String toString() {
		String descripción;
		descripción = String.format("%s* – %d puestos", prefijoAula, getNúmElementos());

		return descripción;
	}

	/**
	 * Detecta y bloquea parámetros nulos.
	 * 
	 * @param <T>     el tipo del objeto verificado
	 * @param objeto  la referencia a analizar
	 * @param mensaje el texto a incluir en la excepción
	 * @throws InventarioException si la referencia es nula
	 */
	public static <T> void verificarNulo(T objeto, String mensaje) throws InventarioException {
		if (objeto == null) {
			throw new InventarioException(mensaje);
		}
	}

	/**
	 * Recorta espacios al comienzo y fin de una cadena de texto y bloquea cadenas
	 * de texto nulas o efectivamente vacías -que solo contengan espacios.
	 * 
	 * @param texto   la referencia al texto a analizar
	 * @param mensaje el texto a incluir en la excepción
	 * @throws InventarioException si la referencia es nula
	 * @return el texto resultante normalizado
	 */
	public static String normalizarTexto(String texto, String mensaje) throws InventarioException {
		if (texto == null) {
			throw new InventarioException(mensaje);
		}

		texto = texto.trim();
		if (texto.length() == 0) {
			throw new InventarioException(mensaje);
		}

		return texto;
	}
}
