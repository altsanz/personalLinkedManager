package core;

import java.io.BufferedWriter;
import java.io.IOException;
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

	Logger log = null;
	Socket tabletSocket = null;
	Gson gson = null;
	BufferedWriter out = null;
	
	public TabletThread(Socket socket) {
		this.tabletSocket = socket;
		log = Logger.getInstance();
	}
	
	@Override
	public void run() {
		gson = new Gson();
		log.log("Hijo - Thread lanzado con éxito");
		try {
			out = new BufferedWriter(new PrintWriter(
					tabletSocket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			
		}
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
