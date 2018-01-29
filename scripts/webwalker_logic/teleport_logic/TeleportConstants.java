package scripts.webwalker_logic.teleport_logic;

import scripts.Utilities.Utilities;

public class TeleportConstants {

    public static final TeleportLimit
            LEVEL_20_WILDERNESS_LIMIT = () -> Utilities.getWildLevel() < 20,
            LEVEL_30_WILDERNESS_LIMIT = () -> Utilities.getWildLevel() < 30;

}
