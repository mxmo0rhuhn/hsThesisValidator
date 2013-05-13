package ch.zhaw.hs.thesisvalidator.model;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import ch.zhaw.mapreduce.MapEmitter;
import ch.zhaw.mapreduce.MapInstruction;

/**
 * Map-Instruktion zur Prüfung der Gültigkeit. Nach Dokumentation vom Software Projekt 2 (thesis-validator) werden für
 * jede Map-Instruktion für eine bestimmte Restklasse und eine bestimmte Menge an Permutationen die Gruppen-Axiome
 * geprüft. Die Map-Instruktion ermittelt dabei die Permutationen, für die diese Axiome gelten.
 * 
 * @author Reto Hablützel
 * 
 */
public class AxiomMapper implements MapInstruction {
	
	/**
	 * Gibt zu einer restklasse die Anzahl an möglichen Permutationen zurück
	 * @param residue die Restklasse
	 * @return die anzahl mödlicher Permutationen
	 */
	public static int calculateMaxPermutations(int residue) {
		// Diese Formel ist eine Annahme... 
		return (int) Math.pow(residue, (int) Math.pow(residue, residue));
	}
	
	private static final Logger LOG = Logger.getLogger(AxiomMapper.class.getName());

	/**
	 * 
	 * Die Map-Funktion extrahiert aus dem input-String die Menge der zu prüfenden Permutationen und die zugehörige
	 * Restklasse. Dabei muss der input-String in folgendem Format aufgebaut sein:
	 * 
	 * <pre>
	 * mod,start,offset
	 * </pre>
	 * 
	 * Der erste Teil (mod) wird als Restklasse interpretiert, der zweite (start) als erste Permutation und der letzte
	 * (offset) als die Anzahl der Permutationen. Beispiel: Map Instruktion soll die ersten beiden Permutationen der
	 * Restklasse 2 auf die Gruppenaxiome überprüfen:
	 * 
	 * <pre>
	 * input = &quot;2;0;2&quot;
	 * </pre>
	 * 
	 * Dabei wird die Nummer der Additions-Wertetabelle/Permutation sowie die Nummer der Inversen-Wertetabelle emitted
	 * mit dem Schlüssel 'mod' (Restgruppe). Beispiel: die Additions-Wertetablle mit der Nummer 6 und die
	 * Inversen-Wertetabelle mit der Nummer 3 ist eine Gruppe in der Restklasse 2:
	 * 
	 * <pre>
	 * emit(&quot;2&quot;, &quot;6,3&quot;)
	 * </pre>
	 * 
	 * @param input
	 *            Beschreibung der zu prüfenden Permutationen
	 */
	@Override
	public void map(MapEmitter emitter, String input) {
		LOG.entering(getClass().getName(), "map", new Object[]{emitter, input});
		final int modulo = readModulo(input);
		final int startPerm = readStartPerm(input);
		final int offset = readOffset(input);

		int maxPerms = calculateMaxPermutations(modulo);
		if (startPerm + offset > maxPerms) {
			throw new IllegalArgumentException("Number of Permutations: " + (startPerm + offset) + " > " + maxPerms);
		}

		// neutrales element pro inversen-permutation
		final Map<Integer, Integer> neutrals = findNeutrals(modulo);

		for (int perm = 0; perm < offset; perm++) {
			int aPerm = perm + startPerm; // additions permutation

			for (Map.Entry<Integer, Integer> ipn : neutrals.entrySet()) {
				int iPerm = ipn.getKey(); // inversen permutation
				int e = ipn.getValue(); // neutrales element

				if (checkAxioms(modulo, aPerm, e)) {
					emitter.emitIntermediateMapResult(Integer.toString(modulo), aPerm + "," + iPerm + "," + e);
				}
			}
		}
	}

	/**
	 * Liefert ein Mapping von Inversen-Permutation mit dem jeweiligen neutralen Element.
	 * 
	 * @param modulo
	 *            für welche Restklasse sollen neutrale Elemente gesucht werden
	 */
	public Map<Integer, Integer> findNeutrals(final int modulo) {
		Map<Integer, Integer> invPermNeutrals = new HashMap<Integer, Integer>();
		int perms = (int) Math.pow(modulo, modulo);
		outer: for (int perm = 0; perm < perms; perm++) {
			for (int a = 0; a < modulo; a++) {
				int i = map1d(a, perm, modulo);
				if (i == a) {
					if (invPermNeutrals.put(perm, i) != null) {
						// Eine Gruppe kann max. ein Neutrales haben
						invPermNeutrals.remove(perm);
						continue outer;
					}
				}
			}
		}
		return invPermNeutrals;
	}

	/**
	 * Prüft die beiden Axiome (es gibt ein Inverses, die Addition ist assoziativ) für eine bestimmte Restklasse
	 * (modulo) für die perm-te Permutation und ein vorbestimmtest neutrales Element.
	 * 
	 * @param modulo
	 *            Restklasse
	 * @param perm
	 *            perm-te Permutation
	 * @param neutral
	 *            neutrales Element
	 * @return flase, wenn mindestens eines der Axiome nicht für sämtliche Elemente gilt. Sonst true.
	 */
	public boolean checkAxioms(final int modulo, final int perm, final int neutral) {
		return neutral(modulo, perm, neutral) && inverse(modulo, perm, neutral) && associative(modulo, perm);
	}

	/**
	 * Prüft, ob das neutrale Element die Axiome, welches für das neutrale Element gelten müssen, gilt.
	 * 
	 * @param modulo
	 *            Restklasse
	 * @param perm
	 *            perm-te Permutation
	 * @param e
	 *            vermeindlich neutrales Element
	 * @return true, wenn das neutrale Element wirklich das neutrale ist
	 */
	public boolean neutral(final int modulo, final int perm, final int e) {
		// Es gibt ein e für das gilt: Es gibt ein a für das gilt: a * e = e * a = a
		for (int a = 0; a < modulo; a++) {
			int b = map2d(a, e, perm, modulo);
			if (a != b) {
				return false;
			}
			int c = map2d(e, a, perm, modulo);
			if (a != c) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prüft, ob es für die Restklasse (modulo) für die perm-te Permutation für jedes Element ein Inverses gibt, welches
	 * auf das Neutrale abbildet.
	 * 
	 * @param modulo
	 *            Restklasse
	 * @param perm
	 *            Nummer der Permutation
	 * @param neutral
	 *            neutrales Element
	 * @return false, wenn mindestens eines der Elemente kein Invereses hat
	 */
	public boolean inverse(final int modulo, final int perm, final int neutral) {
		/*
		 * Es sei a ein Element aus der Restklasse 'modulo'. Dann suchen wir ein b, welches verknüpft mit a auf das
		 * neutrale Element abbildet. Dann gilt b als das inverse Element von a. Wir müssen ein solches b für jedes a
		 * finden.
		 */
		outer: for (int a = 0; a < modulo; a++) {
			boolean found = false;
			for (int b = 0; b < modulo; b++) {
				int i = map2d(a, b, perm, modulo);
				if (i == neutral) {
					found = true;
					continue outer;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Prüft, ob die perm-te Permutation der Restklasse 'modulo' bezüglich der Addition/einer zweistelligen Verknüpfung
	 * assoziativ ist.
	 * 
	 * @param modulo
	 *            Restklasse
	 * @param perm
	 *            perm-te Permutation
	 * @return false, wenn das Assoziativgesetz für mindestens eines nicht gilt. true wenn es für alle gilt.
	 */
	public boolean associative(final int modulo, final int perm) {
		/*
		 * Folgende Gleichung muss für sämtliche a, b und c aus der Restklasse 'modulo' der perm-ten Permutation gelten:
		 * a * (b * c) = (a * b) * c
		 */
		for (int a = 0; a < modulo; a++) {
			for (int b = 0; b < modulo; b++) {
				for (int c = 0; c < modulo; c++) {
					// rechter teil der obigen Gleichung
					int right = map2d(b, c, perm, modulo);
					right = map2d(a, right, perm, modulo);

					// linker teil der obigen Gleichung
					int left = map2d(a, b, perm, modulo);
					left = map2d(left, c, perm, modulo);

					if (left != right) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * Liest die Restklasse aus dem Inpput-String. Siehe Map-Funktion für eine Beschreibung des Pattern.
	 * 
	 * @param input
	 *            ganzer eingabestring
	 * 
	 * @see AxiomMapper#map(MapEmitter, String)
	 */
	public int readModulo(String perms) {
		return Integer.parseInt(perms.split(",")[0]);
	}

	/**
	 * Liest die StartPerm aus dem Inpput-String. Siehe Map-Funktion für eine Beschreibung des Pattern.
	 * 
	 * @param input
	 *            ganzer eingabestring
	 * 
	 * @see AxiomMapper#map(MapEmitter, String)
	 */
	public int readStartPerm(String perms) {
		return Integer.parseInt(perms.split(",")[1]);
	}

	/**
	 * Liest die Anzahl der Permutatione aus dem Inpput-String. Siehe Map-Funktion für eine Beschreibung des Pattern.
	 * 
	 * @param input
	 *            ganzer eingabestring
	 * 
	 * @see AxiomMapper#map(MapEmitter, String)
	 */
	public int readOffset(String perms) {
		return Integer.parseInt(perms.split(",")[2]);
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
	public static int map2d(int x, int y, int perm, int n) {
		// convert to n-number system
		String repr = Integer.toString(perm, n);
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
	 * This function essentially does the same as the map2d function, but for one dimensional lookup tables.
	 * 
	 * @param x
	 *            index of x axis
	 * @param perm
	 *            permutation number
	 * @param n
	 *            number of elements in each table. must be less than 11.
	 * @return the element at the specified x position of the perm-th permutation.
	 */
	public static int map1d(int x, int perm, int n) {
		// see the 2d mapping function. it's the same idea
		String repr = Integer.toString(perm, n);
		return x >= repr.length() ? 0 : repr.charAt(repr.length() - 1 - x) - 48;
	}
}