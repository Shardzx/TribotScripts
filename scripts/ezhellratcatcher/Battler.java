package scripts.ezhellratcatcher;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSMenuNode;
import org.tribot.api2007.types.RSTile;
import scripts.Node;
import scripts.Utilities.EzBanking;
import scripts.Utilities.EzConditions;
import scripts.Utilities.Utilities;
import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.AccurateMouse;

public class Battler extends Node {

    public enum State {
        ENTERING_THE_BASEMENT_OF_DOOM, WALKING_TO_MOMS_HOUSE, HEALING_CAT, WATCHING_BATTLE, STARTING_BATTLE, WALKING_TO_CURTAIN

    }

    State state;

    public State getState() {
        RSTile myPos = Player.getPosition();
        if (isBattling()) {
            if (Vars.currentRatHealth == 0) {
                return State.WATCHING_BATTLE;
            } else if (needToHealMyCat()) {
                return State.HEALING_CAT;
            } else if (NPCChat.getClickContinueInterface() != null) {
                return State.STARTING_BATTLE;
            } else {
                return State.WATCHING_BATTLE;
            }
        } else if (!Const.BASEMENT_OF_DOOM.contains(myPos)) {
            if (Const.MOMS_HOUSE.contains(myPos)) {
                return State.ENTERING_THE_BASEMENT_OF_DOOM;
            } else {
                return State.WALKING_TO_MOMS_HOUSE;
            }
        } else if (foundCurtain()) {
            return State.STARTING_BATTLE;
        } else {
            return State.WALKING_TO_CURTAIN;
        }

    }


    @Override
    public boolean validate() {
        if (Vars.npcCat == null)
            return false;
        if (isBattling()) {
            return true;
        }
        if (EzBanking.isInBank() && Vars.inventorySpices.length > 0) {
            return false;
        }
        return Vars.hasFoundCat() && Vars.catFood.length > 3 && !Inventory.isFull();
    }

    @Override
    public void execute() {
        state = getState();
        switch (state) {
            case ENTERING_THE_BASEMENT_OF_DOOM:
                Catcher.enterBasement();
                break;
            case HEALING_CAT:
                if (useFoodOnCurtain()) {
                    Vars.generateTrackerInfo();
                    Vars.nextHealPercentage = Vars.abc_util.generateEatAtHP() / 100.0;
                    double current = Vars.currentCatHealth;
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100);
                            Vars.currentCatHealth = Vars.npcCat[0].getHealthPercent();
                            return Vars.currentCatHealth > current;
                        }

                    }, 3000);
                }
                break;
            case STARTING_BATTLE:
                if (!Utilities.isConversing() && AccurateMouse.click(Vars.curtain[0], "Enter")) {
                    Timing.waitCondition(EzConditions.isConversing(), 5000);
                }
                String[] options = NPCChat.getOptions();
                if (options != null && options.length > 0) {
                    if (NPCChat.selectOption("Insert your cat", false)) {
                        if (Timing.waitCondition(EzConditions.isNotConversing(), 3000)) {
                            Timing.waitCondition(EzConditions.isConversing(), 5000);
                        }
                    }
                }
                RSInterface chat = NPCChat.getClickContinueInterface();
                if (chat != null && chat.click()) {
                    if (Timing.waitCondition(new Condition() {

                        @Override
                        public boolean active() {
                            General.sleep(100);
                            return isBattling();
                        }

                    }, 5000)) {
                        Vars.generateTrackerInfo();
                        Vars.behemothsFought++;
                        Vars.behemoth = NPCs.find("Hell-Rat Behemoth");
                        break;
                    }
                }
                break;
            case WALKING_TO_CURTAIN:
                if (Walking.blindWalkTo(Vars.color.area.getRandomTile())) {
                    Timing.waitCondition(EzConditions.objectAppeared(5, Vars.color.curtain), 6000);
                }
                break;
            case WALKING_TO_MOMS_HOUSE:
                if (WebWalker.walkTo(Const.MOMS_HOUSE_TILE)) {
                    Timing.waitCondition(EzConditions.inArea(Const.MOMS_HOUSE), 8000);
                }
                break;
            case WATCHING_BATTLE:
                Vars.currentRatHealth = Vars.behemoth.length > 0 ? Vars.behemoth[0].getHealthPercent() : 0;
                if (Vars.abc_util.shouldLeaveGame()) {
                    Vars.abc_util.leaveGame();
                } else if (Mouse.isInBounds()) {
                    if (Vars.shouldHover) {
                        if (Vars.menuOpen) {
                            if (!hasChooseOption()) {
                                hoverCurtain(true);
                            }
                        } else if (!isHoveringCurtain()) {
                            hoverCurtain(false);
                        } else {
                            General.sleep(200, 400);
                        }
                    } else {
                        Vars.idleActions();
                        General.sleep(200, 400);
                    }
                } else {
                    General.sleep(200, 400);
                }
                if (Vars.currentRatHealth == 0) {
                    if (Timing.waitCondition(EzConditions.inArea(Const.BASEMENT_OF_DOOM), 8000)) {
                        Timing.waitCondition(EzConditions.npcAppeared(Vars.myCatFilter), 3000);
                    }
                }
                break;
            default:
                break;

        }
    }

    @Override
    public String toString() {
        return state == null ? "null" : state.toString();
    }

    private boolean hasCatFood() {
        Vars.catFood = Inventory.find(Vars.catFoodFilter);
        return Vars.catFood.length > 0;
    }


    public static boolean isBattling() {
        return NPCs.find(Filters.NPCs.nameEquals("Hell-Rat Behemoth")).length == 1;
    }

    public static boolean needToHealMyCat() {
        Vars.currentCatHealth = Vars.npcCat[0].getHealthPercent();
        if (Vars.abc2eat) {
            return Vars.currentCatHealth <= Vars.nextHealPercentage;
        } else {
            return (int) (Vars.currentCatHealth * 6.0) <= Vars.nextEat;
        }
    }

    private boolean foundCurtain() {
        Vars.curtain = Objects.find(5, Vars.color.curtain);
        return Vars.curtain.length > 0;
    }

    private boolean useFoodOnCurtain() {
        Vars.curtain = Objects.findNearest(2, "Curtain");
        return hasCatFood() && Vars.curtain.length > 0 && Utilities.useItemOnObject(Vars.catFood[0], Vars.curtain[0], EzConditions.inventoryChange(false));
    }

    private boolean isHoveringCurtain() {
        return Game.isUptext("Use") && Game.isUptext("-> Curtain");
    }

    private void hoverCurtain(boolean menuOpen) {
        String selected = Game.getSelectedItemName();
        if (selected == null) {
            if (!Vars.catFood[0].click()) {
                return;
            }
        }
        if (menuOpen) {
            String uptext = Game.getUptext();
            if (uptext == null || !uptext.contains("->"))
                return;
            if (DynamicClicking.clickRSObject(Vars.curtain[0], 3)) {
                if (Timing.waitCondition(EzConditions.chooseOptionIsOpen(), 1500)) {
                    RSMenuNode[] nodes = ChooseOption.getMenuNodes();
                    for (RSMenuNode node : nodes) {
                        if (node.containsAction(uptext + "Curtain")) {
                            Mouse.moveBox(node.getArea());
                            return;
                        }
                    }
                }
            }
        } else {
            Vars.curtain[0].hover();
        }
    }

    private boolean hasChooseOption() {
        String uptext = Game.getUptext();
        if (uptext == null)
            return false;
        return ChooseOption.isOptionValid(new Filter<RSMenuNode>() {

            @Override
            public boolean accept(RSMenuNode arg0) {
                return arg0.correlatesTo(Vars.curtain[0]) && arg0.containsAction(uptext + "Curtain");
            }

        });
    }

}
