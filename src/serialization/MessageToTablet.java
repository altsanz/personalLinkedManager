package serialization;
import java.util.HashMap;
import java.util.List;

import enums.ReturnStates;
import enums.TabletActions;
import enums.TabletTypes;

/**
 * Auxiliar class for building JSon. Structure to send data to tablets from
 * server.
 * 
 * @author altsanz
 * 
 */
public class MessageToTablet {
	private ReturnStates state = null;
	private TabletActions action = null;
	private HashMap<TabletTypes, String> frases = null;
	private String mensaje = null;
	private int numberOfPlayers = 0;
	private List<String> frasesBlanc = null;

	public ReturnStates getState() {
		return state;
	}

	public void setState(ReturnStates state) {
		this.state = state;
	}

	public TabletActions getAction() {
		return action;
	}

	public void setAction(TabletActions action) {
		this.action = action;
	}

	public HashMap<TabletTypes, String> getFrases() {
		return frases;
	}

	public void setFrases(HashMap<TabletTypes, String> frases) {
		this.frases = frases;
	}
	
	public void addFrases(TabletTypes idFrase, String frase) {
		this.frases.put(idFrase, frase);
	}
	
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	public void setNumberOfPlayers(int numberOfPlayers) {
		this.numberOfPlayers = numberOfPlayers;
	}

	public List<String> getFrasesBlanc() {
		return frasesBlanc;
	}

	public void setFrasesBlanc(List<String> frasesBlanc) {
		this.frasesBlanc = frasesBlanc;
	}

	public void setSingleplayer( ReturnStates state, List<String> frasesBlanc) {
		this.state = state;
		this.action = TabletActions.ENVIAR_INICI;
		this.frasesBlanc = frasesBlanc;
		this.numberOfPlayers = 1;
	}
	
	public void setMultiplayer( ReturnStates state, HashMap<TabletTypes, String> frases, int numberOfPlayers) {
		this.state = state;
		this.action = TabletActions.ENVIAR_INICI;
		this.frases = frases;
		this.numberOfPlayers = numberOfPlayers;
	}
	
	@Override
	public String toString() {
		return "{" + "\n state : " + getState() + "\n action : " + getAction()
				+ "\n frases : " + getFrases() + "\n mensaje : " + getMensaje()
				+ "\n numberOfPlayers : " + getNumberOfPlayers()
				+ "\n frasesBlanc : " + getFrasesBlanc() + "\n}";
	}

}
