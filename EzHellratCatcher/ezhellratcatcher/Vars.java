package scripts.ezhellratcatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;

import scripts.Utilities.ACamera;

public class Vars {
	
	public static RSItem[] 	inventoryCat = null,
							inventorySpices = null,
							three = null,
							two = null,
							one = null,
							emptyShakers = null;
	
	public static RSNPC[] 	npcCat = null;
	
	public static RSGroundItem[] groundSpices = null;
	
	public static Clickable itemToInspect = null;
	
	public static RSObject[]	stairsDown = null,
								stairsUp = null;
	
	public static int		miceCaught = 0,
							numberFailed = 0,
							numToDropAt = 1;
	
	public static long 		START_TIME,
							lastHuntedRat = 0,
							totalHuntingTime = 0,
							lastFailedHuntingRat = 0,
							lastInspectedGroundItems = 0;
	
	public static Filter<RSGroundItem> spiceFilter = Filters.GroundItems.nameContains("Orange spice","(4)")
			.combine(Filters.GroundItems.nameNotContains("Red"), true);
	public static Filter<RSItem> invFilter = Filters.Items.nameContains("Orange spice","(4)")
			.combine(Filters.Items.nameNotContains("Red"), true);;
	
	public static boolean	running = true,	
							usingKeyboard = true,
							shouldHover = false,
							menuOpen = false,
							randomSolverState = true,
							catCompletedAction = false,
							shouldMove = false,
							shiftDrop = false,
							shouldManageCat = false,
							catIsHungry = false;
	
	public static String 	myName = null;
	
	public static ACamera	acamera = null;
	
	public static ABCUtil	abc_util = null;
	
	public static boolean hasCat(){
		if(npcCat != null && npcCat.length > 0 && npcCat[0].isValid()){
			return true;
		}
		npcCat = NPCs.findNearest(Filters.NPCs.nameContains("cat","kitten").combine(Filters.NPCs.actionsContains("Pick-up"), false));
		return npcCat.length > 0;
	}
	
	public static void idleActions(){
		if (abc_util.shouldCheckTabs()){
			General.println("ABC2: checking tabs");
			abc_util.checkTabs();
		}

		if (abc_util.shouldCheckXP()){
			General.println("ABC2: checking xp");
			abc_util.checkXP();
		}

		if (abc_util.shouldExamineEntity()){
			General.println("ABC2: examining entity");
			abc_util.examineEntity();
		}

		if (abc_util.shouldMoveMouse()){
			General.println("ABC2: moving mouse");
			abc_util.moveMouse();
		}

		if (abc_util.shouldPickupMouse()){
			General.println("ABC2: picking up mouse");
			abc_util.pickupMouse();
		}

		if (abc_util.shouldRightClick()){
			General.println("ABC2: right clicking");
			abc_util.rightClick();
		}

		if (abc_util.shouldRotateCamera()){
			General.println("ABC2: rotating camera");
			abc_util.rotateCamera();
		}
	}
	
	public static void generateWaitingTime(){
		final int waiting_time=(int) Timing.timeFromMark(lastHuntedRat);
		totalHuntingTime+=waiting_time;
		shouldHover=abc_util.shouldHover();
		menuOpen=shouldHover&&abc_util.shouldOpenMenu();
		final ABCProperties props = abc_util.getProperties();
		props.setWaitingTime(waiting_time);
		props.setHovering(shouldHover);
		props.setMenuOpen(menuOpen);
		props.setUnderAttack(false);
		props.setWaitingFixed(false);
		// Generate the reaction time
		final int reaction_time = abc_util.generateReactionTime();

		// Sleep for the reaction time
		try {
			General.println("abc2 reaction time: "+reaction_time);
			abc_util.sleep(reaction_time);
		} catch (final InterruptedException e) {

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
