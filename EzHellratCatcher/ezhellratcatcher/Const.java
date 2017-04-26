package scripts.ezhellratcatcher;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;

public class Const {
	public static final String[]		CAT_NAMES = {"Cat","Kitten","Hell cat","Hell kitten","Hellcat","Hell kitten","Lazy cat","Wily cat"};
	
	public static final Filter<RSNPC>		CATS = Filters.NPCs.nameContains("kitten","cat")
													.combine(Filters.NPCs.nameNotContains("overgrown"), false)
													.combine(Filters.NPCs.actionsContains("Pick-up"), false);
	
	public static final RSTile			MOMS_HOUSE_TILE = new RSTile(3078, 3493, 0);
	
	public static final RSArea			BASEMENT_OF_DOOM = new RSArea(new RSTile(3072, 9900, 0),new RSTile(3087, 9878, 0)),
										MOMS_HOUSE = new RSArea(new RSTile(3077, 3496, 0),new RSTile(3081, 3489, 0));
	
	public static final Filter<RSObject>LADDER_TO_BASEMENT = Filters.Objects.idEquals(12266),
										LADDER_FROM_BASEMENT = Filters.Objects.idEquals(12265);

	public static final String EMPTY_SHAKER = "Empty spice shaker";
}
