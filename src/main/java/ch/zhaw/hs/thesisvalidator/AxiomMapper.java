package ch.zhaw.hs.thesisvalidator;

import java.util.HashMap;
import java.util.Map;

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
		final int modulo = readModulo(input);
		final int startPerm = readStartPerm(input);
		final int offset = readOffset(input);

		int maxPerms = (int) Math.pow(modulo, Math.pow(modulo, modulo));
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
					emitter.emitIntermediateMapResult(Integer.toString(modulo), aPerm + "," + iPerm);
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
				int i = LookupTables.map1d(a, perm, modulo);
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
		return inverse(modulo, perm, neutral) && associative(modulo, perm);
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
				int i = LookupTables.map2d(a, b, perm, modulo);
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
					int right = LookupTables.map2d(b, c, perm, modulo);
					right = LookupTables.map2d(a, right, perm, modulo);

					// linker teil der obigen Gleichung
					int left = LookupTables.map2d(a, b, perm, modulo);
					left = LookupTables.map2d(left, c, perm, modulo);

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

}
