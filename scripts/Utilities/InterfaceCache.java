package scripts.Utilities;

import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class InterfaceCache {
	
	private static HashMap<Filter<RSInterface>,InterfaceCache> cachedFilterSearches = new HashMap<Filter<RSInterface>,InterfaceCache>();
	
	private static HashMap<String,Filter<RSInterface>> cachedStrings = new HashMap<String,Filter<RSInterface>>();
	
	int master,child,component;
	
	public InterfaceCache(int master, int child, int component){
		this.master = master;
		this.child = child;
		this.component = component;
	}
	
	public static boolean isInterfaceUp(String text){
		Filter<RSInterface> filter;
		if(cachedStrings.containsKey(text)){
			filter = cachedStrings.get(text);
		} else{
			filter = new Filter<RSInterface>(){

				@Override
				public boolean accept(RSInterface arg0) {
					return General.stripFormatting(arg0.getText()).equals(text);
				}
				
			};
			cachedStrings.put(text, filter);
		}
		RSInterface found = getInterface(getInterface(filter));
		return found != null && !found.isHidden();
	}
	
	public static boolean isInterfaceUp(Filter<RSInterface> filter){
		InterfaceCache options = getInterface(filter);
		if(options == null)
			return false;
		RSInterface toScanFor;
		if(options.component != -1){
			toScanFor = Interfaces.get(options.master,options.child);
			if(toScanFor != null){
				toScanFor = toScanFor.getChild(options.component);
			}
		} else if(options.child != -1){
			toScanFor = Interfaces.get(options.master,options.child);
		} else{
			toScanFor = Interfaces.get(options.master);
		}
		return toScanFor != null && !toScanFor.isHidden();
	}
	
	private static RSInterface getInterface(InterfaceCache cache){
		if(cache == null)
			return null;
		RSInterface toScanFor;
		if(cache.component != -1){
			toScanFor = Interfaces.get(cache.master,cache.child);
			if(toScanFor != null){
				toScanFor = toScanFor.getChild(cache.component);
			}
		} else if(cache.child != -1){
			toScanFor = Interfaces.get(cache.master,cache.child);
		} else{
			toScanFor = Interfaces.get(cache.master);
		}
		return toScanFor;
	}
	
	private static InterfaceCache getInterface(Filter<RSInterface> filter){
		if(cachedFilterSearches.containsKey(filter)){
			return cachedFilterSearches.get(filter);
		}
		RSInterface[] all = getAllInterfaces();
		Stream<RSInterface> stream = Arrays.stream(all).filter(i -> filter.accept(i));
		RSInterface[] matching = stream.toArray(size -> new RSInterface[size]);
		InterfaceCache output = null;
		if(matching.length > 0){
			RSInterface target =  matching[0];
			if(target instanceof RSInterfaceComponent){
				output = new InterfaceCache(target.getParent().getParent().getIndex(),target.getParent().getIndex(),target.getIndex());
			} else if(target instanceof RSInterfaceChild){
				output = new InterfaceCache(target.getParent().getIndex(),target.getIndex(),-1);
			} else{
				output = new InterfaceCache(target.getIndex(),-1,-1);
			}
			General.println("had to do a search");
			cachedFilterSearches.put(filter, output);
		}
		return output;
	}
	
	public static RSInterface[] getAllInterfaces(){
		RSInterface[] masters = Interfaces.getAll();
		List<RSInterface> all = new ArrayList<RSInterface>();
		for(RSInterface master:masters){
			RSInterface[] children = master.getChildren();
			if(children != null){
				for(RSInterface child:children){
					RSInterface[] components = child.getChildren();
					if(components != null){
						for(RSInterface component:components){
							all.add(component);
						}
					} else {
						all.add(child);
					}
				}
			} else {
				all.add(master);
			}
		}
		return all.toArray(new RSInterface[all.size()]);
	}
	
	public static RSInterface[] findInterface(Filter<RSInterface> filter) {
	    return matches(filter, Interfaces.getAll());
	}
	public static RSInterface[] findInterface(int master, Filter<RSInterface> filter){
		RSInterface i = Interfaces.get(master);
		if(i == null){
			return new RSInterface[0]; 
		}
		return matches(filter,i.getChildren());
	}
	private static RSInterface[] matches(Filter<RSInterface> filter, RSInterface[] interfaces) {
	    ArrayList<RSInterface> matches = new ArrayList<>();
	    for (RSInterface i : interfaces) {
	        if (i != null) {
	            if (filter.accept(i))
	                matches.add(i);
	            RSInterface[] children = i.getChildren();
	            if (children != null)
	                matches.addAll(Arrays.asList(matches(filter, children)));
	        }
	    }
	    return matches.toArray(new RSInterface[matches.size()]);
	}
	
}
