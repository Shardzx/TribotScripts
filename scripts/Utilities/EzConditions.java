package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.*;

public class EzConditions {
	public static Condition interfaceNotUp(final int id)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(id);
				return inter == null || inter.isHidden();
			}		
		};
	}

	public static Condition interfaceUp(final int id){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Interfaces.get(id) != null;
			}		
		};
	
	}

	public static Condition varbitChanged(final int id){
		final RSVarBit varbit = RSVarBit.get(id);
		if(varbit == null)
			return null;
		final int value = varbit.getValue();
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSVarBit newBit = RSVarBit.get(id);
				return newBit != null && value!=newBit.getValue();
			}		
			
		};
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

	public static Condition objectAppeared(final int distance, final Filter<RSObject> filter){
		final int currentCount = Objects.find(distance, filter).length;
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Objects.find(distance, filter).length>currentCount;
			}
	
		};
	}

	public static Condition expGained(final long startXP, final SKILLS skill){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Skills.getXP(skill)>startXP;
			}
			
		};
	}

	public static Condition isInBank(){
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return EzBanking.isInBank();
			}
		};
	}
	
	public static Condition isConversing(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Utilities.isConversing();
			}
	
		};
	}
	
	public static Condition isNotConversing(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return !Utilities.isConversing();
			}
	
		};
	}
	
	public static Condition bankIsClosed(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return !Banking.isBankScreenOpen();
			}
	
		};
	}
	
	public static Condition bankIsOpen(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Banking.isBankScreenOpen();
			}
	
		};
	}
	
	public static Condition objectVisible(final RSObject obj){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return obj.isOnScreen()&&obj.isClickable();
			}
	
		};
	}
	
	public static Condition inArea(final RSArea area){
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return area.contains(Player.getPosition());
			}
		};
	}

	public static Condition enterAmountMenuUp() {
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return Utilities.enterAmountMenuUp();
			}
		};
	}

	public static Condition itemLeftInventory(String... item) {
		final int count = Inventory.getCount(item);
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Inventory.getCount(item) < count;
			}
			
		};
	}
	public static Condition itemEnteredInventory(String... item) {
		final int count = Inventory.getCount(item);
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Inventory.getCount(item) > count;
			}
			
		};
	}

	public static Condition depositedEquipment() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Equipment.getItems().length == 0;
			}
			
		};
	}

	public static Condition chooseOptionIsOpen() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return ChooseOption.isOpen();
			}
			
		};
	}

	public static Condition npcAppeared(Filter<RSNPC> npc) {
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return NPCs.find(npc).length>0;
			}
	
		};
	}


}
