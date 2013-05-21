package ch.zhaw.hs.thesisvalidator.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.zhaw.hs.thesisvalidator.model.AxiomMapper;
import ch.zhaw.mapreduce.KeyValuePair;

/**
 * @author Max
 * 
 */
public class HTMLFormatter implements Runnable {

	private final File outDirectory;
	private final Iterator<Entry<String, List<KeyValuePair>>> results;

	public HTMLFormatter(File outDirectory, Iterator<Entry<String, List<KeyValuePair>>> results) {
		this.outDirectory = outDirectory;

		// Sollte die Struktur Iterator<Entry<Restklasse , List<KeyValuePair<Restklasse,
		// Permutationen >> haben
		this.results = results;
	}

	@Override
	public void run() {

		int counter = 0;
		File outFile = null;

		if (results.hasNext()) {
			while (results.hasNext()) {

				Entry<String, List<KeyValuePair>> curEntry = results.next();

				if (counter == 0) {
					outFile = new File(outDirectory, "Restklasse" + curEntry.getKey() + ".html");
					fileWriteLn("<!DOCTYPE html>", outFile);
					fileWriteLn("<html><body> ", outFile);

				} else {
					// Darf eigentlich nicht vorkommen
					System.out.println("HILFE: Es ist mehr als eine Restklasse nach MAP Phase vorhanden");
					fileWriteLn(
							"<p><strong>HILFE: Es ist mehr als eine Restklasse nach MAP Phase vorhanden</p></strong>",
							outFile);
				}
				List<KeyValuePair> entryList = curEntry.getValue();

				for (KeyValuePair curPermutation : entryList) {
					formatPermutation(curPermutation, outFile);
				}

				counter++;
			}
			fileWriteLn("</body></html>", outFile);
		} else {

			Logger.getLogger(HTMLFormatter.class.getName()).log(Level.INFO,
					"HTMLFormatter: Sollte HTML aufbereiten aber Ergebnis der MAP-Phase war leer");
		}

	}

	/**
	 * Bereitet eine Permutation als HTML auf
	 */
	private void formatPermutation(KeyValuePair curPermutation, File outFile) {

		int iPerm = Integer.parseInt(curPermutation.getValue().split(",")[0]);
		BigInteger aPerm = new BigInteger(curPermutation.getValue().split(",")[1]);
		int neut = Integer.parseInt(curPermutation.getValue().split(",")[2]);

		int mod = Integer.parseInt(curPermutation.getKey());

		fileWriteLn("<h1>" + curPermutation.getValue() + "</h1>", outFile);

		// neutrales element
		fileWriteLn("<h2>Neutrales Element</h2>", outFile);
		fileWriteLn("<p>" + neut + "</p>", outFile);

		// Inverse
		fileWriteLn("<h2>Inverse</h2>", outFile);
		fileWriteLn("<table border='1'><tr>", outFile);

		// Header
		for (int i = 0; i < mod; i++) {
			fileWriteLn("<td><strong>E" + i + "</strong></td>", outFile);
		}
		fileWriteLn("</tr><tr>", outFile);

		// Zeile
		for (int i = 0; i < mod; i++) {
			fileWriteLn("<td>E" + AxiomMapper.map1d(i, iPerm, mod) + "</td>", outFile);
		}
		fileWriteLn("</tr></table>", outFile);

		fileWriteLn("<h2>Additionstabelle</h2>", outFile);
		fileWriteLn("<table border='1'><tr><td></td>", outFile);

		// Header
		for (int i = 0; i < mod; i++) {
			fileWriteLn("<td><strong>E" + i + "</strong></td>", outFile);
		}

		// Zeile
		for (int y = 0; y < mod; y++) {
			fileWriteLn("</tr><tr><td><strong>E" + y + "</strong></td>", outFile);

			for (int x = 0; x < mod; x++) {
				fileWriteLn("<td>E" + AxiomMapper.map2d(x, y, aPerm, mod) + "</td>", outFile);
			}
		}
		fileWriteLn("</tr></table><hr>", outFile);

	}

	public void fileWriteLn(String line, File outFile) {

		BufferedWriter curFW = null;
		try {
			try {
				curFW = new BufferedWriter(new FileWriter(outFile, true));
				curFW.write(line);
				curFW.newLine();
			} finally {
				if (curFW != null) {
					curFW.close();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
