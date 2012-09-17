package core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import enums.TabletStates;
import enums.TabletTypes;

import serialization.MessageFromTablet;
import serialization.MessageToTablet;

import log.Logger;

public class OptimizedLinkedManager implements OnThreadQuery {
	private static final int PORT = 12347;
	private Logger log = null;
	private HashMap<TabletTypes, InfoTablet> relTabletInfo;

	public OptimizedLinkedManager() {
		// Inicializamos el objeto para hacer el log.
		log = Logger.getInstance();
		log.log("Arranque del programa.");

	}

	public void startManager() {
		Socket tabletSocket = null;
		ServerSocket serverSocket = null;
		TabletThread tAux = null;
		initRelTabletInfo();
		try {
			serverSocket = new ServerSocket(PORT);
			while (true) {
				log.log("Main - Esperando una petición de conexión de una tablet.");
				tabletSocket = serverSocket.accept(); // Waits for a connection
														// request
				log.log("Main - Petición de conexión recibida.");
				tAux = new TabletThread(tabletSocket);
				tAux.setListener(this);
				new Thread(tAux).start();
			}
		} catch (Exception e) {
			log.log("Main - Error en el bucle para aceptar conexiones.");
			log.log(e.getStackTrace());
		}
	}

	private void initRelTabletInfo() {
		relTabletInfo = new HashMap<TabletTypes, InfoTablet>();
		for (TabletTypes tabletType : TabletTypes.values()) {
			relTabletInfo.put(tabletType, null);
		}
		log.log("Main - Relación entre tablets y su info inicializada");
	}

	public static void main(String[] args) {
		(new OptimizedLinkedManager()).startManager();

	}

	@Override
	public synchronized MessageToTablet msgPipe(TabletThread thread,
			MessageFromTablet messageFromTabletAux) {
		try {
			InfoTablet infoTabletAux = null;
			// TODO
			switch (messageFromTabletAux.getAction()) {
			case IDENTIFICAR:
				log.log("Main - Recibido un "
						+ messageFromTabletAux.getAction() + " de "
						+ messageFromTabletAux.getColor());
				// Creamos el infoTablet que se va a asociar al color.
				infoTabletAux = new InfoTablet(
						TabletStates.DISABLED, thread);
				// Lo añadimos a la relación.
				relTabletInfo.put(messageFromTabletAux.getColor(),
						infoTabletAux);
				log.log("Main - Tablet " + messageFromTabletAux.getColor()
						+ " ha sido identificada con éxito.");
				break;
			case ACTIVAR:
				log.log("Main - Recibido un " + messageFromTabletAux.getAction() + " de "
						+ messageFromTabletAux.getColor());
				if ((infoTabletAux = relTabletInfo.get(messageFromTabletAux.getColor())) != null) {
					infoTabletAux.setState(TabletStates.WRITTING_INICI_FRASE);
					relTabletInfo.put(messageFromTabletAux.getColor(), infoTabletAux);
					log.log("Main - Tablet " + messageFromTabletAux.getColor()
						+ " ha comenzado a jugar.");
				} else {
					log.log("Main - WARNING! La acción "
							+ messageFromTabletAux.getAction()
							+ " de "
							+ messageFromTabletAux.getColor()
							+ " no se ha podido procesar porque no está identificado.");
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.log("Main - Error en msgPipe()");
			log.log(e.getStackTrace());
		}
		return null;
	}
}
