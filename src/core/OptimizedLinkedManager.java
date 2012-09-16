package core;

import java.net.ServerSocket;
import java.net.Socket;

import log.Logger;

public class OptimizedLinkedManager {
	private static final int PORT = 12347;
	Logger log = null;

	public OptimizedLinkedManager() {
		// Inicializamos el objeto para hacer el log.
		log = Logger.getInstance();
		log.log("Arranque del programa.");

	}

	public void startManager() {
		Socket tabletSocket = null;
		ServerSocket serverSocket = null;
		TabletThread tAux = null;

		try {
			serverSocket = new ServerSocket(PORT);
			while (true) {
				log.log("Main - Esperando una petición de conexión de una tablet.");
				tabletSocket = serverSocket.accept(); // Waits for a connection
														// request
				log.log("Main - Petición de conexión recibida.");
				tAux = new TabletThread(tabletSocket);
				new Thread(tAux).start();
			}
		} catch (Exception e) {
			log.log("Main - Error en el bucle para aceptar conexiones.");
			log.log(e.getStackTrace());
		}
	}

	public static void main(String[] args) {
		(new OptimizedLinkedManager()).startManager();

	}

}
