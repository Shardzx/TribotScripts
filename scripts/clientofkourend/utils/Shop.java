package scripts.ezquests.clientofkourend.utils;

import org.tribot.api.General;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

public class Shop {

	public static Condition getCondition(boolean opening){
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return opening ? isShopOpen() : !isShopOpen();
			}
			
		};
	}
	
	
	public static int getSlot(int itemId) {
		if (isShopOpen()) {
			RSInterfaceComponent[] items = getComponents();
			for (int i = 0; i < getStockLength(); i++) {
				if (items[i] != null && itemId == items[i].getComponentItem())
					return i;
			}
		}
		return -1;
	}

	public static boolean contains(int id) {
		if (isShopOpen()) {
			RSInterfaceChild child = Interfaces.get(300, 2);
			if(child!=null){
				RSInterfaceComponent[] children = child.getChildren();
				if(children.length>0)
				for (RSInterfaceComponent r : children) {
					if (r.getComponentItem() == id)
						return true;
				}
			}
		}
		return false;
	}
	
	public static boolean buy(int id, int count) {
		int index = -1;
		if (Shop.isShopOpen()) {
			RSInterfaceComponent[] components = Shop.getComponents();
			if(components!=null){
				for (int i = 0; i < components.length; i++) {
					if (components[i].getComponentItem() == id) {
						index = i;
					}
				}
				if (index == -1){
					return false;
				} else {
					RSInterfaceComponent c= Interfaces.get(300,2).getChild(index);
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
		return Interfaces.get(300, 2) != null;
	}

	public String getShopName() {
		if (Interfaces.get(300, 1) != null)
			return Interfaces.get(300, 1).getText();

		return null;
	}

	public static boolean close() {
		if (Interfaces.get(300, 1).getChild(11)!= null) {
			if (Interfaces.get(300, 1).getChild(11).click("Close")) {
				General.sleep(600);
				return true;
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
		RSInterfaceChild c=Interfaces.get(300,2);
		if(c!=null){
			RSInterfaceComponent[] items = c.getChildren();
			if(items!=null&&items.length>0)
			for (RSInterfaceComponent cur: items){
				if(cur.getComponentItem() == id){
					return cur.getComponentStack();
				}
			}
		}
		return 5;
	}
	
	public static int getCount(String name)
	{
		RSInterfaceChild c=Interfaces.get(300,2);
		if(c!=null){
			RSInterfaceComponent[] items = c.getChildren();
			if(items!=null&&items.length>0)
			for (RSInterfaceComponent cur: items){
				if(General.stripFormatting(cur.getComponentName()).equals(name)){
					return cur.getComponentStack();
				}
			}
		}
		return 5;
	}

	public static RSInterfaceComponent[] getComponents() {
		RSInterfaceChild c = Interfaces.get(300,2);
		if(c!=null)
			return c.getChildren();

		return null;
	}

	public static boolean buy(String name, int count) {
		int index = -1;
		if (Shop.isShopOpen()) {
			RSInterfaceComponent[] components = Shop.getComponents();
			if(components!=null){
				for (int i = 0; i < components.length; i++) {
					String itemName = General.stripFormatting(components[i].getComponentName());
					if (itemName.equals(name)) {
						index = i;
					}
				}
				if (index == -1){
					return false;
				} else {
					RSInterfaceComponent c= Interfaces.get(300,2).getChild(index);
					if(c == null)
						return false;
					if (count >= 10) {
						return c.click("Buy 10") && (count-10)>0?buy(name,count-10):true;
					} else {
						if(count ==5) {
							return c.click("Buy 5");
						} else if(count >5){
							if(c.click("Buy 5")){
								General.sleep(50,200);
								return buy(name,count-5);
							}
						}
						else{
							for(int i=0;i<count;i++){
								c.click("Buy 1");
								General.sleep(50,200);
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