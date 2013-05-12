/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import ch.zhaw.mapreduce.KeyValuePair;

/**
 * @author Max
 *
 */
public class HTMLFormatter implements Runnable {

	private final File outDirectory;
	private final Iterator<Entry<String, List<KeyValuePair>>> results;
	
	public HTMLFormatter(File outDirectory, Iterator<Entry<String, List<KeyValuePair>>> results ) {
		this.outDirectory = outDirectory;
		this.results = results;
	}

	@Override
	public void run() { 
	
		fileWriteLn(results.toString(),new File(outDirectory, ""+ results.hashCode() +".txt") );
//		while (results.hasNext()) {
//			Entry<String, List<KeyValuePair>> curEntry = results.next();
//			curEntry.getKey();
//		}
		
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
