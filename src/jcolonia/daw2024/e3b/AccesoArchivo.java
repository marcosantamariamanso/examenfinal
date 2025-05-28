package jcolonia.daw2024.e3b;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Archivo para importar o exportar texto.
 * 
 * @version 1.1 (20240502000)
 * @author <a href="mailto:dmartin.jcolonia@gmail.com">David H. Martín</a>
 */
public class AccesoArchivo {
	/** El nombre o ruta al archivo. */
	private String rutaArchivo;

	/**
	 * Recoge el nombre o ruta al archivo.
	 * 
	 * @param rutaArchivo el texto correspondiente
	 */
	public AccesoArchivo(String rutaArchivo) {
		this.rutaArchivo = rutaArchivo;
	}

	/**
	 * Realiza el volcado de una lista de líneas de texto al archivo. Si el archivo
	 * existe lo sobreescribe con el nuevo contenido. En caso de producirse algún
	 * error de acceso se envía el mensaje a la salida de error estándar y el
	 * programa continua.
	 * 
	 * @param listaTextos el texto a escribir, línea a línea
	 * @return si se ha completado la operación
	 */
	public boolean escribir(List<String> listaTextos) {
		VistaGeneral.mostrarAviso("PENDIENTE: programador ocupado…");
		return false;
	}

	/**
	 * Crea y carga una lista de líneas de texto con el contenido del archivo.. En
	 * caso de producirse algún error de acceso se envía el mensaje a la salida de
	 * error estándar y el programa continua.
	 * 
	 * @return la colección de líneas de texto; <code>null<code> en caso de error de
	 *         acceso o si el archivo está vacío
	 */
	public List<String> leer() {
		File refArchivo;
		List<String> listaTextos;
		String línea;

		boolean finArchivo = false;

		refArchivo = new File(rutaArchivo);
		try (FileReader fr = new FileReader(refArchivo, Charset.forName("UTF-8"));
				BufferedReader in = new BufferedReader(fr);) {
			listaTextos = new ArrayList<String>();

			do {
				línea = in.readLine();

				if (línea != null) {
					listaTextos.add(línea);
				} else {
					finArchivo = true;
				}
			} while (!finArchivo);

			if (listaTextos.size() == 0) {
				System.err.printf("Error de importación: archivo «%s» vacío%n", rutaArchivo);
				listaTextos = null;
			}
		} catch (IOException ex) {
			System.err.printf("Error de importación: %s%n", ex.getLocalizedMessage());
			listaTextos = null;
		}
		return listaTextos;
	}
}
