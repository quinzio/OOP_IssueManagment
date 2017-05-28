package ticketing;

public class Ticket {

    private User author;
    private User assignee;
    private Component component;
    private String description;
    private String descriptionSolution;
    private Severity severity;
    private static int progNum = 1;
    private int number;
    private State state = State.Open;

    public Ticket(User author, Component com, String decription, Severity severity) {
	super();
	this.author = author;
	this.component = com;
	this.description = decription;
	this.severity = severity;
	number = progNum++;
    }

    /**
     * La classe Ticket offre i metodi getter getDescription(), getId(),
     * getComponent(), getAuthor() e getSeverity()
     */

    public String getDescription() {
	return description;
    }

    public int getId() {
	return number;
    }

    public Component getComponent() {
	return component;
    }

    public User getAuthor() {
	return author;
    }

    public Severity getSeverity() {
	return severity;
    }

    public int getNumber() {
	return number;
    };

    public enum Severity {
	Blocking, Critical, Major, Minor, Cosmetic
    };

    public static enum State {
	Open, Assigned, Closed
    }

    public User getAssignee() {
	return assignee;
    }

    public void setAssignee(User assignee) {
	this.assignee = assignee;
    }

    /**
     * La classe Ticket offre il metodo getter getState(), che dà lo stato
     * corrente del ticket.
     */
    public State getState() {
	return state;
    }

    public void close(String descripion) {
	state = State.Closed;
	descriptionSolution = descripion;
    }

    /**
     * Il metodo getSolutionDescription() della classe Ticket dà la descrizione
     * della soluzione; lancia un'eccezione se il ticket non si trova nello
     * stato Closed
     * 
     * @throws TicketException
     */
    public String name() throws TicketException {
	if (state != State.Closed)
	    throw new TicketException();
	return descriptionSolution;
    }

}
