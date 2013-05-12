package ch.zhaw.hs.thesisvalidator.controller;

import java.io.File;

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
		MapReduceFactory.getMapReduce().start();

		File outDirectory = null;
		int startValue = 1;
		int stopValue = 1;

		try {
			startValue = Integer.parseInt(System.getProperty("start"));
		} catch (Exception e) {
			startValue = 1;
		}

		try {
			outDirectory = new File(System.getProperty("path"));
			if (!outDirectory.exists()) {
                                if (!outDirectory.mkdirs()) {
                                        throw new IllegalArgumentException(outDirectory + " does not exist and is not writable.");
                                }
			} else if (!outDirectory.isDirectory()) {
                                throw new IllegalArgumentException(outDirectory + " exists but is not a directory");
                        }
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		ThesisValidator validator = new ThesisValidator(new ResidueProcessorFactory(outDirectory));
		validator.addObserver(new ConsoleObserver(outDirectory, startValue));

		try {
			stopValue = Integer.parseInt(System.getProperty("stop"));
			validator.start(startValue, stopValue);

		} catch (Exception e) {
			validator.startForever(startValue);
		}

		MapReduceFactory.getMapReduce().stop();
		System.exit(0);
	}
}
