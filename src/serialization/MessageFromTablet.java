package serialization;
import enums.TabletActions;
import enums.TabletTypes;

/**
 * Auxiliar class for building JSon. Structure to read data received from
 * tablets.
 * 
 * @author altsanz
 */
public class MessageFromTablet {
	TabletActions action;
	TabletTypes color;
	String message;
	TabletTypes colorSelected;
	String fInici;
	String fFinal;

	public TabletActions getAction() {
		return action;
	}

	public void setAction(TabletActions action) {
		this.action = action;
	}

	public TabletTypes getColor() {
		return color;
	}

	public void setColor(TabletTypes color) {
		this.color = color;
	}

	// public String getMessage() {
	// return message;
	// }

	public void setMessage(String message) {
		this.message = message;
	}

	public TabletTypes getColorSelected() {
		return colorSelected;
	}

	public void setColorSelected(TabletTypes colorSelected) {
		this.colorSelected = colorSelected;
	}

	public String getIniciFrase() {
		return fInici;
	}

	public void setIniciFrase(String fInici) {
		this.fInici = fInici;
	}

	public String getFinalFrase() {
		return fFinal;
	}

	public void setFinalFrase(String fFinal) {
		this.fFinal = fFinal;
	}

	@Override
	public String toString() {
		String output = new String();
		output = "{\n action : " + getAction() + ",\n color : " + getColor()
				+ ",\n colorSelected : " + getColorSelected() + ",\n fInici : "
				+ getIniciFrase() + ",\n fFinal : " + getFinalFrase() + "\n}";
		return output;
	}

}
