package ch.zhaw.hs.thesisvalidator.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.zhaw.hs.thesisvalidator.model.MAPResultHTMLFormatterFactory;
import ch.zhaw.hs.thesisvalidator.model.ThesisValidator;
import ch.zhaw.hs.thesisvalidator.view.ConsoleObserver;
import ch.zhaw.mapreduce.MapReduceFactory;

/**
 * Diese Klasse startet die hsThesis validator Applikation,
 * 
 * @author Max
 * 
 */
public class ProjectLauncher {
	
	private static final Logger LOG = Logger.getLogger(ProjectLauncher.class.getName());

	/**
	 * Startet die Applikation und ruft die benötigten Aufgaben
	 * 
	 * @param args
	 *            Wenn ein Parameter übergeben wird, ein fix definierter Stop-Wert. Wenn zwei
	 *            Parameter übergeben werden: Erster Wert fix definierter Stop-Wert, zweiter Wert
	 *            fest definierter Start-Wert.
	 */
	public static void main(String[] args) {
		new ProjectLauncher();
	}

	public ProjectLauncher() {

		// Pfad, an dem die Output Files gespeichert werden
		String path = System.getProperty("java.io.tmpdir");
        String gui = "no";
		File outDirectory = null;

		// Wert, bei dem die Berechnung startet
		int startValue = 1;
		
		// Wert, bei dem die Berechnung stoppt. wenn der wert <= 0 ist, wird die Berechnung endlos ausgeführt
		int stopValue = -1;
		
		int offset = 10000;

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("hsThesisValidator.properties"));
			
			startValue = Integer.parseInt(prop.getProperty("start"));
			stopValue = Integer.parseInt(prop.getProperty("stop"));
			path = prop.getProperty("path");
            gui = prop.getProperty("gui", "no");

			offset = Integer.parseInt(prop.getProperty("permoffset"));

		} catch (IOException e) {
			// konnten nicht geladen werden - weiter mit oben definierten defaults
		}
		
		LOG.log(Level.INFO, "ThesisValidator Config: Start={0}, Stop={1}, Path={2}, Offset={3}", new Object[]{startValue, stopValue, path, offset});

		try {
			outDirectory = new File(path);
			if (!outDirectory.exists()) {
				if (!outDirectory.mkdirs()) {
					throw new IllegalArgumentException(outDirectory
							+ " does not exist and is not writable.");
				}
			} else if (!outDirectory.isDirectory()) {
				throw new IllegalArgumentException(outDirectory + " exists but is not a directory");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		MapReduceFactory.getMapReduce().start();

		ThesisValidator validator = new ThesisValidator(new MAPResultHTMLFormatterFactory(outDirectory), offset);
        if(gui.equals("yes")) {
            validator.addObserver(new ConsoleObserver(outDirectory, startValue, true));
        } else {
            validator.addObserver(new ConsoleObserver(outDirectory, startValue, false));
        }

		if (stopValue > 0) {
			validator.start(startValue, stopValue);

		} else {
			validator.startForever(startValue);
		}

		MapReduceFactory.getMapReduce().stop();
		System.exit(0);
	}
}
