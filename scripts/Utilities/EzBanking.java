package scripts.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Game;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;


public class EzBanking {
	public static boolean haveReopenedBank = false;
	public static boolean outOfItem = false;
	public static int outOfItemID = -1;
	public static String outOfItemName = null;
	private static String[] lastSearched = null;
	private static RSItem[] cachedSearch;
	
	private static int		DEPOSIT_BOX_MASTER = 192;
	
	public static int getLastSearchedID(){
		if(cachedSearch == null || cachedSearch.length == 0)
			return -1;
		return cachedSearch[0].getID();
	}
	
	public static boolean foundItem(String... name){
		Filter<RSItem> filter = Filters.Items.nameEquals(name).combine(new Filter<RSItem>(){

			@Override
			public boolean accept(RSItem arg0) {
				return arg0.getStack() > 0;
			}
			
		}, haveReopenedBank);
		if(Arrays.asList(name).contains("Coal")){
			filter = filter.combine(Filters.Items.nameNotContains("bag"), false);
		}
		
		lastSearched = name;
		cachedSearch = Banking.find(filter);
		return cachedSearch.length > 0;
	}
	
	public static boolean withdraw(int num, boolean shouldWait, String... items){
		cachedSearch = Banking.find(Filters.Items.nameEquals(items));//.combine(Filters.Items.nameNotContains("raw"), false));
		cachedSearch = removePlaceholders(cachedSearch);
		if(cachedSearch.length==0){
			if(getTotalCount(Filters.Items.nameContains(items))<num){
				outOfItem = true;
			}
			return false;
		}
		if(num == -1){
			return cachedSearch[0].click("Withdraw-All-but-1") && shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
		}
		int count = getCount(cachedSearch[0].getDefinition().getName());
		if(count == 1){
			if(Banking.withdrawItem(cachedSearch[0], 1)){
				haveReopenedBank = false;
				return shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
			}
		}
		if(Banking.withdrawItem(cachedSearch[0],(count==num&&num>1?0:num))){
			haveReopenedBank = false;
			return shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
		}
		if(haveReopenedBank){
			for(String item: items){
				if(getTotalCount(Filters.Items.nameContains(items))<num){
					General.println("Out of item: " + item);
					outOfItem = true;
				}
			}
			return false;
		} else{
			haveReopenedBank = true;
			return close(shouldWait) && open(shouldWait) && withdraw(num,shouldWait,items);
		}
	}
	
	private static RSItem[] removePlaceholders(RSItem[] items){
		List<RSItem> list = new ArrayList<RSItem>();
		for(RSItem item:items){
			if(item.getStack() > 0){
				list.add(item);
			}
		}
		return list.toArray(new RSItem[list.size()]);
	}

	public static boolean withdraw(int num, boolean shouldWait, int... items) {
		if(num == -1){
			cachedSearch = Banking.find(items);
			if(cachedSearch.length==0 || cachedSearch[0].getStack() <= 1){
				outOfItem = true;
				return false;
			}
			return cachedSearch[0].click("Withdraw-All-but-1") && shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
		} 
		if(Banking.withdraw(num, items)){
			haveReopenedBank = false;
			return shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
		}
		if(haveReopenedBank){
			for(int item: items){
				if(getTotalCount(item)<num){
					General.println("Out of item: " + item);
					outOfItem = true;
				}
			}
			return false;
		} else{
			haveReopenedBank = true;
			return close(shouldWait) && open(shouldWait) && withdraw(num,shouldWait,items);
		}
	}
	
	

	public static int getCount(String name) {
		cachedSearch = Banking.find(name);
		return cachedSearch.length > 0 ? cachedSearch[0].getStack() : 0;
	}
	
	public static int getTotalCount(String name){
		if(!areItemsLoaded()){
			General.sleep(1000);
		}
		cachedSearch = Banking.find(name);
		return (cachedSearch.length > 0 ? cachedSearch[0].getStack() : 0) + Inventory.getCount(name);
	}
	
	private static int getTotalCount(Filter<RSItem> filter) {
		if(!areItemsLoaded()){
			General.sleep(1000);
		}
		cachedSearch = Banking.find(filter);
		RSItem[] inv = Inventory.find(filter);
		int invCount = 0;
		if(inv.length > 0){
			RSItemDefinition def = inv[0].getDefinition();
			if(def != null){
				invCount = def.isStackable() || def.isNoted() ? inv[0].getStack() : inv.length;
			}
		}
		return (cachedSearch.length > 0 ? cachedSearch[0].getStack() : 0) + invCount;
	}
	
	public static int getTotalCount(int id) {
		if(!areItemsLoaded()){
			General.sleep(1000);
		}
		cachedSearch = Banking.find(id);
		return (cachedSearch.length > 0 ? cachedSearch[0].getStack() : 0) + Inventory.getCount(id);
	}
	
	public static boolean withdrawToCount(int countToGet, boolean shouldWait, String name){
		int count = Inventory.getCount(name);
		if(count == countToGet)
			return true;
		if(Banking.withdraw(countToGet - count, name)){
			haveReopenedBank = false;
			return shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
		}
		if(haveReopenedBank){
			General.println("Out of item: " + name);
			outOfItem = true;
			return false;
		} else{
			haveReopenedBank = true;
			return close(shouldWait);
		}
	}
	public static boolean withdrawToCount(int countToGet, boolean shouldWait, int ID) {
		int count = Inventory.getCount(ID);
		if(count == countToGet)
			return true;
		if(Banking.withdraw(countToGet - count, ID)){
			haveReopenedBank = false;
			return shouldWait?Timing.waitCondition(EzConditions.inventoryChange(true), 1500):true;
		}
		if(haveReopenedBank){
			General.println("Out of item: " + ID);
			outOfItem = true;
			return false;
		} else{
			haveReopenedBank = true;
			return close(shouldWait);
		}
	}

	public static boolean close(boolean shouldWait){
//		if(General.random(0,10)>4){
//			Keyboard.pressFunctionKey(KeyEvent.VK_ESCAPE);
//			return shouldWait?Timing.waitCondition(EzConditions.bankIsClosed(), 3000):true;
//		}
		return Banking.close()&&shouldWait?Timing.waitCondition(EzConditions.bankIsClosed(), 3000):true;
	}
	
	public static boolean open(boolean shouldWait){
		orCheckInterfaces();
		if(NPCs.find("Grand Exchange Clerk").length>0){
			RSNPC[] banker = NPCs.findNearest("Banker");
			if(EzExchange.isGEopen()){
				EzExchange.closeGE();
			}
			RSObject[] booth = Objects.findNearest(15, Filters.Objects.actionsContains("Bank"));
			if(banker.length==0){
				return booth.length>0 && Utilities.clickObject(booth[0], "Bank",false) && shouldWait ? Timing.waitCondition(EzConditions.bankIsOpen(), 5000) : true;
			} else if(booth.length==0){
				return Utilities.clickNPC(banker[0],"Bank Banker",false) && shouldWait ? Timing.waitCondition(EzConditions.bankIsOpen(), 5000) : true;
			} else if(General.random(0, 1)==0){
				return Utilities.clickObject(booth[0], "Bank",false) && shouldWait ? Timing.waitCondition(EzConditions.bankIsOpen(), 5000) : true;
			} else{
				return Utilities.clickNPC(banker[0],"Bank Banker",false) && shouldWait ? Timing.waitCondition(EzConditions.bankIsOpen(), 5000) : true;
			}
		}
		return (Banking.openBank()&&shouldWait?Timing.waitCondition(EzConditions.bankIsOpen(), 5000):true) || (orCheckInterfaces() && false);
	}
	
	public static boolean openDepositBox(boolean shouldWait){
		RSObject[] box = Objects.findNearest(15, "Bank deposit box");
		return box.length > 0 && Utilities.accurateClickObject(box[0], false, "Deposit") && Timing.waitCondition(EzConditions.interfaceUp(DEPOSIT_BOX_MASTER), 8000);
	}
	public static boolean isDepositBoxOpen(){
		return Interfaces.isInterfaceValid(DEPOSIT_BOX_MASTER);
	}
	public static boolean isNearDepositBox(){
		RSObject[] box = Objects.findNearest(8, "Bank deposit box");
		return box.length > 0;
	}
	public static boolean depositToBox(int quantity, boolean shouldWait, String...names){
		RSInterface master = Interfaces.get(DEPOSIT_BOX_MASTER,2);
		List<String> alreadyDeposited = new ArrayList<String>();
		List<String> namesList = Arrays.asList(names);
		if(master != null){
			RSInterface[] items = master.getChildren();
			if(items != null){
				for(RSInterface item:items){
					String name = General.stripFormatting(item.getComponentName());
					if(namesList.contains(name) && !alreadyDeposited.contains(name)){
						if(quantity == 0){
							item.click("Deposit-All");
							alreadyDeposited.add(name);
						} else if(quantity == 1 || quantity == 5 || quantity == 10){
							item.click("Deposit-" + quantity);
							alreadyDeposited.add(name);
						} else if(item.click("Deposit-X") && Timing.waitCondition(EzConditions.enterAmountMenuUp(), 8000)){
							Keyboard.typeSend(Integer.toString(quantity));
							alreadyDeposited.add(name);
						}
						//item.click("Deposit-" + quantity == 0 ? "All" : "")
					}
				}
				return alreadyDeposited.size() == 0 ? false : (shouldWait ? Timing.waitCondition(EzConditions.itemLeftInventory(names), 5000) : true);
			}
		}
		return false;
	}
	public static boolean depositAllToBox(boolean shouldWait){
		RSInterface deposit = Interfaces.get(DEPOSIT_BOX_MASTER,3);
		return deposit != null && deposit.click() && (shouldWait ? Timing.waitCondition(EzConditions.inventoryChange(false), 5000) : true);
	}
	
	private static boolean orCheckInterfaces(){
		return Utilities.areUnwantedInterfacesOpen() && Utilities.closeUnwantedInterfaces();
	}
	
	public static boolean isInBank(){
        RSObject[] bankObjects = Objects.findNearest(15, Filters.Objects.nameContains("bank", "Bank", "Exchange booth", "Open chest").combine(Filters.Objects.actionsContains("Collect"), true));
        RSNPC[] bankers = NPCs.findNearest(Filters.NPCs.actionsContains("Bank"));
        if (bankObjects.length == 0 && bankers.length == 0){
            return false;
        }
        Positionable bankEntity = bankObjects.length > 0 ? bankObjects[0] : bankers[0];
        HashSet<RSTile> building = getBuilding(bankEntity);
        return building.contains(Player.getPosition()) || (building.size() == 0 && Player.getPosition().distanceTo(bankEntity) < 12) || Banking.isInBank();
    }

    private static HashSet<RSTile> getBuilding(Positionable positionable){
        HashSet<RSTile> tiles = new HashSet<>();
        computeBuilding(positionable, Game.getSceneFlags(), tiles);
        return tiles;
    }

    private static void computeBuilding(Positionable positionable, byte[][][] sceneFlags, HashSet<RSTile> tiles){
    	try{
	        RSTile local = positionable.getPosition().toLocalTile();
	        int localX = local.getX(), localY = local.getY(), localZ = local.getPlane();
	        if (sceneFlags.length < localZ || sceneFlags[localZ].length < localX || sceneFlags[localZ][localX].length < localY){
	            return;
	        }
	        if (sceneFlags[localZ][localX][localY] < 4){
	            return;
	        }
	        if (!tiles.add(local.toWorldTile())){
	            return;
	        }
	        computeBuilding(new RSTile(localX, localY + 1, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
	        computeBuilding(new RSTile(localX + 1, localY, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
	        computeBuilding(new RSTile(localX, localY - 1, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
	        computeBuilding(new RSTile(localX - 1, localY, localZ, RSTile.TYPES.LOCAL).toWorldTile(), sceneFlags, tiles);
    	} catch(Exception e){
    		
    	}
    }

    private static boolean isInBuilding(RSTile localRSTile, byte[][][] sceneFlags) {
        return !(sceneFlags.length < localRSTile.getPlane()
                || sceneFlags[localRSTile.getPlane()].length < localRSTile.getX()
                || sceneFlags[localRSTile.getPlane()][localRSTile.getX()].length < localRSTile.getY())
                && sceneFlags[localRSTile.getPlane()][localRSTile.getX()][localRSTile.getY()] >= 4;
    }
    
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

	public static boolean depositAll(boolean shouldWait) {
		return Banking.depositAll() > 0 && shouldWait ? Utilities.waitInventory(false,1500) : true;
	}

	public static boolean depositEquipment(boolean shouldWait) {
		return Banking.depositEquipment() && shouldWait ? Timing.waitCondition(EzConditions.depositedEquipment(), 2500) : true;
	}

	public static boolean withdraw(int num, boolean shouldWait, Filter<RSItem> filter) {
		RSItem[] item = Banking.find(filter);
		return item.length > 0 && Banking.withdrawItem(item[0], num) && shouldWait ? Timing.waitCondition(EzConditions.inventoryChange(true), 2500) : true;
	}
	public static boolean withdrawLastInstance(int num, boolean shouldWait, Filter<RSItem> filter) {
		RSItem[] item = Banking.find(filter);
		return item.length > 0 && Banking.withdrawItem(item[item.length-1], num) && (shouldWait ? Timing.waitCondition(EzConditions.inventoryChange(true), 2500) : true);
	}

	public static boolean depositAllExcept(Filter<RSItem> filter, boolean shouldWait) {
		List<Integer> itemsDeposited = new ArrayList<Integer>();
		boolean success = true;
		for(RSItem i:Inventory.getAll()){
			if(!Banking.isBankScreenOpen())
				return false;
			if(filter.accept(i) ||itemsDeposited.contains(i.getID())) {
				continue;
			}
			itemsDeposited.add(i.getID());
			if(!Banking.depositItem(i,0)){
				success = false;
			}
		}
		return shouldWait ? Timing.waitCondition(EzConditions.inventoryChange(false), 2500) : success;
	}

	public static int getTotalCounts(String... items) {
		int count = 0;
		for(String item:items){
			count += getTotalCount(item);
		}
		return count;
	}

	public static boolean depositAllExcept(int...ids) {
		Filter<RSItem> filter = Filters.Items.idEquals(ids);
		List<Integer> itemsDeposited = new ArrayList<Integer>();
		for(RSItem i:Inventory.getAll()){
			if(!Banking.isBankScreenOpen())
				return false;
			if(filter.accept(i) ||itemsDeposited.contains(i.getID())) {
				continue;
			}
			itemsDeposited.add(i.getID());
			Banking.depositItem(i,0);
		}
		return itemsDeposited.size() > 0 ? Timing.waitCondition(EzConditions.inventoryChange(false), 2500) : true;
	}

	public static int getCount(String...items) {
		for(String item:items){
			int count = getCount(item);
			if(count > 0){
				return count;
			}
		}
		return 0;
	}

	public static boolean arePlaceholdersOn() {
		RSVarBit var = RSVarBit.get(3755);
		return var != null && var.getValue() == 1;
	}
	public static boolean changePlaceholdersSetting(boolean value){
		if(value == arePlaceholdersOn()){
			return true;
		} else{
			RSInterface placeholders = Interfaces.get(12, 25);
			return placeholders != null && placeholders.click() && Timing.waitCondition(EzConditions.varbitChanged(3755), 4000);
		}
	}
	
	public static boolean removeAllPlaceholders(){
		RSInterface button = Interfaces.get(12,10);
		if(button != null){
			button = button.getChild(10);
			if( button != null && button.click("Remove placeholders") ){
				General.sleep(500,1000);
				return true;
			}
		}
		return false;
	}





	

}
