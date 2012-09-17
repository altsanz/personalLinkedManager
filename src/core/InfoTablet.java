package core;
import java.util.LinkedList;
import java.util.List;

import enums.TabletStates;
import enums.TabletTypes;

public class InfoTablet {
	private TabletThread thread;
	private TabletStates state;

	private String iniciFrase;
	private String fiFrase;
	private List<TabletTypes> playersInGame;

	public InfoTablet(TabletStates state, TabletThread thread) {
		this.state = state;
		this.thread = thread;
		this.iniciFrase = new String("null");
		this.fiFrase = new String("null");
		this.playersInGame = new LinkedList<TabletTypes>();
	}

	public TabletStates getState() {
		return state;
	}

	public void setState(TabletStates state) {
		this.state = state;
	}

	public TabletThread getThread() {
		return thread;
	}

	public void setThread(TabletThread thread) {
		this.thread = thread;
	}

	public String getIniciFrase() {
		return iniciFrase;
	}

	public void setIniciFrase(String iniciFrase) {
		this.iniciFrase = iniciFrase;
	}

	public String getFiFrase() {
		return fiFrase;
	}

	public void setFiFrase(String fiFrase) {
		this.fiFrase = fiFrase;
	}

	public List<TabletTypes> getTabletsJugando() {
		return playersInGame;
	}

	public void addTabletsJugando(TabletTypes tabletType) {
		this.playersInGame.add(tabletType);
	}

	public void clearTabletsJugando() {
		this.playersInGame.clear();
	}

	@Override
	public String toString() {
		String output = new String(state.toString());
		if (thread == null) {
			output = output + ", thread_not_launched";
		} else {
			output = output + ", " + thread.toString();
		}
		output = output + ", iniciFrase : " + getIniciFrase() + ", fiFrase : "
				+ getFiFrase();
		if (playersInGame.isEmpty()) {
			output = output + ", gameMode: SP";
		} else {
			output = output + ", gameMode: MP { ";
			for (TabletTypes tabletColor : playersInGame) {
				output = output + tabletColor + ", ";
			}
			output = output + "}";
		}
		return output;
	}

}
