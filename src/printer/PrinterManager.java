package printer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.print.PrintService;
import enums.*;

public class PrinterManager {
	Scanner scanner;
	Map<TabletTypes, Printer> relTabletPrinter;

	public PrinterManager() {
		scanner = new Scanner(System.in);
		relTabletPrinter = new HashMap<TabletTypes, Printer>();
	}

	/**
	 * Ask for relation between a tablet and a print service.
	 */
	public void cfgPrinters() {
		PrintService[] ps = Printer.getPrintServices();
		
		int i = 1;
		int selection = 0;
		for ( TabletTypes tablet : TabletTypes.values() ) {
			for (PrintService printService : ps) {
				System.out.println(i + ") " + printService.toString());
				i++;
			}
			System.out.print("Choose printer for " + tablet + ": (0 for none) ");

			selection = scanner.nextInt();
			
			if (selection > 0 && selection <= ps.length) {
				relTabletPrinter.put(tablet, new Printer(tablet, ps[selection - 1]));
				System.out.println("Printer " + ps[selection - 1].toString()
						+ " assigned to " + tablet);
			} else if ( selection == 0 ) {
//				 relTabletPrinter.put(tablet);
			} else {
				System.out.println("Invalid option");
			}
			i=1;
		}
	}
	
	public synchronized void requestedPrint( TabletTypes tablet, String iniciFrase, String finalFrase ) {
		
		relTabletPrinter.get(tablet).setFrases(iniciFrase, finalFrase);
		relTabletPrinter.get(tablet).launchPrinting();
	}
	

}
