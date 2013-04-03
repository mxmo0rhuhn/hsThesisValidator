package ch.zhaw.hs.thesisvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

public class AxiomMapperTest {

	@Test
	public void shouldFindAllNeutralElementsInMod2() {
		AxiomMapper mapper = new AxiomMapper();
		Map<Integer, Integer> neutrals = mapper.findNeutrals(2);
		assertEquals(2, neutrals.size());
		assertTrue(neutrals.containsKey(0));
		assertEquals(0, (int) neutrals.get(0));
		assertTrue(neutrals.containsKey(3));
		assertEquals(1, (int) neutrals.get(3));
	}

	@Test
	public void shouldNotConsiderDoubleNeutralElementsForSamePerm() {
		// in der dritten permutation der restgruppe 2 bildet 0 auf 0 und 1 auf 1 ab. also gaebe es zwei inverse.
		// deshalb gibt es keine.
		AxiomMapper mapper = new AxiomMapper();
		Map<Integer, Integer> neutrals = mapper.findNeutrals(2);
		assertFalse(neutrals.containsKey(2));
	}

	@Test
	public void shouldSatisfyInverseAxiomForStandardModulo2() {
		AxiomMapper mapper = new AxiomMapper();
		// ich nehme an, die fuenfte permutation ist die intuitive additionstabelle der restklasse 2
		assertTrue(mapper.inverse(2, 5, 0));
	}

	@Test
	public void shouldReadModulo() {
		AxiomMapper mapper = new AxiomMapper();
		assertEquals(3, mapper.readModulo("3,2,1"));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotAcceptGarbageModulo() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.readModulo("a,2,3");
	}

	@Test
	public void shouldReadNumPerm() {
		AxiomMapper mapper = new AxiomMapper();
		assertEquals(1, mapper.readNumPerm("3,2,1"));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotAcceptGarbageNumPerm() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.readNumPerm("1,2,a");
	}

	@Test
	public void shouldReadStartPerm() {
		AxiomMapper mapper = new AxiomMapper();
		assertEquals(2, mapper.readStartPerm("3,2,1"));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotAcceptGarbageStartPerm() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.readStartPerm("1,a,3");
	}

}
