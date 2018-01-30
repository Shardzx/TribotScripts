package scripts.ezhellratcatcher;

import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSVarBit;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import scripts.Node;
import scripts.Utilities.ACamera;
import scripts.ezhellratcatcher.Nodes.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class EzHellratCatcher extends Script implements MessageListening07,Arguments,Painting{

    private final List<Node> nodes = new ArrayList<Node>();

    private Node currentNode;

    private GUI gui;

    @Override
    public void onPaint(Graphics g) {
        long runtime = Timing.timeFromMark(Vars.START_TIME);
        g.drawString("EzHellratCatcher", 5, 50);
        g.drawString("Time running: " + Timing.msToString(runtime), 5, 70);
        if(currentNode == null){
            if(Vars.idleKittenOnly){
                g.drawString("Waiting to feed kitten.", 5, 90);
                return;
            } else{
                g.drawString("Waiting for GUI to be completed.", 5, 90);
            }
        } else if(Vars.idleKittenOnly){
            g.drawString("Waiting to feed kitten.", 5, 90);
            return;
        } else {
            g.drawString("State: " + currentNode.toString(),5,90);
        }
        if(Vars.battleMode){
            g.drawString("Behemoths battled: " + Vars.behemothsFought + ", defeated: " + Vars.behemothsDefeated, 5, 110);
            g.drawString("Cat current health: " + (int)(Vars.currentCatHealth*6) + ", eat at: " + (Vars.abc2eat ? (int)(Vars.nextHealPercentage*6) : Vars.nextEat), 5, 130);
        } else{
            g.drawString("Mice caught: " + Vars.miceCaught, 5, 110);
        }
        if(Vars.npcCat !=  null && Vars.npcCat.length > 0){
            g.drawPolygon(Vars.npcCat[0].getModel().getEnclosedArea());
        }
        g.draw3DRect(740, 0, 30, 10, false);
        g.drawString("GUI",742,10);
    }

    @Override
    public void passArguments(HashMap<String, String> arg0) {
        String scriptSelect = arg0.get("custom_input");
        String clientStarter = arg0.get("autostart");
        String input = clientStarter != null ? clientStarter : scriptSelect;
        for(String arg:input.split(";")){
            if(arg.startsWith("idleKittenOnly")){
                Vars.idleKittenOnly = true;
            }
        }
    }

    @Override
    public void clanMessageReceived(String arg0, String arg1) {

    }

    @Override
    public void duelRequestReceived(String arg0, String arg1) {

    }

    @Override
    public void personalMessageReceived(String arg0, String arg1) {

    }

    @Override
    public void playerMessageReceived(String arg0, String arg1) {
        if(arg0.equals(Vars.myName)){
            if(arg1.contains("Go on puss")){
                Vars.lastHuntedRat = Timing.currentTimeMillis(); //Update the last time we tried to catch one.
            } else if(arg1.contains("Hey well done")){
                Vars.miceCaught++;
                Vars.numberFailed = 0; //Reset the failed counter upon successfully catching one.
            }
        }
    }

    @Override
    public void serverMessageReceived(String arg0) {
        if(arg0.contains("Your cat cannot")){ //Cat can't reach, increments a counter to make sure we don't get stuck.
            Vars.lastHuntedRat = 0;
            if(Timing.timeFromMark(Vars.lastFailedHuntingRat)<20000){
                Vars.numberFailed++;
                if(Vars.numberFailed >= 3){
                    Vars.shouldMove = true;
                }
            }
            Vars.lastFailedHuntingRat = Timing.currentTimeMillis();
        } else if(arg0.contains("Your kitten is hungry")){
            println("Cat is hungry!");
            Vars.catIsHungry = true;
        } else if(arg0.contains("gobbles up")){
            Vars.catIsHungry = false;
        }
    }

    @Override
    public void tradeRequestReceived(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void run() {
        gui = new GUI();
        while(Login.getLoginState()!=Login.STATE.INGAME || !gui.isCompleted){
            sleep(1000,2000);
        }
        if(!canCatchHellRats()){
            println("Ending script. Hell rats not unlocked. Make sure to progress far enough in the quest.");
            return;
        }
        Vars.abc_util = new ABCUtil();
        Vars.START_TIME = Timing.currentTimeMillis();
        Vars.myName = Player.getRSPlayer().getName();
        Vars.acamera = new ACamera(this);
        ThreadSettings.get().setClickingAPIUseDynamic(true);
        setRandomSolverState(Vars.randomSolverState);
        if(Vars.idleKittenOnly){
            println("We will be just idling and feeding/playing with our kitten.");
            Collections.addAll(nodes, new Banker(),new CatManager());
        } else if(Vars.shouldManageKitten){
            println("We will be managing a kitten while collecting spices.");
            Collections.addAll(nodes, new Banker(),new CatManager(), new Catcher());
        } else if(Vars.battleMode){
            if(!canKillBehemoth()){
                println("Ending script. You must have freed Evil Dave to do behemoths.");
                return;
            }
            println("We will be battling Hell-Rat Behemoths.");
            println("We are obtaining: " + Vars.color.name() + " spices.");
            if(Vars.withdrawAmount == 3){
                println("We will adjust our withdraw amount to 20 since no custom amount was specified.");
                Vars.withdrawAmount = 20;
            }
            if(Vars.abc2eat){
                println("We will use abc2 to determine our cat's life to eat at.");
                Vars.nextHealPercentage = Vars.abc_util.generateEatAtHP() / 100.0;
            } else {
                println("We will feed our cat at a static health: " + Vars.nextEat);
            }
            Collections.addAll(nodes, new Banker(), new Battler());
        } else{
            println("We will be simply catching Hell-Rats and collecting spices.");
            Collections.addAll(nodes, new Banker(), new Catcher());
        }
        loop(40,80);

    }

    private void loop(int min, int max){
        while(Vars.running){
            for(final Node node:nodes){
                if(node.validate()){
                    currentNode = node;
                    node.execute();
                    sleep(min,max);
                }
            }
        }
    }

    public void spawnGUI(){
        java.awt.EventQueue.invokeLater(new Runnable(){
            public void run() {
                gui = new GUI();
                gui.setVisible(true);
            }
        });
    }

    public static boolean canCatchHellRats(){
        return getQuestProgress() >= 3;
    }

    public static boolean canKillBehemoth(){
        return getQuestProgress() >= 5;
    }
    private static int getQuestProgress(){
        RSVarBit evilDaveVarbit = RSVarBit.get(1878);
        return evilDaveVarbit.getValue();
    }
}
