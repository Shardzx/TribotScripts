package scripts.clientofkourend.utils;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

public class Shop {

	private static RSInterface 	shopContents,
					shopWindow;
	private static RSInterface[]	shopItems;
	
	public static Condition getCondition(boolean opening){
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100,200);
				return opening ? isShopOpen() : !isShopOpen();
			}
			
		};
	}
	
	
	public static int getSlot(int itemId) {
		if (isShopOpen()) {
			shopItems = getComponents();
			if(shopItems.length == 0)
				return -1;
			for (int i = 0; i < getStockLength(); i++) {
				if (shopItems[i] != null && itemId == shopItems[i].getComponentItem())
					return i;
			}
		}
		return -1;
	}

	public static boolean contains(int id) {
		if (isShopOpen()) {
			shopContents = Interfaces.get(300, 2);
			if(shopContents!=null){
				shopItems = shopWindow.getChildren();
				if(shopItems.length>0)
				for (RSInterface item : shopItems) {
					if (item.getComponentItem() == id)
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean buy(int id, int count) {
		int index = -1;
		if (Shop.isShopOpen()) {
			shopItems = getComponents();
			if(shopItems!=null){
				for (int i = 0; i < shopItems.length; i++) {
					if (shopItems[i].getComponentItem() == id) {
						index = i;
					}
				}
				if (index == -1){
					return false;
				} else {
					RSInterface c= shopContents.getChild(index);
					if (count >= 10) {
						if (c.click("Buy 10")) {
						General.sleep(200);
							return true;//count-10>=0?buy(id,count-10):true;
						}
					} else {
						if(count ==5)
							if (c.click("Buy " + count)) {
								General.sleep(200,300);
								return true;
							}
						if(count >5){
							if(c.click("Buy 5")){
								General.sleep(200,300);
								return true;//buy(id,count-5);
							}
						}
						else{
							for(int i=0;i<count;i++){
								c.click("Buy 1");
								General.sleep(50,150);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public static boolean isShopOpen() {
		return Interfaces.isInterfaceValid(300);
	}

	public String getShopName() {
		shopWindow = Interfaces.get(300,1);
		if(shopWindow == null)
			return null;
		RSInterface shopName = shopWindow.getChild(1);
		if (shopName != null)
			return shopName.getText();
		return null;
	}

	public static boolean close() {
		shopWindow = Interfaces.get(300,1);
		if(shopWindow == null)
			return false;
		RSInterface close = shopWindow.getChild(11);
		if (close!= null) {
			if (close.click("Close")) {
				return Timing.waitCondition(getCondition(false), 6000);
			}
		}
		return false;
	}

	static int getStockLength() {
		if (isShopOpen())
			return Interfaces.get(300, 2).getChildren().length;

		return 0;
	}

	public static int getCount(int id)
	{
		shopContents = Interfaces.get(300,2);
		if(shopContents!=null){
			shopItems = shopContents.getChildren();
			if(shopItems!=null&&shopItems.length>0)
			for (RSInterface item: shopItems){
				if(item.getComponentItem() == id){
					return item.getComponentStack();
				}
			}
		}
		return 0;
	}
	
	public static int getCount(String name)
	{
		shopContents = Interfaces.get(300,2);
		if(shopContents!=null){
			shopItems = shopContents.getChildren();
			if(shopItems!=null&&shopItems.length>0){
				for (RSInterface item: shopItems){
					if(General.stripFormatting(item.getComponentName()).equals(name)){
						return item.getComponentStack();
					}
				}
			}
		}
		return 0;
	}

	public static RSInterface[] getComponents() {
		shopContents = Interfaces.get(300,2);
		if(shopContents!=null)
			return shopContents.getChildren();

		return null;
	}

	public static boolean buy(String name, int count) {
		int index = -1;
		if (isShopOpen()) {
			shopItems = Shop.getComponents();
			if(shopItems!=null){
				for (int i = 0; i < shopItems.length; i++) {
					String itemName = shopItems[i].getComponentName();
					if(itemName == null)
						continue;
					if (General.stripFormatting(itemName).equals(name)) {
						index = i;
					}
				}
				if (index == -1){
					return false;
				} else {
					RSInterface item = shopWindow.getChild(index);
					if(item == null)
						return false;
					if (count >= 10) {
						return item.click("Buy 10") && (count-10)>0?buy(name,count-10):true;
					} else {
						if(count ==5) {
							return item.click("Buy 5");
						} else if(count >5){
							if(item.click("Buy 5")){
								General.sleep(50,200);
								return buy(name,count-5);
							}
						}
						else{
							for(int i=0;i<count;i++){
								item.click("Buy 1");
								General.sleep(50,300);
							}
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
}
