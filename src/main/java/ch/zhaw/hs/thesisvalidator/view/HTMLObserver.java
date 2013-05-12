/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.view;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * @author Max
 *
 */
public class HTMLObserver implements Observer {

	public HTMLObserver(File outDirectory) {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		pool.execute(new Runnable() {
			public void run() {
				doSomething();			
			}

		});
	}

}
