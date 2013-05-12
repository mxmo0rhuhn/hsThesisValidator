/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.model;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import ch.zhaw.hs.thesisvalidator.view.HTMLFormatter;
import ch.zhaw.mapreduce.KeyValuePair;
import ch.zhaw.mapreduce.ShuffleProcessorFactory;

/**
 * @author Max
 *
 */
public class ResidueProcessorFactory implements ShuffleProcessorFactory {

	private final File outDirectory;
	
	public ResidueProcessorFactory(File outDirectory) {
		this.outDirectory = outDirectory;
	}

	/* (non-Javadoc)
	 * @see ch.zhaw.mapreduce.ShuffleProcessorFactory#getNewRunnable(java.util.Iterator)
	 */
	@Override
	public Runnable getNewRunnable(Iterator<Entry<String, List<KeyValuePair>>> results) {
		return new HTMLFormatter(outDirectory, results);
	}

}
