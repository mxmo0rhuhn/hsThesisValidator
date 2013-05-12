/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.view;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
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
		
	}

}
