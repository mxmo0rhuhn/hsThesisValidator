package ch.zhaw.hs.thesisvalidator;

import java.util.HashMap;
import java.util.Map;

import ch.zhaw.mapreduce.MapEmitter;
import ch.zhaw.mapreduce.MapInstruction;

public class AxiomMapper implements MapInstruction {

	@Override
	public void map(MapEmitter emitter, String perms) {
		final int modulo = readModulo(perms);
		final int startPerm = readStartPerm(perms);
		final int numPerm = readNumPerm(perms);

		// neutrales element pro inversen-permutation
		final Map<Integer, Integer> neutrals = findNeutrals(modulo);
		int perm = 0;
		while (perm < numPerm) {
			for (Map.Entry<Integer, Integer> ipn : neutrals.entrySet()) {
				int iPerm = ipn.getKey();
				int e = ipn.getValue();

				int aPerm = perm + startPerm;
				if (checkAxioms(modulo, aPerm, e)) {
					emitter.emitIntermediateMapResult(Integer.toString(modulo), aPerm + "," + iPerm);
				}
			}
			perm++;
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

	public boolean associative(final int modulo, final int perm) {
		// a * (b * c) = (a * b) * c
		for (int a = 0; a < modulo; a++) {
			for (int b = 0; b < modulo; b++) {
				for (int c = 0; c < modulo; c++) {
					int right = LookupTables.map2d(b, c, perm, modulo);
					right = LookupTables.map2d(a, right, perm, modulo);

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

	public int readModulo(String perms) {
		return Integer.parseInt(perms.split(",")[0]);
	}

	public int readStartPerm(String perms) {
		return Integer.parseInt(perms.split(",")[1]);
	}

	public int readNumPerm(String perms) {
		return Integer.parseInt(perms.split(",")[2]);
	}

}
