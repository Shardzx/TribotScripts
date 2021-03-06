package scripts.clientofkourend;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import org.tribot.script.EnumScript;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.Arguments;
import org.tribot.script.interfaces.Painting;
import scripts.Utilities.ACamera;
import scripts.Utilities.EzConditions;
import scripts.Utilities.Shop;
import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.InteractionHelper;
import scripts.webwalker_logic.local.walker_engine.interaction_handling.NPCInteraction;
import scripts.webwalker_logic.local.walker_engine.local_pathfinding.Reachable;
import scripts.webwalker_logic.shared.helpers.BankHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@ScriptManifest(authors= {"FALSkills"}, category= "Quests", name= "Client of Kourend")
public class ClientOfKourend extends EnumScript<State> implements Painting,Arguments{

	public State			state;
	
	private final String	FEATHER = "Feather",
							ENCHANTED_SCROLL = "Enchanted scroll",
							ENCHANTED_QUILL = "Enchanted quill",
							ORB = "Mysterious orb",
							VEOS = "Veos",
							PISCARILIUS = "Leenz",
							HOSIDIUS = "Horace",
							SHAYZIEN = "Jennifer",
							LOVAKENGJ = "Munty",
							ARCEUUS = "Regath",
							COINS = "Coins",
							GERRANT = "Gerrant",
							ANTIQUE_LAMP = "Antique lamp";

	private final int	QUEST_COMPLETE_MASTER = 277,
						QUEST_COMPLETE_CHILD = 17,
						LAMP_MASTER = 134,
						LAMP_CONFIRM = 26;

	private final RSArea	QUEST_START_AREA = new RSArea(new RSTile(1821,3691,0),new RSTile(1826,3685,0)),
				PISCARILIUS_SHOP_AREA = new RSArea(new RSTile(1803,3723,0),new RSTile(1808,3728,0)),
				HOSIDIUS_SHOP_AREA = new RSArea(new RSTile(1667,3624,0),new RSTile(1673,3615,0)),
				SHAYZIEN_SHOP_AREA = new RSArea(new RSTile(1540,3635,0),new RSTile(1550,3620,0)),
				LOVAKENGJ_SHOP_AREA = new RSArea(new RSTile(1549,3714,0),new RSTile(1558,3722,0)),
				ARCEUUS_SHOP_AREA = new RSArea(new RSTile(1718,3730,0),new RSTile(1725,3720,0)),
				DARK_ALTAR_AREA = new RSArea(new RSTile(1710,3885,0),new RSTile(1720,3880,0)),
				FISHING_STORE_AREA = new RSArea(new RSTile(3011,3229,0),new RSTile(3017,3222,0));
	
	private ArrayList<String>	VEOS_CHAT_1 = 		new ArrayList<String>(Arrays.asList(new String[]{
									"Have you got any quests for me?",
									"Sounds interesting! How can I help?"})),
					VEOS_CHAT_2 = 		new ArrayList<String>(Arrays.asList(new String[]{
									"Let's talk about your client..."})),
					VEOS_CHAT_3 =		new ArrayList<String>(Arrays.asList(new String[]{
									"Let's talk about your client..."})),
					PISCARILIUS_CHAT = 	new ArrayList<String>(Arrays.asList(new String[]{
									"Can I ask you about the Piscarilius house?",
									"How do people start gaining favour in Piscarilius?",
									"What is it that Piscarilius provides for Kourend?"})),
					HOSIDIUS_CHAT = 	new ArrayList<String>(Arrays.asList(new String[]{
									"Can I ask you about the Hosidius house?",
									"What is it that Hosidius provides for Great Kourend?",
									"How do people start gaining favour in Hosidius?"})),
					SHAYZIEN_CHAT = 	new ArrayList<String>(Arrays.asList(new String[]{
									"Can I ask you about the Shayzien house?",
									"What is it that Shayzien provides for Great Kourend?",
									"How do people start gaining favour in Shayzien?"})),
					LOVAKENGJ_CHAT =	new ArrayList<String>(Arrays.asList(new String[]{
									"Can I ask you about the Lovakengj house?",
									"What is it that Lovakengj provides for Great Kourend?",
									"How do people start gaining favour in Lovakengj?"})),
					ARCEUUS_CHAT = 		new ArrayList<String>(Arrays.asList(new String[]{
									"Can I ask you about the Arceuus house?",
									"What is it that Arceuus provides for Great Kourend?",
									"How do people start gaining favour in Arceuus?"}));
	
	private ACamera			acamera;
	
	private Script			callingScript;

	private final int		GAME_SETTING = 1566;

	public int			currentStep;
	
	private RSItem[]		feather,
					enchanted_scroll,
					enchanted_quill,
					orb,
					lamp;
	
	private Filter<RSNPC>		VEOS_FILTER = Filters.NPCs.nameEquals(VEOS).combine(Filters.NPCs.inArea(QUEST_START_AREA), true);
	
	private RSNPC[]			veos,
					store_owner;
	
	private long			START_TIME,
					RUNTIME;

	private boolean			needToBuyFeather = false;
	
	private String			HOUSE_TO_CHOOSE = "Arceuus";
	
	private Skills.SKILLS		SKILL_1,
								SKILL_2;
	
	public ClientOfKourend(){
		callingScript = this;
	}
	public ClientOfKourend(Script script){
		callingScript = script;
	}
	
	
	@Override
	public void onPaint(Graphics g) {
		RUNTIME = Timing.currentTimeMillis()-START_TIME;
		g.setColor(Color.CYAN);
		g.drawString("Client of Kourend",5,50);
		g.drawString("Running for: " + Timing.msToString(RUNTIME),5,70);
		g.drawString("State: " + state + " step: "+currentStep, 5, 90);
	}

	
	/*
	 * Arguments separated by semi-colon (;)
	 * input of argument separated by colon (:)
	 * skill argument must be the entire skill name. see Skills.SKILLS enum for full name.
	 * house argument only needs first letter. Script will not function properly without proper input.
	 * 
	 * examples:
	 * house:Lovakengj;skill1:slayer;skill2:agility
	 * 
	 */
	@Override
	public void passArguments(HashMap<String, String> arg0) {
		String scriptSelect = arg0.get("custom_input");
		String clientStarter = arg0.get("autostart");
		String input = clientStarter != null ? clientStarter : scriptSelect;
		for(String arg:input.split(";")){
			try{
				if(arg.startsWith("house")){
					HOUSE_TO_CHOOSE = arg.split(":")[1];
				} else if(arg.startsWith("skill1")){
					SKILL_1 = Enum.valueOf(Skills.SKILLS.class, (arg.split(":")[1]).toUpperCase());
				} else if(arg.startsWith("skill2")){
					SKILL_2 = Enum.valueOf(Skills.SKILLS.class, (arg.split(":")[1]).toUpperCase());
				}
			} catch(Exception e){
				println("Error in arguments. See script thread for instructions.");
			}
		}
		
	}

	@Override
	public State getInitialState() {
		START_TIME = Timing.currentTimeMillis();
		acamera = new ACamera(callingScript);
		if(HOUSE_TO_CHOOSE.toLowerCase().startsWith("a")){
			HOUSE_TO_CHOOSE = "Arceuus";
		} else if(HOUSE_TO_CHOOSE.toLowerCase().startsWith("h")){
			HOUSE_TO_CHOOSE = "Hosidius";
		} else if(HOUSE_TO_CHOOSE.toLowerCase().startsWith("l")){
			HOUSE_TO_CHOOSE = "Lovakengj";
		} else if(HOUSE_TO_CHOOSE.toLowerCase().startsWith("p")){
			HOUSE_TO_CHOOSE = "Piscarilius";
		} else if(HOUSE_TO_CHOOSE.toLowerCase().startsWith("s")){
			HOUSE_TO_CHOOSE = "Shayzien";
		} else{
			println("Terminating script, invalid house argument.");
			return null;
		}
		if(SKILL_1 == null){
			SKILL_1 = Skills.SKILLS.values()[General.random(0, Skills.SKILLS.values().length-1)];
		}
		if(SKILL_2 == null){
			SKILL_2 = Skills.SKILLS.values()[General.random(0, Skills.SKILLS.values().length-1)];
		}
		println("Starting Client of Kourend.");
		println("We will be gaining 20% favor to house: " + HOUSE_TO_CHOOSE);
		println("We will be gaining exp in skills: " + SKILL_1 + ", " + SKILL_2);
		return getState();
	}

	public State getState() {
		RSTile myPos = Player.getPosition();
		if(Login.getLoginState() != Login.STATE.INGAME){
			return State.LOGGING_IN;
		}
		currentStep = Game.getSetting(GAME_SETTING);
		switch(currentStep){
		case 0://Quest not started.
			feather = Inventory.find(FEATHER);
			if(feather.length == 0){
				return getFeatherState();
			} else {
				veos = NPCs.find(VEOS_FILTER);
				if(veos.length > 0){
					if(isConversing()){
						return State.CHAT_VEOS_1;
					} else {
						return State.TALKING_TO_VEOS;
					}
				} else{
					return State.WALKING_TO_QUEST_START;
				}
			}
		case 1://Quest started, talked to Veos. Need to use Feather on Enchanted scroll to get Enchanted quill, and then go to Piscarilius general store
			enchanted_quill = Inventory.find(ENCHANTED_QUILL);
			if(enchanted_quill.length == 0){
				return getEnchantedQuillState();
			}
			store_owner = NPCs.find(PISCARILIUS);
			if(store_owner.length > 0){
				if(isConversing()){
					return State.CHAT_PISCARILIUS;
				} else{
					return State.TALKING_TO_PISCARILIUS_STORE_OWNER;
				}
			} else {
				return State.WALKING_TO_PISCARILIUS_STORE;
			}
		case 65://from Piscarilius -> Hosidius
			enchanted_quill = Inventory.find(ENCHANTED_QUILL);
			if(enchanted_quill.length == 0){
				return getEnchantedQuillState();
			}
			store_owner = NPCs.find(HOSIDIUS);
			if(store_owner.length > 0){
				if(isConversing()){
					return State.CHAT_HOSIDIUS;
				} else {
					return State.TALKING_TO_HOSIDIUS_STORE_OWNER;
				}
			} else {
				return State.WALKING_TO_HOSIDIUS_STORE;
			}
		case 1089://from Hosidius -> Shayzien
			enchanted_quill = Inventory.find(ENCHANTED_QUILL);
			if(enchanted_quill.length == 0){
				return getEnchantedQuillState();
			}
			store_owner = NPCs.find(SHAYZIEN);
			if(store_owner.length > 0){
				if(isConversing()){
					return State.CHAT_SHAYZIEN;
				} else{
					return State.TALKING_TO_SHAYZIEN_STORE_OWNER;
				}
			} else {
				return State.WALKING_TO_SHAYZIEN_STORE;
			}
		case 1601://from Shayzien -> Lovakengj
			enchanted_quill = Inventory.find(ENCHANTED_QUILL);
			if(enchanted_quill.length == 0){
				return getEnchantedQuillState();
			}
			store_owner = NPCs.find(LOVAKENGJ);
			if(store_owner.length > 0){
				if(isConversing()){
					return State.CHAT_LOVAKENGJ;
				} else{
					return State.TALKING_TO_LOVAKENGJ_STORE_OWNER;
				}
			} else {
				return State.WALKING_TO_LOVAKENGJ_STORE;
			}
		case 1857://from Lovakengj -> Arceuus
			enchanted_quill = Inventory.find(ENCHANTED_QUILL);
			if(enchanted_quill.length == 0){
				return getEnchantedQuillState();
			}
			store_owner = NPCs.find(ARCEUUS);
			if(store_owner.length > 0){
				if(isConversing()){
					return State.CHAT_ARCEUUS;
				} else if(myPos.distanceTo(store_owner[0]) > 1 && !PathFinding.canReach(store_owner[0], false)){
					return State.WALKING_TO_ARCEUUS_STORE;
				} else{
					return State.TALKING_TO_ARCEUUS_STORE_OWNER;
				}
			} else {
				return State.WALKING_TO_ARCEUUS_STORE;
			}
		case 1986://from Arceuus -> Veos
			veos = NPCs.find(VEOS_FILTER);
			if(veos.length > 0){
				if(isConversing()){
					return State.CHAT_VEOS_2;
				} else {
					return State.TALKING_TO_VEOS;
				}
			} else{
				return State.WALKING_TO_QUEST_START;
			}
		case 1987://get new assignment, agree to do it. combined with 1988, just make sure you talk to veos if you don't have the orb.
		case 1988://use orb at dark altar
			orb = Inventory.find(ORB);
			if(orb.length == 0){
				if(BankHelper.isInBank()){
					if(Banking.isBankScreenOpen()){
						return State.WITHDRAWING_ORB;
					} else{
						return State.OPENING_BANK;
					}
				} else{
					return State.WALKING_TO_BANK;
				}
			} else if(DARK_ALTAR_AREA.contains(myPos)){
				return State.ACTIVATING_ORB;
			} else{
				return State.WALKING_TO_DARK_ALTAR;
			}
		case 1989://Used orb at dark altar, return to veos
			veos = NPCs.find(VEOS_FILTER);
			if(veos.length > 0){
				if(isConversing()){
					return State.CHAT_VEOS_3;
				} else {
					return State.TALKING_TO_VEOS;
				}
			} else{
				return State.WALKING_TO_QUEST_START;
			}
		case 1990://client reveals himself, asks to choose which house to increase favor 
			veos = NPCs.find(VEOS_FILTER);
			if(veos.length > 0){
				if(isConversing()){
					return State.CHOOSING_HOUSE_FAVOR;
				} else {
					return State.TALKING_TO_VEOS;
				}
			} else{
				return State.WALKING_TO_QUEST_START;
			}
		case 18374:
			lamp = Inventory.find(ANTIQUE_LAMP);
			if(isConversing()){
				return State.CHAT_VEOS_3;
			} else if(questCompleteInterfaceIsOpen()){
				return State.CLOSING_QUEST_COMPLETE_INTERFACE;//277,17
			} else if(lamp.length > 1){
				return State.USING_FIRST_LAMP;
			} else{
				return State.USING_SECOND_LAMP;
			}
		case 10183://Use first lamp
			lamp = Inventory.find(ANTIQUE_LAMP);
			return State.USING_FIRST_LAMP;
		case 10184://Use second lamp
			lamp = Inventory.find(ANTIQUE_LAMP);
			return State.USING_SECOND_LAMP;
		case 16329://Quest complete with garden of tranquility done
		case 10185://Quest complete
			println("Quest complete! Ending script.");
			return null;
		}
		return null;
	}
	
	/*
	 * getState method for banking and getting feather.
	 */
	private State getFeatherState(){
		int coinsCount = Inventory.getCount(COINS);
		if(needToBuyFeather || coinsCount >= 2){
			if(coinsCount < 2){
				if(Banking.isInBank()){
					if(Banking.isBankScreenOpen()){
						if(Inventory.isFull()){
							return State.DEPOSITING_ALL;
						} else{
							return State.WITHDRAWING_COINS;
						}
					} else{
						return State.OPENING_BANK;
					}
				} else{
					return State.WALKING_TO_BANK;
				}
			} else {
				store_owner = NPCs.findNearest(GERRANT);
				if(store_owner.length > 0){
					if(Shop.isShopOpen()){
						return State.BUYING_FEATHER;
					} else{
						return State.OPENING_SHOP;
					}
				} else{
					if(Banking.isBankScreenOpen()){
						return State.CLOSING_BANK;
					}
					return State.WALKING_TO_FISHING_STORE;
				}
			}
		} else if(Banking.isInBank()){
			if(Banking.isBankScreenOpen()){
				if(Banking.find(FEATHER).length > 0){
					return State.WITHDRAWING_FEATHER;
				} else if(Banking.find(ENCHANTED_SCROLL).length > 0){
					return State.WITHDRAWING_ENCHANTED_SCROLL;
				} else{
					println("Feather not detected. Withdrawing coins to buy one.");
					needToBuyFeather = true;
					return getState();
				}
			} else{
				return State.OPENING_BANK;
			}
		} else{
			return State.WALKING_TO_BANK;
		}
	}
	
	private State getEnchantedQuillState(){
		feather = Inventory.find(FEATHER);
		enchanted_scroll = Inventory.find(ENCHANTED_SCROLL);
		if(feather.length > 0 && enchanted_scroll.length > 0){
			return State.USING_FEATHER_ON_ENCHANTED_SCROLL;
		} else { 
			return getFeatherState();
		}
	}
	
	@Override
	public State handleState(State state) {
		this.state = state;
		switch(state){
		case ACTIVATING_ORB:
			if(orb[0].click("")){
				Timing.waitCondition(EzConditions.itemLeftInventory(ORB), 5000);
			}
			break;
		case BUYING_FEATHER:
			if(Shop.buy("Feather", 1)){
				Timing.waitCondition(EzConditions.itemEnteredInventory(FEATHER), 4000);
			}
			break;
		case CHAT_ARCEUUS:
			skipChat(ARCEUUS_CHAT);
			if(NPCChat.getClickContinueInterface() != null){
				NPCChat.clickContinue(true);
			}
			break;
		case CHAT_HOSIDIUS:
			skipChat(HOSIDIUS_CHAT);
			break;
		case CHAT_LOVAKENGJ:
			skipChat(LOVAKENGJ_CHAT);
			break;
		case CHAT_PISCARILIUS:
			skipChat(PISCARILIUS_CHAT);
			break;
		case CHAT_SHAYZIEN:
			skipChat(SHAYZIEN_CHAT);
			break;
		case CHAT_VEOS_1:
			skipChat(VEOS_CHAT_1);
			break;
		case CHAT_VEOS_2:
			skipChat(VEOS_CHAT_2);
			break;
		case CHAT_VEOS_3:
			skipChat(VEOS_CHAT_3);
			break;
		case CHOOSING_HOUSE_FAVOR:
			NPCChat.selectOption("The " + HOUSE_TO_CHOOSE + " house.",true);
			NPCInteraction.handleConversation();
			break;
		case CLOSING_BANK:
			if(Banking.close()){
				Timing.waitCondition(EzConditions.bankIsClosed(), 3000);
			}
			break;
		case CLOSING_QUEST_COMPLETE_INTERFACE:
			RSInterface close = Interfaces.get(QUEST_COMPLETE_MASTER, QUEST_COMPLETE_CHILD);
			if(close != null && close.click()){
				Timing.waitCondition(EzConditions.interfaceNotUp(QUEST_COMPLETE_MASTER), 5000);
			}
			break;
		case DEPOSITING_ALL:
			if(Banking.depositAll() > 0){
				Timing.waitCondition(EzConditions.inventoryChange(false), 2000);
			}
			break;
		case LOGGING_IN:
			sleep(1000,2000);
			break;
		case OPENING_BANK:
			if(Banking.openBank()){
				Timing.waitCondition(EzConditions.bankIsOpen(), 8000);
			}
			break;
		case OPENING_SHOP:
			if(clickNPC(store_owner[0],"Trade")){
				Timing.waitCondition(Shop.getCondition(true), 8000);
			}
			break;
		case TALKING_TO_ARCEUUS_STORE_OWNER:
		case TALKING_TO_HOSIDIUS_STORE_OWNER:
		case TALKING_TO_LOVAKENGJ_STORE_OWNER:
		case TALKING_TO_PISCARILIUS_STORE_OWNER:
		case TALKING_TO_SHAYZIEN_STORE_OWNER:
			if(!Reachable.canReach(store_owner[0].getPosition())){
				if(WebWalker.walkTo(store_owner[0].getPosition())){
					sleep(400,800);
				}
			} else if(clickNPC(store_owner[0],"Talk-to")){
				Timing.waitCondition(EzConditions.isConversing(), 8000);
			}
			break;
		case TALKING_TO_VEOS:
			if(clickNPC(veos[0],"Talk-to")){
				Timing.waitCondition(EzConditions.isConversing(), 8000);
			}
			break;
		case USING_FEATHER_ON_ENCHANTED_SCROLL:
			useFeatherOnScroll();
			break;
		case USING_FIRST_LAMP:
			useLamp(SKILL_1);
			break;
		case USING_SECOND_LAMP:
			useLamp(SKILL_2);
			break;
		case WALKING_TO_ARCEUUS_STORE:
			walkToArea(ARCEUUS_SHOP_AREA);
			break;
		case WALKING_TO_BANK:
			if(WebWalker.walkToBank()){
				Timing.waitCondition(EzConditions.isInBank(), 8000);
			}
			break;
		case WALKING_TO_DARK_ALTAR:
			walkToArea(DARK_ALTAR_AREA);
			break;
		case WALKING_TO_FISHING_STORE:
			walkToArea(FISHING_STORE_AREA);
			break;
		case WALKING_TO_HOSIDIUS_STORE:
			walkToArea(HOSIDIUS_SHOP_AREA);
			break;
		case WALKING_TO_LOVAKENGJ_STORE:
			walkToArea(LOVAKENGJ_SHOP_AREA);
			break;
		case WALKING_TO_PISCARILIUS_STORE:
			walkToArea(PISCARILIUS_SHOP_AREA);
			break;
		case WALKING_TO_QUEST_START:
			walkToArea(QUEST_START_AREA);
			break;
		case WALKING_TO_SHAYZIEN_STORE:
			walkToArea(SHAYZIEN_SHOP_AREA);
			break;
		case WITHDRAWING_COINS:
			withdraw(1,COINS);
			break;
		case WITHDRAWING_ENCHANTED_SCROLL:
			withdraw(1,ENCHANTED_SCROLL);
			break;
		case WITHDRAWING_FEATHER:
			withdraw(1,FEATHER);
			break;
		case WITHDRAWING_ORB:
			withdraw(1,ORB);
			break;		
		default:
			break;
		
		}
		sleep(100,200);
		return getState();
	}
	
	private boolean isConversing(){
		return NPCChat.getClickContinueInterface() != null || NPCChat.getOptions() != null;
	}
	
	private void skipChat(List<String> options){
//		List<String> toRemove = new ArrayList<String>();
//		for(String option:options){
//			NPCInteraction.handleConversation(new String[]{option});
//		}
//		options.remove(toRemove);
		NPCInteraction.handleConversation(options.toArray(new String[options.size()]));
	}
	
	private boolean clickNPC(RSNPC npc,String... action){
		if(npc == null)
			return false;
		if(!npc.isOnScreen()){
			acamera.turnToTile(npc);
			
		}
		return InteractionHelper.click(npc, action) && NPCInteraction.waitForConversationWindow();
	}
	
	private boolean walkToArea(RSArea area){
		return WebWalker.walkTo(area.getRandomTile()) && Timing.waitCondition(EzConditions.inArea(area), 8000);
	}
	
	private boolean withdraw(int quantity, String... itemName){
		return Banking.withdraw(1, itemName) && Timing.waitCondition(EzConditions.inventoryChange(true), 2500);
	}
	
	private void useFeatherOnScroll(){
		RSItem[]	feather = Inventory.find(FEATHER),
					scroll = Inventory.find(ENCHANTED_SCROLL);
		if(feather.length == 0 || scroll.length == 0)
			return;
		if(feather[0].click() && scroll[0].click()){
			Timing.waitCondition(EzConditions.itemLeftInventory(FEATHER), 3000);
		}
	}
	
	private boolean questCompleteInterfaceIsOpen(){
		return Interfaces.isInterfaceValid(QUEST_COMPLETE_MASTER);
	}
	
	private boolean useLamp(Skills.SKILLS skill){
		RSInterface main = Interfaces.get(LAMP_MASTER);
		if(main == null){
			if(lamp.length > 0){
				return lamp[0].click() && Timing.waitCondition(EzConditions.interfaceUp(LAMP_MASTER), 4000) && useLamp(skill);
			}
		} else{

			int index = getIndex(skill);
			if(index == -1)
				return false;
			RSInterface child = main.getChild(index);
			if(child != null && child.click("Advance")){
				RSInterface close = Interfaces.get(LAMP_MASTER,LAMP_CONFIRM);
				return close != null && close.click() && Timing.waitCondition(EzConditions.expGained(Skills.getXP(skill), skill), 6000);
			}
		}
		return false;
	}
	
	private int getIndex(Skills.SKILLS skill){
		switch(skill){
		case AGILITY:
			return 10;
		case ATTACK:
			return 3;
		case CONSTRUCTION:
			return 24;
		case COOKING:
			return 18;
		case CRAFTING:
			return 13;
		case DEFENCE:
			return 7;
		case FARMING:
			return 23;
		case FIREMAKING:
			return 19;
		case FISHING:
			return 17;
		case FLETCHING:
			return 21;
		case HERBLORE:
			return 11;
		case HITPOINTS:
			return 8;
		case HUNTER:
			return 25;
		case MAGIC:
			return 6;
		case MINING:
			return 15;
		case PRAYER:
			return 9;
		case RANGED:
			return 5;
		case RUNECRAFTING:
			return 14;
		case SLAYER:
			return 22;
		case SMITHING:
			return 16;
		case STRENGTH:
			return 4;
		case THIEVING:
			return 12;
		case WOODCUTTING:
			return 20;
		default:
			break;
		
		}
		return -1;
	}
	
}
