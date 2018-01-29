package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utilities {

    public static ACamera acamera;

    public static boolean[] indices = new boolean[28];

    public static boolean   shouldWaitForDropping = true;

    public static boolean isConversing(){
        return NPCChat.getClickContinueInterface() != null || NPCChat.getOptions() != null;
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
}
