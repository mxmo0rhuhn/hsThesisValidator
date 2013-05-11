/**
 * 
 */
package ch.zhaw.hs.thesisvalidator;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.zhaw.hs.thesisvalidator.model.ResidueIterator;

/**
 * @author Max
 *
 */
public class ResidueIteratorTest {

	// Anz < offset
	@Test
	public void numberOfResultsLowerThanOffset() {
		// Restklasse 2 hat 16 verschiedene Permutationen
		ResidueIterator toTest = new ResidueIterator(2, 20);
		
		assertTrue(toTest.hasNext());
		assertEquals("2,0,16", toTest.next());
		assertFalse(toTest.hasNext());
	}
	
	// anz = offset
	@Test
	public void numberOfResultsEqualsOffset() {
		// Restklasse 2 hat 16 verschiedene Permutationen
		ResidueIterator toTest = new ResidueIterator(2, 16);
		
		assertTrue(toTest.hasNext());
		assertEquals("2,0,16", toTest.next());
		assertFalse(toTest.hasNext());
	}
	
	// anz > offset
	@Test
	public void numberOfResultsOneLowerThanOffset() {
		// Restklasse 2 hat 16 verschiedene Permutationen
		ResidueIterator toTest = new ResidueIterator(2, 15);
		
		assertTrue(toTest.hasNext());
		assertEquals("2,0,1", toTest.next());
		assertTrue(toTest.hasNext());
		assertEquals("2,1,15", toTest.next());
		assertFalse(toTest.hasNext());
	}
	
	@Test
	public void manyNextIterations() {
		// Restklasse 2 hat 16 verschiedene Permutationen
		ResidueIterator toTest = new ResidueIterator(2, 2);
		
		for (int i = 0; i < 16; i = i + 2) {
			assertTrue(toTest.hasNext());
			assertEquals("2,"+ i +",2", toTest.next());
		}
		assertFalse(toTest.hasNext());
	}
	
	@Test
	public void manyNextIterationsOffsetNotMatchingNumberOfPermutations(){
		// Restklasse 2 hat 16 verschiedene Permutationen
		ResidueIterator toTest = new ResidueIterator(2, 3);
		
		assertTrue(toTest.hasNext());
		assertEquals("2,0,1", toTest.next());
		
		for (int i = 1; i < 16; i = i + 3) {
			assertTrue(toTest.hasNext());
			assertEquals("2,"+ i +",3", toTest.next());
		}
		
		assertFalse(toTest.hasNext());
	}
}
