package ch.zhaw.hs.thesisvalidator;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.zhaw.hs.thesisvalidator.model.AxiomMapper;
import ch.zhaw.mapreduce.MapEmitter;

@RunWith(JMock.class)
public class AxiomMapperTest {
	
	static BigInteger ZERO = BigInteger.ZERO;
	static BigInteger ONE = BigInteger.ONE;
	static BigInteger TWO = ONE.add(ONE);
	static BigInteger THREE = TWO.add(ONE);
	static BigInteger FOUR = THREE.add(ONE);
	static BigInteger FIVE = FOUR.add(ONE);
	static BigInteger SIX = FIVE.add(ONE);
	static BigInteger SEVEN = SIX.add(ONE);
	static BigInteger EIGHT = SEVEN.add(ONE);
	static BigInteger NINE = EIGHT.add(ONE);
	
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
			oneOf(emitter).emitIntermediateMapResult("2", "6,1,0");
			oneOf(emitter).emitIntermediateMapResult("2", "9,1,1");
		}});
		mapper.map(emitter, "2,0,16");
	}
	
	@Test
	public void shouldEmitAllForModThree() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("3", "4069,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "11453,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "14001,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "23752,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "31136,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "33684,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "43435,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "50819,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "53367,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "63118,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "70502,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "73050,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "82801,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "90185,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "92733,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "102484,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "109868,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "112416,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "122167,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "129551,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "132099,21,1");
			oneOf(emitter).emitIntermediateMapResult("3", "141850,7,0");
			oneOf(emitter).emitIntermediateMapResult("3", "149234,11,2");
			oneOf(emitter).emitIntermediateMapResult("3", "151782,21,1");
		}});
		mapper.map(emitter, "3,0,151783");
	}
	
	@Test
	public void shouldOnlyYieldResultsFromRange() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("2", "9,3,1");
		}});
		mapper.map(emitter, "2,8,8");
	}
	
	@Test
	public void shouldOnlyYieldResultsFromRange2() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			oneOf(emitter).emitIntermediateMapResult("2", "6,0,0");
		}});
		mapper.map(emitter, "2,6,1");
	}
	
	@Test
	public void shouldOnlyYieldResultsFromRange3() {
		final MapEmitter emitter = this.context.mock(MapEmitter.class);
		AxiomMapper mapper = new AxiomMapper();
		this.context.checking(new Expectations() {{
			never(emitter);
		}});
		mapper.map(emitter, "2,0,6");
	}

	@Test
	public void shouldFindAllNeutralElementsInMod2() {
		AxiomMapper mapper = new AxiomMapper();
		Map<Integer, Integer> neutrals = mapper.findNeutrals(2, SIX);
		assertEquals(2, neutrals.size());
		assertTrue(neutrals.containsKey(0));
		assertEquals(0, (int) neutrals.get(0));
		assertTrue(neutrals.containsKey(3));
		assertEquals(1, (int) neutrals.get(3));
	}
	
	@Test
	public void shouldFindNeutralElementsInMod3() {
		AxiomMapper mapper = new AxiomMapper();
		Map<Integer, Integer> neutrals = mapper.findNeutrals(3, new BigInteger("012120201", 3));
		assertTrue("Expected size 3: " + neutrals.toString(), neutrals.size() == 3);
		assertEquals(0, (int)neutrals.get(Integer.parseInt("021", 3)));
		assertEquals(2, (int)neutrals.get(Integer.parseInt("210", 3)));
		assertEquals(1, (int)neutrals.get(Integer.parseInt("102", 3)));
	}

	@Test
	public void shouldSatisfyNeutralElementForStandardModulo2() {
		AxiomMapper mapper = new AxiomMapper();
		assertTrue(mapper.neutral(2, BigInteger.valueOf(6), 0));
	}
	
	@Test
	public void shouldNotSatisfyNotNeutralElementForStandardModulo2() {
		AxiomMapper mapper = new AxiomMapper();
		assertFalse(mapper.neutral(2, BigInteger.valueOf(6), 1));
	}
	
	@Test
	public void shouldNotSatisfyAnyNeutralInAllZeroMappingForMod2() {
		AxiomMapper mapper = new AxiomMapper();
		assertFalse(mapper.neutral(2, BigInteger.valueOf(0), 0));
		assertFalse(mapper.neutral(2, BigInteger.valueOf(0), 1));
	}

	@Test
	public void shouldSatisfyInverseAxiomForStandardModulo2() {
		AxiomMapper mapper = new AxiomMapper();
		// ich nehme an, die fuenfte permutation ist die intuitive additionstabelle der restklasse 2
		assertTrue(mapper.inverse(2, BigInteger.valueOf(6), 0));
	}
	
	@Test
	public void shouldSatisfyAssociativityAxiomForStandardModulo2() {
		AxiomMapper mapper = new AxiomMapper();
		// ich nehme an, die fuenfte permutation ist die intuitive additionstabelle der restklasse 2
		assertTrue(mapper.associative(2, BigInteger.valueOf(6)));
	}
	
	@Test
	public void shouldSatisfyNeutralElementForStandardModulo3() {
		AxiomMapper mapper = new AxiomMapper();
		assertTrue(mapper.neutral(3, BigInteger.valueOf(4069), 0));
	}

	@Test
	public void shouldNotSatisfyNotNeutralElementForStandardModulo3() {
		AxiomMapper mapper = new AxiomMapper();
		assertFalse(mapper.neutral(3, BigInteger.valueOf(4069), 1));
		assertFalse(mapper.neutral(3, BigInteger.valueOf(4069), 2));
	}
	
	@Test
	public void shouldNotSatisfyAnyNeutralInAllZeroMappingForMod3() {
		AxiomMapper mapper = new AxiomMapper();
		assertFalse(mapper.neutral(3, BigInteger.valueOf(0), 0));
		assertFalse(mapper.neutral(3, BigInteger.valueOf(0), 1));
		assertFalse(mapper.neutral(3, BigInteger.valueOf(0), 2));
	}


	@Test
	public void shouldSatisfyInverseAxiomForStandardModulo3() {
		AxiomMapper mapper = new AxiomMapper();
		assertTrue(mapper.inverse(3, BigInteger.valueOf(4069), 0));
	}
	
	@Test
	public void shouldSatisfyAssociativityAxiomForStandardModulo3() {
		AxiomMapper mapper = new AxiomMapper();
		assertTrue(mapper.associative(3, BigInteger.valueOf(4069)));
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
			never(emitter);
		}});
		mapper.map(emitter, "2,16,0");
		mapper.map(emitter, "2,15,1");
	}

}
