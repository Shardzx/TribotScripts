package scripts.ezhellratcatcher;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import scripts.Utilities.ACamera;
import scripts.ezhellratcatcher.Nodes.Battler;

public class Vars {

    public static RSItem[] 	inventoryCat = null,
            inventorySpices = null,
            three = null,
            two = null,
            one = null,
            emptyShakers = null,
            catFood = null;


    public static RSNPC[] 	npcCat = null,
            behemoth = null;

    public static RSGroundItem[] groundSpices = null;

    public static Clickable itemToInspect = null;

    public static RSObject[]	stairsDown = null,
            stairsUp = null,
            curtain = null;

    public static int		miceCaught = 0,
            numberFailed = 0,
            numToDropAt = 1,
            mouseSpeed = 100,
            currentCatSettingValue,
            behemothsFought = 0,
            behemothsDefeated = 0,
            withdrawAmount = 3,
            nextEat = 0;

    public static long 		START_TIME,
            lastAction = 0,
            totalHuntingTime = 0,
            lastFailedHuntingRat = 0,
            lastInspectedGroundItems = 0;

    public static Filter<RSNPC>	myCatFilter = new Filter<RSNPC>(){

        @Override
        public boolean accept(RSNPC arg0) {
            return arg0.isInteractingWithMe();
        }

    }.combine(Filters.NPCs.nameContains("cat","kitten").combine(Filters.NPCs.actionsContains("Pick-up"), false),true);
    public static Filter<RSGroundItem> spiceFilter = null;
    public static Filter<RSItem> invFilter = Filters.Items.nameContains("spice (");
    public static Filter<RSItem> catFoodFilter = Filters.Items.nameContains("Raw").combine(Filters.Items.nameNotContains("pizza","pie","beef","bear","chicken","snail","leaping"), false);

    public static boolean	running = true,
            usingKeyboard = true,
            shouldHover = false,
            menuOpen = false,
            randomSolverState = false,
            catCompletedAction = false,
            shouldMove = false,
            shiftDrop = false,
            shouldManageKitten = false,
            catIsHungry = false,
            red = true,
            yellow = true,
            brown = true,
            orange = true,
            abc2Debug = false,
            idleKittenOnly = false,
            battleMode = false,
            abc2eat = true;

    public static double	nextHealPercentage,
            currentCatHealth,
            currentRatHealth;

    public static String 	myName = null;

    public static ACamera	acamera = null;

    public static ABCUtil	abc_util = null;

    public static Const.COLOR color = null;

    public static boolean hasFoundCat(){
        if(npcCat != null && npcCat.length > 0 && npcCat[0].isValid()){
            return true;
        }
        if(battleMode && Battler.isBattling()){
            npcCat = NPCs.find(Const.CATS);
            if(npcCat.length > 0){
                currentCatSettingValue = Game.getSetting(Const.CAT_SETTING);
                return true;
            }
            return false;
        }
        npcCat = NPCs.find(myCatFilter);
        if(npcCat.length > 0){
            currentCatSettingValue = Game.getSetting(Const.CAT_SETTING);
            return true;
        }
        return false;
    }

    public static boolean hasCat(){
        return Game.getSetting(Const.CAT_SETTING) != -1;
    }

    //	Setting: 447: 366834984 -> 106525992
    public static boolean hasKitten(){//I DONT KNOW IF THIS IS RELIABLE.
        return Game.getSetting(Const.CAT_SETTING) == 366834984;
    }
    public static boolean hasGrownCat(){//I DONT KNOW IF THIS IS RELIABLE.
        int setting = Game.getSetting(Const.CAT_SETTING);
        return setting >= 106525992 && setting < 366834984;
    }

    public static boolean callFollower(){
        if(GameTab.TABS.EQUIPMENT.open()){
            RSInterface callFollower = Interfaces.get(Const.CALL_FOLLOWER_MASTER,Const.CALL_FOLLOWER_CHILD);
            return callFollower != null && !callFollower.isHidden() && callFollower.click();
        }
        return false;
    }

    public static void idleActions(){
        if (abc_util.shouldCheckTabs()){
            if(abc2Debug) General.println("ABC2: checking tabs");
            abc_util.checkTabs();
        }

        if (abc_util.shouldCheckXP()){
            if(abc2Debug) General.println("ABC2: checking xp");
            abc_util.checkXP();
        }

        if (abc_util.shouldExamineEntity()){
            if(abc2Debug) General.println("ABC2: examining entity");
            abc_util.examineEntity();
        }

        if (abc_util.shouldMoveMouse()){
            if(abc2Debug) General.println("ABC2: moving mouse");
            abc_util.moveMouse();
        }

        if (abc_util.shouldPickupMouse()){
            if(abc2Debug) General.println("ABC2: picking up mouse");
            abc_util.pickupMouse();
        }

        if (abc_util.shouldRightClick()){
            if(abc2Debug) General.println("ABC2: right clicking");
            abc_util.rightClick();
        }

        if (abc_util.shouldRotateCamera()){
            if(abc2Debug) General.println("ABC2: rotating camera");
            abc_util.rotateCamera();
        }
    }

    public static void generateWaitingTime(){
        final int waiting_time=(int) (lastAction > 0 ? Timing.timeFromMark(lastAction) : 6000);
        totalHuntingTime+=waiting_time;
        shouldHover=abc_util.shouldHover();
        menuOpen=shouldHover&&abc_util.shouldOpenMenu();
        final ABCProperties props = abc_util.getProperties();
        props.setWaitingTime(waiting_time);
        props.setHovering(shouldHover);
        props.setMenuOpen(menuOpen);
        props.setUnderAttack(battleMode);
        props.setWaitingFixed(false);

        // Generate the reaction time
        final int reaction_time = abc_util.generateReactionTime();

        //custom abc2 reaction sleep so that our kitty doesn't die
        if(Vars.battleMode){
            Timing.waitCondition(new Condition(){

                @Override
                public boolean active() {
                    General.sleep(50);
                    if(Battler.needToHealMyCat() || Mouse.isInBounds()){
                        return true;
                    }
                    return false;
                }

            }, reaction_time);
            return;
        }
        // Sleep for the reaction time
        try {
            if(abc2Debug) General.println("ABC2: reaction time "+reaction_time);
            abc_util.sleep(reaction_time);
        } catch (final InterruptedException e) {
            if(abc2Debug) General.println("ABC2: reaction cancelled because: " + e.getMessage());
        }
    }
    public static void generateTrackerInfo(){
        final int est_waiting;
        if(miceCaught>0)
            est_waiting=(int) (totalHuntingTime/miceCaught);
        else
            est_waiting=6000;
        abc_util.generateTrackers(abc_util.generateBitFlags(est_waiting));
    }



}
