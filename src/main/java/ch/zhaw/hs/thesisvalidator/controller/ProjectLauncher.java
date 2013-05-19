package ch.zhaw.hs.thesisvalidator.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import ch.zhaw.hs.thesisvalidator.model.ResidueProcessorFactory;
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
		File outDirectory = null;

		// Wert, bei dem die Berechnung startet
		int startValue = 1;
		
		// Wert, bei dem die Berechnung stoppt. wenn der wert <= 0 ist, wird die Berechnung endlos ausgeführt
		int stopValue = 0;

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("mapreduce.properties"));

			startValue = Integer.parseInt(prop.getProperty("start"));
			stopValue = Integer.parseInt(prop.getProperty("stop"));
			path = prop.getProperty("path");

		} catch (IOException e) {
			// konnten nicht geladen werden - weiter mit oben definierten defaults
		}

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

		ThesisValidator validator = new ThesisValidator(new ResidueProcessorFactory(outDirectory));
		validator.addObserver(new ConsoleObserver(outDirectory, startValue));

		if (stopValue > 0) {
			validator.start(startValue, stopValue);

		} else {
			validator.startForever(startValue);
		}

		MapReduceFactory.getMapReduce().stop();
		System.exit(0);
	}
}
