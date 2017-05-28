package ticketing;

import java.util.Map;
import java.util.TreeMap;

public class Component {
    private String name;
    private Map<String, Component> subcomponents = new TreeMap<>();
    private Component parent;

    public Component(String name, Map<String, Component> subcomponents, Component parent) {
	super();
	this.name = name;
	if (subcomponents != null)
	    this.subcomponents = subcomponents;
	this.parent = parent;
    }

    public void addSubcomponent(String name) {
	subcomponents.put(name, new Component(name, null, this));
    }

    public String getName() {
	return name;
    }

    public Map<String, Component> getSubcomponents() {
	return subcomponents;
    }

    public Component getParent() {
	return parent;
    }

    public void setParent(Component parent) {
	this.parent = parent;
    }

}
