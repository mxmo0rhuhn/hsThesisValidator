package ch.zhaw.hs.thesisvalidator.model;

import java.math.BigInteger;
import java.util.Iterator;

/**
 * Zerlegt alle möglichen Permutationen einer Restklasse in einzelne Stücke mit einer maximalen Grösse
 * 
 * @author Max
 * 
 */
public class ResidueIterator implements Iterator<String> {

	// Die grösse der einzelnen Stücke
	private final BigInteger offset;

	// Die Anzahl an Permutation für diese Restklasse
	private final BigInteger maxPerm;

	// Die Derzeitige Permutatuion
	private BigInteger curPerm;

	// die derzeitige Restklasse
	private final int residue;

	/**
	 * Erstellt einen neuen Restklassen-Iterator
	 * 
	 * @param residue
	 *            die Restklasse für diesen Splitter
	 */
	public ResidueIterator(int residue, int offset) {
		this.offset = new BigInteger("" + offset);
		this.residue = residue;
		this.curPerm = new BigInteger("" + 0);
		// n^n^n 
		maxPerm = AxiomMapper.calculateMaxPermutations(residue);
	}

	/**
	 * Solange diese Methode true returniert, kann weiter konsumiert werden.
	 */
	@Override
	public boolean hasNext() {
		return curPerm.compareTo(maxPerm) < 0;
	}

	/**
	 * Gibt das nächste tripel aus Inputwerten zurück
	 */
	@Override
	public String next() {
		if (!hasNext()) {
			return null;
		}

		if (curPerm.compareTo(BigInteger.ZERO) > 0) {
			BigInteger savPerm = new BigInteger(curPerm.toString());
			System.out.println("curPerm: " + curPerm);
			System.out.println("offset: " + offset);
			curPerm = curPerm.add(offset);
			System.out.println("curPerm after: " + curPerm);

			// mod,start,offset
			return "" + residue + "," + savPerm + "," + offset;
		} else {
			// Das erste Stück ist speziell, da nicht davon ausgegangen werden kann, dass die Anzahl
			// effektiver Permutationen durch die Stückgrösse teilbar ist.
			curPerm = maxPerm.mod(offset);
			
			if (curPerm.compareTo(BigInteger.ZERO) == 0 ) {
				curPerm = new BigInteger("" + offset);
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
