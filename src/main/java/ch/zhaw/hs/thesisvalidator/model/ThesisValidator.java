/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import ch.zhaw.mapreduce.MapReduce;
import ch.zhaw.mapreduce.MapReduceFactory;

/**
 * @author Max
 * 
 */
public class ThesisValidator extends Observable{

	private final MapReduce computer;
	// Ein die Grösse der einzelnen zu berechnenden Teile
	private static final int OFFSET = 10000;
	
	public ThesisValidator() {
		computer = MapReduceFactory.getMapReduce().newMRTask(new AxiomMapper() , new AxiomReducer(), null, configureMRTask());
	}

	/**
	 * Berechnet ein gewisses Intervall an Restklassen
	 * 
	 * @param startValue
	 *            der Wert, bei dem die Berechnung gestartet werden soll.
	 * @param stopValue
	 *            der Wert, bei dem die Berechnung gestoppt werden soll.
	 */
	public void start(int startValue, int stopValue) {
		for (int i = startValue; i <= stopValue; i++) {
			compute(i);
		}
	}

	/**
	 * Berechnet bis zur manuellen Beendigung 
	 * @param startValue 
	 */
	public void startForever(int startValue) {
		while (true) {
			int i = startValue;
			compute(i);
			i++;
		}
	}

	private Map<String, String> configureMRTask() {
		
		Map<String, String> myConfig = new HashMap<String, String>();
//		myConfig.put("rescheduleStartPercentage", "intValue");
//		myConfig.put("rescheduleEvery", "intValue");
//		myConfig.put("waitTime", "intValue");
		
//		... könnten hier eingestellt werden
//		Defaults sind:
//		rescheduleStartPercentage = 90%
//		rescheduleEvery = 10 durchläufe
//		waitTime = 1000 millisekunden
 
		return myConfig;
	}
	
	private void compute(int residue) {
		Map<String, String> results = computer.runMapReduceTask(new ResidueIterator(residue, OFFSET));
		
	    super.setChanged(); 
	    super.notifyObservers(results); 
	}
}
