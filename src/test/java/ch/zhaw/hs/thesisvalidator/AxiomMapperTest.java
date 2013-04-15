package ch.zhaw.hs.thesisvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;

import ch.zhaw.mapreduce.MapEmitter;

public class AxiomMapperTest {
	
	private Mockery context;
	
	@Before
	public void init() {
		this.context = new JUnit4Mockery();
	}
	
	@Test
	public void shouldEmitAllForModTwo() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("2", "0,0");
			oneOf(emitter).emitIntermediateMapResult("2", "1,0");
			oneOf(emitter).emitIntermediateMapResult("2", "3,0");
			oneOf(emitter).emitIntermediateMapResult("2", "3,3");
			oneOf(emitter).emitIntermediateMapResult("2", "6,0");
			oneOf(emitter).emitIntermediateMapResult("2", "6,3");
			oneOf(emitter).emitIntermediateMapResult("2", "7,3");
			oneOf(emitter).emitIntermediateMapResult("2", "9,0");
			oneOf(emitter).emitIntermediateMapResult("2", "9,3");
			oneOf(emitter).emitIntermediateMapResult("2", "15,3");
		}});
		mapper.map(emitter, "2,0,16");
		this.context.assertIsSatisfied();
	}
	
	@Test
	public void shouldOnlyYieldResultsFromRange() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("2", "9,0");
			oneOf(emitter).emitIntermediateMapResult("2", "9,3");
			oneOf(emitter).emitIntermediateMapResult("2", "15,3");
		}});
		mapper.map(emitter, "2,8,8");
		this.context.assertIsSatisfied();
	}
	
	@Test
	public void shouldOnlyYieldResultsFromRange2() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("2", "7,3");
		}});
		mapper.map(emitter, "2,7,1");
		this.context.assertIsSatisfied();
	}
	
	@Test
	public void shouldOnlyYieldResultsFromRange3() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("2", "0,0");
			oneOf(emitter).emitIntermediateMapResult("2", "1,0");
			oneOf(emitter).emitIntermediateMapResult("2", "3,0");
			oneOf(emitter).emitIntermediateMapResult("2", "3,3");
		}});
		mapper.map(emitter, "2,0,6");
		this.context.assertIsSatisfied();
	}

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
		assertTrue(mapper.inverse(2, 6, 0));
	}
	
	@Test
	public void shouldSatisfyAssociativityAxiomForStandardModulo2() {
		AxiomMapper mapper = new AxiomMapper();
		// ich nehme an, die fuenfte permutation ist die intuitive additionstabelle der restklasse 2
		assertTrue(mapper.associative(2, 6));
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
	public void shouldReadOffset() {
		AxiomMapper mapper = new AxiomMapper();
		assertEquals(1, mapper.readOffset("3,2,1"));
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotAcceptGarbageOffset() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.readOffset("1,2,a");
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
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptExceedingOffset() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.map(null, "2,0,17");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptExceedingOffset2() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.map(null, "2,1,16");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptExceedingOffset3() {
		AxiomMapper mapper = new AxiomMapper();
		mapper.map(null, "2,16,1");
	}
	
	@Test
	public void shouldAcceptNotExceedingOffset() {
		AxiomMapper mapper = new AxiomMapper();
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		this.context.checking(new Expectations() {{ 
			oneOf(emitter).emitIntermediateMapResult("2", "15,3");
		}});
		mapper.map(emitter, "2,16,0");
		mapper.map(emitter, "2,15,1");
	}

}
