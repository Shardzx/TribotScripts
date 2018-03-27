package scripts.ezfarm;

import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSPlayer;
import org.tribot.api2007.util.ThreadSettings;
import org.tribot.script.Script;

import scripts.Utilities.Utilities;

public class Universal {
    public static void loginSafely(Script script){
        ThreadSettings.get().setClickingAPIUseDynamic(true);
        if(Login.getLoginState() != Login.STATE.INGAME){
            boolean value = script.getLoginBotState();
            script.setLoginBotState(false);
            Login.login();
            Timing.waitCondition(new Condition(){

                @Override
                public boolean active() {
                    script.sleep(1000);
                    return Login.getLoginState() == Login.STATE.WELCOMESCREEN;
                }

            },10000);
            if(Utilities.isWelcomeScreenUp()){
                Utilities.handleWelcomeScreen();
                script.sleep(2000);
            }
            script.setLoginBotState(value);
        }
    }

    public static boolean foundNPC(String name){
        return NPCs.find(name).length > 0;
    }

    public static boolean hasTypedSomething(){
        RSPlayer me = Player.getRSPlayer();
        if(me==null){
            return false;
        }
        RSInterface chatBox = Interfaces.get(162,42);
        if(chatBox == null){
            return false;
        }
        return chatBox.getText().length() - me.getName().length() != 39;
    }

    public static Filter<RSItem> getNotesFilter() {
        return new Filter<RSItem>(){

            @Override
            public boolean accept(RSItem arg0) {
                RSItemDefinition def = arg0.getDefinition();
                return def!=null && def.isNoted();
            }

        };
    }
    public static Filter<RSItem> getUnnotedFilter() {
        return new Filter<RSItem>(){

            @Override
            public boolean accept(RSItem arg0) {
                RSItemDefinition def = arg0.getDefinition();
                return def!=null && !def.isNoted();
            }

        };
    }

    public static boolean isGameLoaded() {
        if(Login.getLoginState() != Login.STATE.INGAME){
//			General.println("Login state is not ingame.");
            return false;
        }
        if(Game.getGameState() < 25){
//			General.println("Game state is not 30");
            return false;
        }
        if(Game.getSettingsArray().length < 70){
//			General.println("Game settings array has length less than 70");
            return false;
        }
        if(Utilities.isWelcomeScreenUp()){
//			General.println("Welcome screen is up.");
            return false;
        }
        return true;
    }

}
