package scripts.ezhellratcatcher.Nodes;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import scripts.Node;
import scripts.Utilities.EzBanking;
import scripts.Utilities.EzConditions;
import scripts.Utilities.Utilities;
import scripts.ezhellratcatcher.Const;
import scripts.ezhellratcatcher.Vars;
import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.AccurateMouse;

public class Catcher extends Node{

    public enum State{
        LOOTING_SPICE,INTERACTING_WITH_CAT,WAITING_FOR_CAT,COMBINE_SPICES,WALKING_TO_MOMS_HOUSE,
        ENTERING_THE_BASEMENT_OF_DOOM,INSPECTING_GROUND_ITEMS, DROPPING_EMPTY_SHAKERS, MOVING_TO_RANDOM_TILE
    }

    State state;

    @Override
    public boolean validate() {
        if(EzBanking.isInBank() && Vars.inventorySpices.length > 0){
            return false;
        }
        return Vars.hasFoundCat() && !Inventory.isFull() && (!Vars.shouldManageKitten || Vars.catFood.length > 0);
    }

    public State getState(){
        RSTile myPos = Player.getPosition();
        if(!Const.BASEMENT_OF_DOOM.contains(myPos)){
            if(Const.MOMS_HOUSE.contains(myPos)){
                return State.ENTERING_THE_BASEMENT_OF_DOOM;
            } else{
                return State.WALKING_TO_MOMS_HOUSE;
            }
        }
        if(Vars.npcCat[0].isInteractingWithMe()){
            Vars.groundSpices = GroundItems.findNearest(Vars.spiceFilter);
            if(Vars.groundSpices.length > 0){
                return State.LOOTING_SPICE;
            } else if(shouldCombineSpices()){
                return State.COMBINE_SPICES;
            } else if(Vars.shouldMove){
                return State.MOVING_TO_RANDOM_TILE;
            } else if(shouldDropEmptyShakers()){
                return State.DROPPING_EMPTY_SHAKERS;
            } else {
                return State.INTERACTING_WITH_CAT;
            }
        } else{
            return State.WAITING_FOR_CAT;
        }
    }

    @Override
    public void execute() {
        this.state = getState();
        switch(this.state){
            case COMBINE_SPICES:
                combineSpices();
                break;
            case DROPPING_EMPTY_SHAKERS:
                if(Vars.shiftDrop){
                    Utilities.shiftDrop(Const.EMPTY_SHAKER);
                } else{
                    if(Inventory.drop(Vars.emptyShakers)>0){
                        Timing.waitCondition(EzConditions.inventoryChange(false), 2500);
                    }
                }
                break;
            case ENTERING_THE_BASEMENT_OF_DOOM:
                enterBasement();
                break;
            case INTERACTING_WITH_CAT:
                if((Vars.miceCaught > 0 || Vars.lastHuntedRat != 0) && !Mouse.isInBounds()){
                    Vars.generateWaitingTime();
                }
                if(isConversing() || (ChooseOption.isOpen() && ChooseOption.select("Chase")) ||
                        AccurateMouse.click(Vars.npcCat[0],"Chase")){
                    if(waitTillChasingRat()){
                        Vars.generateTrackerInfo();
                        Timing.waitCondition(new Condition(){

                            @Override
                            public boolean active() {
                                General.sleep(100,200);
                                return !Vars.npcCat[0].isInteractingWithMe();
                            }

                        }, 6000);
                    }
                }
                break;
            case LOOTING_SPICE:
                lootSpice();
                break;
            case MOVING_TO_RANDOM_TILE:
                if(Walking.walkTo(Const.BASEMENT_OF_DOOM.getRandomTile())){
                    General.sleep(500,1000);
                    Vars.numberFailed = 0;
                    Vars.shouldMove = false;
                }
                break;
            case WAITING_FOR_CAT:
                if(Vars.shouldHover){
                    if(Vars.menuOpen){
                        if(!hasChooseOption()){
                            hoverCat(true);
                        }
                    } else if(!isHovering()){
                        hoverCat(false);
                    } else{
                        General.sleep(200,400);
                    }
                } else if(Mouse.isInBounds() && Vars.abc_util.shouldLeaveGame()){
                    Vars.abc_util.leaveGame();
                } else{
                    Vars.idleActions();
                    General.sleep(200,400);
                }
                break;
            case WALKING_TO_MOMS_HOUSE:
                if(WebWalker.walkTo(Const.MOMS_HOUSE_TILE)){
                    Timing.waitCondition(EzConditions.inArea(Const.MOMS_HOUSE), 8000);
                }
                break;
            default:
                break;

        }
    }

    private boolean isConversing(){
        return NPCChat.getClickContinueInterface() != null || NPCChat.getOptions() != null;
    }

    private boolean waitTillChasingRat(){
//		if(Vars.usingKeyboard){
//			long current = Timing.currentTimeMillis() - General.random(0, 1000); // for 5-6000ms timeout
//			Keyboard.holdKey('2',0,new Condition(){
//
//				@Override
//				public boolean active() {
//					return Timing.timeFromMark(current) > 5000 || NPCChat.getSelectOptionInterface()!=null ||
//							NPCChat.getClickContinueInterface()!=null;
//				}
//
//			});
//		} else{
//			if(Timing.waitCondition(EzConditions.isConversing(),6000)){
//				if(!NPCChat.selectOption("Chase-Vermin", true)){
//					return false;
//				}
//			}
//		}
        return Timing.waitCondition(successfullyChasedRat(), 2000);
    }

    private void lootSpice(){
        RSGroundItem spice = Vars.groundSpices[0];
        RSItemDefinition def = spice.getDefinition();
        if(def == null)
            return;
        String name = def.getName();
        if(name == null)
            return;
        if(!spice.isClickable()){
            Vars.acamera.turnToTile(spice);
            if(!Player.isMoving() && Walking.walkTo(spice)){ //Walk to it, if we aren't already
                General.sleep(200,400);
            }
        }
        if(ChooseOption.isOpen()){
            if(!ChooseOption.isOptionValid("Take " + name)){
                ChooseOption.close();
            }
        }
        if(Clicking.click("Take " + name, spice)){
            Timing.waitCondition(EzConditions.inventoryChange(true), 6000);
        }
    }

    private boolean shouldCombineSpices(){
        return 	(Vars.orange && shouldCombineSpice(Const.ORANGE)) ||
                (Vars.brown && shouldCombineSpice(Const.BROWN)) ||
                (Vars.red && shouldCombineSpice(Const.RED)) ||
                (Vars.yellow && shouldCombineSpice(Const.YELLOW));
    }

    private boolean shouldCombineSpice(Filter<RSItem> color){
        Vars.inventorySpices = Inventory.find(color);
        if(Vars.inventorySpices.length > 1){
            Vars.three = Inventory.find(color.combine(Filters.Items.nameContains("3"), true));
            Vars.two = Inventory.find(color.combine(Filters.Items.nameContains("2"), true));
            Vars.one = Inventory.find(color.combine(Filters.Items.nameContains("1"), true));
            return Vars.two.length > 1 ||
                    (Vars.three.length > 0 && Vars.one.length > 0) ||
                    (Vars.two.length > 0 &&Vars. one.length > 0) ||
                    (Vars.two.length > 0 && Vars.three.length > 0) ||
                    (Vars.one.length > 1) || (Vars.three.length > 1);
        }
        return false;
    }

    private void combineSpices(){
        if(Vars.two.length > 1){
            Vars.two = Utilities.shuffleArray(Vars.two);
            if(Vars.two[0].click()){
                General.sleep(100,200);
                if(Vars.two[1].click()){
                    General.sleep(100,200);
                }
            }
        } else if(Vars.three.length > 0 && Vars.one.length > 0){
            Vars.three = Utilities.shuffleArray(Vars.three);
            Vars.one = Utilities.shuffleArray(Vars.one);
            if(Vars.three[0].click()){
                General.sleep(100,200);
                if(Vars.one[0].click()){
                    General.sleep(100,200);
                }
            }
        } else if(Vars.two.length > 0 && Vars.one.length > 0){
            Vars.two = Utilities.shuffleArray(Vars.two);
            Vars.one = Utilities.shuffleArray(Vars.one);
            if(Vars.two[0].click()){
                General.sleep(100,200);
                if(Vars.one[0].click()){
                    General.sleep(100,200);
                }
            }
        } else if(Vars.two.length > 0 && Vars.three.length > 0){
            Vars.three = Utilities.shuffleArray(Vars.three);
            Vars.two = Utilities.shuffleArray(Vars.two);
            if(Vars.two[0].click()){
                General.sleep(100,200);
                if(Vars.three[0].click()){
                    General.sleep(100,200);
                }
            }
        } else if(Vars.one.length > 1){
            Vars.one = Utilities.shuffleArray(Vars.one);
            if(Vars.one[0].click()){
                General.sleep(100,200);
                if(Vars.one[1].click()){
                    General.sleep(100,200);
                }
            }
        } else if(Vars.three.length > 1){
            Vars.three = Utilities.shuffleArray(Vars.three);
            if(Vars.three[0].click()){
                General.sleep(100,200);
                if(Vars.three[1].click()){
                    General.sleep(100,200);
                }
            }
        }
        Timing.waitCondition(new Condition(){

            @Override
            public boolean active() {
                General.sleep(100,200);
                return !shouldCombineSpices();
            }

        }, 2000);
    }

    public static boolean enterBasement(){
        Vars.stairsDown = Objects.find(10, Const.LADDER_TO_BASEMENT);
        if(Vars.stairsDown.length > 0){
            if(!Vars.stairsDown[0].isClickable()){
                Vars.acamera.turnToTile(Vars.stairsDown[0]);
            }
            if(Filters.Objects.actionsContains("Go-down").accept(Vars.stairsDown[0]) && Clicking.click("Go-down",Vars.stairsDown[0])){
                return Timing.waitCondition(EzConditions.inArea(Const.BASEMENT_OF_DOOM),8000) && Timing.waitCondition(EzConditions.npcAppeared(Vars.myCatFilter), 5000);
            }
            return Clicking.click("Open",Vars.stairsDown[0]) &&
                    Timing.waitCondition(EzConditions.objectAppeared(2, Const.LADDER_TO_BASEMENT.combine(Filters.Objects.actionsContains("Go-down"), false)),5000)
                    && enterBasement();
        }
        return false;
    }

    private void hoverCat(boolean menuOpen){
        if(menuOpen){
            if(DynamicClicking.clickRSNPC(Vars.npcCat[0], 3)){
                if(Timing.waitCondition(EzConditions.chooseOptionIsOpen(), 1500)){
                    RSMenuNode[] nodes = ChooseOption.getMenuNodes();
                    for(RSMenuNode node:nodes){
                        if(node.containsAction("Chase")){
                            Mouse.moveBox(node.getArea());
                            return;
                        }
                    }
                }
            }
        } else{
            Vars.npcCat[0].hover();
        }
    }

    private Condition successfullyChasedRat(){
        return new Condition(){

            @Override
            public boolean active() {
                General.sleep(100,200);
                return Timing.timeFromMark(Vars.lastHuntedRat) < 2500;
            }

        };
    }

    @Override
    public String toString(){
        return state != null ? state.toString() : "";
    }

    private boolean isHovering(){
        return Game.isUptext("Pick-up");
    }

    private boolean hasChooseOption(){
        return ChooseOption.isOptionValid(new Filter<RSMenuNode>(){

            @Override
            public boolean accept(RSMenuNode arg0) {
                return arg0.correlatesTo(Vars.npcCat[0]) && arg0.containsAction("Interact");
            }

        });
    }

    private boolean shouldDropEmptyShakers(){
        Vars.emptyShakers = Inventory.find(Const.EMPTY_SHAKER);
        return Vars.emptyShakers.length >= Vars.numToDropAt;
    }
}
