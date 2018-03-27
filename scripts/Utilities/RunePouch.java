package scripts.Utilities;

import java.util.Arrays;
import java.util.List;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSVarBit;

public class RunePouch{
	private static int	SLOT_1_TYPE_BIT = 29,
						SLOT_1_QUANTITY_BIT = 1624,
						SLOT_2_TYPE_BIT = 1622,
						SLOT_2_QUANTITY_BIT = 1625,
						SLOT_3_TYPE_BIT = 1623,
						SLOT_3_QUANTITY_BIT = 1626;
	
	private static int	RUNE_POUCH_INTERFACE_MASTER = 190,
						RUNE_POUCH_INTERFACE_CHILD = 1,
						RUNE_POUCH_INTERFACE_COMPONENT_CLOSE = 11;
	
	public static boolean hasRunePouchInterface(){
		return Interfaces.isInterfaceValid(RUNE_POUCH_INTERFACE_MASTER);
	}
	
	public static boolean closeRunePouchInterface(){
		RSInterface close = Interfaces.get(RUNE_POUCH_INTERFACE_MASTER,RUNE_POUCH_INTERFACE_CHILD);
		if(close != null){
			close = close.getChild(RUNE_POUCH_INTERFACE_COMPONENT_CLOSE);
			return close != null && close.click();
		}
		return false;
	}
						
	// air,water,earth,fire,mind,chaos,death,blood,cosmic,nature,law,body,soul,astral,mist,mud,dust,lava,steam,smoke
	public enum RuneSlot {
		FIRST(SLOT_1_TYPE_BIT,SLOT_1_QUANTITY_BIT),
		SECOND(SLOT_2_TYPE_BIT,SLOT_2_QUANTITY_BIT),
		THIRD(SLOT_3_TYPE_BIT,SLOT_3_QUANTITY_BIT);
		
		private int type;
		private int quantityIndex;
		private RuneSlot(int type, int quantity){
			this.type = type;
			this.quantityIndex = quantity;
		}
		
		public String getRune(){
			RSVarBit bit = RSVarBit.get(type);
			if(bit == null){
				return null;
			}
			switch(bit.getValue()){
			case 0:
				return null;
			case 1:
				return "Air rune";
			case 2:
				return "Water rune";
			case 3:
				return "Earth rune";
			case 4:
				return "Fire rune";
			case 5:
				return "Mind rune";
			case 6:
				return "Chaos rune";
			case 7:
				return "Death rune";
			case 8:
				return "Blood rune";
			case 9:
				return "Cosmic rune";
			case 10:
				return "Nature rune";
			case 11:
				return "Law rune";
			case 12:
				return "Body rune";
			case 13:
				return "Soul rune";
			case 14:
				return "Astral rune";
			case 15:
				return "Mist rune";
			case 16:
				return "Mud rune";
			case 17:
				return "Dust rune";
			case 18:
				return "Lava rune";
			case 19:
				return "Steam rune";
			case 20:
				return "Smoke rune";
			}
			return null;
		}
		
		public int getQuantity(){
			RSVarBit bit = RSVarBit.get(quantityIndex);
			return bit != null ? bit.getValue() : 0;
		}
		
	}
	
	public static int getQuantity(String... rune){
		List<String> runes = Arrays.asList(rune);
		for(RuneSlot slot:RuneSlot.values()){
			if(runes.contains(slot.getRune())){
				return slot.getQuantity();
			}
		}
		return 0;
	}
	
	public static boolean hasPouch(){
		return Inventory.getCount("Rune pouch") > 0;
	}
	
	public static RSItem getPouch(){
		RSItem[] pouch = Inventory.find("Rune pouch");
		return pouch.length > 0 ? pouch[0] : null;
	}
	
	
}
