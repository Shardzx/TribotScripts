package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;

public class EzConditions {
	
	
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
	
	
	
	public static Condition npcAppeared(final String... name){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return NPCs.find(name).length>0;
			}
	
		};
	}
	
	
	
	public static Condition npcVisible(final String name){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				RSNPC[] npcs = NPCs.findNearest(name);
				return npcs.length>0 && npcs[0].isOnScreen();
			}
	
		};
	}
	
	public static Condition npcVisible(final int ID){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				RSNPC[] npcs = NPCs.findNearest(ID);
				return npcs.length>0 && npcs[0].isOnScreen();
			}
	
		};
	}
	
	public static Condition npcVisible(final Filter<RSNPC> filter){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				RSNPC[] npcs = NPCs.findNearest(filter);
				return npcs.length>0 && npcs[0].isOnScreen();
			}
	
		};
	}
	
	
	public static Condition isInBank(){
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return Banking.isInBank();
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
	
	public static Condition notInArea(final RSArea area){
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return !area.contains(Player.getPosition());
			}
		};
	}
	
	public static Condition empty(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return false;
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
	
	public static Condition interfaceUp(int id){
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
	
	public static Condition interfaceNotUp(int id){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Interfaces.get(id) == null;
			}		
		};
	}
	
	public static Condition expGained(final long startXP, final Skills.SKILLS skill){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Skills.getXP(skill)>startXP;
			}
			
		};
	}

}
