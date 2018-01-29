package scripts.webwalker_logic.teleport_logic;

import java.util.ArrayList;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Skills;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;

import scripts.webwalker_logic.WebPath;
import scripts.webwalker_logic.local.walker_engine.WalkerEngine;
import scripts.webwalker_logic.shared.helpers.BankHelper;
import scripts.webwalker_logic.teleport_logic.TeleportManager.TeleportAction;

public class TeleportWithdrawer {
 
	private static BankCache cache;
	
	public static TeleportAction nextTeleportAction;
	
	public static boolean useTabsFirst = true;
	
	
	
	private static Filter<RSItem> 	COMBAT_BRACELETS = Filters.Items.nameContains("Combat bracelet("),
									GLORIES = Filters.Items.nameContains("Amulet of glory("),
									SKILLS_NECKLACES = Filters.Items.nameContains("Skills necklace("),
									GAMES_NECKLACES = Filters.Items.nameContains("Games necklace("),
									DUELING_RINGS = Filters.Items.nameContains("Ring of dueling("),
									WEALTHS = Filters.Items.nameContains("Ring of wealth(");
	
	private static ArrayList<RSTile>pathToBank,
									pathToDestinationFromBank;
	
	private static TeleportAction 	nextAction;
	
	public static void setup(Script script){
		if(script == null)
			return;
		TeleportManager.withdrawTeleports = true;
		cache = new BankCache(script);
	}
	
	public static boolean isLoaded(){
		return cache!= null;
	}
	
	
	public static TeleportAction getNextTeleportAction(int originalPathLength, RSTile destination){
		nextTeleportAction = TeleportManager.getWithdrawableTeleportAction(originalPathLength, destination);
		return nextTeleportAction;
	}
	
	public static boolean withdrawTeleportMethod(TeleportMethod method){
		if(!canWithdrawTeleport(method) || pathToBank == null || pathToDestinationFromBank == null || nextAction == null){
			return false;
		}
		long current = Timing.currentTimeMillis();
		while(!method.canUse() && Timing.timeFromMark(current) < 60000){
			if(!BankHelper.isInBank()){
				if(WalkerEngine.getInstance().walkPath(pathToBank)){
					Timing.waitCondition(isInBank(), 6000);
				}
			} else if(Banking.isBankScreenOpen()){
				withdrawTeleport(method);
			} else{
				if(Banking.openBank()){
					Timing.waitCondition(isBankOpen(),8000);
				}
			}
		}
		if(Banking.isBankScreenOpen()){
			Banking.close();
		}
		return method.canUse();
	}
	
	public static boolean shouldBankForTeleport(int originalPathLength, RSTile destination){
    	//first, cache path to bank from current location, including the path length
    	//second, calculate path from there to the destination using specific teleport actions
    	pathToBank = WebPath.getPathToBank();
    	final RSTile bankEndTile = pathToBank.get(pathToBank.size()-1);
    	pathToDestinationFromBank = WebPath.getPath(bankEndTile, destination);
    	nextAction = TeleportManager.getWithdrawableTeleportAction(pathToDestinationFromBank.size(),destination);
    	return nextAction != null && nextAction.getPath().size() + pathToBank.size() < originalPathLength;
    }
	
	
	public static boolean withdrawItem(int quantity,String name){
		if(quantity<5 && quantity > 0){//Left click item multiple times for small amounts.
			RSItem[] itemToWithdraw = Banking.find(name);
			int currentCount = Inventory.getCount(name);
			if(itemToWithdraw.length == 0){
				return false;
			}
			for(int i=0;i<quantity;i++){
				itemToWithdraw[0].click();
			}
			return Timing.waitCondition(inventoryAmountEquals(quantity + currentCount,name), 4000);
		}
		return Banking.withdraw(quantity, name) && Timing.waitCondition(inventoryChange(true), 4000);
	}
	public static boolean withdrawItem(int quantity, Filter<RSItem> filter){
		RSItem[] items = Banking.find(filter);
		return items.length > 0 && Banking.withdrawItem(items[0], quantity) && Timing.waitCondition(inventoryChange(true), 4000);
	}
	
	public static boolean withdrawTeleport(TeleportMethod method){
		if(Inventory.isFull()){
			return false;
		}
		switch(method){
		case ARDOUGNE_TELPORT:
			return useTabsFirst ? 
					withdrawItem(1,"Ardougne teleport") || (withdrawItem(2,"Law rune") && withdrawItem(2,"Water rune")) :
						(withdrawItem(2,"Law rune") && withdrawItem(2,"Water rune")) || withdrawItem(1,"Ardougne teleport");
		case CAMELOT_TELEPORT:
			return useTabsFirst ? 
					withdrawItem(1,"Camelot teleport") || (withdrawItem(1,"Law rune") && withdrawItem(5,"Air rune")) :
						(withdrawItem(1,"Law rune") && withdrawItem(5,"Air rune")) || withdrawItem(1,"Camelot teleport");
		case COMBAT_BRACE:
			return withdrawItem(1,COMBAT_BRACELETS);
		case DUELING_RING:
			return withdrawItem(1,DUELING_RINGS);
		case ECTOPHIAL:
			return withdrawItem(1,"Ectophial");
		case FALADOR_TELEPORT:
			return useTabsFirst ? 
					withdrawItem(1,"Falador teleport") || (withdrawItem(1,"Law rune") && withdrawItem(3,"Air rune") && withdrawItem(1,"Water rune")) :
						(withdrawItem(1,"Law rune") && withdrawItem(3,"Air rune") && withdrawItem(1,"Water rune")) || withdrawItem(1,"Falador teleport");
		case GAMES_NECKLACE:
			return withdrawItem(1,GAMES_NECKLACES);
		case GLORY:
			return withdrawItem(1,GLORIES);
		case LUMBRIDGE_TELEPORT:
			return useTabsFirst ? 
					withdrawItem(1,"Lumbridge teleport") || (withdrawItem(1,"Law rune") && withdrawItem(3,"Air rune") && withdrawItem(1,"Earth rune")) :
						(withdrawItem(1,"Law rune") && withdrawItem(3,"Air rune") && withdrawItem(1,"Earth rune")) || withdrawItem(1,"Lumbridge teleport");
		case RING_OF_WEALTH:
			return withdrawItem(1,WEALTHS);
		case SKILLS_NECKLACE:
			return withdrawItem(1,SKILLS_NECKLACES);
		case VARROCK_TELEPORT:
			return useTabsFirst ? 
					withdrawItem(1,"Varrock teleport") || (withdrawItem(1,"Law rune") && withdrawItem(3,"Air rune") && withdrawItem(1,"Fire rune")) :
						(withdrawItem(1,"Law rune") && withdrawItem(3,"Air rune") && withdrawItem(1,"Fire")) || withdrawItem(1,"Varrock teleport");
		default:
			break;
		
		}
		return false;
	}
	
	public static boolean canWithdrawTeleport(TeleportMethod method){
		if(Inventory.isFull()){
			return false;
		}
		switch(method){
		case ARDOUGNE_TELPORT:
			return Game.getSetting(165) >= 30 && ((Skills.SKILLS.MAGIC.getCurrentLevel() >= 51 && 
					canWithdraw("Law rune",2) && canWithdraw("Water rune",2)) || canWithdraw("Ardougne teleport",1));
		case CAMELOT_TELEPORT:
			return (Skills.SKILLS.MAGIC.getCurrentLevel() >= 45 && canWithdraw("Law rune",1) && canWithdraw("Air rune",5)) ||
					canWithdraw("Camelot teleport",1);
		case COMBAT_BRACE:
			return canWithdraw("Combat bracelet(",1);
		case DUELING_RING:
			return canWithdraw("Ring of dueling(",1);
		case ECTOPHIAL:
			return canWithdraw("Ectophial",1);
		case FALADOR_TELEPORT:
			return (Skills.SKILLS.MAGIC.getCurrentLevel() >= 37 && canWithdraw("Law rune",1) && canWithdraw("Air rune",3) &&
					canWithdraw("Water rune",1)) || canWithdraw("Falador teleport",1);
		case GAMES_NECKLACE:
			return canWithdraw("Games necklace(",1);
		case GLORY:
			return canWithdraw("Amulet of glory(",1);
		case LUMBRIDGE_TELEPORT:
			return (Skills.SKILLS.MAGIC.getCurrentLevel() >= 31 && canWithdraw("Law rune",1) && canWithdraw("Air rune",3) &&
			canWithdraw("Earth rune",1)) || canWithdraw("Lumbridge teleport",1);
		case RING_OF_WEALTH:
			return canWithdraw("Ring of wealth(",1);
		case SKILLS_NECKLACE:
			return canWithdraw("Skills necklace(",1);
		case VARROCK_TELEPORT:
			return (Skills.SKILLS.MAGIC.getCurrentLevel() >= 25 && canWithdraw("Law rune",1) && canWithdraw("Air rune",3) &&
			canWithdraw("Fire rune",1)) || canWithdraw("Varrock teleport",1);
		default:
			break;
		
		}
		return false;
	}
	
	public static boolean canWithdraw(String name,int quantity){
		return cache!=null && cache.hasLoadedCache() && cache.canWithdraw(name, quantity);
	}
	
	public static Condition inventoryChange(final boolean increase){
		final int count = Inventory.getAll().length;
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return increase?count<Inventory.getAll().length:count>Inventory.getAll().length;
			}
	
		};
	}
	public static Condition inventoryAmountEquals(final int quantity, final String name){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Inventory.getCount(name) == quantity;
			}
	
		};
	}
	public static Condition isInBank(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return BankHelper.isInBank();
			}
	
		};
	}
	public static Condition isBankOpen(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Banking.isBankScreenOpen();
			}
	
		};
	}
}
