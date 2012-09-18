package dataBase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import log.Logger;

public class IniciFrasesDB {

	List<String> listIniciFrase = null;
	String fileName = null;
	Logger log = null;

	/**
	 * Constructor Sets the fileName of the text file where iniciFrases are
	 * stored and retrieves all the sentences.
	 * 
	 * @param fileName
	 */
	public IniciFrasesDB(String fileName) {
		this.fileName = fileName;
		listIniciFrase = new LinkedList<String>();
		String auxLine = new String();
		BufferedReader bf = null;
		log = Logger.getInstance();
		try {
			FileReader fr = new FileReader(fileName);
			bf = new BufferedReader(fr);
			while ((auxLine = bf.readLine()) != null) {
				listIniciFrase.add(auxLine);
			}
			log.log("BBDD - Singleplayer iniciFrases cargadas correctamente del fichero " + fileName + ".");
		} catch (FileNotFoundException e) {
			log.log("BBDD - Error obteniendo los iniciFrases de la BBDD.");
			log.log("BBDD - \"" + fileName + "\" no encontrado.");
		} catch (IOException e) {
			log.log("BBDD - Error obteniendo los iniciFrases de la BBDD.");
			log.log(e.getStackTrace());
		} finally {
			try {
				if (bf != null)
					bf.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Gets a number of elements from a List. Elements are chosen randomly but
	 * are all different.
	 * 
	 * @param numInicis
	 *            Number of sentences requested
	 * @return List with a total of numInicis elements from main List. If
	 *         numInicis > listIniciFrase.size() returns Null.
	 */
	public List<String> getRandomIniciFrases(int numInicis) {
		if (numInicis > listIniciFrase.size()) {
			log.log("BBDD - ERROR! El número de iniciFrase's reclamado es mayor que el número que hay en la BBDD");
			return null;
		} else {
			Random randomGenerator = new Random();
			int randomInt = 0;
			List<String> auxListIniciFrase = new LinkedList<String>();
			List<Integer> alreadyUsed = new LinkedList<Integer>();

			for (int i = 0; i < numInicis; i++) {
				randomInt = randomGenerator.nextInt(listIniciFrase.size());
				while (alreadyUsed.contains(randomInt)) {
					randomInt = randomGenerator.nextInt(listIniciFrase.size());
				}
				alreadyUsed.add(randomInt);
				auxListIniciFrase.add(listIniciFrase.get(randomInt));
			}
			log.log("BBDD - Lista de inicifrase's aleatorias retornada con éxito.");
			return auxListIniciFrase;
		}
	}

	public void addIniciFraseToDB(String iniciFrase) {
		FileWriter fileOut = null;
		try {
			fileOut = new FileWriter(fileName, true);
			fileOut.write("\n"+iniciFrase);
			fileOut.flush();
			listIniciFrase.add(iniciFrase);
			log.log("BBDD - \""+iniciFrase+"\" añadida con éxito a " + fileName + " y a la lista en memoria.");
		} catch (Exception e) {
			log.log("BBDD - Error al añadir \""+iniciFrase+"\" al fichero o a la memoria");
			e.printStackTrace();
		}
	}
}
