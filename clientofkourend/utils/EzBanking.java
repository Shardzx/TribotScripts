package scripts.clientofkourend.utils;

import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

public class EzBanking {
	public static boolean areItemsLoaded() {
        return getCurrentBankSpace() == Banking.getAll().length;
    }
	private static int getCurrentBankSpace() {
	        RSInterface amount = Interfaces.get(12,5);
	        if(amount != null) {
	            String txt = amount.getText();
	            if(txt != null) {
	                try {
	                    int toInt = Integer.parseInt(txt);
	                    if(toInt > 0)
	                        return toInt;
	                } catch(NumberFormatException e) {
	                    return -1;
	                }
	            }
	        }
	        return -1;
	}
}
