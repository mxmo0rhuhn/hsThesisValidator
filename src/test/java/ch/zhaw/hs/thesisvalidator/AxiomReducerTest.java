package ch.zhaw.hs.thesisvalidator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Iterator;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

import ch.zhaw.hs.thesisvalidator.model.AxiomReducer;
import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ReduceEmitter;

public class AxiomReducerTest {

	@Rule
	public JUnitRuleMockery mockery = new JUnitRuleMockery();

	@Mock
	private ReduceEmitter emitter;
	
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
	
	@Test
	public void shouldEmitForNonGroupInMod2() {
		AxiomReducer reducer = new AxiomReducer();
		mockery.checking(new Expectations() {
			{
				oneOf(emitter).emit("x,0");
			}
		});
		reducer.reduce(emitter, "2", i(2, 0));
	}

	@Test
	public void shouldNotEmitForGroupMod2() {
		AxiomReducer reducer = new AxiomReducer();
		mockery.checking(new Expectations() {
			{
				never(emitter).emit(with(any(String.class)));
			}
		});
		reducer.reduce(emitter, "2", i(2, 6));
	}

	@Test
	public void shouldNotEmitForGroupMod3() {
		AxiomReducer reducer = new AxiomReducer();
		mockery.checking(new Expectations() {
			{
				never(emitter).emit(with(any(String.class)));
			}
		});
		reducer.reduce(emitter, "3", i(3, 63118));
		reducer.reduce(emitter, "3", i(3, 63118));
		reducer.reduce(emitter, "3", i(3, 70502));
		reducer.reduce(emitter, "3", i(3, 73050));
		reducer.reduce(emitter, "3", i(3, 82801));
		reducer.reduce(emitter, "3", i(3, 90185));
		reducer.reduce(emitter, "3", i(3, 92733));
		reducer.reduce(emitter, "3", i(3, 102484));
		reducer.reduce(emitter, "3", i(3, 109868));
		reducer.reduce(emitter, "3", i(3, 112416));
		reducer.reduce(emitter, "3", i(3, 122167));
		reducer.reduce(emitter, "3", i(3, 129551));
	}

	@Test
	public void cancellationLawHoldsForIntuitiveAdditionOnMod3() {
		AxiomReducer reducer = new AxiomReducer();
		int mod = 3;
		BigInteger perm = BigInteger.valueOf(4069); // intuitive additionstabelle (d.h. 1+2 = 0, 1+1=2, etc)
		for (int a = 0; a < mod; a++) {
			for (int b = 0; b < mod; b++) {
				for (int c = 0; c < mod; c++) {
					assertTrue(reducer.cancellation(mod, perm, a, b, c));
				}
			}
		}
	}

	@Test
	public void cancellationLawHoldsForIntuitiveAdditionOnMod2() {
		AxiomReducer reducer = new AxiomReducer();
		int mod = 2;
		BigInteger perm = BigInteger.valueOf(6); // intuitive additionstabelle (d.h. 1+0 = 1, 1+1=0, etc)
		for (int a = 0; a < mod; a++) {
			for (int b = 0; b < mod; b++) {
				for (int c = 0; c < mod; c++) {
					assertTrue(reducer.cancellation(mod, perm, a, b, c));
				}
			}
		}
	}

	@Test(expected = RuntimeException.class)
	public void shouldNotAcceptGarbageInput() {
		new AxiomReducer().readAPerm("9,x");
	}

	@Test
	public void shouldParseAxiomPerm() {

		assertEquals(NINE, new AxiomReducer().readAPerm("9,9"));
		assertEquals(NINE.add(ONE), new AxiomReducer().readAPerm("9,10"));
	}

	private Iterator<KeyValuePair> i(final int mod, final int... aperms) {
		return new Iterator<KeyValuePair>() {

			int index = 0;

			@Override
			public boolean hasNext() {
				return index < aperms.length;
			}

			@Override
			public KeyValuePair next() {
				return new KeyValuePair("" + mod, "x," + aperms[index++]);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

}
