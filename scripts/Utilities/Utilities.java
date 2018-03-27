package scripts.Utilities;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import scripts.webwalker_logic.local.walker_engine.NPCInteraction;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.AccurateMouse;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utilities {

    public static ACamera acamera;

    public static boolean[] indices = new boolean[28];

    private static Rectangle[] inventoryRectangles;

    public static boolean   shouldWaitForDropping = true;

    public final static int	WELCOME_SCREEN_MASTER_ID = 378,
            WELCOME_SCREEN_CHILD_ID = 6;

    public static boolean isWelcomeScreenUp() {
        return Interfaces.isInterfaceValid(WELCOME_SCREEN_MASTER_ID);
    }
    public static boolean handleWelcomeScreen(){
        RSInterfaceChild login = Interfaces.get(WELCOME_SCREEN_MASTER_ID,WELCOME_SCREEN_CHILD_ID);
        return login != null && login.click() && Timing.waitCondition(EzConditions.interfaceNotUp(WELCOME_SCREEN_MASTER_ID), 8000);
    }

    public static boolean isConversing(){
        return NPCChat.getClickContinueInterface() != null || NPCChat.getOptions() != null;
    }

    public static String extractNumber(final String str) {

        if(str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for(char c : str.toCharArray()){
            if(Character.isDigit(c)){
                sb.append(c);
                found = true;
            } else if(found){
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }

        return sb.toString();
    }
    public static int getWildLevel(){
        try{
            RSInterface level = Interfaces.get(90, 46);
            if(level == null)
                return 0;
            String txt = level.getText();
            if(txt == null)
                return 0;
            return Integer.parseInt(Utilities.extractNumber(txt));
        } catch(Exception e){
            return 0;
        }
    }

    public static boolean useItemOnObject(RSItem item, RSObject object, Condition completed){
        if(item==null || object==null){
            return false;
        }
        if(!object.isOnScreen()){
            findObject(object,false);
        }
        RSItemDefinition itemDef = item.getDefinition();
        RSObjectDefinition objDef = object.getDefinition();
        String itemName = itemDef != null ? itemDef.getName() : "";
        String objName = objDef != null ? objDef.getName() : "";
        return (Game.isUptext("Use " + itemName + " ->")||item.click("Use")) && object.click("Use " + (itemName.length() > 0 ? (itemName + " -> " + objName) : "")) && Timing.waitCondition(completed, 6000);
    }

    public static boolean findObject(RSObject obj, boolean hasTried){
        if(obj==null)
            return false;
        RSTile myPos = Player.getPosition(),
                objPos = getNearestTile(myPos,obj);
        int distance = myPos.distanceTo(objPos);
        if(distance>25){
            return false;
        }
        if(distance<15){
            acamera.turnToTile(objPos);
            boolean canReach = false;
            for(RSTile tile:obj.getAllTiles()){
                if(PathFinding.canReach(tile, true)){
                    canReach = true;
                }
            }
            if(!canReach){
                return false;
            }
        }
        if(obj.isOnScreen()&&obj.isClickable())
            return true;
        if(hasTried||distance> General.random(5,8)){
            long t = Timing.currentTimeMillis();
            while(Player.isMoving()&&!obj.isClickable()&&Timing.timeFromMark(t)<5000)
                General.sleep(100,200);
            return obj.isOnScreen()&&obj.isClickable()||objPos.isClickable()? Walking.walkTo(objPos):Walking.walkPath(Walking.generateStraightPath(objPos));
        }
        else{

            final RSObject temp = obj;
            return Timing.waitCondition(new Condition(){

                @Override
                public boolean active() {
                    General.sleep(100,200);
                    return temp.isOnScreen()&&temp.isClickable();
                }

            }, 2000)||findObject(obj,true);
        }
    }

    public static boolean accurateClickObject(RSObject obj, boolean hasTried,String... options){
        if(obj==null||!isTileOnMinimap(obj.getPosition()))
            return !hasTried && PathFinding.canReach(obj, true) && Walking.walkPath(Walking.generateStraightPath(obj),EzConditions.objectVisible(obj),100)&&accurateClickObject(obj,true,options);
        return AccurateMouse.click(obj,options)||(!hasTried&&isTileOnMinimap(obj.getPosition())&&findObject(obj,false)&&Timing.waitCondition(EzConditions.objectVisible(obj), 5000)&&accurateClickObject(obj,true,options));
    }

    public static boolean clickObject(RSObject obj,String option, boolean hasTried){
        if(obj==null||!isTileOnMinimap(obj.getPosition()))
            return !hasTried && PathFinding.canReach(obj, true) && Walking.walkPath(Walking.generateStraightPath(obj),EzConditions.objectVisible(obj),100)&&clickObject(obj,option,true);
        return Clicking.click(option,obj)||(!hasTried&&isTileOnMinimap(obj.getPosition())&&findObject(obj,false)&&Timing.waitCondition(EzConditions.objectVisible(obj), 5000)&&clickObject(obj,option,true));
    }

    public static boolean findNPC(RSNPC npc, boolean hasTried){
        if(npc==null)
            return false;
        RSTile myPos = Player.getPosition(),
                npcPos = npc.getPosition();
        int distance = myPos.distanceTo(npcPos);
        if(distance>25){
            return false;
        }
        if(distance<15){
            acamera.turnToTile(npcPos);
            if(!PathFinding.canReach(npcPos,false)){
                return false;
            }
        }
        if(npc.isOnScreen()&&npc.isClickable())
            return true;
        if(hasTried||distance> General.random(5,8)){
            long t = Timing.currentTimeMillis();
            while(Player.isMoving()&&!npc.isClickable()&&Timing.timeFromMark(t)<5000)
                General.sleep(100,200);
            return npc.isOnScreen()&&npc.isClickable()||npcPos.isClickable()? Walking.walkTo(npcPos):Walking.walkPath(Walking.generateStraightPath(npcPos));
        }
        else{

            final RSNPC temp = npc;
            return Timing.waitCondition(new Condition(){

                @Override
                public boolean active() {
                    General.sleep(100,200);
                    return temp.isOnScreen()&&temp.isClickable();
                }

            }, 2000)||findNPC(npc,true);
        }
    }
    public static boolean clickNPC(RSCharacter npc,String option, boolean hasTried){
        if(npc==null)
            return false;
        return Clicking.click(option,npc)||(!hasTried&&!npc.isOnScreen()&&findNPC((RSNPC)npc,false)&&Timing.waitCondition(EzConditions.npcVisible(npc.getName()), 5000)&&clickNPC(npc,option,true));
    }

    public static RSTile getNearestTile(RSTile compareTo,RSObject o){
        RSTile[] tiles = o.getAllTiles();
        int minDistance = -1;
        RSTile output = null;
        for(RSTile tile:tiles){
            if(minDistance==-1||compareTo.distanceTo(tile)<minDistance){
                minDistance = compareTo.distanceTo(tile);
                output = tile;
            }
        }
        return output;
    }

    public static boolean shiftDrop(String... items){
        RSItem[] itemsToDrop = Inventory.find(items);
        for(int i=0;i<indices.length;i++){
            indices[i]=false;
        }
        if(itemsToDrop.length == 0)	return true;
        Keyboard.sendPress(KeyEvent.CHAR_UNDEFINED,KeyEvent.VK_SHIFT);
        General.sleep(General.randomSD(20, 400, 80, 30));
        RSItem item;
        while((item = getItemClosestToMouse(items)) != null){
            item.click();
            indices[item.getIndex()] = true;
            General.sleep(General.randomSD(20, 400, 80, 30));
        }
        Keyboard.sendRelease(KeyEvent.CHAR_UNDEFINED, KeyEvent.VK_SHIFT);
        return Inventory.find(items).length == 0 || shouldWaitForDropping ? Timing.waitCondition(EzConditions.inventoryChange(false), 1500) :
                true;
    }

    public static RSItem getItemClosestToMouse(String... names) {
        RSItem[] items = Inventory.find(names);
        return getItemClosestToMouse(items);
    }

    public static RSItem getItemClosestToMouse(RSItem... items){
        Point mouse_pos = Mouse.getPos();
        RSItem closest_item = null;
        double distance = 9999, temp_distance;
        for (RSItem item: items) {
            if(indices[item.getIndex()]){
                continue;
            }
            Rectangle rectangle = item.getArea();
            if (rectangle == null) {
                continue;
            }
            Point item_pos = rectangle.getLocation();
            item_pos.translate(20, 20);
            if ((temp_distance = item_pos.distance(mouse_pos)) < distance) {
                distance = temp_distance;
                closest_item = item;
            }
        }
        return closest_item;
    }

    public static <T> T[] shuffleArray(T[] arr){
        if(arr.length == 0 || arr.length == 1){
            return arr;
        }
        List<T> solution = new ArrayList<>();
        for (T t: arr) {
            solution.add(t);
        }
        Collections.shuffle(solution);
        return solution.toArray(arr);
    }

    public static boolean enterAmountMenuUp() {
        return Screen.getColorAt(new Point(260, 429)).equals(
                new Color(0, 0, 128))||Screen.getColorAt(new Point(263, 429)).equals(
                new Color(0, 0, 128));
    }

    public static void misclick(){
        Mouse.click(General.random(740,760),General.random(50,400),1);
        General.sleep(20,40);
    }

    public static boolean hasInterface(int m){
        return Interfaces.isInterfaceValid(m);
    }
    public static boolean hasInterfaces(int... m){
        for(int i:m){
            if(hasInterface(i)){
                return true;
            }
        }
        return false;
    }
    public static boolean hasInterface(int m,int c){
        return Interfaces.get(m,c)!=null;
    }
    public static RSInterface getInterface(int m){
        RSInterface i = Interfaces.get(m);
        return i != null ? i : null;
    }
    public static RSInterface getInterface(int m,int c){
        RSInterface i = Interfaces.get(m,c);
        return i != null ? i : null;
    }

    public static boolean areUnwantedInterfacesOpen(){
        return hasInterfaces(345,193,229,233,84,464,102,214,400,402,382,310,553,451);
    }
    public static boolean closeUnwantedInterfaces(){
        RSInterface close;
        if(hasInterface(345)){
            close = getInterface(345,1);
            if(close == null || close.getChildren() == null){
                close = getInterface(345,2);
            }
            if(close != null){
                close = close.getChild(11);
                return close != null && close.click();
            }
        } else if(hasInterface(193)){
            close = getInterface(193,2);
            return close != null && close.click();
        } else if(hasInterface(229)){
            close = getInterface(229,1);
            return close != null && close.click();
        } else if(hasInterface(233)){
            close = getInterface(233,2);
            return close != null && close.click();
        } else if(hasInterface(84)){
            close = getInterface(84,4);
            return close != null && close.click();
        } else if(hasInterface(464)){
            close = getInterface(464,1);
            if(close != null){
                close = close.getChild(3);
                return close != null && close.click();
            }
        } else if(hasInterface(102)){
            close = getInterface(102,7);
            return close != null && close.click();
        } else if(hasInterface(214)){
            close = getInterface(214,25);
            return close != null && close.click();
        } else if(hasInterface(400)){
            close = getInterface(400,2);
            if(close != null){
                close = close.getChild(3);
                return close != null && close.click();
            }
        } else if(hasInterface(402)){
            close = getInterface(400,2);
            if(close != null){
                close = close.getChild(11);
                return close != null && close.click();
            }
        } else if(hasInterface(382)){
            close = getInterface(382,18);
            return close != null && close.click();
        } else if(hasInterface(310)){
            close = getInterface(310,1);
            if(close == null || close.getChildren() == null){
                close = getInterface(310,2);
            }
            if(close != null){
                close = close.getChild(11);
                return close != null && close.click();
            }
        } else if(hasInterface(553)){
            close = getInterface(553,1);
            if(close != null){
                close = close.getChild(11);
                return close != null && close.click();
            }
        } else if(hasInterface(451)){
            close = getInterface(451,1);
            if(close != null){
                close = close.getChild(11);
                return close != null && close.click();
            }
        }
        return false;
    }

    public static Rectangle getClosestEmptyInventorySlot(){
        return getClosestRectangle(getEmptyInventorySlots());
    }

    public static Rectangle getClosestRectangle(Rectangle... rectangles){
        Point myMouse = Mouse.getPos();
        int closest = 1000;
        Rectangle output = null;
        for(Rectangle r:rectangles){
            int current = (int) new Point((int)r.getCenterX(),(int)r.getCenterY()).distance(myMouse);
            if(current < closest){
                output = r;
                closest = current;
            }
        }
        return output;
    }

    public static Rectangle[] getEmptyInventorySlots(){
        if(inventoryRectangles == null)
            buildRectangles();
        RSItem[] inv = Inventory.getAll();
        List<Integer> containingIndices = new ArrayList<Integer>();
        for(RSItem i:inv){
            containingIndices.add(i.getIndex());
        }
        List<Rectangle> output = new ArrayList<Rectangle>();
        for(int i=0;i<28;i++){
            if(!containingIndices.contains(i)){
                output.add(inventoryRectangles[i]);
            }
        }
        return output.toArray(new Rectangle[output.size()]);
    }

    private static void buildRectangles(){
        inventoryRectangles = new Rectangle[28];
        for(int i=0;i<28;i++){
            RSItem item = new RSItem(i,1,1,RSItem.TYPE.INVENTORY);
            inventoryRectangles[i] = item.getArea();
        }
    }

    public static RSTile minimapToTile(Point p) {
        if (!Projection.isInMinimap(p))
            return null;
        RSTile pos = Player.getPosition();
        for (int x = pos.getX() - 20; x < pos.getX() + 20; x++) {
            for (int y = pos.getY() - 20; y < pos.getY() + 20; y++) {
                RSTile tile = new RSTile(x, y, pos.getPlane());
                Point t = Projection.tileToMinimap(tile);
                if (Math.abs(t.x - p.x) <= 2 && Math.abs(t.y - p.y) <= 2)
                    return tile;
            }
        }
        return null;
    }
    public static boolean isTileOnMinimap(RSTile tile){
        return Projection.isInMinimap(Projection.tileToMinimap(tile));
    }

    public static boolean waitInventory(boolean increase, long ms){
        return Timing.waitCondition(EzConditions.inventoryChange(increase),ms);
    }

    public static boolean waitBank(boolean isOpen){
        return Timing.waitCondition(isOpen?EzConditions.bankIsClosed():EzConditions.bankIsOpen(),8000);
    }

    public static boolean waitChat(long ms){
        return Timing.waitCondition(EzConditions.isConversing(),ms);
    }

    public static boolean waitArea(RSArea area,long ms){
        return Timing.waitCondition(EzConditions.inArea(area),ms);
    }

    public static boolean waitInterface(int master, long ms){
        return Timing.waitCondition(EzConditions.interfaceUp(master),ms);
    }

    public static boolean skipChat(boolean chooseOptions, String... options){
        if(chooseOptions) {
            NPCInteraction.handleConversation(options);
            return true;
        } else {
            NPCInteraction.handleConversation();
            return true;
        }
    }

}
