package ch.zhaw.hs.thesisvalidator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


/**
 * Diese Klasse stellt eine Ausgabe in einem eigenen Fenster dar. 
 * 
 * @author Max Schrimpf
 */
@SuppressWarnings("serial")
// Wird nicht Serialisiert
public class ConsoleOutput extends JFrame {
	private final JTextArea textArea;
	private final JPanel panel;

	/**
	 * Erstellt ein neues Konsolen-Fenster, welches ab sofort die Konsolenausgabe darstellt.
	 */
	public ConsoleOutput() {
		this.textArea = new JTextArea();
		this.textArea.setEditable(false);


        this.textArea.setFont(new Font( Font.MONOSPACED, Font.PLAIN, 18 ));
		this.panel = new JPanel(new BorderLayout());
		this.panel.add(new JScrollPane(this.textArea), BorderLayout.CENTER);

		add(this.panel);

		setResizable(true);
		setLocationRelativeTo(null);
		setTitle("Log");
		setSize(new Dimension(800, 300));
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	/**
	 * Schreibt Text in die Text Area des Frames.
	 * 
	 * @param text
	 *            Der Text der geschrieben werden soll.
	 */
	public void println(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			/** {@inheritDoc} */
			@Override
			public void run() {
				ConsoleOutput.this.textArea.append(text + "\n");
			}
		});
	}
}