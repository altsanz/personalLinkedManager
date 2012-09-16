package log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Logger {

	private static Logger instance = null;
	private String filename = null;
	private FileWriter fileOut = null;
	private Calendar calendar = null;

	protected Logger() {
		// Implemented to defeat regular instantiation.
		calendar = Calendar.getInstance();

		filename = calendar.get(Calendar.DAY_OF_MONTH) + "-"
				+ calendar.get(Calendar.MONTH) + "-"
				+ calendar.get(Calendar.YEAR) + " "
				+ calendar.get(Calendar.HOUR_OF_DAY) + "_"
				+ calendar.get(Calendar.MINUTE) + "_"
				+ calendar.get(Calendar.SECOND) + ".log";

		createFile();
		try {
			fileOut = new FileWriter(filename, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public synchronized static Logger getInstance() {
		if (instance == null) {
			instance = new Logger();
		}
		return instance;
	}

	public synchronized boolean log(String message) {
		String temp = getTimestamp() + " " + message + "\n";
		try {
			System.out.print(temp);
			fileOut.write(temp);
			fileOut.flush();
			return true;
		} catch (Exception e) {
			System.out.println("Error logging.");
			e.printStackTrace();
			return false;
		}
	}

	public synchronized boolean log(StackTraceElement[] stElements) {
		String temp = null;
		try {
			for (StackTraceElement stElement : stElements) {
				temp = getTimestamp() + " " + stElement + "\n";
				System.out.print(temp);
				fileOut.write(temp);
				fileOut.flush();
			}

			return true;
		} catch (Exception e) {
			System.out.println("Error logging.");
			e.printStackTrace();
			return false;
		}
	}

	private String getTimestamp() {
		String timestamp = null;
		calendar = new GregorianCalendar();
		timestamp = "[ " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
				+ calendar.get(Calendar.MINUTE) + ":"
				+ calendar.get(Calendar.SECOND) + " ]";
		return timestamp;
	}

	private boolean createFile() {
		try {
			// Crea el archivo de log.
			File fileAux = new File(".");
			File file = new File(fileAux.getPath() + "logs\\"
					+ filename);
			if (file.createNewFile()) {
				System.out.println("Fichero log " + filename
						+ " creado con éxito.");
				return true;
			} else {
				System.out.println("El fichero " + filename
						+ " no se pudo crear.");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}
}
