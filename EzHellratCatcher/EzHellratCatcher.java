package scripts;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import scripts.Node.Node;
import scripts.ezhellratcatcher.*;

public class EzHellratCatcher extends Script implements MessageListening07,Arguments,Painting {

	private final List<Node> nodes = new ArrayList<Node>();
	private Node currentNode = null;
	
	@Override
	public void onPaint(Graphics g) {
		long runtime = Timing.timeFromMark(Vars.START_TIME);
		g.drawString("EzHellratCatcher", 5, 50);
		g.drawString("Time running: " + Timing.msToString(runtime), 5, 70);
		g.drawString("State: " + currentNode != null ? currentNode.toString() : "null",5,90);
		g.drawString("Mice caught: " + Vars.miceCaught, 5, 110);
	}

	@Override
	public void passArguments(HashMap<String, String> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clanMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void duelRequestReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void personalMessageReceived(String arg0, String arg1) {
		// TODO Auto-generated method stub
		
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
			Vars.catIsHungry = true;
		}
	}

	@Override
	public void tradeRequestReceived(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		while(Login.getLoginState()!=Login.STATE.INGAME){
			sleep(1000,2000);
		}
		println(Vars.hasCat());
		Vars.abc_util = new ABCUtil();
		Vars.START_TIME = Timing.currentTimeMillis();
		Vars.myName = Player.getRSPlayer().getName();
		ThreadSettings.get().setClickingAPIUseDynamic(true);
		setRandomSolverState(Vars.randomSolverState);
		Collections.addAll(nodes, new Banker(), new Catcher());
//		groundItemTracker.start();
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
	
	
	/*
	 * Interface 217,3 "I think it's hungry!", server message: "Your kitten is hungry."
	 * 
	 */
}
