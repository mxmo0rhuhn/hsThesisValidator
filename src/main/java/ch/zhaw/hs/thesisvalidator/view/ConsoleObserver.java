/**
 * 
 */
package ch.zhaw.hs.thesisvalidator.view;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Max
 * 
 */
public class ConsoleObserver implements Observer {

	private final DateFormat fmt = new SimpleDateFormat( "hh:mm:ss:SS" );
	private final File outFile;
	private final ConsoleOutput outConsole;

	public ConsoleObserver(File outDirectory) {
		outFile = new File(outDirectory, "log.txt");
		outConsole = new ConsoleOutput();
		
		printStreams("HSThesisValidator - Software Projekt 2 - ZHAW FS 2013 - Reto Hablützel & Max Schrimpf");
		redirectSystemStreams();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable o, Object arg) {
		Map<String, List<String>> results = (Map<String, List<String>>) arg;
		
		printStreams("----------------------------------------------------------------");
		if (results.size() == 0) {
			printStreams("Satz war in allen Fällen gültig");
		} else {
			// sollte NIE vorkommen
			// im ssh sind die values zu einem Key durch space getrennt
		}
		
	}
	
    /**
     * Leitet die Standard Nachrichten Streams auf dieses Fenster um.
     */
    private void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            public void write(int b) throws IOException {
            	printStreams(String.valueOf((char) b));
            }

            public void write(byte[] b, int off, int len) throws IOException {
            	printStreams(new String(b, off, len));
            }

            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }
        };

        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(out, true));
    }

    public void printStreams(String line) {
    	String tsd = fmt.format(Calendar.getInstance().getTime());
    	
    	outConsole.println(tsd + " " + line);
    	fileWriteLn(tsd + " " + line);
    }

	public void fileWriteLn(String line) {
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
