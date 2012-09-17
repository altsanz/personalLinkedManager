package core;

import serialization.MessageFromTablet;
import serialization.MessageToTablet;

public interface OnThreadQuery {

	public MessageToTablet msgPipe(TabletThread thread, MessageFromTablet messageFromTabletAux);
}
