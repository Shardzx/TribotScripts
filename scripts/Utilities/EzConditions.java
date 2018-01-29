package scripts.Utilities;

import java.awt.Point;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.ChooseOption;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.GameTab.TABS;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;
import org.tribot.api2007.Prayer;
import org.tribot.api2007.Prayer.PRAYERS;
import org.tribot.api2007.Projection;
import org.tribot.api2007.Skills;
import org.tribot.api2007.Trading;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.ext.Ships;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.types.RSVarBit;

import scripts.Shop;
import scripts.ezfarm.Universal;

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
	
	public static Condition interfaceNotUp(final int master,final int child)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(master,child);
				return inter == null || inter.isHidden();
			}		
		};
	}
	
	public static Condition interfaceNotUp(final int master,final int child,final int component)
	{
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				RSInterface inter = Interfaces.get(master,child);
				if(inter==null){
					return false;
				}
				inter = inter.getChild(component);
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
	
	public static Condition interfaceUp(final int id,final int child){
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Interfaces.get(id,child) != null;
			}		
		};
	
	}
	
	public static Condition interfaceUp(final int id,final int child,final int component){
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
				return inter != null;
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
	
	public static Condition varbitChanged(final RSVarBit varbit){
		final int value = varbit.getValue();
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return varbit.getValue()!=value;
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
	
	public static Condition settingChanged(final int setting){
		final int value = Game.getSetting(setting);
		return new Condition()
		{
			@Override
			public boolean active()
			{
				General.sleep(100);
				return Game.getSetting(setting)!=value;
			}		
			
		};
	}
	
	public static Condition notAnimating(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Player.getAnimation()==-1;
			}
			
		};
	}
	
	public static Condition areAnimating(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Player.getAnimation()!=-1;
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
	
	public static Condition isMovingToTile(final RSTile tile){
		Timing.waitCondition(new Condition(){

			@Override
			public boolean active() {
				General.sleep(50);
				return Player.isMoving() || Game.getDestination() != null;
			}
			
		},500);
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				RSTile destination = Game.getDestination();
				return Player.getPosition().equals(tile)||Player.isMoving()&&destination!=null&&destination.equals(tile);
			}
	
		};
	}
	
	public static Condition objectDisappeared(final int distance, final Filter<RSObject> filter){
		final int count = Objects.find(distance, filter).length;
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Objects.find(distance, filter).length < count;
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
	
	public static Condition npcDisappeared(final String name){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return NPCs.find(name).length==0;
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
	
	public static Condition statRestored(final SKILLS skill){
		final int current = Skills.getCurrentLevel(skill);
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Skills.getCurrentLevel(skill)>current;
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
	
	public static Condition isBetween(final RSTile tile1, final RSTile tile2){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Utilities.isBetween(tile1, tile2);
			}
			
		};
	}
	
	public static Condition hpChanged(){
		final int currentHP = Skills.getCurrentLevel(SKILLS.HITPOINTS);
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return Skills.getCurrentLevel(SKILLS.HITPOINTS)!=currentHP;
			}
		};
	}
	
	public static Condition hpGained(){
		final int currentHP = Skills.getCurrentLevel(SKILLS.HITPOINTS);
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return Skills.getCurrentLevel(SKILLS.HITPOINTS)>currentHP;
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
	
	public static Condition npcChatChanged(){
		final RSInterface current = NPCChat.getClickContinueInterface();
		if(current==null){
			return new Condition()
			{

				@Override
				public boolean active() {
					General.sleep(100);
					return Utilities.isConversing();
				}
				
			};
		}
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				RSInterface newInterface = NPCChat.getClickContinueInterface();
				return (newInterface != null && !newInterface.equals(current)) || NPCChat.getOptions()!=null;
			}
			
		};
	}
	
	public static Condition tradeWindowChanged(){
		final Trading.WINDOW_STATE ws = Trading.getWindowState();
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				Trading.WINDOW_STATE current = Trading.getWindowState();
				return ws == null ? current != null : (current == null || current != ws);
			}
	
		};
	}
	
	public static Condition tradeWindowChanged(boolean hasAccepted){
		final Trading.WINDOW_STATE ws = Trading.getWindowState();
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				Trading.WINDOW_STATE current = Trading.getWindowState();
				return (hasAccepted ? !Trading.hasAccepted(false) : true) || ws == null ? current != null : (current == null || current != ws);
			}
	
		};
	}
	
	public static Condition areNotesOn(){
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Utilities.areNotesOn();
			}
	
		};
	}

	public static Condition objectVisible(String name) {
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				RSObject[] obj = Objects.find(10, name);
				return obj.length>0 && obj[0].isOnScreen() && obj[0].isClickable();
			}
	
		};
	}

	public static Condition isEquipped(String... dueling) {
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return Equipment.find(dueling).length > 0;
			}
		};
	}

	public static Condition isTabOpen(TABS tab) {
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(100);
				return GameTab.getOpen() == tab;
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

	public static Condition objectAppeared(int distance, int id) {
		final int currentCount = Objects.find(distance, id).length;
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return Objects.find(distance, id).length>currentCount;
			}
	
		};
	}

	public static Condition itemLeftInventory(int... id) {
		final int count = Inventory.getCount(id);
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Inventory.getCount(id) < count;
			}
			
		};
	}

	public static Condition planeChanged(boolean increasing) {
		RSTile position = Player.getPosition();
		final int plane = position.getPlane();
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return increasing ? Player.getPosition().getPlane() > plane : Player.getPosition().getPlane() < plane;
			}
			
		};
	}

	public static Condition isPrayerDeactivated(PRAYERS... boostingPrayers) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				for(PRAYERS prayer:boostingPrayers){
					if(Prayer.isPrayerEnabled(prayer))
						return false;
				}
				return true;
			}
			
		};
	}
	
	public static Condition isPrayerActivated(PRAYERS... boostingPrayers) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				for(PRAYERS prayer:boostingPrayers){
					if(!Prayer.isPrayerEnabled(prayer))
						return false;
				}
				return true;
			}
			
		};
	}

	public static Condition skillIncreased(SKILLS prayer) {
		final int current = Skills.getCurrentLevel(prayer);
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Skills.getCurrentLevel(prayer) > current;
			}
			
		};
	}

	public static Condition npcVisible(RSCharacter npc) {
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return npc.isOnScreen();
			}
	
		};
	}
	
	public static Condition npcNotVisible(RSCharacter npc) {
		return new Condition()
		{

			@Override
			public boolean active() {
				General.sleep(100);
				return npc == null || !npc.isOnScreen();
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

	public static Condition bankItemsAreLoaded() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Utilities.isBankItemsLoaded();
			}
			
		};
	}

	public static Condition haveAnimation(int id) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(50);
				return Player.getAnimation() == id;
			}
			
		};
	}

	public static Condition isEquipped(int... id) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Equipment.isEquipped(id);
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
	
	public static Condition waitAreaAndHoverTile(RSArea area,RSTile tileToHover){
		return new Condition()
		{
			@Override
			public boolean active() {
				General.sleep(50);
				Point p = Projection.tileToMinimap(tileToHover);
				if(Player.getPosition().distanceTo(tileToHover) < 20 && Mouse.getPos().distance(p)>5){
					Mouse.move(p);
				}
				return area.contains(Player.getPosition());
			}
			
		};
	}

	public static Condition isRunOn() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Game.isRunOn();
			}
			
		};
	}

	public static Condition isOnShip(boolean currentlyOn) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return currentlyOn ? !Ships.isOnShip() : Ships.isOnShip();
			}
			
		};
	}

	public static Condition tileEquals(Positionable tile){
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Player.getPosition().equals(tile);
			}
			
		};
	}

	public static Condition hasOffered(boolean otherPlayer,String name, int count) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return EzTrading.getCount(otherPlayer, name) >= count;
			}
			
		};
	}

	public static Condition hasOffered(boolean otherPlayer, String name) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return EzTrading.getCount(otherPlayer, name) >= 0;
			}
			
		};
	}

	public static Condition shopIsOpen() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Shop.isShopOpen();
			}
			
		};
	}
	
	public static Condition shopIsClosed() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return !Shop.isShopOpen();
			}
			
		};
	}

	public static Condition retaliateOn() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Combat.isAutoRetaliateOn();
			}
			
		};
	}

	public static Condition isInWild() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Utilities.getWildLevel() > 0;
			}
			
		};
	}

	public static Condition isInCombat() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Combat.getAttackingEntities().length > 0 || Combat.getTargetEntity() != null;
			}
			
		};
	}

	public static Condition destinationNotNull() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Game.getDestination() != null;
			}
			
		};
	}

	public static Condition targetNotNull() {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Combat.getTargetEntity() != null;
			}
			
		};
	}

	public static Condition itemNotInInventory(String... items) {
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Inventory.find(items).length == 0;
			}
			
		};
	}

	
	public static Condition isGameLoaded(){
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Universal.isGameLoaded();
			}
			
		};
	}
	public static Condition isGameNotLoaded(){
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return !Universal.isGameLoaded();
			}
			
		};
	}
	
	public static Condition distanceToTileLessThan(RSTile tile, int distance){
		return new Condition(){

			@Override
			public boolean active() {
				General.sleep(100);
				return Player.getPosition().distanceTo(tile) <= distance;
			}
		};
	}
}
