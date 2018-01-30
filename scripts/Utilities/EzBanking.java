package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class EzBanking {
	public static boolean haveReopenedBank = false;
	private static RSItem[] cachedSearch;
	
	private static int		DEPOSIT_BOX_MASTER = 192;

	public static boolean withdraw(int num, boolean shouldWait, String... items){
		cachedSearch = Banking.find(Filters.Items.nameEquals(items));//.combine(Filters.Items.nameNotContains("raw"), false));
		cachedSearch = removePlaceholders(cachedSearch);
		if(cachedSearch.length==0){
			if(getTotalCount(Filters.Items.nameContains(items))<num){
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


	public static int getCount(String name) {
		cachedSearch = Banking.find(name);
		return cachedSearch.length > 0 ? cachedSearch[0].getStack() : 0;
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

	public static boolean close(boolean shouldWait){
//		if(General.random(0,10)>4){
//			Keyboard.pressFunctionKey(KeyEvent.VK_ESCAPE);
//			return shouldWait?Timing.waitCondition(EzConditions.bankIsClosed(), 3000):true;
//		}
		return Banking.close()&&shouldWait?Timing.waitCondition(EzConditions.bankIsClosed(), 3000):true;
	}
	
	public static boolean open(boolean shouldWait){
		checkInterfaces();
		return (Banking.openBank()&&shouldWait?Timing.waitCondition(EzConditions.bankIsOpen(), 5000):true);
	}

	private static boolean checkInterfaces(){
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
		return Banking.depositAll() > 0 && shouldWait ? Timing.waitCondition(EzConditions.inventoryChange(false),5000) : true;
	}

	public static boolean withdraw(int num, boolean shouldWait, Filter<RSItem> filter) {
		RSItem[] item = Banking.find(filter);
		return item.length > 0 && Banking.withdrawItem(item[0], num) && shouldWait ? Timing.waitCondition(EzConditions.inventoryChange(true), 2500) : true;
	}


}
