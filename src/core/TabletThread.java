package core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import log.Logger;
import serialization.MessageFromTablet;
import serialization.MessageToTablet;

import com.google.gson.Gson;

import enums.TabletActions;
import enums.TabletTypes;

/**
 * Thread that has a socket associated and controls its inputs and outputs.
 * 
 * @author alejandro
 * 
 */
public class TabletThread implements Runnable {

	private static final int BUFFER_LENGTH = 1024;

	private static final long DELAY_BETWEEN_GAMES = 17000;

	private Logger log = null;
	private Socket tabletSocket = null;
	private Gson gson = null;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private char bRecv[] = null;
	private StringBuffer sb = null;
	private OnThreadQuery listener;
	private TabletTypes idTablet = null;
	
	public TabletThread(Socket socket) {
		this.tabletSocket = socket;
		log = Logger.getInstance();
	}

	public void setListener(OnThreadQuery listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		MessageFromTablet messageFromTabletAux = null;
		MessageFromTablet lightsOffMessageFromTabletAux = null;
		gson = new Gson();
		log.log("Hijo - Thread lanzado con éxito");
		try {
			in = new BufferedReader(new InputStreamReader(
					tabletSocket.getInputStream()));
			out = new BufferedWriter(new PrintWriter(
					tabletSocket.getOutputStream()));
			while (true) {
				// Reads data
				messageFromTabletAux = readData(tabletSocket);
				if (messageFromTabletAux == null) break;
				listener.msgPipe(this, messageFromTabletAux);
				if (messageFromTabletAux.getAction() == TabletActions.ENVIAR_COMPLETA) {
					try {
						log.log("Hijo - Thread " + messageFromTabletAux.getColor() + " se encuentra en Stand By");
						Thread.sleep(DELAY_BETWEEN_GAMES);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					lightsOffMessageFromTabletAux = new MessageFromTablet();
					lightsOffMessageFromTabletAux.setAction(TabletActions.APAGA_LLUMS);
					lightsOffMessageFromTabletAux.setColor(messageFromTabletAux.getColor());
					listener.msgPipe(this, lightsOffMessageFromTabletAux);
				}
				// Gives data to the main thread
			}

		} catch (IOException e) {
			log.log(e.getStackTrace());
		} catch ( NullPointerException e2 ) {
			log.log(e2.getStackTrace());
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
	public MessageFromTablet readData(Socket tabletSocket) {
		int numBytesRecv = 0;
		MessageFromTablet messageFromTabletAux = null;
		String inputLine = new String();
		bRecv = new char[BUFFER_LENGTH];
		try {
			sb = new StringBuffer(); // Buffer where concatenate Strings
										// received
			while ((numBytesRecv = in.read(bRecv)) == BUFFER_LENGTH) {
				sb.append(bRecv);
			}
			if (numBytesRecv != -1)
				sb.append(bRecv, 0, numBytesRecv);
		
			inputLine = sb.toString();
			
			log.log("Hijo - Mensaje recibido:\n" + inputLine);
			// Deserialize the String received
			messageFromTabletAux = gson.fromJson(inputLine,
					MessageFromTablet.class);
			idTablet = messageFromTabletAux.getColor();
			log.log("Hijo - Mensaje deserializado correctamente:");
			log.log(messageFromTabletAux.toString());
			return messageFromTabletAux;
		} catch (IOException e) {
			MessageFromTablet byeByeFromTablet = new MessageFromTablet();
			byeByeFromTablet.setColor(idTablet);
			byeByeFromTablet.setAction(TabletActions.ERROR);
			listener.msgPipe(this, byeByeFromTablet);
			log.log("Hijo - Error en readData()");
			log.log(e.getStackTrace());
			try {
				log.log("Hijo - Cerrando streams/socket.");
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (tabletSocket != null)
					tabletSocket.close();
			} catch (IOException e1) {
				log.log("Hijo - Error cerrando streams/socket");
				e1.printStackTrace();
				System.exit(0);
				return null;
			}
			
		} catch ( NullPointerException e2) {
			log.log("Hijo - NullPointerException, cerrando socket.");
			log.log(e2.getStackTrace());
			return null;
		}
		return	messageFromTabletAux;
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
		} catch (NullPointerException e) {
			log.log("Hijo - NullPointerException");
			log.log(e.getStackTrace());
		}
	}
}
