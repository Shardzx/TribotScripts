package scripts.ezhellratcatcher.Nodes;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import scripts.Node;
import scripts.Utilities.EzConditions;
import scripts.ezhellratcatcher.Const;
import scripts.ezhellratcatcher.Vars;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.AccurateMouse;

public class CatManager extends Node {

    @Override
    public boolean validate() {
        return Vars.shouldManageKitten && Vars.catIsHungry;
    }

    @Override
    public void execute() {
        if (Player.getPosition().distanceTo(Vars.npcCat[0]) > 7 &&
                Vars.callFollower()) {
            Timing.waitCondition(EzConditions.npcAppeared(Const.CATS), 5000);
        }
        if ((Game.isUptext("->") || Vars.catFood[0].click("Use")) &&
                AccurateMouse.click(Vars.npcCat[0], "Use " + Vars.catFood[0].getDefinition().getName() + " ->")) {
            if (Timing.waitCondition(new Condition() {

                @Override
                public boolean active() {
                    General.sleep(100);
                    return Player.getAnimation() != -1;
                }

            }, 6000)) {
                General.sleep(400, 800);
            }
        }
    }

    @Override
    public String toString() {
        return "FEEDING CAT";
    }

}
