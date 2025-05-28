package jcolonia.daw2024.e3b;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Aplicación de ventanas con una única ventana principal y una caja de texto
 * central.
 * 
 * @version 09.08 (20250525000)
 * @author <a href="dmartin.jcolonia@gmail.com">David H. Martín</a>
 */
public class VentanaVisorBase {

	/** El panel general exterior. */
	private JFrame ventanaVisor;
	/** El panel general exterior. */
	private JPanel panelExterior;
	/** El panel situado en la zona central para ponerle borde y título. */
	private JPanel panelEtiqueta;
	/** El panel de contenido central con márgenes. */
	private JPanel panelBorde;
	/** El panel para el texto central, con barras deslizantes. */
	private JScrollPane panelDeslizante;
	/** El área de texto multiusos central. */
	private JTextArea cajaTexto;
	/** El panel inferior, para los botones principales. */
	private JPanel panelBotones;
	/** El botón de cancelar. */
	private JButton botónCancelar;
	/** El botón de cancelar. */
	private JButton botónAceptar;

	/**
	 * Lanza la aplicación. Establece la apariencia general de la ventana y registra
	 * el lanzador.
	 * 
	 * @param argumentos Opciones en línea de órdenes –no se usan–.
	 */
	public static void main(String[] argumentos) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(() -> {
			try {
				VentanaVisorBase window = new VentanaVisorBase();
				window.ventanaVisor.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/** Crea la aplicación e inicializa los componentes de la ventana. */
	public VentanaVisorBase() {
		initComponents();
	}

	/** Inicializa los componentes de la ventana. */
	private void initComponents() {
		ventanaVisor = new JFrame();
		ventanaVisor.setTitle("Visor Base");
		ventanaVisor.setBounds(100, 100, 450, 300);
		ventanaVisor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventanaVisor.getContentPane().add(getPanelExterior(), BorderLayout.CENTER);
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el panel general exterior.
	 * 
	 * @return el panel indicado
	 */
	private JPanel getPanelExterior() {
		if (panelExterior == null) {
			panelExterior = new JPanel();
			panelExterior.setBorder(new EmptyBorder(10, 10, 10, 10));
			panelExterior.setLayout(new BorderLayout(10, 10));
			panelExterior.add(getPanelEtiqueta(), BorderLayout.CENTER);
			panelExterior.add(getPanelBotones(), BorderLayout.SOUTH);
		}
		return panelExterior;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el panel situado en la
	 * zona central para ponerle borde y título «Resultados».
	 * 
	 * @return el panel indicado
	 */
	private JPanel getPanelEtiqueta() {
		if (panelEtiqueta == null) {
			panelEtiqueta = new JPanel();
			panelEtiqueta.setBorder(new TitledBorder(null, " Contenidos ", TitledBorder.LEADING, TitledBorder.TOP, null,
					new Color(59, 59, 59)));
			panelEtiqueta.setLayout(new BorderLayout(0, 0));
			panelEtiqueta.add(getPanelBorde(), BorderLayout.CENTER);
		}
		return panelEtiqueta;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el panel de contenido
	 * central con márgenes.
	 * 
	 * @return el panel indicado
	 */
	private JPanel getPanelBorde() {
		if (panelBorde == null) {
			panelBorde = new JPanel();
			panelBorde.setBorder(new EmptyBorder(5, 5, 5, 5));
			panelBorde.setLayout(new BorderLayout(0, 0));
			panelBorde.add(getPanelDeslizante(), BorderLayout.CENTER);
		}
		return panelBorde;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el panel para la caja de
	 * texto multiusos, con barras deslizantes para desplazarse por el contenido.
	 * 
	 * @return el panel indicado
	 */
	private JScrollPane getPanelDeslizante() {
		if (panelDeslizante == null) {
			panelDeslizante = new JScrollPane();
			panelDeslizante.setViewportView(getCajaTexto());
		}
		return panelDeslizante;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el área de texto multiusos
	 * central.
	 * 
	 * @return el área de texto indicada
	 */
	private JTextArea getCajaTexto() {
		if (cajaTexto == null) {
			cajaTexto = new JTextArea();
			cajaTexto.setFocusable(false);
			cajaTexto.setEditable(false);
		}
		return cajaTexto;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el panel inferior, para
	 * los botones principales.
	 * 
	 * @return el panel indicado
	 */
	private JPanel getPanelBotones() {
		if (panelBotones == null) {
			panelBotones = new JPanel();
			GroupLayout gl_panelBotones = new GroupLayout(panelBotones);
			gl_panelBotones.setHorizontalGroup(gl_panelBotones.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_panelBotones.createSequentialGroup().addContainerGap(268, Short.MAX_VALUE)
							.addComponent(getBotónCancelar()).addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(getBotónAceptar()).addContainerGap()));
			gl_panelBotones.setVerticalGroup(gl_panelBotones.createParallelGroup(Alignment.TRAILING)
					.addGroup(gl_panelBotones.createSequentialGroup().addContainerGap(14, Short.MAX_VALUE)
							.addGroup(gl_panelBotones.createParallelGroup(Alignment.BASELINE)
									.addComponent(getBotónAceptar()).addComponent(getBotónCancelar()))
							.addContainerGap()));
			panelBotones.setLayout(gl_panelBotones);
		}
		return panelBotones;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el botón «cancelar», abajo
	 * a la derecha.
	 * 
	 * @return el botón indicado
	 */
	private JButton getBotónCancelar() {
		if (botónCancelar == null) {
			botónCancelar = new JButton("Cancelar");
			botónCancelar.setMnemonic(KeyEvent.VK_C);
		}
		return botónCancelar;
	}

	/**
	 * Localiza –o inicializa si no se ha creado todavía– el botón «aceptar», abajo
	 * a la izquierda del resto de botones.
	 * 
	 * @return el botón indicado
	 */
	private JButton getBotónAceptar() {
		if (botónAceptar == null) {
			botónAceptar = new JButton("Aceptar");
			botónAceptar.setMnemonic(KeyEvent.VK_A);
		}
		return botónAceptar;
	}
}
