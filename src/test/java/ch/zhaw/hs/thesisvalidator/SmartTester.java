package ch.zhaw.hs.thesisvalidator;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SmartTester {

	@Test
	public void shouldGenerateDistinctCombinationsForMod2() {
		int n = 2;
		int elems = (int) Math.pow(Math.pow(n, n), n);
		Set<String> perms = new HashSet<String>(elems);
		for (int perm = 0; perm < elems; perm++) {
			String num = "";
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					num = SmartMapper.lookup(x, y, perm, n) + num;
				}
			}
			Assert.assertTrue(num + " was already in there", perms.add(num));
		}
	}

	@Test
	public void shouldGenerateDistinctCombinationsForMod3() {
		int n = 3;
		int elems = (int) Math.pow(Math.pow(n, n), n);
		Set<String> perms = new HashSet<String>(elems);
		for (int perm = 0; perm < elems; perm++) {
			String num = "";
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					num = SmartMapper.lookup(x, y, perm, n) + num;
				}
			}
			Assert.assertTrue(num + " was already in there", perms.add(num));
		}
	}

}
