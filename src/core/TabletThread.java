package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import log.Logger;
import serialization.MessageToTablet;


/**
 * Thread that has a socket associated and controls its inputs and outputs.
 * 
 * @author alejandro
 * 
 */
public class TabletThread implements Runnable {

	private static final int BUFFER_LENGTH = 1024;

	Logger log = null;
	Socket tabletSocket = null;
	Gson gson = null;
	BufferedReader in = null;
	BufferedWriter out = null;
	char bRecv[] = null;
	StringBuffer sb = null;



	
	public TabletThread(Socket socket) {
		this.tabletSocket = socket;
		log = Logger.getInstance();
	}
	
	@Override
	public void run() {
		String inputLine = null;
		gson = new Gson();
		log.log("Hijo - Thread lanzado con éxito");
		try {
			out = new BufferedWriter(new PrintWriter(
					tabletSocket.getOutputStream()));
			inputLine = readData(tabletSocket);
		} catch (IOException e) {
			log.log(e.getStackTrace());
		}
	}
	
	/**
	 * Reads data from the socket and return a string with the JSon.
	 * 
	 * Needs a fix. When data received is bigger than buffer, read() don't
	 * finishes reading and when another JSon message is received, both of them
	 * are processed at the same time and causes Json deserialization to fail.
	 * 
	 * @param tabletSocket
	 *            Socket connected to the tablet.
	 * @return String - Prepared to be JSonized.
	 */
	public String readData(Socket tabletSocket) {
		int numBytesRecv = 0;
		String inputLine = new String();
		bRecv = new char[BUFFER_LENGTH];
		try {
			in = new BufferedReader(new InputStreamReader(
					tabletSocket.getInputStream()));
			sb = new StringBuffer(); // Buffer where concatenate Strings
										// received
			while ((numBytesRecv = in.read(bRecv)) == BUFFER_LENGTH) {
				sb.append(bRecv);
			}
			if (numBytesRecv != -1)
				sb.append(bRecv, 0, numBytesRecv);
		} catch (IOException e) {
			log.log("Hijo - Error en readData()");
			log.log(e.getStackTrace());
		}
		inputLine = sb.toString();
		log.log("Hijo - Mensaje recibido:\n" + inputLine);
		return inputLine;
	}
	
	
	public void sendData(MessageToTablet messageToTablet) {
		String outputLine = new String();
		try {
			// Produces JSon String
			outputLine = gson.toJson(messageToTablet);

			// Sends data
			out.write(outputLine.toCharArray());
			out.newLine();
			out.flush();
			
			log.log("Hijo - Enviado" + messageToTablet);
		} catch (IOException e) {
			log.log(e.getStackTrace());
			e.printStackTrace();
		}
	}
}
