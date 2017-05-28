package ticketing;

import java.util.HashSet;
import java.util.Set;

import ticketing.IssueManager.UserClass;

public class User {
    private String name;
    private Set<UserClass> roles = new HashSet<>();

    public User(String name, Set<UserClass> roles) {
	super();
	this.name = name;
	this.roles = roles;
    }

    public User(String name, IssueManager.UserClass role) {
	super();
	this.name = name;
	this.roles.add(role);
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Set<UserClass> getRoles() {
	return roles;
    }

    public void setRoles(Set<UserClass> roles) {
	this.roles = roles;
    }

}
