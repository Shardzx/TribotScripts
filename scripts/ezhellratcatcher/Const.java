package scripts.ezhellratcatcher;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.*;

public class Const {
    public static final String[] CAT_NAMES = {"Cat", "Kitten", "Hell cat", "Hell kitten", "Hellcat", "Hell kitten", "Lazy cat", "Wily cat"};

    public static final Filter<RSNPC> CATS = Filters.NPCs.nameContains("kitten", "cat")
            .combine(Filters.NPCs.nameNotContains("overgrown"), false)
            .combine(Filters.NPCs.actionsContains("Pick-up"), false);

    public static final Filter<RSItem> ORANGE = Filters.Items.nameContains("Orange spice"),
            BROWN = Filters.Items.nameContains("Brown spice"),
            RED = Filters.Items.nameContains("Red spice"),
            YELLOW = Filters.Items.nameContains("Yellow spice");

    public static final RSTile MOMS_HOUSE_TILE = new RSTile(3078, 3493, 0);

    public static final RSArea BASEMENT_OF_DOOM = new RSArea(new RSTile(3072, 9900, 0), new RSTile(3087, 9878, 0)),
            MOMS_HOUSE = new RSArea(new RSTile(3077, 3496, 0), new RSTile(3081, 3489, 0));

    public static final Filter<RSObject> LADDER_TO_BASEMENT = Filters.Objects.idEquals(12266),
            LADDER_FROM_BASEMENT = Filters.Objects.idEquals(12265);

    public static final String EMPTY_SHAKER = "Empty spice shaker";

    public static final int CAT_SETTING = 447,
            CALL_FOLLOWER_MASTER = 387,
            CALL_FOLLOWER_CHILD = 23;

    public enum COLOR {
        RED(new RSArea(new RSTile(3078, 9900, 0), new RSTile(3080, 9903, 0)), Filters.Objects.nameEquals("Curtain").combine(Filters.Objects.tileEquals(new RSTile(3078, 9899, 0)), false)),
        ORANGE(new RSArea(new RSTile(3071, 9897, 0), new RSTile(3067, 9894, 0)), Filters.Objects.nameEquals("Curtain").combine(Filters.Objects.tileEquals(new RSTile(3072, 9896, 0)), false)),
        YELLOW(new RSArea(new RSTile(3072, 9882, 0), new RSTile(3069, 9880, 0)), Filters.Objects.nameEquals("Curtain").combine(Filters.Objects.tileEquals(new RSTile(3073, 9882, 0)), false)),
        BROWN(new RSArea(new RSTile(3081, 9877, 0), new RSTile(3084, 9875, 0)), Filters.Objects.nameEquals("Curtain").combine(Filters.Objects.tileEquals(new RSTile(3082, 9878, 0)), false));

        public RSArea area;
        public Filter<RSObject> curtain;

        COLOR(RSArea area, Filter<RSObject> curtain) {
            this.area = area;
            this.curtain = curtain;
        }
    }


}
