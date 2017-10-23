package scripts.Utilities;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
 
public class triClient {
	
	public static <T> T[] shuffleArray(T[] arr){
		if(arr.length == 0 || arr.length == 1){
			return arr;
		}
		List<T> solution = new ArrayList<>();
		for (T t: arr) {
		    solution.add(t);
		}
		Collections.shuffle(solution);
		return solution.toArray(arr);
	}
	
	
	public static boolean shiftDrop(String... items){
		RSItem[] itemsToDrop = Inventory.find(items);
		if(itemsToDrop.length == 0)	return true;
		Keyboard.sendPress(KeyEvent.CHAR_UNDEFINED,KeyEvent.VK_SHIFT);
		for(RSItem item:itemsToDrop){
			item.click();
		}
		Keyboard.sendRelease(KeyEvent.CHAR_UNDEFINED, KeyEvent.VK_SHIFT);
		return Timing.waitCondition(EzConditions.inventoryChange(false), 1500) && Inventory.find(items).length == 0;
	}
	
}
