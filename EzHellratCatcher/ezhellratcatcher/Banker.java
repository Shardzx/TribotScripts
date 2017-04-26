package scripts.ezhellratcatcher;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Objects;
import org.tribot.api2007.Player;

import scripts.Node.Node;
import scripts.Utilities.EzConditions;
import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.shared.helpers.BankHelper;

public class Banker extends Node{

	State state;
	
	@Override
	public void execute() {
		this.state = getState();
		switch(this.state){
		case CLOSING_BANK:
			if(Banking.close()){
				Timing.waitCondition(EzConditions.bankIsClosed(), 6000);
			}
			break;
		case DEPOSITING_ALL:
			if(Banking.depositAll() > 0) {
				Timing.waitCondition(EzConditions.inventoryChange(false), 2500);
			}
			break;
		case DROPPING_CAT:
			if(dropCat()){
				Timing.waitCondition(EzConditions.npcAppeared(Const.CATS), 6000);
			}
			break;
		case LEAVING_BASEMENT:
			exitBasement();
			break;
		case OPENING_BANK:
			if(Banking.openBank()){
				Timing.waitCondition(EzConditions.bankIsOpen(), 6000);
			}
			break;
		case WALKING_TO_BANK:
			WebWalker.walkToBank();
			break;
		case WITHDRAWING_CAT:
			if(Banking.withdraw(1,Const.CAT_NAMES)){
				Timing.waitCondition(EzConditions.inventoryChange(true), 2500);
			}
			break;
		default:
			break;
		
		}
		
	}

	@Override
	public boolean validate() {
		return !Vars.hasCat() || Inventory.isFull() || (BankHelper.isInBank() && 
				Inventory.getAll().length > Inventory.find(Const.CAT_NAMES).length);
	}
	
	public enum State{
		WALKING_TO_BANK, OPENING_BANK, DEPOSITING_ALL, WITHDRAWING_CAT, DROPPING_CAT,
		CLOSING_BANK,LEAVING_BASEMENT
	}
	
	public State getState(){
		boolean hasCat = Vars.hasCat();
		if(!hasCat||Inventory.isFull()){
			Vars.inventoryCat = Inventory.find(Const.CAT_NAMES);
			if(Vars.inventoryCat.length > 0){
				if(Banking.isBankScreenOpen()){
					return State.CLOSING_BANK;
				} else{
					return State.DROPPING_CAT;
				}
			} else if(Const.BASEMENT_OF_DOOM.contains(Player.getPosition())){
				return State.LEAVING_BASEMENT;
			} else if(Banking.isBankScreenOpen()){
				if(!hasCat && Vars.inventoryCat.length == 0){
					return State.WITHDRAWING_CAT;
				} else{
					return State.DEPOSITING_ALL;
				}
			} else {
				return State.OPENING_BANK;
			}
		}
		return getState();
	}
	
	private boolean dropCat(){
		return Vars.inventoryCat.length > 0 && Vars.inventoryCat[0].click("Drop") && 
			Timing.waitCondition(EzConditions.inventoryChange(false), 4000);
	}
	
	private boolean exitBasement(){
		Vars.stairsUp = Objects.find(10, Const.LADDER_FROM_BASEMENT);
		if(Vars.stairsUp.length > 0){
			if(!Vars.stairsUp[0].isClickable()){
				Vars.acamera.turnToTile(Vars.stairsUp[0]);
			}
			return Clicking.click("Climb",Vars.stairsUp[0]) &&
				Timing.waitCondition(EzConditions.inArea(Const.MOMS_HOUSE),8000);
		}
		return false;
	}
	
	@Override
	public String toString(){
		return state != null ? state.toString() : "";
	}
}
