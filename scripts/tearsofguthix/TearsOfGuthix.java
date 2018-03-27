package scripts.tearsofguthix;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;
import org.tribot.script.EnumScript;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import scripts.Teleports.Games;
import scripts.Utilities.*;
import scripts.ezfarm.Universal;
import scripts.webwalker_logic.WebWalker;
import scripts.webwalker_logic.local.walker_engine.NPCInteraction;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ScriptManifest(authors = {"FALSkills"}, category = "Quests", name = "Tears of Guthix")
public class TearsOfGuthix extends EnumScript<State> implements Painting, MessageListening07 {

	private final int SAPPHIRE_LANTERN_LIT_ID = 4702,
			SAPPHIRE_LANTERN_UNLIT_ID = 4701,
			BULLSEYE_LANTERN_UNLIT_ID = 4548,
			BULLSEYE_LANTERN_LIT_ID = 4550;

	private State state;

	private int currentStep,
			startAttempts = 0,
			currentTearsCount = 0;

	private boolean collectedTears = false;

	private long START_TIME;

	private RSTile myPos,
			junaTile = new RSTile(3251, 9517, 2),
			beforeNorthRocks = new RSTile(3238, 9525, 2),
			afterNorthRocks = new RSTile(3241, 9525, 2),
			beforeSouthRocks = new RSTile(3237, 9499, 2);

	private RSArea MINING_AREA = new RSArea(new RSTile(3215, 9505, 2), new RSTile(3238, 9489, 2)),
			ENTRANCE_TO_TEARS_CAVE = new RSArea(new RSTile(3219, 9552, 0), new RSTile(3236, 9542, 0)),
			TEARS_CAVE = new RSArea(new RSTile(3212, 9532, 2), new RSTile(3262, 9489, 2)),
			TEARS_UPPER_AREA = new RSArea(new RSTile(3212, 9532, 2), new RSTile(3239, 9524, 2)),
			JUNA_AREA = new RSArea(new RSTile(3240, 9497, 2), new RSTile(3251, 9526, 2)),
			WALK_TO_STEPPING_STONE = new RSArea(new RSTile(3220, 9556, 0), new RSTile(3222, 9558, 0));

	private RSObject[] climbable;
	private RSObject[] crossable;
	private RSObject[] blueTears;
	private RSObject[] juna;

	private RSItem[] sapphireLantern;
	private RSItem[] bullseyeLantern;

	private String[] usablePickaxes = null,
			chat = {"Okay..."};

	@Override
	public State getInitialState() {
		START_TIME = Timing.currentTimeMillis();
		collectedTears = false; // check msgs
		ACamera camera;
		Utilities.acamera = (camera = new ACamera(this));
		if(currentStep < 2){
			if (!hasQuestItems() && TEARS_CAVE.contains(Player.getPosition())) {
				println("Error starting up. Please make sure you have all quest items.");
				return null;
			} else if(Skills.SKILLS.FIREMAKING.getActualLevel() < 50 || Skills.SKILLS.CRAFTING.getActualLevel() < 20 || Skills.SKILLS.MINING.getActualLevel() < 20){
				println("Error starting up. You don't meet the requirements for Tears of Guthix.");
				return null;
			}
		}

		return getState();
	}

	public State getState() {
		currentStep = getQuestStep();
		myPos = Player.getPosition();
		if (currentStep < 2) {
			return getQuestState();
		} else if (hasCompletedTearsThisWeek()) {
			println("Collected " + currentTearsCount + " tears. Ending script.");
			return null;
		} else if (getTicksRemaining() > 0) { // is currently collecting tears
			if (isCollectingBlueTears()) {
				return State.COLLECTING_IDLE;
			} else if (foundBlueTears()) {
				return State.COLLECTING_TEARS;
			} else if (isCollectingGreenTears()) {
				return State.CANCELLING_COLLECTION;
			}
		} else if (JUNA_AREA.contains(myPos)) {
			if (Equipment.getItem(Equipment.SLOTS.WEAPON) != null || Equipment.getItem(Equipment.SLOTS.SHIELD) != null) {
				return State.UNEQUIPPING_WEAPON_AND_SHIELD;
			} else if (Utilities.isConversing()) {
				if (startAttempts >= 3) {
					println("Problem starting collection. Maybe you can't do it this week yet? Try checking the quest log.");
					return null;
				}
				return State.JUNA_CHAT_COLLECT_TEARS;
			} else if (hasQuestCompleteInterface()) {
				return State.CLOSING_QUEST_COMPLETE_INTERFACE;
			} else if (foundJuna()) {
				return State.STORY_JUNA;
			} else {
				return State.WALKING_TO_JUNA;
			}
		} else if (hasGamesNecklace()) {
			if (Banking.isBankScreenOpen()) {
				return State.CLOSING_BANK;
			}
			return State.TELEPORTING_TO_TEARS_OF_GUTHIX;
		} else if (EzBanking.isInBank()) {
			if (Banking.isBankScreenOpen()) {
				if (Inventory.isFull()) {
					return State.DEPOSITING_ALL;
				} else {
					return State.WITHDRAWING_GAMES_NECKLACE;
				}
			} else {
				return State.OPENING_BANK;
			}
		} else {
			return State.WALKING_TO_BANK;
		}
		return null;
	}

	private State getQuestState() {
		if (currentStep == 0) {
			if (hasQuestItems()) {
				if (Banking.isBankScreenOpen()) {
					return State.CLOSING_BANK;
				} else if (Inventory.find(SAPPHIRE_LANTERN_UNLIT_ID).length > 0) {
					return State.LIGHTING_LANTERN;
				} else if (TEARS_CAVE.contains(myPos)) {
					if (JUNA_AREA.contains(myPos)) {
						if (Utilities.isConversing()) {
							return State.JUNA_CHAT;
						} else if (foundJuna()) {
							return State.TALKING_TO_JUNA;
						} else {
							return State.WALKING_TO_JUNA;
						}
					} else {
						climbable = Objects.findNearest(10, Filters.Objects.nameEquals("Rocks").combine(Filters.Objects.actionsContains("Climb"), false));
						if (climbable.length > 0) {
							return State.CLIMBING_DOWN_ROCKS;
						} else {
							return State.WALKING_TO_NORTHERN_ROCKS;
						}
					}
				} else if (!hasOpenedLumbridgeSwampCaves()) {
					println("Please unlock Lumbridge swamp caves first.");
					return null;
				} else {
					return navigateToTearsCave();
				}
			} else if (Banking.isInBank()) {
				if (Banking.isBankScreenOpen()) {
					if (Inventory.isFull()) {
						return State.DEPOSITING_ALL;
					} else if (!hasPickaxe()) {
						return State.WITHDRAWING_PICKAXE;
					} else if (!hasChisel()) {
						return State.WITHDRAWING_CHISEL;
					} else if (!hasTinderbox()) {
						return State.WITHDRAWING_TINDERBOX;
					} else if (!hasSapphireLantern()) {
						if (Banking.find("Sapphire lantern").length > 0) {
							return State.WITHDRAWING_SAPPHIRE_LANTERN;
						} else if (!hasBullseyeLantern()) {
							return State.WITHDRAWING_BULLSEYE_LANTERN;
						} else if (!hasSapphire()) {
							return State.WITHDRAWING_SAPPHIRE;
						} else {
							return State.CLOSING_BANK;
						}
					}
				} else if (hasBullseyeLantern() && hasSapphire()) {
					if (itemContainsAction(bullseyeLantern[0], "Extinguish")) {
						return State.EXTINGUISHING_BULLSEYE_LANTERN;
					}
					return State.CREATING_SAPPHIRE_LANTERN;
				} else {
					return State.OPENING_BANK;
				}
			} else {
				return State.WALKING_TO_BANK;
			}
		} else {
			if (!TEARS_CAVE.contains(myPos)) {
				return navigateToTearsCave();
			}
			boolean hasStoneBowl = Inventory.find("Stone bowl").length > 0;
			if (hasStoneBowl) {
				if (MINING_AREA.contains(myPos)) {
					if (foundSouthRocks()) {
						return State.CLIMBING_DOWN_ROCKS;
					} else {
						return State.WALKING_TO_SOUTHERN_ROCKS;
					}
				} else if (JUNA_AREA.contains(myPos)) {
					if (Equipment.getItem(Equipment.SLOTS.WEAPON) != null || Equipment.getItem(Equipment.SLOTS.SHIELD) != null) {
						return State.UNEQUIPPING_WEAPON_AND_SHIELD;
					} else if (Utilities.isConversing()) {
						return State.JUNA_CHAT_COLLECT_TEARS;
					} else if (foundJuna()) {
						return State.TALKING_TO_JUNA;
					} else {
						return State.WALKING_TO_JUNA;
					}
				} else {
					return getState();
				}
			} else if (MINING_AREA.contains(myPos)) {
				if (hasMagicStone()) {
					return State.CREATING_BOWL;
				} else {
					return State.MINING_MAGIC_STONE;
				}
			} else if (TEARS_UPPER_AREA.contains(myPos)) {
				return State.CROSSING_CHASM;
			} else if (foundNorthRocks()) {
				return State.CLIMBING_UP_NORTH_ROCKS;
			} else {
				return State.WALKING_TO_NORTHERN_ROCKS;
			}
		}
		return getState();
	}

	private State navigateToTearsCave() {
		if (foundSteppingStone()) {
			if (crossable[0].getPosition().getY() > myPos.getY()) {
				return State.ENTERING_TEARS_CAVE;
			} else {
				return State.CROSSING_STEPPING_STONES;
			}
		} else {
			return State.WALKING_TO_STEPPING_STONE;
		}
	}


	@Override
	public State handleState(State state) {
		this.state = state;
		switch (state) {
			case CANCELLING_COLLECTION:
				if (myPos.click("Walk")) {
					Timing.waitCondition(EzConditions.varbitChanged(453, 0), 5000);
				}
				break;
			case CLOSING_QUEST_COMPLETE_INTERFACE:
				if (closeQuestCompleteInterface()) {
					Timing.waitCondition(EzConditions.interfaceNotUp(277), 5000);
				}
				break;
			case OPENING_BANK:
				EzBanking.open(true);
				break;
			case JUNA_CHAT:
				NPCInteraction.handleConversation(chat);
				break;
			case TALKING_TO_JUNA:
				if (Utilities.accurateClickObject(juna[0], false, "Talk-to")) {
					Timing.waitCondition(EzConditions.isConversing(), 10000);
				}
				break;
			case STORY_JUNA:
				if (Utilities.accurateClickObject(juna[0], false, "Story")) {
					Timing.waitCondition(EzConditions.isConversing(), 10000);
				}
				break;
			case CLIMBING_DOWN_ROCKS:
				if (Utilities.clickObject(climbable[0], "Climb", false)) {
					Timing.waitCondition(EzConditions.inArea(JUNA_AREA), 10000);
				}
				break;
			case WALKING_TO_NORTHERN_ROCKS:
				if ((JUNA_AREA.contains(myPos) && PathFinding.aStarWalk(afterNorthRocks)) ||
						PathFinding.aStarWalk(beforeNorthRocks)) {
					Timing.waitCondition(EzConditions.objectAppeared(5, Filters.Objects.nameEquals("Rocks").combine(Filters.Objects.actionsContains("Climb"), false)), 8000);
				}
				break;
			case WALKING_TO_SOUTHERN_ROCKS:
				if (PathFinding.aStarWalk(beforeSouthRocks)) {
					Timing.waitCondition(EzConditions.objectAppeared(5, Filters.Objects.nameEquals("Rocks").combine(Filters.Objects.actionsContains("Climb"), false)), 8000);
				}
				break;
			case COLLECTING_IDLE:
				sleep(200, 400);
				int current = getCurrentTearsCount();
				if (current > 0) {
					currentTearsCount = current;
				}
				break;
			case COLLECTING_TEARS:
				if (Utilities.clickObject(blueTears[0], "Collect", false)) {
					if (isCurrentlyCollecting()) {
						Timing.waitCondition(new Condition() {

							@Override
							public boolean active() {
								sleep(40, 80);
								return !isCurrentlyCollecting() || isCollectingBlueTears();
							}

						}, 5000);
					}
					Timing.waitCondition(new Condition() {

						final RSTile lastTile = blueTears[0].getPosition();

						@Override
						public boolean active() {
							sleep(40, 80);
							return isCurrentlyCollecting() || !containsBlueTears(lastTile);
						}

					}, 5000);
				}
				break;
			case JUNA_CHAT_COLLECT_TEARS:
				NPCInteraction.handleConversation();
				startAttempts++;
				if (!Utilities.isConversing() && Timing.waitCondition(new Condition() {

					@Override
					public boolean active() {
						General.sleep(100, 200);
						return Player.getPosition().getX() > 3251;
					}

				}, 6000)) {
					EzCamera.setCameraRotation(270);
					Timing.waitCondition(EzConditions.tileEquals(new RSTile(3257, 9517, 2)), 8000);
				}
				break;
			case TELEPORTING_TO_TEARS_OF_GUTHIX:
				Games.teleport(Games.LOCATIONS.TEARS_OF_GUTHIX, true);
				break;
			case DEPOSITING_ALL:
				EzBanking.depositAll(true);
				break;
			case WITHDRAWING_GAMES_NECKLACE:
				EzBanking.withdraw(1, true, Filters.Items.nameContains("Games necklace"));
				break;
			case UNEQUIPPING_WEAPON_AND_SHIELD:
				RSItem weapon = Equipment.getItem(Equipment.SLOTS.WEAPON);
				final int count = Inventory.getAll().length;
				int newCount = count;
				if (weapon != null && newCount < 28 && weapon.click()) {
					newCount++;
				}
				RSItem shield = Equipment.getItem(Equipment.SLOTS.SHIELD);
				if (shield != null && newCount < 28 && shield.click()) {
					newCount++;
				}
				if (newCount > count) {
					Timing.waitCondition(EzConditions.inventoryChange(true), 5000);
				}
				if (newCount == 28) {
					println("I'm not going to drop your inventory - you need space to unequip your weapon and shield. Ending script.");
					return null;
				}
				break;
			case CLIMBING_UP_NORTH_ROCKS:
				if (Utilities.clickObject(climbable[0], "Climb", false)) {
					Timing.waitCondition(EzConditions.notInArea(JUNA_AREA), 10000);
				}
				break;
			case WALKING_TO_STEPPING_STONE:
				if (WebWalker.walkTo(WALK_TO_STEPPING_STONE.getRandomTile())) {
					Timing.waitCondition(EzConditions.objectAppeared(5, Filters.Objects.nameEquals("Stepping stone")), 8000);
				}
				break;
			case ENTERING_TEARS_CAVE:
				RSObject[] cave = Objects.findNearest(20, "Tunnel");
				if (cave.length > 0 && Utilities.clickObject(cave[0], "Enter", false)) {
					Timing.waitCondition(EzConditions.inArea(TEARS_UPPER_AREA), 12000);
				}
				break;
			case CROSSING_STEPPING_STONES:
				if (Utilities.clickObject(crossable[0], "Cross", false)) {
					Timing.waitCondition(EzConditions.inArea(ENTRANCE_TO_TEARS_CAVE), 8000);
				}
				break;
			case CROSSING_CHASM:
				RSNPC[] lights = NPCs.findNearest(Filters.NPCs.nameEquals("Light creature").combine(Filters.NPCs.inArea(TEARS_UPPER_AREA), true));
				if (lights.length > 0) {
					if ((Game.getItemSelectionState() == 1 || sapphireLantern[0].click("Use")) && lights[0].click()) {
						if (Timing.waitCondition(EzConditions.areAnimating(), 8000)) {
							Timing.waitCondition(EzConditions.notAnimating(), 30000);
						}
					}
				} else {
					if (Timing.waitCondition(EzConditions.npcAppeared(Filters.NPCs.nameEquals("Light creature").combine(Filters.NPCs.inArea(TEARS_UPPER_AREA), true)), 10000)) {
						return State.CROSSING_CHASM;
					}
				}
				break;
			case CREATING_BOWL:
				if (Game.getItemSelectionState() == 1) {
					if (Game.isUptext("Use Chisel ->")) {
						RSItem item2 = Utilities.getItemClosestToMouse("Magic stone");
						if (item2 != null && item2.click()) {
							Timing.waitCondition(EzConditions.itemEnteredInventory("Stone bowl"), 5000);
						}
					} else if (Game.isUptext("Use Magic stone ->")) {
						RSItem item2 = Utilities.getItemClosestToMouse("Chisel");
						if (item2 != null && item2.click()) {
							Timing.waitCondition(EzConditions.itemEnteredInventory("Stone bowl"), 5000);
						}
					} else {
						Utilities.misclick();
					}
				} else {
					RSItem item1 = Utilities.getItemClosestToMouse("Magic stone", "Chisel");
					if (item1 != null && item1.click() && Timing.waitUptext("->", 1500)) {
						return State.CREATING_BOWL;
					}
				}
				break;
			case MINING_MAGIC_STONE:
				RSObject[] minable = Objects.findNearest(10, Filters.Objects.nameContains("Rock").combine(Filters.Objects.actionsContains("Mine"), false));
				if (minable.length > 0 && Utilities.accurateClickObject(minable[0], false, "Mine")) {
					Timing.waitCondition(EzConditions.inventoryChange(true), 8000);
				}
				break;
			case WALKING_TO_JUNA:
				if (PathFinding.aStarWalk(junaTile)) {
					Timing.waitCondition(EzConditions.objectVisible("Juna"), 5000);
				}
				break;
			case WITHDRAWING_PICKAXE:
				if (!EzBanking.withdraw(1, true, getUsablePickaxes())) {
					if (EzBanking.areItemsLoaded() && EzBanking.outOfItem) {
						println("Could not detect pickaxe. Ending script.");
						return null;
					}
				}
				break;
			case WITHDRAWING_CHISEL:
				if (!EzBanking.withdraw(1, true, "Chisel")) {
					if (EzBanking.areItemsLoaded() && EzBanking.outOfItem) {
						println("Could not detect chisel. Ending script.");
						return null;
					}
				}
				break;
			case WITHDRAWING_BULLSEYE_LANTERN:
				if (!EzBanking.withdraw(1, true, Filters.Items.idEquals(BULLSEYE_LANTERN_LIT_ID, BULLSEYE_LANTERN_UNLIT_ID))) {
					if (EzBanking.areItemsLoaded() && EzBanking.outOfItem) {
						println("Could not detect bullseye lantern. Ending script.");
						return null;
					}
				}
				break;
			case WITHDRAWING_SAPPHIRE_LANTERN:
				if (EzBanking.withdraw(1, true, "Sapphire lantern")) {
					if (EzBanking.areItemsLoaded() && EzBanking.outOfItem) {
						println("Could not detect sapphire lantern. Ending script.");
						return null;
					}
				}
				break;
			case WITHDRAWING_SAPPHIRE:
				if (!EzBanking.withdraw(1, true, "Sapphire")) {
					if (EzBanking.areItemsLoaded() && EzBanking.outOfItem) {
						println("Could not detect sapphire. Ending script.");
						return null;
					}
				}
				break;
			case WITHDRAWING_TINDERBOX:
				if (!EzBanking.withdraw(1, true, "Tinderbox")) {
					if (EzBanking.areItemsLoaded() && EzBanking.outOfItem) {
						println("Could not detect tinderbox. Ending script.");
						return null;
					}
				}
				break;
			case CLOSING_BANK:
				EzBanking.close(true);
				break;
			case CREATING_SAPPHIRE_LANTERN:
				if (Game.getItemSelectionState() == 1) {
					if (Game.isUptext("Use Sapphire ->")) {
						RSItem item2 = Utilities.getItemClosestToMouse("Bullseye lantern");
						if (item2 != null && item2.click()) {
							sleep(100, 400);
							Timing.waitCondition(EzConditions.itemEnteredInventory("Sapphire lantern"), 5000);
						}
					} else if (Game.isUptext("Use Bullseye lantern ->")) {
						RSItem item2 = Utilities.getItemClosestToMouse("Sapphire");
						if (item2 != null && item2.click()) {
							sleep(100, 400);
							Timing.waitCondition(EzConditions.itemEnteredInventory("Sapphire lantern"), 5000);
						}
					} else {
						Utilities.misclick();
					}
				} else {
					RSItem item1 = Utilities.getItemClosestToMouse("Bullseye lantern", "Sapphire");
					if (item1 != null && item1.click() && Timing.waitUptext("->", 1500)) {
						return State.CREATING_SAPPHIRE_LANTERN;
					}
				}
				break;
			case LIGHTING_LANTERN:
				if (Game.getItemSelectionState() == 1) {
					if (Game.isUptext("Use Sapphire lantern ->")) {
						RSItem item2 = Utilities.getItemClosestToMouse("Tinderbox");
						if (item2 != null && item2.click()) {
							sleep(100, 400);
							Timing.waitCondition(EzConditions.itemEnteredInventory(SAPPHIRE_LANTERN_LIT_ID), 5000);
						}
					} else if (Game.isUptext("Use Tinderbox ->")) {
						RSItem item2 = Utilities.getItemClosestToMouse("Sapphire lantern");
						if (item2 != null && item2.click()) {
							sleep(100, 400);
							Timing.waitCondition(EzConditions.itemEnteredInventory(SAPPHIRE_LANTERN_LIT_ID), 5000);
						}
					} else {
						Utilities.misclick();
					}
				} else {
					RSItem item1 = Utilities.getItemClosestToMouse("Sapphire lantern", "Tinderbox");
					if (item1 != null && item1.click() && Timing.waitUptext("->", 1500)) {
						return State.LIGHTING_LANTERN;
					}
				}
				break;
			case EXTINGUISHING_BULLSEYE_LANTERN:
				if (bullseyeLantern[0].click("Extinguish")) {
					Timing.waitCondition(EzConditions.itemLeftInventory(BULLSEYE_LANTERN_LIT_ID), 5000);
				}
				break;
			case WALKING_TO_BANK:
				if (WebWalker.walkToBank()) {
					Timing.waitCondition(EzConditions.isInBank(), 8000);
				}
				break;
		}
		sleep(100, 400);
		return getState();
	}


	@Override
	public void playerMessageReceived(String s, String s1) {

	}

	@Override
	public void duelRequestReceived(String s, String s1) {

	}

	@Override
	public void tradeRequestReceived(String s) {

	}

	@Override
	public void serverMessageReceived(String s) {
		if (s.contains("Your time in the cave")) {
			collectedTears = true;
		}
	}

	@Override
	public void clanMessageReceived(String s, String s1) {

	}

	@Override
	public void personalMessageReceived(String s, String s1) {

	}

	@Override
	public void onPaint(Graphics g) {
		long RUNTIME = Timing.currentTimeMillis() - START_TIME;
		g.setColor(Color.CYAN);
		g.drawString("Tears of guthix", 5, 50);
		g.drawString("Running for: " + Timing.msToString(RUNTIME), 5, 70);
		g.drawString("State: " + state, 5, 90);
	}

	private boolean hasCompletedTearsThisWeek() {
		return collectedTears;
	}

	private int getQuestStep() {
		RSVarBit quest = RSVarBit.get(451);
		return quest != null ? quest.getValue() : 0;
	}

	private int getTicksRemaining() {
		RSVarBit ticks = RSVarBit.get(5099);
		return ticks != null ? ticks.getValue() : 0;
	}

	private int getCurrentTearsCount() {
		RSVarBit count = RSVarBit.get(455);
		return count != null ? count.getValue() : 0;
	}

	private boolean isCurrentlyCollecting() {
		RSVarBit status = RSVarBit.get(453);
		return status != null && status.getValue() == 1;
	}

	private boolean hasQuestItems() {
		return hasPickaxe() && hasChisel() && hasSapphireLantern() && hasTinderbox();
	}

	private boolean hasPickaxe() {
		RSItem[] pickaxe = Inventory.find(Filters.Items.nameEquals(getUsablePickaxes()).combine(Universal.getUnnotedFilter(), false));
		return pickaxe.length > 0;
	}

	private boolean hasChisel() {
		RSItem[] chisel = Inventory.find(Filters.Items.nameEquals("Chisel").combine(Universal.getUnnotedFilter(), false));
		return chisel.length > 0;
	}

	private boolean hasTinderbox() {
		RSItem[] tinderbox = Inventory.find(Filters.Items.nameEquals("Tinderbox").combine(Universal.getUnnotedFilter(), false));
		return tinderbox.length > 0;
	}

	private boolean hasSapphireLantern() {
		sapphireLantern = Inventory.find(SAPPHIRE_LANTERN_LIT_ID, SAPPHIRE_LANTERN_UNLIT_ID);
		return sapphireLantern.length > 0;
	}

	private boolean hasBullseyeLantern() {
		bullseyeLantern = Inventory.find(BULLSEYE_LANTERN_UNLIT_ID, BULLSEYE_LANTERN_LIT_ID);
		return bullseyeLantern.length > 0;
	}

	private boolean hasSapphire() {
		RSItem[] sapphire = Inventory.find(Filters.Items.nameEquals("Sapphire").combine(Universal.getUnnotedFilter(), false));
		return sapphire.length > 0;
	}

	private boolean hasOpenedLumbridgeSwampCaves() {
		RSVarBit bit = RSVarBit.get(279);
		return bit != null && bit.getValue() >= 1;
	}

	private boolean isCollectingBlueTears() {
		if (!isCurrentlyCollecting())
			return false;
		int orientation = Player.getRSPlayer().getOrientation();
		RSCharacter.DIRECTION dir = getDirection(orientation);
		return getObjectsAtDirection(Filters.Objects.nameEquals("Blue tears"), dir).length > 0;
	}

	private boolean isCollectingGreenTears() {
		if (!isCurrentlyCollecting())
			return false;
		int orientation = Player.getRSPlayer().getOrientation();
		RSCharacter.DIRECTION dir = getDirection(orientation);
		return getObjectsAtDirection(Filters.Objects.nameEquals("Green tears"), dir).length > 0;
	}

	private RSObject[] getObjectsAtDirection(Filter<RSObject> filter, RSCharacter.DIRECTION direction) {
		switch (direction) {
			case N:
				return Objects.getAt(new RSTile(myPos.getX(), myPos.getY() + 1, myPos.getPlane()), filter);
			case E:
				return Objects.getAt(new RSTile(myPos.getX() + 1, myPos.getY(), myPos.getPlane()), filter);
			case S:
				return Objects.getAt(new RSTile(myPos.getX(), myPos.getY() - 1, myPos.getPlane()), filter);
		}
		return new RSObject[0];
	}

	private RSCharacter.DIRECTION getDirection(int orientation) {
		switch (orientation) {
			case 0:
				return RSCharacter.DIRECTION.S;
			case 1024:
				return RSCharacter.DIRECTION.N;
			case 1536:
				return RSCharacter.DIRECTION.E;
		}
		return RSCharacter.DIRECTION.NONE;
	}

	private boolean containsBlueTears(RSTile tile) {
		return Objects.getAt(tile, Filters.Objects.nameEquals("Blue tears")).length > 0;
	}

	private boolean foundBlueTears() {
		blueTears = Objects.findNearest(8, Filters.Objects.nameEquals("Blue tears"));
		return blueTears.length > 0;
	}

	private boolean hasGamesNecklace() {
		RSItem[] gamesNecklace = Inventory.find(Filters.Items.nameContains("Games necklace"));
		return gamesNecklace.length > 0;
	}

	private boolean foundSteppingStone() {
		return (crossable = Objects.find(5, "Stepping stone")).length > 0;

	}

	private boolean hasMagicStone() {
		RSItem[] magicStone = Inventory.find("Magic stone");
		return magicStone.length > 0;
	}

	private String[] getUsablePickaxes() {
		if (usablePickaxes != null)
			return usablePickaxes;
		List<String> usable = new ArrayList<>();
		int miningLevel = Skills.SKILLS.MINING.getActualLevel();
		if (miningLevel >= 61) usable.add("Dragon pickaxe");
		if (miningLevel >= 41) usable.add("Rune pickaxe");
		if (miningLevel >= 31) usable.add("Adamant pickaxe");
		if (miningLevel >= 21) usable.add("Mithril pickaxe");
		if (miningLevel >= 11) usable.add("Black pickaxe");
		if (miningLevel >= 6) usable.add("Steel pickaxe");
		usable.add("Iron pickaxe");
		usable.add("Bronze pickaxe");
		usablePickaxes = usable.toArray(new String[usable.size()]);
		return usablePickaxes;
	}

	private boolean itemContainsAction(RSItem item, String... action) {
		RSItemDefinition def = item.getDefinition();
		List<String> set = new ArrayList<>(Arrays.asList(action));
		if (def == null)
			return false;
		String[] actions = def.getActions();
		if (actions == null)
			return false;
		for (String current : actions) {
			if (set.contains(current)) {
				return true;
			}
		}
		return false;
	}

	private boolean foundJuna() {
		juna = Objects.find(15, "Juna");
		return juna.length > 0;
	}

	private boolean foundNorthRocks() {
		climbable = Objects.findNearest(10, Filters.Objects.nameEquals("Rocks").combine(new Filter<RSObject>() {

			@Override
			public boolean accept(RSObject o) {
				return o.getPosition().getY() > 9520;
			}
		}, false));
		return climbable.length > 0;
	}

	private boolean foundSouthRocks() {
		climbable = Objects.findNearest(10, Filters.Objects.nameEquals("Rocks").combine(new Filter<RSObject>() {

			@Override
			public boolean accept(RSObject o) {
				return o.getPosition().getY() < 9500;
			}
		}, false).combine(Filters.Objects.actionsContains("Climb"), false));
		return climbable.length > 0;
	}

	private boolean hasQuestCompleteInterface() {
		return Interfaces.isInterfaceValid(277);
	}

	private boolean closeQuestCompleteInterface() {
		RSInterface close = Interfaces.get(277, 17);
		return close != null && close.click();
	}
}
