package ch.zhaw.hs.thesisvalidator.controller;

import ch.zhaw.hs.thesisvalidator.model.ThesisValidator;
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
		MapReduceFactory.getMapReduce().start();

		int startValue = 1;
		int stopValue = 1;

		switch (args.length) {
		case 2:
			try {
				startValue = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.err.println("Argument 1 must be an integer");
				System.exit(1);
			}
			try {
				stopValue = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Argument 0 must be an integer");
				System.exit(1);
			}
			new ThesisValidator(startValue, stopValue);
			break;
		case 1:
			try {
				stopValue = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				System.err.println("Argument 0 must be an integer");
				System.exit(1);
			}
			new ThesisValidator(stopValue);
			break;
		default:
			new ThesisValidator();
			break;
		}

		MapReduceFactory.getMapReduce().stop();
	}
}