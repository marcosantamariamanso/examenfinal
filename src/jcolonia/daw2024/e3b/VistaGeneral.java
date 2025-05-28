package jcolonia.daw2024.e3b;

import static java.lang.System.out;

import java.util.Scanner;

/**
 * Utilidades básicas para interface en consola de texto.
 * 
 * @version 1.2 (2025426000)
 * @author <a href="mailto:dmartin.jcolonia@gmail.com">David H. Martín</a>
 */
public class VistaGeneral {
	/** Secuencia de escape ANSI para activar el color rojo. */
	public static final String ANSI_ROJO = "\33[31;1m";
	/** Secuencia de escape ANSI para restablecer la visualización normal. */
	public static final String ANSI_NORMAL = "\33[m";

	/**
	 * Interface ({@link java.util.Scanner Scanner}) asociado a la entrada por
	 * consola de texto –entrada estándar–.
	 */
	private static Scanner scEntrada;

	/** Nombre o título identificativo de la vista. */
	private String nombre;

	/**
	 * Almacena el nombre o título.
	 * 
	 * @param nombre el nombre o título del menú
	 */
	public VistaGeneral(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * Genera una línea con guiones «-» o iguales «=» de la misma longitud que el
	 * texto facilitado. La línea resultante se puede utilizar como subrayado del
	 * texto referido.
	 * 
	 * @param texto    el texto de referencia
	 * @param realzado indicando si el carácter a utilizar será «=» (para
	 *                 <code>true</code>) o «-» (para <code>false</code>)
	 * @return el texto generado
	 */
	public static String generarLínea(String texto, boolean realzado) {
		String línea;
		String relleno = realzado ? "=" : "-";

		// A: Composición manual
		// StringBuffer línea = new StringBuffer();
		// for (int i = 0; i < texto.length(); i++) {
		// línea.append(relleno);
		// }

		// B: API Java, String...
		línea = relleno.repeat(texto.length());

		return línea;
	}

	/**
	 * Genera varias líneas de texto con el texto facilitado integrado dentro de un
	 * marco simulado con caracteres de texto.
	 * 
	 * @param texto el texto de referencia
	 * @return el texto generado
	 */
	public static String generarMarco(String texto) {
		StringBuffer marco = new StringBuffer();
		String líneaArribaAbajo, líneaTexto;

		// String líneaHueco;
		// líneaHueco = String.format("|%s|", " ".repeat(2 + texto.length()));

		líneaArribaAbajo = String.format("+%s+", "-".repeat(2 + texto.length()));
		líneaTexto = String.format("| %s |", texto);

		marco.append(líneaArribaAbajo);
		marco.append("\n");
		// marco.append(líneaHueco);
		// marco.append("\n");
		marco.append(líneaTexto);
		marco.append("\n");
		// marco.append(líneaHueco);
		// marco.append("\n");
		marco.append(líneaArribaAbajo);
		marco.append("\n");
		return marco.toString();
	}

	/**
	 * Muestra el nombre o título, con subrayado sencillo.
	 */
	public void mostrarTítulo() {
		out.println();
		out.println(nombre);
		out.println(generarLínea(nombre, false));
	}

	/**
	 * Muestra el nombre o título, con subrayado doble.
	 */
	public void mostrarTítuloPrincipal() {
		String línea = generarLínea(nombre, true);

		out.println();
		out.println(línea);
		out.println(nombre);
		out.println(línea);
	}

	/**
	 * Facilita un interface ({@link java.util.Scanner Scanner}) único vinculado a
	 * la entrada estándar. La unicidad se consigue con un patrón al estilo
	 * «Singleton».
	 * 
	 * @return el gestor correspondiente
	 */
	public static Scanner getScEntrada() {
		if (scEntrada == null) {
			scEntrada = new Scanner(System.in);
		}
		return scEntrada;
	}

	/**
	 * Permite cerrar el interface ({@link java.util.Scanner Scanner}) único
	 * vinculado a la entrada estándar. De no estar este definido no hace nada.
	 */
	public static void close() {
		if (scEntrada != null) {
			scEntrada.close();
		}
	}

	/** Envía a la salida una línea vacía, a modo de separador. */
	public static void dejarEspacio() {
		out.println();
	}

	/**
	 * Envía directamente a la salida un texto sin ningún añadido más que el salto
	 * de línea final.
	 * 
	 * @param texto el texto deseado
	 */
	public static void mostrarTexto(String texto) {
		out.println(texto);
	}

	/**
	 * Envía a la salida un texto destacado en color rojo, a modo de aviso.
	 * 
	 * @param textoAviso el texto deseado
	 */
	public static void mostrarAviso(String textoAviso) {
		out.printf("%s*** ATENCIÓN: %s ***%s%n%n", ANSI_ROJO, textoAviso, ANSI_NORMAL);
	}

	/**
	 * Envía a la salida un texto destacado en color rojo, a modo de error.
	 * 
	 * @param textoError el texto deseado
	 */
	public static void mostrarError(String textoError) {
		out.printf("%s*** ERROR: %s ***%s%n%n", ANSI_ROJO, textoError, ANSI_NORMAL);
	}

	/**
	 * Invita a pulsar «Intro» y espera la entrada para continuar.
	 */
	public static void preguntaSeguir() {
		out.print("\nPulse «Intro» para continuar…");
		getScEntrada().nextLine();
	}

	/**
	 * Solicita una confirmación introduciendo únicamente la letra «S» en mayúscula
	 * o minúscula indistintamente. Cualquier otro texto introducido se interpreta
	 * como negativa.
	 * 
	 * @param mensaje el texto previo, pregunta a mostrar
	 * @return <code>true</code> en caso afirmativo, si no <code>false</code>.
	 */
	public static boolean pedirConfirmación(String mensaje) {
		boolean respuesta;
		String línea;

		Scanner in = getScEntrada();

		out.printf("\t%s (s/N): ", mensaje);
		línea = in.nextLine();

		respuesta = "S".equalsIgnoreCase(línea); // Solo «S» mayúscula o minúscula.

		return respuesta;
	}

	/**
	 * Pide al usuario confirmación sobre una cuestión delicada. Se exige una
	 * respuesta exacta «S» o «N» en mayúscula o minúscula indistintamente. En caso
	 * de introducir cualquier otro texto se repite la consulta; tras cinco errores
	 * se da por rechazada la cuestión.
	 * 
	 * @param txtInformación la indicación previa a mostrar, objeto de la
	 *                       confirmación
	 * @param txtPregunta    la pregunta para interrogar sobre la confirmación
	 * @return si el usuario confirma los datos
	 */
	public static boolean pedirConfirmación(String txtInformación, String txtPregunta) {
		Scanner in = getScEntrada();
		String letra;
		boolean entradaValidada = false;
		boolean respuesta = false;

		int intentosRestantes = 5;

		String pregunta = txtPregunta + " (S/N): ";
		out.println(txtInformación);

		do {
			out.print(pregunta);
			letra = in.nextLine();
			switch (letra) {
			case "S", "s":
				respuesta = true;
				entradaValidada = true;
				break;
			case "N", "n":
				respuesta = false;
				entradaValidada = true;
				break;
			default:
				intentosRestantes--;
				break;
			}
		} while (!entradaValidada && intentosRestantes > 0);

		return respuesta;
	}
}
