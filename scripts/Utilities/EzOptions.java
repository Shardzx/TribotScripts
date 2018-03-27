package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Options;
import org.tribot.api2007.Options.TABS;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSVarBit;

import java.util.Arrays;
import java.util.List;

public class EzOptions {
	public static int		INTERFACE_MASTER_ID = 261,
							TAB_BAR_CHILD_ID = 1,
							DISPLAY_TAB_COMPONENT_ID = 0,
							AUDIO_TAB_COMPONENT_ID = 2,
							CHAT_TAB_COMPONENT_ID = 4,
							CONTROLS_TAB_COMPONENT_ID = 6,
							CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_CHILD_ID = 67,
							CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_COMPONENT_ID = 4,
							CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID = 83,
							CONTROLS_OPEN_NPC_ATTACK_OPTIONS_CHILD_ID = 68,
							CONTROLS_OPEN_NPC_ATTACK_OPTIONS_COMPONENT_ID = 4,
							CONTROLS_NPC_ATTACK_OPTIONS_CHILD_ID = 84,
							SHIFT_CLICK_DROP_BUTTON = 66,
							SHIFT_CLICK_VARBIT_ID = 5542;
	public static GameTab.TABS	OPTIONS = GameTab.TABS.OPTIONS;
	public static TABS	DISPLAY = TABS.DISPLAY,
								AUDIO = TABS.AUDIO,
								CHAT = TABS.CHAT,
								CONTROLS = TABS.CONTROLS;
	public static CLICKING_OPTIONS	clickingOptions = CLICKING_OPTIONS.ALWAYS_RIGHT_CLICK;
	
	private static RSVarBit		shiftClickBit = RSVarBit.get(SHIFT_CLICK_VARBIT_ID);
	
	public static enum CLICKING_OPTIONS{
		DEPENDS_ON_COMBAT_LEVELS(0),
		ALWAYS_RIGHT_CLICK(1),
		LEFT_CLICK_WHERE_AVAILABLE(2),
		HIDDEN(3);
		
		private int ID;
		private CLICKING_OPTIONS(int id){
			this.ID = id;
		}
		public int getID(){
			return ID;
		}
	}
	public static boolean openTab(){
		return GameTab.open(OPTIONS);
	}
	
	public static boolean openOptionsTab(TABS tab){
		return Options.openTab(tab);
	}
	
	public static boolean areSoundsOn(){
		return isMusicOn() || isEffectsSoundOn() || isAreaSoundOn();
	}
	private static boolean isMusicOn(){
		return Game.getSetting(168) != 4;
	}
	private static boolean isEffectsSoundOn(){
		return Game.getSetting(169) != 4;
	}
	private static boolean isAreaSoundOn(){
		return Game.getSetting(872) != 4;
	}
	
	public static boolean turnSoundsOff(){
		if(openTab()){
			if(openOptionsTab(TABS.AUDIO)){
				if(isMusicOn()){
					RSInterface music = Interfaces.get(261,24);
					if(music != null){
						music.click();
					}
				}
				if(isEffectsSoundOn()){
					RSInterface effects = Interfaces.get(261,30);
					if(effects != null){
						effects.click();
					}
				}
				if(isAreaSoundOn()){
					RSInterface area = Interfaces.get(261,36);
					if(area != null){
						area.click();
					}
				}
				return Timing.waitCondition(new Condition(){

					@Override
					public boolean active() {
						General.sleep(100);
						return !areSoundsOn();
					}
					
				}, 1000);
			}
		}
		return false;
	}
	
	
	public static String getCurrentNPCOption(){
		RSInterface npcOptions = Interfaces.get(INTERFACE_MASTER_ID,CONTROLS_OPEN_NPC_ATTACK_OPTIONS_CHILD_ID);
		if(npcOptions!=null){
			RSInterface option = npcOptions.getChild(CONTROLS_OPEN_NPC_ATTACK_OPTIONS_COMPONENT_ID);
			if(option!=null){
				return option.getText();
			}
		}
		return null;
	}
	public static String getCurrentPlayerOption(){
		RSInterface playerOptions = Interfaces.get(INTERFACE_MASTER_ID,CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_CHILD_ID);
		if(playerOptions!=null){
			RSInterface option = playerOptions.getChild(CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_COMPONENT_ID);
			if(option!=null){
				return option.getText();
			}
		}
		return null;
	}
	
	public static CLICKING_OPTIONS getPlayerOption(){
		int id = Game.getSetting(1107);
		switch(id){
		case 0:
			return CLICKING_OPTIONS.DEPENDS_ON_COMBAT_LEVELS;
		case 1:
			return CLICKING_OPTIONS.ALWAYS_RIGHT_CLICK;
		case 2:
			return CLICKING_OPTIONS.LEFT_CLICK_WHERE_AVAILABLE;
		case 3:
			return CLICKING_OPTIONS.HIDDEN;
		}
		return null;
	}
	
	public static CLICKING_OPTIONS getNPCOption(){
		int id = Game.getSetting(1306);
		switch(id){
		case 0:
			return CLICKING_OPTIONS.DEPENDS_ON_COMBAT_LEVELS;
		case 1:
			return CLICKING_OPTIONS.ALWAYS_RIGHT_CLICK;
		case 2:
			return CLICKING_OPTIONS.LEFT_CLICK_WHERE_AVAILABLE;
		case 3:
			return CLICKING_OPTIONS.HIDDEN;
		}
		return null;
	}
	
	public static boolean setNPCClickingOptions(){
		if(getNPCOption().equals(clickingOptions))
			return true;
		if(openTab()){	
			RSInterface master = Interfaces.get(INTERFACE_MASTER_ID);
			if(master!=null&&openOptionsTab(CONTROLS)){
				RSInterface clickboxMaster = master.getChild(CONTROLS_NPC_ATTACK_OPTIONS_CHILD_ID);
				if(clickboxMaster==null){
					return false;
				} 
				if(clickboxMaster.isHidden()){
					String currentOption = getCurrentNPCOption();
					if(currentOption!=null){
						switch(clickingOptions){
						case ALWAYS_RIGHT_CLICK:
							if(currentOption.contains("Always")){
								return true;
							}
							break;
						case DEPENDS_ON_COMBAT_LEVELS:
							if(currentOption.contains("Depends")){
								return true;
							}
							break;
						case HIDDEN:
							if(currentOption.contains("Hidden")){
								return true;
							}
							break;
						case LEFT_CLICK_WHERE_AVAILABLE:
							if(currentOption.contains("Left")){
								return true;
							}
							break;
						default:
							break;
						
						}
					}
					RSInterface openClickbox = master.getChild(CONTROLS_OPEN_NPC_ATTACK_OPTIONS_CHILD_ID);
					if(openClickbox!=null)
						openClickbox= openClickbox.getChild(CONTROLS_OPEN_NPC_ATTACK_OPTIONS_COMPONENT_ID);
					if(openClickbox==null){
						return false;
					}
					if(openClickbox.click()){
						if(!Timing.waitCondition(EzConditions.interfaceVisible(INTERFACE_MASTER_ID, CONTROLS_NPC_ATTACK_OPTIONS_CHILD_ID, clickingOptions.getID()), 1000)){
							return false;
						} else{
							General.sleep(100,200);
							clickboxMaster = master.getChild(CONTROLS_NPC_ATTACK_OPTIONS_CHILD_ID);
						}
					}
				}
				if(clickboxMaster==null||clickboxMaster.isHidden()){
					return false;
				}
				RSInterface target = clickboxMaster.getChild(clickingOptions.getID());
				String txt = target.getText();
				General.println("Clicking option: " + txt);
				return target!=null&&target.click()&&
						Timing.waitCondition(EzConditions.interfaceNotVisible(INTERFACE_MASTER_ID, CONTROLS_NPC_ATTACK_OPTIONS_CHILD_ID, clickingOptions.getID()), 1000);
			}
		}
		return false;
	}
	
	public static boolean setPlayerClickingOptions(){
		if(getPlayerOption().equals(clickingOptions))
			return true;
		if(openTab()){
			RSInterface master = Interfaces.get(INTERFACE_MASTER_ID);
			if(master!=null&&openOptionsTab(CONTROLS)){
				String currentOption = getCurrentPlayerOption();
				if(currentOption!=null){
					switch(clickingOptions){
					case ALWAYS_RIGHT_CLICK:
						if(currentOption.contains("Always")){
							return true;
						}
						break;
					case DEPENDS_ON_COMBAT_LEVELS:
						if(currentOption.contains("Depends")){
							return true;
						}
						break;
					case HIDDEN:
						if(currentOption.contains("Hidden")){
							return true;
						}
						break;
					case LEFT_CLICK_WHERE_AVAILABLE:
						if(currentOption.contains("Left")){
							return true;
						}
						break;
					default:
						break;
					
					}
				}
				RSInterface clickboxMaster = master.getChild(CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID);
				if(clickboxMaster==null){
					return false;
				} 
				if(clickboxMaster.isHidden()){
					RSInterface openClickbox = master.getChild(CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_CHILD_ID);
					if(openClickbox!=null)
						openClickbox= openClickbox.getChild(CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_COMPONENT_ID);
					if(openClickbox==null){
						return false;
					}
					if(openClickbox.click()){
						if(!Timing.waitCondition(EzConditions.interfaceVisible(INTERFACE_MASTER_ID, CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID, clickingOptions.getID()), 1000)){
							return false;
						} else{
							General.sleep(100,200);
							clickboxMaster = master.getChild(CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID);
						}
					}
				}
				if(clickboxMaster==null||clickboxMaster.isHidden()){
					return false;
				}
				RSInterface target = clickboxMaster.getChild(clickingOptions.getID() + 1);
				String txt = target.getText();
				General.println("Clicking option: " + txt);
				return target!=null&&target.click()&&
						Timing.waitCondition(EzConditions.interfaceNotVisible(INTERFACE_MASTER_ID, CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID, clickingOptions.getID()), 1000);
			}
		}
		return false;
	}
	
	public static boolean setClickingOptions(int option){
		if(openTab()){
			RSInterface master = Interfaces.get(INTERFACE_MASTER_ID);
			if(master!=null&&openOptionsTab(CONTROLS)){
				RSInterface clickboxMaster = master.getChild(CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID);
				if(clickboxMaster==null){
					return false;
				} 
				if(clickboxMaster.isHidden()){
					RSInterface openClickbox = master.getChild(CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_CHILD_ID);
					if(openClickbox!=null)
						openClickbox= openClickbox.getChild(CONTROLS_OPEN_PLAYER_ATTACK_OPTIONS_COMPONENT_ID);
					if(openClickbox==null){
						return false;
					}
					if(openClickbox.click()){
						if(!Timing.waitCondition(EzConditions.interfaceVisible(INTERFACE_MASTER_ID, CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID, option), 1000)){
							return false;
						} else{
							General.sleep(100,200);
							clickboxMaster = master.getChild(CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID);
						}
					}
				}
				if(clickboxMaster==null||clickboxMaster.isHidden()){
					return false;
				}
				RSInterface target = clickboxMaster.getChild(option);
				String txt = target.getText();
				General.println("Clicking option: " + txt);
				return target!=null&&target.click()&&
						Timing.waitCondition(EzConditions.interfaceNotVisible(INTERFACE_MASTER_ID, CONTROLS_PLAYER_ATTACK_OPTIONS_CHILD_ID, option), 1000);
			}
		}
		return false;
	}
	
	public static boolean isShiftClickOn(){
		shiftClickBit = RSVarBit.get(SHIFT_CLICK_VARBIT_ID);
		return shiftClickBit != null && shiftClickBit.getValue() == 1;
	}
	
	public static boolean changeShiftClickOption(){
		if(openTab()){
			if(openOptionsTab(TABS.CONTROLS)){
				RSInterface button = Interfaces.get(INTERFACE_MASTER_ID, SHIFT_CLICK_DROP_BUTTON);
				if(button != null && button.click()){
					return Timing.waitCondition(EzConditions.varbitChanged(SHIFT_CLICK_VARBIT_ID, isShiftClickOn()?1:0), 6000);
				}
			}
		}
		return false;
	}

	public static boolean pressButton(String string){
		List<String> list = Arrays.asList(string);
		RSInterface[] iface = InterfaceCache.findInterface(INTERFACE_MASTER_ID, new Filter<RSInterface>(){

			@Override
			public boolean accept(RSInterface arg0) {
				String[] actions = arg0.getActions();
				if(actions == null)
					return false;
				for(String action:actions){
					if(list.contains(action)){
						return true;
					}
				}
				return false;
			}

		});
		return iface.length > 0 && iface[0].click();
	}
	
	
	
	
}
