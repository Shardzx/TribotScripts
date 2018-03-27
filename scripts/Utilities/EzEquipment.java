package scripts.Utilities;

import org.tribot.api2007.Equipment;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.types.RSItem;

public class EzEquipment {
	public static final int		INTERFACE_MASTER = 387,
								HELMET_CHILD_ID = 6,
								CAPE_CHILD_ID = 7,
								AMULET_CHILD_ID = 8,
								WEAPON_CHILD_ID = 9,
								CHEST_CHILD_ID = 10,
								OFFHAND_CHILD_ID = 11,
								LEGS_CHILD_ID = 12,
								GLOVES_CHILD_ID = 13,
								BOOTS_CHILD_ID = 14,
								RING_CHILD_ID = 15,
								AMMO_CHILD_ID = 16;
	
	public static boolean clickEquipment(String action, String... name){
		if(!GameTab.open(TABS.EQUIPMENT))
			return false;
		RSItem[] equip = Equipment.find(name);
		return equip.length > 0 && equip[0].click(action);
	}
	
	public static boolean clickEquipment(String action, int... id){
		if(!GameTab.open(TABS.EQUIPMENT))
			return false;
		RSItem[] equip = Equipment.find(id);
		return equip.length > 0 && equip[0].click(action);
	}

}