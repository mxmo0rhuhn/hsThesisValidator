package ch.zhaw.hs.thesisvalidator.view;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class HTMLFormatterTest {

	@Test
	public void shouldGenerateRightAdditionTables() {
		HTMLFormatter formy = new HTMLFormatter(null, null);
		String genHTML = formy.generateAdditionHTML(BigInteger.valueOf(6), 2);

		String returnString = "<h2>Additionstabelle</h2>";
		returnString += "<table border='1'><tr><td></td>";
		returnString += "<td><strong>E0</strong></td>";
		returnString += "<td><strong>E1</strong></td>";
		returnString += "</tr><tr><td><strong>E0</strong></td>";
		returnString += "<td>E0</td>";
		returnString += "<td>E1</td>";
		returnString += "</tr><tr><td><strong>E1</strong></td>";
		returnString += "<td>E1</td>";
		returnString += "<td>E0</td>";
		returnString += "</tr></table><hr>";

		assertEquals(genHTML, returnString);
	}
}
