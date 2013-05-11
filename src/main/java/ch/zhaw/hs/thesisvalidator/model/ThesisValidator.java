/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.model;

/**
 * @author Max
 * 
 */
public class ThesisValidator {

	/**
	 * Erstellt ein neues, bis zur manuellen beendigung laufendes Programm.
	 */
	public ThesisValidator() {
		while (true) {
			int i = 1;
			compute(i);
			i++;
		}
	}

	/**
	 * Erstellt ein neues, bis zu einem gewissen stopwert laufendes Programm.
	 * 
	 * @param stopValue
	 *            der Wert bei dem die Berechnung gestoppt werden soll.
	 */
	public ThesisValidator(int stopValue) {
		this(1, stopValue);
	}

	/**
	 * Erstellt ein neues ab einem gewissen Wert bis zu einem Stopwert laufendes Programm.
	 * 
	 * @param startValue
	 *            der Wert, bei dem die Berechnung gestartet werden soll.
	 * @param stopValue
	 *            der Wert, bei dem die Berechnung gestoppt werden soll.
	 */
	public ThesisValidator(int startValue, int stopValue) {
		for (int i = startValue; i <= stopValue; i++) {
			compute(i);
		}
	}

	private void compute(int i) {
		// TODO do the magic

	}
}
