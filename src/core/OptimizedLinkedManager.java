package core;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import printer.PrinterManager;

import dataBase.IniciFrasesDB;

import enums.TabletActions;
import enums.TabletStates;
import enums.TabletTypes;

import serialization.MessageFromTablet;
import serialization.MessageToTablet;

import log.Logger;

public class OptimizedLinkedManager implements OnThreadQuery {
	private static final int PORT = 12347;
	private static final int NUM_INICI_FRASES_SP = 4;
	private Logger log = null;
	private HashMap<TabletTypes, InfoTablet> relTabletInfo;
	private IniciFrasesDB iniciFrasesDB = null;
	private PrinterManager printerManager = null;

	public OptimizedLinkedManager() {
		// Inicializamos el objeto para hacer el log.
		log = Logger.getInstance();
		log.log("Arranque del programa.");
		printerManager = new PrinterManager();

	}

	public void startManager() {
		Socket tabletSocket = null;
		ServerSocket serverSocket = null;
		TabletThread tAux = null;
		initRelTabletInfo();
		printerManager.cfgPrinters();
		iniciFrasesDB = new IniciFrasesDB("iniciFrasesDB.txt");
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
			MessageToTablet messageToTabletAux = new MessageToTablet();
			switch (messageFromTabletAux.getAction()) {
			case IDENTIFICAR:
				log.log("Main - Recibido un "
						+ messageFromTabletAux.getAction() + " de "
						+ messageFromTabletAux.getColor());
				// Creamos el infoTablet que se va a asociar al color.
				infoTabletAux = new InfoTablet(TabletStates.DISABLED, thread);
				// Lo añadimos a la relación.
				relTabletInfo.put(messageFromTabletAux.getColor(),
						infoTabletAux);
				log.log("Main - Tablet " + messageFromTabletAux.getColor()
						+ " ha sido identificada con éxito.");
				break;
			case ACTIVAR:
				log.log("Main - Recibido un "
						+ messageFromTabletAux.getAction() + " de "
						+ messageFromTabletAux.getColor());
				if ((infoTabletAux = relTabletInfo.get(messageFromTabletAux
						.getColor())) != null) {
					infoTabletAux.setState(TabletStates.WRITTING_INICI_FRASE);
					relTabletInfo.put(messageFromTabletAux.getColor(),
							infoTabletAux);
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
			case ENVIAR_INICI:
				log.log("Main - Recibido un "
						+ messageFromTabletAux.getAction() + " de "
						+ messageFromTabletAux.getColor());
				messageToTabletAux.setAction(TabletActions.ENVIAR_INICI);
				// Se crea la infoTablet que se va a asociar al tablet que envia
				// el paquete
				infoTabletAux = new InfoTablet(
						TabletStates.COMPLETED_INICI_FRASE, thread);
				infoTabletAux.setIniciFrase(messageFromTabletAux
						.getIniciFrase());

				// Se crea infoTablet temporal para ir almacenando los
				// infoTablets de las tablets restantes.
				InfoTablet tempInfoTablet = null;
				for (TabletTypes tabletType : TabletTypes.values()) {
					if (tabletType != messageFromTabletAux.getColor()
							&& relTabletInfo.get(tabletType) != null) {
						tempInfoTablet = relTabletInfo.get(tabletType);
						if (tempInfoTablet.getState() == TabletStates.WRITTING_INICI_FRASE) {
							infoTabletAux.addTabletsJugando(tabletType);
							messageToTabletAux.addFrases(tabletType, "null");
						} else if (tempInfoTablet.getState() == TabletStates.COMPLETED_INICI_FRASE) {
							if (tempInfoTablet.getTabletsJugando().contains(
									messageFromTabletAux.getColor())) {
								infoTabletAux.addTabletsJugando(tabletType);
								messageToTabletAux.addFrases(tabletType,
										tempInfoTablet.getIniciFrase());
							}
						}
					}
				}

				// Se asocia la nueva infoTablet a la tablet actual.
				relTabletInfo.put(messageFromTabletAux.getColor(),
						infoTabletAux);

				if (infoTabletAux.getTabletsJugando().isEmpty()) {
					log.log("Main - La tablet "
							+ messageFromTabletAux.getColor()
							+ " juega en modo Singleplayer");
					messageToTabletAux.setFrasesBlanc(iniciFrasesDB.getRandomIniciFrases(NUM_INICI_FRASES_SP));
					messageToTabletAux.setNumberOfPlayers(1);
					relTabletInfo.get(messageFromTabletAux.getColor()).getThread().sendData(messageToTabletAux);
				} else {
					messageToTabletAux.addFrases(
							messageFromTabletAux.getColor(),
							messageFromTabletAux.getIniciFrase());
					messageToTabletAux.setNumberOfPlayers(messageToTabletAux
							.getFrases().size() + 1);
					log.log("Main - Mensaje para hacer broadcast de iniciFrase's:");
					log.log(messageToTabletAux.toString());
					for (TabletTypes tablet : messageToTabletAux.getFrases()
							.keySet()) {
						if (!(messageToTabletAux.getFrases().get(tablet)
								.toString().equals("null"))) {
							log.log("Main - Haciendo broadcast a " + tablet);
							relTabletInfo.get(tablet).getThread()
									.sendData(messageToTabletAux);
							log.log("Main - Broadcast de iniciFrase's a "
									+ tablet + ": OK!");
						}
					}
				}

				break;
			case ENVIAR_COMPLETA:
				log.log("Main - Tablet " + messageFromTabletAux.getColor()
						+ " ha enviado un " + messageFromTabletAux.getAction());
				infoTabletAux = relTabletInfo.get(messageFromTabletAux
						.getColor());
				infoTabletAux.setState(TabletStates.FINISHING);
				infoTabletAux.setFiFrase(messageFromTabletAux.getFinalFrase());
				relTabletInfo.put(messageFromTabletAux.getColor(),
						infoTabletAux);
				log.log("Main - Datos de tablet "
						+ messageFromTabletAux.getColor()
						+ " actualizados. Estado FINISHING.");

				printerManager.requestedPrint(messageFromTabletAux.getColor(), messageFromTabletAux.getIniciFrase(), messageFromTabletAux.getFinalFrase());
				log.log("Main - La frase finalizada por "
						+ messageFromTabletAux.getColor()
						+ " es "
						+ messageFromTabletAux.getIniciFrase() + " " + messageFromTabletAux.getFinalFrase());
				log.log("Main - Imprimiendo frase completa de "+messageFromTabletAux.getColor());

				break;
			case ERROR:
				relTabletInfo.put(messageFromTabletAux.getColor(), null);
				log.log("Main - El tablet " + messageFromTabletAux.getColor() + " ha sido eliminado por un error.");
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
