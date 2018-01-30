package scripts.ezhellratcatcher.Nodes;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import scripts.Node;
import scripts.Utilities.EzBanking;
import scripts.Utilities.EzConditions;
import scripts.ezhellratcatcher.Const;
import scripts.ezhellratcatcher.Vars;
import scripts.webwalker_logic.WebWalker;

public class Banker extends Node{

    State state;

    boolean hasFoundCat, needToWithdrawFood, inventoryIsFull, needToDeposit;

    @Override
    public void execute() {
        this.state = getState();
        switch(this.state){
            case CALLING_CAT://waits 1.5-2.5 sec for cat to appear.
                if(!Timing.waitCondition(new Condition(){

                    @Override
                    public boolean active() {
                        General.sleep(100);
                        return Vars.hasFoundCat();
                    }

                }, General.random(1500,2500))){
                    if(Vars.callFollower()){
                        Timing.waitCondition(EzConditions.npcAppeared(Const.CATS), 5000);
                    }
                }
                break;
            case CLOSING_BANK:
                if(Banking.close()){
                    Timing.waitCondition(EzConditions.bankIsClosed(), 6000);
                }
                break;
            case DEPOSITING_ALL:
                EzBanking.depositAll(true);
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
                if(WebWalker.walkToBank()){
                    Timing.waitCondition(EzConditions.isInBank(), 6000);
                }
                break;
            case WITHDRAWING_CAT:
                if(EzBanking.withdraw(1,true,Const.CAT_NAMES)){
                    Timing.waitCondition(EzConditions.inventoryChange(true), 2500);
                }
                break;
            case WITHDRAWING_CAT_FOOD:
                EzBanking.withdraw(Vars.withdrawAmount,true,Vars.catFoodFilter);
                break;
            default:
                break;

        }

    }

    @Override
    public boolean validate() {
        hasFoundCat = Vars.hasFoundCat();
        Vars.inventorySpices = Inventory.find(Filters.Items.nameContains("spice ("));
        Vars.catFood = Inventory.find(Vars.catFoodFilter);
        if(!hasFoundCat)
            return true;
        if(Vars.battleMode){
            needToWithdrawFood = (Vars.catFood.length < 4 && !Battler.isBattling()) || (Vars.catFood.length == 0 && Vars.npcCat[0].getHealthPercent() <= .5);
            needToDeposit = EzBanking.isInBank() && Vars.inventorySpices.length > 0;
            return needToWithdrawFood || needToDeposit;
        }
        if(Vars.shouldManageKitten){
            needToWithdrawFood = Vars.catFood.length == 0;
            if(needToWithdrawFood){
                return true;
            }
        }
        if(Vars.idleKittenOnly){
            return !EzBanking.isInBank() || Banking.isBankScreenOpen();
        }
        inventoryIsFull = Inventory.isFull();
        if(inventoryIsFull)
            return true;
        needToDeposit = EzBanking.isInBank() &&
                (Vars.inventorySpices.length > 0 || (Vars.shouldManageKitten ? false : Vars.catFood.length > 0));
        return needToDeposit;
//		return !Vars.hasCat() || Inventory.isFull() || (BankHelper.isInBank() &&
//				Inventory.getAll().length > Inventory.find(Const.CAT_NAMES).length + (Vars.shouldManageCat ? Vars.catFood.length : 0));
    }

    public enum State{
        WALKING_TO_BANK, OPENING_BANK, DEPOSITING_ALL, WITHDRAWING_CAT, DROPPING_CAT,
        CLOSING_BANK,LEAVING_BASEMENT, WITHDRAWING_CAT_FOOD, CALLING_CAT
    }

    public State getState(){
        Vars.inventoryCat = Inventory.find(Const.CAT_NAMES);
        if(Vars.inventoryCat.length > 0){
            if(Banking.isBankScreenOpen()){
                return State.CLOSING_BANK;
            } else{
                return State.DROPPING_CAT;
            }
        } else if(!hasFoundCat && Vars.hasCat()){
            if(Banking.isBankScreenOpen()){
                return State.CLOSING_BANK;
            } else{
                return State.CALLING_CAT;
            }
        } else if(Const.BASEMENT_OF_DOOM.contains(Player.getPosition()) || (Vars.battleMode && Battler.isBattling())){
            return State.LEAVING_BASEMENT;
        } else if(Banking.isInBank()){
            if(Banking.isBankScreenOpen()){
                if(inventoryIsFull || needToDeposit){
                    return State.DEPOSITING_ALL;
                } else if(!hasFoundCat && Vars.inventoryCat.length == 0){
                    return State.WITHDRAWING_CAT;
                } else if(needToWithdrawFood){
                    return State.WITHDRAWING_CAT_FOOD;
                } else{
                    return State.CLOSING_BANK;
                }
            } else {
                return State.OPENING_BANK;
            }
        } else {
            return State.WALKING_TO_BANK;
        }
    }

    private boolean dropCat(){
        return Vars.inventoryCat.length > 0 && Vars.inventoryCat[0].click("Drop") &&
                Timing.waitCondition(EzConditions.inventoryChange(false), 4000);
    }

    private boolean exitBasement(){
        Vars.stairsUp = Objects.find(20, Const.LADDER_FROM_BASEMENT);
        if(Vars.stairsUp.length > 0){
            if(!Vars.stairsUp[0].isClickable()){
                Vars.acamera.turnToTile(Vars.stairsUp[0]);
                if(Player.getPosition().distanceTo(Vars.stairsUp[0]) > 8){
                    if(Walking.walkTo(Vars.stairsUp[0])){
                        Timing.waitCondition(EzConditions.objectVisible(Vars.stairsUp[0]), 5000);
                    }
                }
            }
            return Clicking.click("Climb",Vars.stairsUp[0]) &&
                    Timing.waitCondition(EzConditions.inArea(Const.MOMS_HOUSE),8000) &&
                    Timing.waitCondition(EzConditions.npcAppeared(Vars.myCatFilter), 2500);
        }
        return false;
    }

    @Override
    public String toString(){
        return state != null ? state.toString() : "";
    }
}
