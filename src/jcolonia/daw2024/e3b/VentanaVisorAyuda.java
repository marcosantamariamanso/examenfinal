package jcolonia.daw2024.e3b;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Submenu estandar de ayuda de la ventana de venta visor con opcion de menu para los créditos.
 * @author Marco S.
 */
public class VentanaVisorAyuda extends VentanaVisorBase {
	
	/**
	 * muestra la ayuda del visor de la ventana y los créditos del programa.
	 */
	private static final String TITULO = "Ayuda de Venta Visor";
	/**
	 * muestra el contenido de la ayuda del visor de la ventana y los créditos del programa.
	 */
	private static final String CONTENIDO = """
			Este es el visor de ventas del programa.
			Desarrollado por Marco S.
			Fecha: 2025-05-25.
			Nombre del programa: Ventana Visor.
			
			Para más información, consulte la documentación oficial o contacte con el soporte técnico.
			""";
	/**
	 * muestra la ayuda en una ventana emergente.
	 */
	public static void mostrarAyuda() {
		JTextArea textArea = new JTextArea(CONTENIDO);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		JOptionPane.showMessageDialog(null, scrollPane, TITULO, JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * Muestra los créditos del programa en una ventana emergente.
	 */
	public static void mostrarCreditos() {
		String creditos = """
				Desarrollado por Marco S.
				Fecha: 2025-05-25.
				Nombre del programa: Ventana Visor.
				""";
		JOptionPane.showMessageDialog(null, creditos, "Créditos", JOptionPane.INFORMATION_MESSAGE);
	}
	/**
	 * Main que ejecuta el flujo principal del programa.
	 * @param args
	 */
	public static void main(String[] args) {
		mostrarAyuda();
		mostrarCreditos();
	}
	

}
