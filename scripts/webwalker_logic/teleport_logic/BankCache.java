package scripts.webwalker_logic.teleport_logic;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.script.Script;

public class BankCache extends Thread{
	
	public BankCache(Script script){
		callingScript = script;
		this.start();
	}
	
	private Script callingScript;
	
	public RSItem[] bankCache;
	
	@Override
	public void run() {
		while(callingScript.isActive()){
			if(Banking.isBankScreenOpen() && areBankItemsLoaded()){
				bankCache = Banking.getAll();
			}
			General.sleep(1000,2000);
		}
	}
	
	public boolean hasLoadedCache(){
		return bankCache != null;
	}
	
	public boolean canWithdraw(String name, int quantity){
		Filter<RSItem> filter = Filters.Items.nameContains(name);
		for(RSItem i:bankCache){
			if(filter.accept(i) && i.getStack() >= quantity){
				return true;
			}
		}
		return false;
	}
	
	public boolean areBankItemsLoaded() {
        return getCurrentBankSpace() == Banking.getAll().length;
    }
	private int getCurrentBankSpace() {
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
