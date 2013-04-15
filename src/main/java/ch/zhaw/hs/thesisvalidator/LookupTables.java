package ch.zhaw.hs.thesisvalidator;

/**
 * 
 * @author Reto Hablützel
 * 
 */
public final class LookupTables {

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
