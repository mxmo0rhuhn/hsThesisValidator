package ch.zhaw.hs.thesisvalidator.model;

import java.util.Iterator;

/**
 * Zerlegt alle möglichen Permutationen einer Restklasse in einzelne Stücke mit einer maximalen Grösse
 * 
 * @author Max
 * 
 */
public class ResidueIterator implements Iterator<String> {

	// Die grösse der einzelnen Stücke
	private final int offset;

	// Die Anzahl an Permutation für diese Restklasse
	private final int maxPerm;

	// Die Derzeitige Permutatuion
	private int curPerm;

	// die derzeitige Restklasse
	private final int residue;

	/**
	 * Erstellt einen neuen Restklassen-Iterator
	 * 
	 * @param residue
	 *            die Restklasse für diesen Splitter
	 */
	public ResidueIterator(int residue, int offset) {
		this.offset = offset;
		this.residue = residue;
		// n^n^n 
		maxPerm = (int) Math.pow(residue, (int) Math.pow(residue, residue));
	}

	/**
	 * Solange diese Methode true returniert, kann weiter konsumiert werden.
	 */
	@Override
	public boolean hasNext() {
		return curPerm < maxPerm;
	}

	/**
	 * Gibt das nächste tripel aus Inputwerten zurück
	 */
	@Override
	public String next() {
		if (!hasNext()) {
			return null;
		}

		if (curPerm > 0) {
			int savPerm = curPerm;
			curPerm += offset;

			// mod,start,offset
			return "" + residue + "," + savPerm + "," + offset;
		} else {
			// Das erste Stück ist speziell, da nicht davon ausgegangen werden kann, dass die Anzahl
			// effektiver Permutationen durch die Stückgrösse teilbar ist.
			curPerm = maxPerm % offset;
			
			if (curPerm == 0 ) {
				curPerm = offset;
			}
			// mod,start,offset
			return "" + residue + ",0," + curPerm;
		}
	}

	/**
	 * Dieser Iterator ist read-only.
	 */
	@Override
	public void remove() {
		throw new UnsupportedOperationException("ResidueIterator is readonly");
	}

}
