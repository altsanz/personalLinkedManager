package printer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.LinkedList;
import java.util.List;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;

import enums.TabletTypes;

public class Printer implements Printable {

	private static final int MAX_CHARS_PER_LINE = 18;

	boolean isNull = false;

	String iniciFrase;
	String finalFrase;

	List<Integer> posX;
	List<Integer> posY;
	int posx1 = 75;
	int posx2 = 315;
	int posy1 = 160;
	int posy2 = 350;
	int saltoLinia = 20;

	List<String> iniciFraseArray = null;
	List<String> finalFraseArray = null;

	TabletTypes tablet;
	PrintService ps;

	public Printer(TabletTypes tablet, PrintService ps) {
		this.tablet = tablet;
		this.ps = ps;
		initPosLists();
		iniciFraseArray = new LinkedList<String>();
		finalFraseArray = new LinkedList<String>();

	}

	public Printer() {
		this.isNull = true;
	}

	private void initPosLists() {
		posX = new LinkedList<Integer>();
		posY = new LinkedList<Integer>();
		posX.add(75);
		posX.add(315);
		posY.add(160);
		posY.add(350);
	}

	public static PrintService[] getPrintServices() {
		return PrintServiceLookup.lookupPrintServices(
				DocFlavor.SERVICE_FORMATTED.PRINTABLE, null);
	}

	public void launchPrinting() {
		System.out.println(ps.toString());
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		pras.add(OrientationRequested.LANDSCAPE);
		pras.add(new Copies(1));
		pras.add(MediaSizeName.ISO_A4);
		pras.add(new MediaPrintableArea(0, 0, 210000, 297000,
				MediaPrintableArea.MM));

		PrinterJob job = PrinterJob.getPrinterJob();
		try {
			job.setPrintService(ps);
		} catch (PrinterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		job.setPrintable(this);

		try {
			job.print(pras);

		} catch (PrinterException e) {
			e.printStackTrace();
		}
	}

	public void setFrases(String iniciFrase, String finalFrase) {
		this.iniciFrase = iniciFrase;
		this.finalFrase = finalFrase;
		
		iniciFraseArray = new LinkedList<String>();
		finalFraseArray = new LinkedList<String>();
		iniciFraseArray = splitFrases(this.iniciFrase, iniciFraseArray);
		finalFraseArray = splitFrases(this.finalFrase, finalFraseArray);
		iniciFraseArray = centerFrases(iniciFraseArray);
		finalFraseArray = centerFrases(finalFraseArray);
	}

	private List<String> splitFrases(String frase,
			List<String> fraseArrayDestination) {
		// Separar las frases en líneas de 15 carácteres aproximadamente y
		// meterlo en List.
		String[] words = frase.split(" ");
		String fragFraseAux = "";
		int wordsArraySize = words.length;
		int i = 0;
		for (i = 0; i < wordsArraySize; i++) {
			if (fragFraseAux.length() + words[i].length() + 1 < MAX_CHARS_PER_LINE) {
				fragFraseAux += " " + words[i];
			} else {
				fraseArrayDestination.add(fragFraseAux);
				fragFraseAux = "";
				i--;
			}
		}
		fraseArrayDestination.add(fragFraseAux);
		return fraseArrayDestination;
	}

	/**
	 * Returns index of the longest element in a list.
	 * 
	 * @param line
	 * @return
	 */
	private int longestLine(List<String> line) {
		int indexLongestLine = 0;
		int maxChars = 0;
		int i;
		for (i = 0; i < line.size(); i++) {
			if (line.get(i).length() > maxChars) {
				indexLongestLine = i;
				maxChars = line.get(i).length();
			}
		}
		return indexLongestLine;
	}

	private List<String> centerFrases(List<String> fraseArray) {
		int indexLongestLine = longestLine(fraseArray);
		String longestFrase = fraseArray.get(indexLongestLine);
		String fraseAux;
		int spaceNumber;
		int i, j;
		for (i = 0; i < fraseArray.size(); i++) {
			if (i != indexLongestLine) {
				fraseAux = fraseArray.get(i);
				spaceNumber = (int) (longestFrase.length() - fraseAux.length()) / 2;
				for (j = 0; j < spaceNumber; j++) {
					fraseAux = " " + fraseAux;
				}
				fraseArray.set(i, fraseAux);
			}
		}
		return fraseArray;
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pagina)
			throws PrinterException {
		Graphics2D g2d;
		if (pagina == 0) {
			g2d = (Graphics2D) g;
			g2d.setColor(Color.black);
			Font font = new Font("Liberation Mono", Font.PLAIN, 10);
			g2d.setFont(font);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.translate(pf.getImageableX(), pf.getImageableY());
			
			for (int i = 0; i < iniciFraseArray.size(); i++) {
				for (int x = 0; x < posX.size(); x++) {
					g2d.drawString(iniciFraseArray.get(i), posX.get(x),
							posY.get(0) + (saltoLinia * i));

				}
			}
			for (int i = 0; i < finalFraseArray.size(); i++) {
				for (int x = 0; x < posX.size(); x++) {

					g2d.drawString(finalFraseArray.get(i), posX.get(x),
							posY.get(1) + (saltoLinia * i));
				}
			}
			return (PAGE_EXISTS);
		} else {
			return (NO_SUCH_PAGE);
		}
	}

}