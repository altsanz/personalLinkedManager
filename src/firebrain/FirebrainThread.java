package firebrain;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import log.Logger;

import com.google.gson.Gson;

import enums.FireBrainActions;
import enums.FirebrainZones;
import enums.TabletTypes;

/**
 * Thread that launches a Firebrain socket associated and controls its inputs
 * and outputs.
 * 
 * @author alejandro
 */
public class FirebrainThread extends Thread {
	private static final int BUFFER_LENGTH = 3;

	int port = 50505;

	private Socket fireBrainSocket = null;

	BufferedReader fbIn = null;
	BufferedWriter fbOut = null;

	char bSend[] = null;
	char bRecv[] = new char[BUFFER_LENGTH];

	Gson gson = new Gson();

	Map<String, String> msg = new HashMap<String, String>();
	Map<String, FirebrainZones> relConnZones = new HashMap<String, FirebrainZones>();

	Logger log = null;
	
	public FirebrainThread() {
	
	}

	@Override
	public void run() {
		log = Logger.getInstance();
		relConnZones = new HashMap<String, FirebrainZones>();
		relConnZones.put(TabletTypes.R.toString()+ TabletTypes.G.toString(), FirebrainZones.Y);
		relConnZones.put(TabletTypes.G.toString()+ TabletTypes.R.toString(), FirebrainZones.Y);
		relConnZones.put(TabletTypes.G.toString()+ TabletTypes.B.toString(), FirebrainZones.C);
		relConnZones.put(TabletTypes.B.toString()+ TabletTypes.G.toString(), FirebrainZones.C);
		relConnZones.put(TabletTypes.B.toString()+ TabletTypes.R.toString(), FirebrainZones.P);
		relConnZones.put(TabletTypes.R.toString()+ TabletTypes.B.toString(), FirebrainZones.P);
		try {
			log.log("FB - Firebrain socket launched.");
			fireBrainSocket = new Socket("192.168.0.197", port);
			fbOut = new BufferedWriter(new PrintWriter(
					fireBrainSocket.getOutputStream(), true));

			while(true){
				Thread.sleep(100);
			}
		} catch (IOException e) {
			System.out.println("Init: " + e.getMessage());
			try {
				if (fireBrainSocket != null)
					fireBrainSocket.close();
				if (fbOut != null)
					fbOut.close();
				if (fbIn != null)
					fbIn.close();
			} catch (Exception e2) {
				System.out.println(e.getMessage());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
		
		}

	}

	public synchronized void sendAction(FireBrainActions action, TabletTypes tabletA, TabletTypes tabletB) {
		String json = "";
		msg = new HashMap<String, String>();
		switch(action) {
		case LightsOn:
			msg.put("function", FireBrainActions.LightsOn.toString());
			msg.put("zone", tabletA.toString());
			break;
		case LightsAll:
			msg = new HashMap<String, String>();
			msg.put("function", FireBrainActions.LightsAll.toString());
			break;
		case LightsAny:
			msg = new HashMap<String, String>();
			msg.put("function", FireBrainActions.LightsAny.toString());
			break;
		case LightsConnection:
			msg.put("function", FireBrainActions.LightsOn.toString());
			FirebrainZones connection = relConnZones.get(tabletA.toString()+tabletB.toString());
			msg.put("zone", connection.toString());
			break;
		case LightsOff:
			msg.put("function", FireBrainActions.LightsOff.toString());
			msg.put("zone", tabletA.toString());
			break;
		}
		json = gson.toJson(msg);
		sendData(json.toCharArray());
	}	

	/**
	 * Reads answer message from Firebrain Socket and returns a boolean depending of the state, { OK, KO }
	 * @return Bool { OK = true, KO = false }
	 */

	/**
	 * Initializes relation between 2 {@link TabletTypes} and its connected zone {@link FirebrainZones}.
	 * @return
	 */
	
	private void sendData(char[] msg) {
		try {
			// Sends data
			fbOut.write(msg);
			fbOut.newLine();
			fbOut.flush();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
