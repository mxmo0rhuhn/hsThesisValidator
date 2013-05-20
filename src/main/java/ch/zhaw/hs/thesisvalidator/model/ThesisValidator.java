/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.model;

import java.util.List;
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
	
	private final int permoffset;
	
	public ThesisValidator(MAPResultHTMLFormatterFactory residueProcessorFactory, int permoffset) {
		computer = MapReduceFactory.getMapReduce().newMRTask(new AxiomMapper() , new AxiomReducer(), null, residueProcessorFactory);
		this.permoffset = permoffset;
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
	public void startForever(int i) {
		while (true) {
			compute(i);
			i++;
		}
	}

	private void compute(int residue) {
//		Map<Restklasse, Liste<Permutationen>> mit Permutationen, für die der Satz nicht gilt (Erwartungswert: Keine)
		Map<String, List<String>> results = computer.runMapReduceTask(new ResidueIterator(residue, permoffset));
		
	    super.setChanged(); 
	    super.notifyObservers(results); 
	}
}
