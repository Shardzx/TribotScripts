package scripts.ezhellratcatcher.Nodes;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.Player;
import scripts.Node;
import scripts.Utilities.EzConditions;
import scripts.ezhellratcatcher.Const;
import scripts.ezhellratcatcher.Vars;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.AccurateMouse;

public class CatManager extends Node {

    @Override
    public boolean validate() {
        return Vars.shouldManageKitten && Vars.catIsHungry || Vars.idleKittenOnly;
    }

    @Override
    public void execute() {
        if (Player.getPosition().distanceTo(Vars.npcCat[0]) > 7 &&
                Vars.callFollower()) {
            Timing.waitCondition(EzConditions.npcAppeared(Const.CATS), 5000);
        }if(Vars.catIsHungry ){
            Vars.generateWaitingTime();
            if ((Game.isUptext("->") || Vars.catFood[0].click("Use")) &&
                    Clicking.click("Use " + Vars.catFood[0].getDefinition().getName() + " -> " + Vars.npcCat[0].getName(),Vars.npcCat[0])) {
                if (Timing.waitCondition(new Condition() {

                    @Override
                    public boolean active() {
                        General.sleep(100);
                        return Player.getAnimation() != -1;
                    }

                }, 6000)) {
                    Vars.generateWaitingTime();
                    General.sleep(400, 800);
                }
            }
        } else if(Vars.catWantsAttention){
            Vars.generateWaitingTime();
            if(NPCChat.getOptions() == null){
                if(AccurateMouse.click(Vars.npcCat[0],"Interact")){
                    Timing.waitCondition(EzConditions.isConversing(),6000);
                }
            }
            if(NPCChat.getOptions() != null){
                if(NPCChat.selectOption("Stroke",true)){
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100);
                            return !Vars.catWantsAttention;
                        }
                    },3000);
                }
            }
        } else {
            if(Vars.npcCat[0].getName().contains("cat")){
                General.println("We have a cat following us! Ending idle mode.");
                Vars.running = false;
            }
            if(NPCChat.getClickContinueInterface() != null){
                NPCChat.clickContinue(true);
            }
            if(Mouse.isInBounds()) {
                Vars.idleActions();
            } else if(Vars.abc_util.shouldLeaveGame()){
                Vars.abc_util.leaveGame();
            }
            General.sleep(1000, 1500);
        }
    }

    @Override
    public String toString() {
        return "Cat Manager";
    }

}
