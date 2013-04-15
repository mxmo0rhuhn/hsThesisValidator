package ch.zhaw.hs.thesisvalidator;

import java.util.Iterator;

import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ReduceEmitter;
import ch.zhaw.mapreduce.ReduceInstruction;

/**
 * Der Reducer für die Axiome reduziert die gefundenen Gruppen auf diejenigen, für die einige bestimmte Sätze
 * <b>nicht</b> zutreffen.
 * 
 * @author Reto Hablützel
 * 
 */
public class AxiomReducer implements ReduceInstruction {

	/**
	 * Für jeweils eine Restgruppe (sMod) werden alle Sätze für die Gruppen validiert. Wenn ein Satz für eine Gruppe
	 * nicht zutrifft, wird diese Gruppe emittiert. Die Gruppen werden durch den Permutationsindex identifiziert,
	 * welcher durch den Algorithmus in den Lookup-Tables gegeben ist.
	 * 
	 * Die Werte KeyValuePairs sind die Indizes der Inversen- und Additions-Permutationen als kommasepariertes Tupel.
	 * z.B. für den Inversen-Index 3 und den Additions-Index 10:
	 * 
	 * <pre>
	 * &quot;3,10&quot;
	 * </pre>
	 * 
	 * Der Schlüssel der KeyValuePairs ist die Restklasse.
	 * 
	 * @param emitter
	 *            Emitter für die Gruppen, für die die Sätze nicht gelten
	 * @param sMod
	 *            Restklasse
	 * @param groups
	 *            alle Permutationen, die laut dem {@link AxiomMapper} eine Gruppe sind
	 * 
	 * @see LookupTables
	 */
	@Override
	public void reduce(ReduceEmitter emitter, String sMod, Iterator<KeyValuePair> groups) {
		while (groups.hasNext()) {
			KeyValuePair group = groups.next();
			int aPerm = readAPerm(group.getValue());
			int mod = Integer.parseInt(sMod);
			sentences: for (int a = 0; a < mod; a++) {
				for (int b = 0; b < mod; b++) {
					for (int c = 0; c < mod; c++) {
						if (!cancellation(mod, aPerm, a, b, c)) {
							emitter.emit(group.getValue());
							break sentences;
						}
					}
				}
			}
		}
	}

	/**
	 * Prüft für eine Restklasse (mod) und eine Permutation (perm), ob der Satz der Auslöschung (rechts und links) für
	 * drei Elemente gilt (a, b, c). Dabei ist die Rechts- bzw. Linksauslöschung folgendermassen definiert:
	 * 
	 * <pre>
	 * b * a == c * a ==> b == c
	 * a * b == a * c ==> b == c
	 * </pre>
	 * 
	 * @param mod
	 *            Restklasse
	 * @param perm
	 *            Index der Permutation (n-te Permutationstabelle)
	 * @param a
	 *            erstes Element
	 * @param b
	 *            zweites Element
	 * @param c
	 *            drittes Element
	 * @return true, wenn der Satz gilt, sonst false
	 */
	public boolean cancellation(int mod, int perm, int a, int b, int c) {
		// left cancellation
		int ab = LookupTables.map2d(a, b, perm, mod);
		int ac = LookupTables.map2d(a, c, perm, mod);
		if (ab == ac && b != c) {
			return false;
		}
		// right cancellation
		int ba = LookupTables.map2d(b, a, perm, mod);
		int ca = LookupTables.map2d(c, a, perm, mod);
		if (ba == ca && b != c) {
			return false;
		}
		return true;
	}

	/**
	 * Liest die Additions-Permutation aus dem Input-String für den Reducer. Der Input String ist ein Tupel aus
	 * Inversen-Index und Additions-Index. Beides müssen Zahlen im Dezimalsystem sein!
	 * 
	 * @param value
	 *            String-Encodiertes Tupel
	 * @return den Inversen-Index
	 */
	public int readAPerm(String value) {
		return Integer.parseInt(value.split(",")[1]);
	}
}
