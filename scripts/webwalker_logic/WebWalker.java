package scripts.webwalker_logic;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Game;
import org.tribot.api2007.Magic;
import org.tribot.api2007.Options;
import org.tribot.api2007.Player;
import org.tribot.api2007.WebWalking;
import org.tribot.api2007.types.RSTile;

import scripts.EzFalconry;
import scripts.Teleports.Dueling;
import scripts.Teleports.Glory;
import scripts.webwalker_logic.local.walker_engine.WalkerEngine;
import scripts.webwalker_logic.local.walker_engine.WalkingCondition;
import scripts.webwalker_logic.local.walker_engine.bfs.BFS;
import scripts.webwalker_logic.shared.helpers.BankHelper;
import scripts.webwalker_logic.teleport_logic.TeleportManager;

import java.util.ArrayList;

public class WebWalker {

    private static final WalkingCondition EMPTY_WALKING_CONDITION = () -> WalkingCondition.State.CONTINUE_WALKER;
    private final String version = "1.1.2";

    private static WebWalker instance;
    private boolean logging;
    private WalkingCondition globalWalkingCondition;
    public static int runAt;
    
    public static boolean useABC = true;
    public static boolean useRun = true;
	public static boolean stuckFix = true;
    private static int failedPathCount = 0;

    private org.tribot.api.util.ABCUtil abcUtilV1;
    public static ABCUtil abcUtilV2;

    private WebWalker() {
    	if(useABC){
            abcUtilV2 = new ABCUtil();
            runAt = abcUtilV2.generateRunActivation();
            
    	} else {
    		runAt = General.random(45,70);
    	}
    	logging = true;
    	globalWalkingCondition = () -> {
            if (useRun && !Game.isRunOn() && Game.getRunEnergy() > runAt){
                Options.setRunOn(true);
                runAt = useABC ? abcUtilV2.generateRunActivation() : General.random(45,70);
            }
            return WalkingCondition.State.CONTINUE_WALKER;
        };
    }

    private WebWalker(ABCUtil util) {
        logging = true;
        abcUtilV2 = util;
        runAt = abcUtilV2.generateRunActivation();
        globalWalkingCondition = () -> {
            if (useRun && !Game.isRunOn() && Game.getRunEnergy() > runAt){
                Options.setRunOn(true);
                runAt = abcUtilV2.generateRunActivation();
            }
            return WalkingCondition.State.CONTINUE_WALKER;
        };
    }

    private WebWalker(org.tribot.api.util.ABCUtil util) {
        logging = true;
        abcUtilV1 = util;
        runAt = abcUtilV1.INT_TRACKER.NEXT_RUN_AT.next();
        globalWalkingCondition = () -> {
            if (!Game.isRunOn() && Game.getRunEnergy() > runAt){
                Options.setRunOn(true);
                runAt = abcUtilV1.INT_TRACKER.NEXT_RUN_AT.next();
                abcUtilV1.INT_TRACKER.NEXT_RUN_AT.reset();
            }
            return WalkingCondition.State.CONTINUE_WALKER;
        };
    }

    private static WebWalker getInstance(){
        return instance != null ? instance : (instance = new WebWalker());
    }

    private static WebWalker getInstance(ABCUtil abcUtil){
        return instance != null ? instance : (instance = new WebWalker(abcUtil));
    }

    private static WebWalker getInstance(org.tribot.api.util.ABCUtil abcUtil){
        return instance != null ? instance : (instance = new WebWalker(abcUtil));
    }

    /**
     *
     * @return Whether the walker will be outputting debug.
     */
    public static boolean isLogging(){
        return getInstance().logging;
    }

    /**
     *
     * @param value True to output debug.
     */
    public static void setLogging(boolean value){
        getInstance().logging = value;
    }

    public enum Offset {
        NONE (0), LOW (8), MEDIUM (16), HIGH (24), VERY_HIGH(32);
        private int value;
        Offset (int value){
            this.value = value;
        }
    }

    /**
     *
     * @param offset How much we deviate from the original path.
     */
    public static void setPathOffset(Offset offset){
        BFS.OFFSET_SEARCH = offset.value;
    }

    /**
     * Sets the global walking condition. {@code walkingCondition} will be automatically
     * set in wall webwalker calls.
     *
     * @param walkingCondition global walking condition
     */
    public static void setGlobalWalkingCondition(WalkingCondition walkingCondition){
        getInstance().globalWalkingCondition = walkingCondition;
    }

    /**
     *
     * @return version number of webwalker.
     */
    public static String getVersion(){
        return getInstance().version;
    }

    /**
     *
     * @param destination
     * @return Whether destination was successfully reached.
     */
    public static boolean walkTo(RSTile destination){
        return walkTo(destination, EMPTY_WALKING_CONDITION);
    }

    /**
     *
     * @param destination
     * @param walkingCondition Refer to @{@link WalkingCondition}. #WalkingCondition action()
     *                         is called roughly 3- 5 times every second unless walker engine
     *                         is handling an object.
     * @return Whether destination was successfully reached or depending on what your walking
     *         condition returns.
     */
    public static boolean walkTo(RSTile destination, WalkingCondition walkingCondition){
    	if(WebPathCore.serverDown){
    		return WebWalking.walkTo(destination);
    	}
        if (Player.getPosition().equals(destination)){
            return true;
        } else if(EzFalconry.FALCONRY_AREA.contains(Player.getPosition()) && !EzFalconry.FALCONRY_AREA.contains(destination)){
        	if(Dueling.getRing() != null){
        		General.println("TELEPORTING FROM WEBWALKER CUZ FAILED PATH");
    			Dueling.teleport(Dueling.LOCATIONS.CASTLE_WARS, true);
    		} else if(Glory.getAmulet() != null){
				Glory.teleport(Glory.LOCATIONS.EDGEVILLE, true);
			} else{
				if(Magic.selectSpell("Lumbridge Home Teleport")){
					RSTile myPos = Player.getPosition();
					Timing.waitCondition(new Condition(){
						@Override
						public boolean active() {
							General.sleep(100,200);
							return !myPos.equals(Player.getPosition());
						}
					}, 20000);
				}
			}
        }
        ArrayList<RSTile> path = WebPath.getPath(destination);
        if (path.size() == 0){
        	failedPathCount++;
        	if(stuckFix && failedPathCount > 50 && destination.distanceTo(Player.getPosition()) > 10){
        		failedPathCount = 0;
        		if(Dueling.getRing() != null){
        			Dueling.teleport(Dueling.LOCATIONS.CASTLE_WARS, true);
        		} else if(Glory.getAmulet() != null){
    				Glory.teleport(Glory.LOCATIONS.EDGEVILLE, true);
    			} else{
    				if(Magic.selectSpell("Lumbridge Home Teleport")){
    					RSTile myPos = Player.getPosition();
    					Timing.waitCondition(new Condition(){
    						@Override
    						public boolean active() {
    							General.sleep(100,200);
    							return !myPos.equals(Player.getPosition());
    						}
    					}, 20000);
    				}
    			}
        	}
            return false;
        } else{
        	failedPathCount = 0;
        }
        ArrayList<RSTile> bestPath = TeleportManager.teleport(path.size(), destination);
        if (bestPath != null){
            path = bestPath;
        }

        return WalkerEngine.getInstance().walkPath(path, walkingCondition.combine(getInstance().globalWalkingCondition));
    }

    public static boolean walkToBank(){
        return walkToBank(EMPTY_WALKING_CONDITION);
    }

    public static boolean walkToBank(WalkingCondition walkingCondition) {
    	if(WebPathCore.serverDown){
    		return WebWalking.walkToBank();
    	}
        if (BankHelper.isInBank()){
            System.out.println("already in bank");
            return true;
        } else if(EzFalconry.FALCONRY_AREA.contains(Player.getPosition())){
        	if(Dueling.getRing() != null){
    			Dueling.teleport(Dueling.LOCATIONS.CASTLE_WARS, true);
    		} else if(Glory.getAmulet() != null){
				Glory.teleport(Glory.LOCATIONS.EDGEVILLE, true);
			} else{
				if(Magic.selectSpell("Lumbridge Home Teleport")){
					RSTile myPos = Player.getPosition();
					Timing.waitCondition(new Condition(){
						@Override
						public boolean active() {
							General.sleep(100,200);
							return !myPos.equals(Player.getPosition());
						}
					}, 20000);
				}
			}
        }

        ArrayList<RSTile> bankPath = WebPath.getPathToBank();

        if (bankPath.size() == 0){
        	failedPathCount++;
        	if(stuckFix && failedPathCount > 50){
        		failedPathCount = 0;
        		if(Dueling.getRing() != null){
        			General.println("TELEPORTING FROM WEBWALKER CUZ FAILED PATH");
        			Dueling.teleport(Dueling.LOCATIONS.CASTLE_WARS, true);
        		} else if(Glory.getAmulet() != null){
    				Glory.teleport(Glory.LOCATIONS.EDGEVILLE, true);
    			} else{
    				if(Magic.selectSpell("Lumbridge Home Teleport")){
    					RSTile myPos = Player.getPosition();
    					Timing.waitCondition(new Condition(){
    						@Override
    						public boolean active() {
    							General.sleep(100,200);
    							return !myPos.equals(Player.getPosition());
    						}
    					}, 20000);
    				}
    			}
        	}
            return false;
        } else{
        	failedPathCount = 0;
        }

        ArrayList<RSTile> bestPath = TeleportManager.teleport(bankPath.size(), bankPath.get(bankPath.size() - 1));
        if (bestPath != null){
            bankPath = bestPath;
        }

        return WalkerEngine.getInstance().walkPath(bankPath, ((WalkingCondition) () -> {
            RSTile destination = Game.getDestination();
            return destination != null && BankHelper.isInBank(destination) ? WalkingCondition.State.EXIT_OUT_WALKER_SUCCESS : WalkingCondition.State.CONTINUE_WALKER;
        }).combine(walkingCondition.combine(getInstance().globalWalkingCondition)));
    }

    public static void setLocal(boolean b){
        if (b){
            setApiKey("0243f275-cf5f-428b-be41-e4804e06e0da", "4CB3A01E48C3F79F");
            System.out.println("Switching to local api key");
        }
        WebPathCore.setLocal(b);
    }

    public static void setApiKey(String apiKey, String secretKey){
        WebPathCore.setAuth(apiKey, secretKey);
    }

    private static void setABCUtil(ABCUtil util) {
        WebWalker walker = getInstance(util);

        if (walker.abcUtilV2 != util) {
            walker.abcUtilV2.close();
            walker.abcUtilV2 = util;
        }

        if (walker.abcUtilV1 != null) {
            walker.abcUtilV1 = null;
        }
    }

    private static void setABCUtil(org.tribot.api.util.ABCUtil util) {
        WebWalker walker = getInstance(util);

        if (walker.abcUtilV1 != util) {
            walker.abcUtilV1 = null;
            walker.abcUtilV1 = util;
        }

        if (walker.abcUtilV2 != null) {
            walker.abcUtilV2.close();
        }
    }
}
