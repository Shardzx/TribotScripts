package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EzConditions {
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
	public static Condition itemLeftInventory(int... item) {
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
	public static Condition itemEnteredInventory(int... item) {
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

	public static Condition interfaceVisible(final int id,final int child,final int component){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(id,child);
				if(inter==null)
					return false;
				inter = inter.getChild(component);
				return inter != null && !inter.isHidden();
			}
		};

	}
	public static Condition interfaceVisible(final int id){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(id);

				return inter != null && !inter.isHidden();
			}
		};

	}

	public static Condition interfaceNotVisible(final int id,final int child,final int component){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(id,child);
				if(inter==null)
					return false;
				inter = inter.getChild(component);
				return inter == null || inter.isHidden();
			}
		};

	}

	public static Condition varbitChanged(final RSVarBit varbit, final int newValue){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return varbit.getValue()==newValue;
			}

		};
	}

	public static Condition varbitChanged(final int id, final int newValue){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSVarBit newBit = RSVarBit.get(id);
				return newBit != null && newBit.getValue() == newValue;
			}

		};
	}

    public static Condition areAnimating() {
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getAnimation() != -1;
			}

		};
    }
    public static Condition notAnimating() {
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Player.getAnimation() == -1;
			}

		};
	}

	public static Condition objectVisible(String... name){
		List<String> names = new ArrayList(Arrays.asList(name));
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				RSObject[] o = Objects.findNearest(10,new Filter<RSObject>(){

					@Override
					public boolean accept(RSObject obj) {
						RSObjectDefinition def = obj.getDefinition();
						if(def == null)
							return false;
						String name = def.getName();
						if(name == null)
							return false;
						return names.contains(name) && obj.isOnScreen();
					}
				});
				return o.length > 0;
			}
		};
	}

	public static Condition tileEquals(Positionable tile) {
		return new Condition() {
			@Override
			public boolean active() {
				General.sleep(100);
				return Player.getPosition().equals(tile);
			}

			;
		};
	}
}
