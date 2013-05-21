package ch.zhaw.hs.thesisvalidator.model;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

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

	private static final Logger LOG = Logger.getLogger(AxiomReducer.class.getName());

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
			BigInteger aPerm = readAPerm((String) group.getValue());
			int mod = Integer.parseInt(sMod);
			sentences: for (int a = 0; a < mod; a++) {
				for (int b = 0; b < mod; b++) {
					for (int c = 0; c < mod; c++) {
						if (!cancellation(mod, aPerm, a, b, c)) {
							emitter.emit((String) group.getValue());
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
	public boolean cancellation(int mod, BigInteger perm, int a, int b, int c) {
		// left cancellation
		int ab = map2d(a, b, perm, mod);
		int ac = map2d(a, c, perm, mod);
		if (ab == ac && b != c) {
			logL(mod, perm, a, b, c, ab, ac);
			return false;
		}
		// right cancellation
		int ba = map2d(b, a, perm, mod);
		int ca = map2d(c, a, perm, mod);
		if (ba == ca && b != c) {
			logR(mod, perm, a, b, c, ba, ca);
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
	public BigInteger readAPerm(String value) {
		return new BigInteger(value.split(",")[1]);
	}

	/**
	 * This function generates elements of lookup tables. It is assumed that we have two dimensional lookup tables with
	 * x and y axis each ranging from 0 to n (exclusive). For n, this means there are n^n^n possible permutations. <br />
	 * Example table (n = 3, perm = 1)
	 * 
	 * <pre>
	 *        x
	 *      0 1 2
	 *     ------
	 *   0| 0 0 0
	 * y 1| 0 0 0
	 *   2| 0 0 1
	 * </pre>
	 * 
	 * If this was the first permutation, all values would be zeros. The idea of the algorithm is to look at these
	 * tables as a sequence of numbers. The above example would therefore translate to the following sequence:
	 * '0000001'. If we now look at all possible permutations of zeros, ones and twos of this sequence, it should be
	 * obvious that these are nothing else but the ternary representation of all numbers ranging from 0 to n^n^n.
	 * 
	 * @param x
	 *            index of x axis
	 * @param y
	 *            index of y axis
	 * @param perm
	 *            permutation number
	 * @param n
	 *            number of elements in each table. must be less than 11.
	 * @return the element at the specified x and y position of the perm-th permutation.
	 */
	public static int map2d(int x, int y, BigInteger perm, int n) {
		// convert to n-number system
		String repr = perm.toString(n);
		// repr is only as long as it has to be, that is, it contains no leading zeros
		int pos = n * n - (x + n * y + 1);
		// the repr string is only as long as necessary. everything else are zeros
		if (pos >= repr.length()) {
			return 0;
		}
		// LSB is on the right side
		pos = repr.length() - pos - 1;
		return repr.charAt(pos) - 48;
	}

	/**
	 * Loggt den Term.
	 */
	private void logL(int mod, BigInteger perm, int a, int b, int c, int ab, int ac) {
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.log(Level.FINEST,
					"Left Cancellation Law not satisfied! Mod={0}, A-Perm={1}, a={2}, b={3}, ab={4}, ac={5}",
					new Object[] { mod, perm, a, b, c, ab, ac });
		}
	}

	/**
	 * Loggt den Term.
	 */
	private void logR(int mod, BigInteger perm, int a, int b, int c, int ab, int ac) {
		if (LOG.isLoggable(Level.FINEST)) {
			LOG.log(Level.FINEST,
					"Right Cancellation Law not satisfied! Mod={0}, A-Perm={1}, a={2}, b={3}, ba={4}, ca={5}",
					new Object[] { mod, perm, a, b, c, ab, ac });
		}
	}
}
