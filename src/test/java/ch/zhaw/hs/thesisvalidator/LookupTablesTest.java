package ch.zhaw.hs.thesisvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ch.zhaw.hs.thesisvalidator.model.AxiomMapper;

public class LookupTablesTest {

	@Test
	public void shouldCreateDistinct1DTables() {
		for(int mod = 1; mod < 7; mod++) {
			System.out.println(mod);
			int elems = (int) Math.pow(mod, mod);
			Set<String> perms = new HashSet<String>(elems);
			for (int perm = 0; perm < elems; perm++) {
				String comb = "";
				for (int x = 0; x < mod; x++) {
					comb += AxiomMapper.map1d(x, perm, mod);
				}
				assertTrue(comb + " was already in there", perms.add(comb));
			}
		}
	}
	@Test
	public void shouldGenerateDistinctCombinationsForMod2() {
		int n = 2;
		BigInteger elems = AxiomMapper.calculateMaxPermutations(n);
		Set<String> perms = new HashSet<String>();

		for (BigInteger perm = BigInteger.ZERO; perm.compareTo(elems) < 0; perm = perm.add(BigInteger.ONE)) {
			String num = "";
			for (int y = 0; y < n; y++) {
				for (int x = 0; x < n; x++) {
					num = AxiomMapper.map2d(x, y, perm, n) + num;
				}
			}
			assertTrue("Perm " + perm +  " with value " + num + " was already in there", perms.add(num));
		}
	}

	@Test
	public void shouldGenerateDistinctCombinationsForMod3() {
		int n = 3;
		
		BigInteger elems = AxiomMapper.calculateMaxPermutations(n);
		Set<String> perms = new HashSet<String>();
		
		for (BigInteger perm = BigInteger.ZERO; perm.compareTo(elems) < 0; perm = perm.add(BigInteger.ONE)) {
			String num = "";
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					num = AxiomMapper.map2d(x, y, perm, n) + num;
				}
			}
			assertTrue(num + " was already in there", perms.add(num));
		}
	}

	@Test
	public void shouldGenerateDistinctPermuatationsForAnyMod() {
		for (int n = 1; n < 7; n++) {
			int nperms = (int) Math.pow(n, n);
			Set<String> perms = new HashSet<String>(nperms);
			for (int perm = 0; perm < nperms; perm++) {
				String vals = "";
				for (int pos = 0; pos < n; pos++) {
					try {
					vals += AxiomMapper.map1d(pos, perm, n);
					} catch (Exception e) {
						e.printStackTrace();
						fail("Failed with pos = " + pos + ", perm = " + perm + ", n = " + n);
					}
				}
				assertTrue(vals + " was already in there for perm " + perm + " and n " + n, perms.add(vals));
			}
		}
	}

	@Test
	public void shouldCreateIntuitiveInverseTableAtPredictablePosition() {
		assertEquals(0, AxiomMapper.map1d(0, 1, 2));
	}

	@Test
	public void shouldCreateIntuitiveInverseTableForMod2() {
		assertEquals(1, AxiomMapper.map1d(0, 2, 2));
		assertEquals(0, AxiomMapper.map1d(1, 2, 2));
	}

	@Test
	public void shouldCreateIntuitiveAdditionTableAtPredictablePosition() {
		// intuitive additionstabelle fuer restklasse 2 ist die fuenfte permutation
		assertEquals(0, AxiomMapper.map2d(1, 1, BigInteger.valueOf(6), 2));
		assertEquals(1, AxiomMapper.map2d(1, 0, BigInteger.valueOf(6), 2));
		assertEquals(1, AxiomMapper.map2d(0, 1, BigInteger.valueOf(6), 2));
		assertEquals(0, AxiomMapper.map2d(0, 0, BigInteger.valueOf(6), 2));
	}

	@Test
	public void shouldProduceIntuitiveAdditionsTableAtPredictablePositionForMod3() {
		// intuitive additionstabelle fuer restklasse 3 (2+1=0, 1+1=2, etc), also: 012120201
		assertEquals(0, AxiomMapper.map2d(0, 0, BigInteger.valueOf(4069), 3));
		assertEquals(1, AxiomMapper.map2d(1, 0, BigInteger.valueOf(4069), 3));
		assertEquals(2, AxiomMapper.map2d(2, 0, BigInteger.valueOf(4069), 3));
		assertEquals(1, AxiomMapper.map2d(0, 1, BigInteger.valueOf(4069), 3));
		assertEquals(2, AxiomMapper.map2d(1, 1, BigInteger.valueOf(4069), 3));
		assertEquals(0, AxiomMapper.map2d(2, 1, BigInteger.valueOf(4069), 3));
		assertEquals(2, AxiomMapper.map2d(0, 2, BigInteger.valueOf(4069), 3));
		assertEquals(0, AxiomMapper.map2d(1, 2, BigInteger.valueOf(4069), 3));
		assertEquals(1, AxiomMapper.map2d(2, 2, BigInteger.valueOf(4069), 3));
	}

	@Test
	public void shouldCreateOneAsSecondTableForMod2() {
		assertEquals(1, AxiomMapper.map2d(1, 1, BigInteger.valueOf(1), 2));
		assertEquals(0, AxiomMapper.map2d(1, 0, BigInteger.valueOf(1), 2));
		assertEquals(0, AxiomMapper.map2d(0, 1, BigInteger.valueOf(1), 2));
		assertEquals(0, AxiomMapper.map2d(0, 0, BigInteger.valueOf(1), 2));
	}

	@Test
	public void shouldCreateThreeAsFourthTableForMod2() {
		assertEquals(1, AxiomMapper.map2d(1, 1, BigInteger.valueOf(3), 2));
		assertEquals(0, AxiomMapper.map2d(1, 0, BigInteger.valueOf(3), 2));
		assertEquals(1, AxiomMapper.map2d(0, 1, BigInteger.valueOf(3), 2));
		assertEquals(0, AxiomMapper.map2d(0, 0, BigInteger.valueOf(3), 2));
	}

	@Test
	public void shouldCreate2dTablesInOrder() {
		int mod = 3;
		for (BigInteger perm = BigInteger.ZERO; perm.compareTo(AxiomMapper.calculateMaxPermutations(mod)) < 0; perm = perm.add(BigInteger.ONE)) {
			String mapped = "";
			for (int y = 0; y < mod; y++) {
				for (int x = 0; x < mod; x++) {
					mapped += AxiomMapper.map2d(x, y, perm, mod);
				}
			}
			assertEquals(perm, BigInteger.valueOf(Integer.parseInt(mapped, mod)));
		}
	}

	@Test
	public void shouldCreate1dTableInOrder() {
		int mod = 3;
		for (int perm = 0; perm < Math.pow(mod, mod); perm++) {
			String mapped = "";
			for (int x = 0; x < mod; x++) {
				mapped += AxiomMapper.map1d(x, perm, mod);
			}
			assertEquals(perm, Integer.parseInt(mapped, mod));
		}
	}
}
